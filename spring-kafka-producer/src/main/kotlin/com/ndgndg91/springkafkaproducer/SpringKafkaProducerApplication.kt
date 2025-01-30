package com.ndgndg91.springkafkaproducer

import com.ndgndg91.springkafkacommon.EventMessage
import com.ndgndg91.springkafkacommon.GIRI_TOPIC
import com.ndgndg91.springkafkacommon.GiriItem
import com.ndgndg91.springkafkaproducer.config.KafkaProducerConfig.Companion.KAFKA_TRANSACTION_MANAGER
import io.cloudevents.CloudEvent
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
@SpringBootApplication
class SpringKafkaProducerApplication(
    private val kafkaTemplate: KafkaTemplate<String, CloudEvent>
) {

    @Transactional(KAFKA_TRANSACTION_MANAGER)
    @PostMapping("/api/produce")
    fun produce(
        @RequestBody body: Map<String, String>
    ) {
        kafkaTemplate.send(GIRI_TOPIC, EventMessage(
            subject = "spring-kafka-producer",
            source = "/api/produce",
            type = "api-test",
            data = GiriItem(id = body["id"]!!.toLong(), name = body["name"]!!),
        ))
    }
}

fun main(args: Array<String>) {
    runApplication<SpringKafkaProducerApplication>(*args)
}