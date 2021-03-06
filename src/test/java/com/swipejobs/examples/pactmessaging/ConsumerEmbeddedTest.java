package com.swipejobs.examples.pactmessaging;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.MessagePactProviderRule;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.model.v3.messaging.MessagePact;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
//import org.mockito.junit.MockitoJUnitRunner;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gnewitt
 * @version 1.0
 * @since 31/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsumerEmbeddedTest {
    Consumer testee=new Consumer();

    @Rule
    public MessagePactProviderRule mockProvider = new MessagePactProviderRule(this);

    @Pact(provider="sampleKafkaProvider", consumer="sampleKafkaConsumer")
    public MessagePact createPact(MessagePactBuilder builder){
        PactDslJsonBody body = new PactDslJsonBody()
                .stringValue("type","embedded")
                .integerType("count")
                .asBody();
        Map<String, String> metadata = new HashMap<>();

        return builder
                .expectsToReceive("embedded")
                .withMetadata(metadata)
                .withContent(body)
                .toPact();

    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    @PactVerification({"test_provider"})
    public void consumerTest() throws Exception{
        Map<TopicPartition, List<ConsumerRecord<String, String>>> topicPartitions=new HashMap<>();
        TopicPartition topicPartition=new TopicPartition("messages",1);
        List<ConsumerRecord<String, String>> recs=new ArrayList<>();
        ConsumerRecord<String,String> record=new ConsumerRecord<>("messages",1,0,"","{\"type\":\"embedded\", \"count\":1}");
        recs.add(record);
        topicPartitions.put(topicPartition,recs);
        ConsumerRecords<String, String> records=new ConsumerRecords<String,String>(topicPartitions);
        testee.processRecords(records);
    }

}