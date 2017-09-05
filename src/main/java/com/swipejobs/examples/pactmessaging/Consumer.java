package com.swipejobs.examples.pactmessaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

/**
 * This program reads messages from the topic "messages".
 * The intention is to demonstrate how the Pact framework can be used for
 * contract testing applications using Kafka messaging.
 * <p/>
 */
public class Consumer {
    // set up house-keeping
    private ObjectMapper mapper;
    private int timeouts = 0;

    KafkaConsumer<String, String> kafkaConsumer;


    Consumer()
    {
        // set up house-keeping
        mapper = new ObjectMapper();
    }

    private void createConsumer(Properties properties) throws IOException {
        // and the kafkaConsumer
        kafkaConsumer = new KafkaConsumer<>(properties);
    }

    private void createConsumer() throws IOException {
        // and the kafkaConsumer
        Properties properties = getConsumerProperties();

        createConsumer(properties);
        kafkaConsumer.subscribe(Collections.singletonList("messages"));
    }

    void createConsumer(String kafkaHost) throws IOException {
        // and the kafkaConsumer
        Properties properties = getConsumerProperties();

        properties.setProperty("bootstrap.servers",kafkaHost);
        createConsumer(properties);
//        kafkaConsumer.subscribe(Collections.singletonList("messages"));
    }

    private Properties getConsumerProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream props = Resources.getResource("consumer.props").openStream()) {
            properties.load(props);
            if (properties.getProperty("group.id") == null) {
                properties.setProperty("group.id", "group-" + new Random().nextInt(100000));
            }
        }
        return properties;
    }

    private void consume() throws IOException {
        //noinspection InfiniteLoopStatement
        while (true) {
            // read records with a short timeout. If we time out, we don't really care.
            ConsumerRecords<String, String> records = kafkaConsumer.poll(200);

            processRecords(records);
        }

    }

    void processRecords(ConsumerRecords<String, String> records) throws IOException {
        if (records.count() == 0) {
            timeouts++;
        } else {
            System.out.printf("Got %d records after %d timeouts%n", records.count(), timeouts);
            timeouts = 0;
        }
        for (ConsumerRecord<String, String> record : records) {
            processRecord(record);
        }

    }

    private void processRecord(ConsumerRecord<String, String> record) throws IOException {
        switch (record.topic()) {
            case "messages":
                JsonNode msg = mapper.readTree(record.value());
                switch (msg.get("type").asText()) {
                    case "embedded":
                        System.out.println("Embedded message received");
                        break;
                    case "simple":
                        System.out.println("Simple message received");
                        break;
                    default:
                        throw new IllegalArgumentException("Illegal message type: " + msg.get("type"));
                }
                break;
            default:
                throw new IllegalStateException("Unknown topic " + record.topic());
        }

    }


    public static void main(String[] args) throws IOException {

        Consumer thisConsumer= new Consumer();

        thisConsumer.createConsumer();

        thisConsumer.consume();

    }

}
