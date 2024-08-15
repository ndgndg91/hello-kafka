package com.ndgndg91.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*


fun main() {
    val log = LoggerFactory.getLogger("ConsumerDemo")
    log.info("I am a Kafka Consumer!")

    val groupId = "my-java-application"
    val topic = "demo_java"


    // create Producer Properties
    val properties = Properties()


//     connect to Localhost
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")


    // create consumer configs
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")


    // create a consumer
    val consumer = KafkaConsumer<String, String>(properties)


    // subscribe to a topic
    consumer.subscribe(listOf(topic))


    // poll for data
    while (true) {
        log.info("Polling")

        val timeout = Duration.ofMillis(1000)
        val records: ConsumerRecords<String, String> = consumer.poll(timeout)

        for (record in records) {
            log.info("Key: " + record.key() + ", Value: " + record.value())
            log.info("Partition: " + record.partition() + ", Offset: " + record.offset())
        }
    }
}