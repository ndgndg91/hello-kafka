package com.ndgndg91.springkafkacommon

import io.cloudevents.CloudEventData

interface EventData: CloudEventData {
    override fun toBytes(): ByteArray {
        return this.toJsonByteArray()
    }
}