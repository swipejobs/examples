package com.swipejobs.examples.pactmessaging;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.AmqpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.kafka.test.rule.KafkaEmbedded;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Greg Newitt
 * @version 1.0
 * @since 4/09/2017.
 */
@RunWith(PactRunner.class)
@Provider("sampleKafkaProvider")
@PactBroker(host="localhost", port="8080")
public class ProducerEmbeddedKafkaTest {
    /** This test case is intended to illustrate the more complex way of
     * testing a messaging provider using Pact.
     * We startup an embedded Kafka instance and point the provider at it.
     * We then create a consumer for our desired messages and extract them
     * from Kafka after the Provider produces them.
     * There is more effort required to set this test up, but it more
     * completely tests the messaging infrastructure.
     */

    @ClassRule
    public static KafkaEmbedded kafka=new KafkaEmbedded(1,true,"messages");

    private Producer producer = new Producer();
    private Consumer consumer2 = new Consumer();

    @TestTarget
    public final Target target = new AmqpTarget();

    @BeforeClass
    public static void setUpService() {


    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void produce() throws Exception {

    }

    @PactVerifyProvider("embedded")
    public String verifyAnotherMessageForOrder2() throws Exception {

        String receivedValue=null;
        producer.createProducer(kafka.getBrokersAsString());
        consumer2.createConsumer(kafka.getBrokersAsString());

        consumer2.kafkaConsumer.subscribe(Collections.singletonList("messages"));
        ConsumerRecords<String, String> records = consumer2.kafkaConsumer.poll(2000);
        producer.produceEmbedded();
        assertThat(records,is(notNullValue()));
        assertThat(records.isEmpty(),is(true));
        records = consumer2.kafkaConsumer.poll(2000);

        assertThat(records,is(notNullValue()));
        assertThat(records.isEmpty(),is(false));
        for (ConsumerRecord<String, String> record : records) {
            receivedValue=record.value();
            System.out.println("received value - "+receivedValue);
        }
        return receivedValue;
    }
}
