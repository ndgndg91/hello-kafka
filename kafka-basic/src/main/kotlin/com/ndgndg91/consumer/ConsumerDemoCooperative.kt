package com.ndgndg91.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*


fun main() {
    val log = LoggerFactory.getLogger("ConsumerDemoCooperative")
    log.info("I am a Kafka Consumer!")

    val groupId = "my-java-application"
    val topic = "demo_java"

    // create Producer Properties
    val properties = Properties()

    // connect to Localhost
    properties.setProperty("bootstrap.servers", "127.0.0.1:9092")

    // create consumer configs
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor::class.java.name)
    //        properties.setProperty("group.instance.id", "...."); // strategy for static assignments

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