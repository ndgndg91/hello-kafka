package com.ndgndg91.springkafkaconsumer.config

import io.cloudevents.CloudEvent
import io.cloudevents.kafka.CloudEventDeserializer
import io.cloudevents.kafka.CloudEventSerializer
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.util.backoff.FixedBackOff
import java.time.Duration

@Configuration
class KafkaConsumerConfig(
    private val kafkaConsumerProperties: KafkaConsumerProperties,
) {
    companion object {
        const val DEFAULT_KAFKA_LISTENER_CONTAINER_FACTORY = "kafkaListenerContainerFactory"
    }


    @Bean
    @Qualifier("dltPublisherTemplate")
    fun dltPublisherTemplate(): KafkaTemplate<String, CloudEvent> {
        return KafkaTemplate(
            DefaultKafkaProducerFactory(
                mapOf(
                    ProducerConfig.ACKS_CONFIG to kafkaConsumerProperties.dlt?.acks!!,
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaConsumerProperties.bootstrapServer,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to CloudEventSerializer::class.java,
                    CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to kafkaConsumerProperties.dlt?.securityProtocol!!
                )
            )
        )
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, CloudEvent> {
        return DefaultKafkaConsumerFactory(mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaConsumerProperties.bootstrapServer!!,
            ConsumerConfig.GROUP_ID_CONFIG to kafkaConsumerProperties.groupId!!,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to CloudEventDeserializer::class.java,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to kafkaConsumerProperties.autoOffsetReset,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to kafkaConsumerProperties.enableAutoCommit,
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to CooperativeStickyAssignor::class.java.name,
            CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to kafkaConsumerProperties.securityProtocol,
        ))
    }

    @Bean(DEFAULT_KAFKA_LISTENER_CONTAINER_FACTORY)
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, CloudEvent> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, CloudEvent>()
        factory.setConcurrency(10)
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        factory.containerProperties.setConsumerRebalanceListener(KafkaConsumerRebalanceHandler())
        factory.containerProperties.isSyncCommits = kafkaConsumerProperties.syncCommits
        factory.containerProperties.syncCommitTimeout = Duration.ofSeconds(kafkaConsumerProperties.syncCommitTimeoutSeconds)
        factory.consumerFactory = consumerFactory()
        val recoverer = DeadLetterPublishingRecoverer(dltPublisherTemplate())
        val fixedBackOff = FixedBackOff(kafkaConsumerProperties.backOff?.interval!!, kafkaConsumerProperties.backOff?.maxFailure!!)
        factory.setCommonErrorHandler(DeserializationErrorHandler(recoverer, fixedBackOff))
        return factory
    }
}