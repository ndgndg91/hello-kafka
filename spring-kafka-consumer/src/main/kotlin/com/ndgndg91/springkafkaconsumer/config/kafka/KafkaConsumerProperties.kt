package com.ndgndg91.springkafkaconsumer.config.kafka

import com.ndgndg91.springkafkacommon.toJson
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "giri.kafka.consumer")
class KafkaConsumerProperties {
    var bootstrapServer: String? = null
    var syncCommits: Boolean = false
    var syncCommitTimeoutSeconds: Long = 5
    var groupId: String? = null
    var autoOffsetReset: String? = null
    var securityProtocol: String? = null
    var enableAutoCommit: Boolean? = null
    var backOff: BackOff? = null
    var dlt: Dlt? = null

    class BackOff {
        var interval: Long? = null
        var maxFailure: Long? = null
    }

    class Dlt {
        var acks: String? = null
        var securityProtocol: String? = null
    }

    override fun toString(): String {
        return this.toJson()
    }
}