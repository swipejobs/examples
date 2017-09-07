package com.swipejobs.examples.pactmessaging;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.AmqpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Greg Newitt
 * @version 1.0
 * @since 31/08/2017.
 */
@RunWith(PactRunner.class)
@Provider("sampleKafkaProvider2")
@PactBroker(host="localhost", port="8080")
public class ProducerSimpleTest {
    /** This test case is intended to illustrate the simple
     * way of testing a messaging provider using Pact.
     * We just pass back the message produced by the provider.
     * The advantage is simplicity.
     * The disadvantage is that we are not testing the messaging
     * infrastructure.
     * Also it may not be simple to extract the generated message.
     */
    Producer producer=new Producer();

    @TestTarget
    public final Target target2 = new AmqpTarget();

    @BeforeClass
    public static void setUpService(){


    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void produce() throws Exception {

    }

    @PactVerifyProvider("simple")
    public String verifyAnotherMessageForOrder1() throws Exception{
        return producer.createSimpleMessage(0);
    }


}