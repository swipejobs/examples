package com.swipejobs.examples.pactmessaging;

import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This kafkaProducer will send messages to topic "messages".
 * There are 2 different message types, simple and embedded.
 * This is a demonstration of how Pact can be used for contract testing
 * an application using Kafka.  It also demonstrates two different ways of doing this.
 * (Simple and embedded)
 */
public class Producer {

    KafkaProducer<String, String> kafkaProducer;

    public Producer () {

    }

    private void createProducer(Properties properties) {
        // set up the kafkaProducer
            kafkaProducer = new KafkaProducer<>(properties);
    }

    private void createProducer() throws IOException {
        // set up the kafkaProducer
        Properties properties = getProducerProperties();
        createProducer(properties);
    }

    void createProducer(String kafkaHost) throws IOException {
        // set up the kafkaProducer
        Properties properties = getProducerProperties();
        properties.setProperty("bootstrap.servers",kafkaHost);
        createProducer(properties);
    }

    private Properties getProducerProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream props = Resources.getResource("producer.props").openStream()) {
            properties.load(props);
        }
        return properties;
    }

    void produceEmbedded()  {
        try {
            for (int i = 0; i < 1; i++) {
                // send lots of messages
                kafkaProducer.send(new ProducerRecord<String, String>(
                        "messages","key1",
                        createEmbeddedMessage(i)));

            }
        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            kafkaProducer.close();
        }

    }

    public void produceSimple()  {
        try {
            for (int i = 0; i < 1; i++) {
                // send lots of messages
                kafkaProducer.send(new ProducerRecord<String, String>(
                        "messages","simple",createSimpleMessage(i)
                ));

            }
        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            kafkaProducer.close();
        }

    }

    private String createEmbeddedMessage(int i){
        return createMessage(i,"embedded");
    }

    private String createMessage(int i, String type){
        return String.format("{\"type\":\"%s\", \"count\":%d, \"metadata\":\"\"}",type,i);

    }

    String createSimpleMessage(int i){
        return createMessage(i,"simple");
    }

    public static void main(String[] args) throws IOException {
        Producer thisProducer=new Producer();

        thisProducer.createProducer();

        thisProducer.produceEmbedded();

    }
}
