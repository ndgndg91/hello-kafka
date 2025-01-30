package com.ndgndg91.springkafkacommon

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal const val ISO_8061_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

internal val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(ISO_8061_TIMESTAMP)
internal val localDateTimeSerializer = LocalDateTimeSerializer(formatter)
internal val localDateTimeDeserializer = LocalDateTimeDeserializer(formatter)

internal val javaTimeModule: SimpleModule = JavaTimeModule()
    .addSerializer(LocalDateTime::class.java, localDateTimeSerializer)
    .addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)
internal val om = jacksonObjectMapper()
    .registerModule(javaTimeModule)


fun Any.toJson(): String = om.writeValueAsString(this)

fun Any.toJsonByteArray(): ByteArray = om.writeValueAsBytes(this)