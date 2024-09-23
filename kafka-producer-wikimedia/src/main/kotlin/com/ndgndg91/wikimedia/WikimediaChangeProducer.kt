package com.ndgndg91.wikimedia

import com.launchdarkly.eventsource.EventSource
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.net.URI
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val bootstrapServers = "127.0.0.1:9092"

    // create producer properties
    val properties = Properties()

    // connect to localhost
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)

    // set producer properties
    properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "wikimedia-producer")
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

    // set safe producer configs (kafka <= 2.8)
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "all") // same as -1
    properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
    properties.setProperty(ProducerConfig.RETRIES_CONFIG, Int.MAX_VALUE.toString())

    // set high throughput producer config
    properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20")
    properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, (32*1024).toString())
    properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy")

    // create the Producer
    val producer = KafkaProducer<String, String>(properties)

    val topic = "wikimedia.recentchange"

    val eventHandler = WikimediaChangeHandler(producer, topic)
    val url = "https://stream.wikimedia.org/v2/stream/recentchange"

    val eventSource = EventSource.Builder(eventHandler, URI.create(url))
        .build()

    //start the producer in another thread
    eventSource.start()

    // Schedule to close the eventSource after 10 minutes
    val scheduler = Executors.newScheduledThreadPool(1)
    scheduler.schedule({
        eventSource.close()
        println("EventSource has been closed after 10 minutes.")
    }, 10, TimeUnit.MINUTES)
}