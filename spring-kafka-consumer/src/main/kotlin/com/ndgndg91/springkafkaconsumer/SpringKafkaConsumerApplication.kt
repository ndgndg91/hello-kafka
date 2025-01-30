package com.ndgndg91.springkafkaconsumer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ndgndg91.springkafkacommon.GIRI_TOPIC
import com.ndgndg91.springkafkacommon.GiriItem
import com.ndgndg91.springkafkaconsumer.config.kafka.KafkaConsumerConfig.Companion.DEFAULT_KAFKA_LISTENER_CONTAINER_FACTORY
import io.cloudevents.CloudEvent
import io.cloudevents.core.CloudEventUtils
import io.cloudevents.jackson.PojoCloudEventDataMapper
import org.apache.kafka.clients.consumer.Consumer
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@SpringBootApplication
class SpringKafkaConsumerApplication {
    private val logger = LoggerFactory.getLogger(javaClass)
    @KafkaListener(
        topics = [GIRI_TOPIC],
        containerFactory = DEFAULT_KAFKA_LISTENER_CONTAINER_FACTORY
    )
    fun consume(message: CloudEvent, consumer: Consumer<*, *>) {
        val giriItem = CloudEventUtils
            .mapData(message, PojoCloudEventDataMapper.from(jacksonObjectMapper(), GiriItem::class.java))
            ?.value

        logger.info("GiriItem: $giriItem")
    }
}

fun main(args: Array<String>) {
    runApplication<SpringKafkaConsumerApplication>(*args)
}
