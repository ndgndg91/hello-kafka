package com.ndgndg91.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*


fun main() {
    val log = LoggerFactory.getLogger("ConsumerDemoWithShutdown")
    log.info("I am a Kafka Consumer!")

    val groupId = "my-java-application"
    val topic = "demo_java"


    // create Producer Properties
    val properties = Properties().apply {
        // connect to Localhost
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
        // create consumer configs
        put("key.deserializer", StringDeserializer::class.java.name)
        put("value.deserializer", StringDeserializer::class.java.name)
        put("group.id", groupId)
        put("auto.offset.reset", "earliest")
    }

    // create a consumer
    val consumer = KafkaConsumer<String, String>(properties)

    // get a reference to the main thread
    val mainThread = Thread.currentThread()


    // adding the shutdown hook
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...")
            consumer.wakeup()

            // join the main thread to allow the execution of the code in the main thread
            try {
                mainThread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    })

    try {
        // subscribe to a topic
        consumer.subscribe(listOf(topic))
        // poll for data
        while (true) {
            val timeout = Duration.ofMillis(1000)
            val records: ConsumerRecords<String, String> = consumer.poll(timeout)
            val assignment = consumer.assignment()
            assignment.forEach { log.info("assignment : {}", it) }

            for (record in records) {
                log.info("Key: " + record.key() + ", Value: " + record.value())
                log.info("Partition: " + record.partition() + ", Offset: " + record.offset())
            }
        }
    } catch (e: WakeupException) {
        log.info("Consumer is starting to shut down")
    } catch (e: Exception) {
        log.error("Unexpected exception in the consumer", e)
    } finally {
        consumer.close() // close the consumer, this will also commit offsets
        log.info("The consumer is now gracefully shut down")
    }
}