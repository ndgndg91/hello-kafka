package com.ndgndg91.springkafkaproducer.config

import io.cloudevents.CloudEvent
import io.cloudevents.kafka.CloudEventSerializer
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.transaction.KafkaTransactionManager
import java.util.UUID

@Configuration
class KafkaProducerConfig(
    private val kafkaProducerProperties: KafkaProducerProperties
) {
    companion object {
        const val KAFKA_TRANSACTION_MANAGER = "kafkaTransactionManager"
    }

    @Bean
    @Qualifier("queueTemplate")
    fun queueTemplate(): KafkaTemplate<String, CloudEvent> {
        return KafkaTemplate(queueProducerFactory())
    }

    @Bean
    fun kafkaTransactionManager(): KafkaTransactionManager<String, CloudEvent> {
        return KafkaTransactionManager(queueProducerFactory())
    }

    @Bean
    fun queueProducerFactory(): DefaultKafkaProducerFactory<String, CloudEvent> {
        return DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.ACKS_CONFIG to kafkaProducerProperties.acks,
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProducerProperties.bootstrapServer,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to CloudEventSerializer::class.java,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.TRANSACTIONAL_ID_CONFIG to "kafka-tx-${UUID.randomUUID()}",
                CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to kafkaProducerProperties.securityProtocol,
            )
        )
    }
}