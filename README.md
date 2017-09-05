# Sample Programs for Pact Consumer contract testing Kafka Messaging

This project provides a simple example of a Kafka
producer and consumer. These are then tested using the Pact consumer contract
testing framework.
There are very few examples of this out there, so I thought it was worthwhile
putting together a working example.

The tests use an embedded Kafka, if you want to run the code you will need
to install kafka.

You need to generate the Pacts.  Run the 2 consumer tests:
ConsumerSimpleTest and ConsumerEmbeddedTest
This should generate 2 pact files in target/pacts.

Use gradle pactPublish to send them to your pactBroker.

Then 

This README takes you through the steps for downloading and installing
a single node version of Kafka. We don't focus on the requirements for
a production Kafka cluster because we want to focus on the code itself
and various aspects of starting and restarting.

## Pre-requisites
To start, you need to get Kafka up and running and create some topics.

### Step 1: Download Kafka
Download the 0.9.0.0 release and un-tar it.
```
$ tar -xzf kafka_2.11-0.9.0.0.tgz
$ cd kafka_2.11-0.9.0.0
```
### Step 2: Start the server
Start a ZooKeeper server. Kafka has a single node Zookeeper configuration built-in.
```
$ bin/zookeeper-server-start.sh config/zookeeper.properties &
[2013-04-22 15:01:37,495] INFO Reading configuration from: config/zookeeper.properties (org.apache.zookeeper.server.quorum.QuorumPeerConfig)
...
```
Note that this will start Zookeeper in the background. To stop
Zookeeper, you will need to bring it back to the foreground and use
control-C or you will need to find the process and kill it.

Now start Kafka itself:
```
$ bin/kafka-server-start.sh config/server.properties &
[2013-04-22 15:01:47,028] INFO Verifying properties (kafka.utils.VerifiableProperties)
[2013-04-22 15:01:47,051] INFO Property socket.send.buffer.bytes is overridden to 1048576 (kafka.utils.VerifiableProperties)
...
```
As with Zookeeper, this runs the Kafka broker in the background. To
stop Kafka, you will need to bring it back to the foreground or find
the process and kill it explicitly using `kill`.

### Step 3: Create the topics for the example programs
We need two topics for the example program
```
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic messages
```
These can be listed
```
$ bin/kafka-topics.sh --list --zookeeper localhost:2181
messages
```
Note that you will see log messages from the Kafka process when you
run Kafka commands. You can switch to a different window if these are
distracting.

The broker can be configured to auto-create new topics as they are mentioned, but that is often considered a bit 
dangerous because mis-spelling a topic name doesn't cause a failure.

## Now for the real fun
At this point, you should have a working Kafka broker running on your
machine. The next steps are to compile the example programs and play
around with the way that they work.

### Step 4: Compile and package up the example programs
Go back to the directory where you have the example programs and
compile and build the example programs.
```
$ cd ..
$ gradle build
...
```

For convenience, use:
 gradle run -Pmyargs=consumer
 gradle run -Pmyargs=producer

### Step 5: Run the example producer

The producer will send a message to `messages`. Since there isn't
any consumer running yet, nobody will receive the messages. 

```
$ gradle run -Pmyargs=producer
```
### Step 6: Start the example consumer
Running the consumer will not actually cause any messages to be
processed. The reason is that the first time that the consumer is run,
this will be the first time that the Kafka broker has ever seen the
consumer group that the consumer is using. That means that the
consumer group will be created and the default behavior is to position
newly created consumer groups at the end of all existing data.
```
$ gradle run -Pmyargs=consumer
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```
After running the consumer once, however, if we run the producer again
and then run the consumer *again*, we will see the consumer pick up
and start processing messages shortly after it starts.


```
$ gradle run -Pmyargs=consumer
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Got 1 records after 847 timeouts
Embedded message received
```

### Step 7: Send more messages
In a separate window, run the producer again without stopping the
consumer. 

## Remaining Mysteries
If you change the consumer properties, particular the buffer sizes
near the end of properties file, you may notice that the
consumer can easily get into a state where it has about 5 seconds of
timeouts during which no data comes from Kafka and then a full
bufferful arrives. Once in this mode, the consumer tends to not
recover to normal processing. It isn't clear what is going on, but
setting the buffer sizes large enough can avoid the problem.

## Cleaning Up
When you are done playing, stop Kafka and Zookeeper and delete the
data directories they were using from /tmp

```
$ fg
bin/kafka-server-start.sh config/server.properties
^C[2016-02-06 18:06:56,683] INFO [Kafka Server 0], shutting down (kafka.server.KafkaServer)
...
[2016-02-06 18:06:58,977] INFO EventThread shut down (org.apache.zookeeper.ClientCnxn)
[2016-02-06 18:06:58,978] INFO Closed socket connection for client /fe80:0:0:0:0:0:0:1%1:65170 which had sessionid 0x152b958c3300000 (org.apache.zookeeper.server.NIOServerCnxn)
[2016-02-06 18:06:58,979] INFO [Kafka Server 0], shut down completed (kafka.server.KafkaServer)
$ fg
bin/zookeeper-server-start.sh config/zookeeper.properties
^C
$ rm -rf /tmp/zookeeper/version-2/log.1  ; rm -rf /tmp/kafka-logs/
$
```
