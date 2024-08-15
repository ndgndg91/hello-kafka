package com.ndgndg91.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*


fun main() {
    val log = LoggerFactory.getLogger("ProducerDemoKey")
    log.info("I am a Kafka Producer!")

    // create Producer Properties
    val properties = Properties()

    // connect to localhost
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")

    // set producer properties
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

    // create the Producer
    val producer = KafkaProducer<String, String>(properties)


    for (j in 0..1) {
        for (i in 0..9) {
            val topic = "demo_java"
            val key = "id_$i"
            val value = "hello world $i"

            // create a Producer Record
            val producerRecord =
                ProducerRecord(topic, key, value)

            // send data
            producer.send(
                producerRecord
            ) { metadata: RecordMetadata, e: Exception? ->
                // executes every time a record successfully sent or an exception is thrown
                if (e == null) {
                    // the record was successfully sent
                    log.info("Key: " + key + " | Partition: " + metadata.partition())
                } else {
                    log.error("Error while producing", e)
                }
            }
        }

        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    // tell the producer to send all data and block until done -- synchronous
    producer.flush()

    // flush and close the producer
    producer.close()
}