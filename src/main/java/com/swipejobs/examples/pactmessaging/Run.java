package com.swipejobs.examples.pactmessaging;

import java.io.IOException;

/**
 * Pick whether we want to run as Producer or Consumer. This lets us
 * have a single executable as a build target.  Doesn't work quite so well with gradle.
 * Have had to implement a workaround using -Pmyargs=
 */
public class Run {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Must have either 'kafkaProducer' or 'kafkaConsumer' as argument");
        }
        switch (args[0]) {
            case "producer":
                Producer.main(args);
                break;
            case "consumer":
                Consumer.main(args);
                break;
            default:
                throw new IllegalArgumentException("Don't know how to do " + args[0]);
        }
    }
}
