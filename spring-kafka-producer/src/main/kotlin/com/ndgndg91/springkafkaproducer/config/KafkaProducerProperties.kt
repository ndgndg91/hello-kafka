package com.ndgndg91.springkafkaproducer.config

import com.ndgndg91.springkafkacommon.toJson
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "giri.kafka.producer")
class KafkaProducerProperties {
    var bootstrapServer: String? = null
    var acks: String? = null
    var securityProtocol: String? = null
    override fun toString(): String {
        return this.toJson()
    }
}