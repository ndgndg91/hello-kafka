package com.ndgndg91.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


fun main() {
    val logger = LoggerFactory.getLogger("ProducerDemo")
    logger.info("Starting Kafka producer")

    // create Producer Properties
    val properties = Properties()

    // connect to localhost
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")

    // set producer properties
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

    // create the Producer
    val producer = KafkaProducer<String, String>(properties)

    // create a Producer Record
    val producerRecord = ProducerRecord<String, String>("demo_java", "hello world")

    // send data
    producer.send(producerRecord)

    // tell the producer to send all data and block until done -- synchronous
    producer.flush()

    // flush and close the producer
    producer.close()
}