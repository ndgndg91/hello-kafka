package com.ndgndg91.kafka

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.opensearch.action.index.IndexRequest
import org.opensearch.client.RequestOptions
import org.opensearch.client.RestClient
import org.opensearch.client.RestHighLevelClient
import org.opensearch.client.indices.CreateIndexRequest
import org.opensearch.client.indices.GetIndexRequest
import org.opensearch.common.xcontent.XContentType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.kafka.common.errors.WakeupException
import org.opensearch.action.bulk.BulkRequest
import org.slf4j.LoggerFactory
import java.net.URI
import java.time.Duration
import java.util.*

val logger = LoggerFactory.getLogger("openSearchConsumer")

fun main() {
    // first create on OpenSearch Client
    val connString = "http://localhost:9200"
    val connUri = URI.create(connString)
    val restHighLevelClient = if (connUri.userInfo != null) {
        val userInfo = connUri.userInfo.split(":")
        val username = userInfo[0]
        val password = userInfo[1]

        val credentialsProvider = BasicCredentialsProvider()
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            UsernamePasswordCredentials(username, password)
        )

        RestHighLevelClient(
            RestClient.builder(HttpHost(connUri.host, connUri.port, connUri.scheme))
                .setHttpClientConfigCallback { httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                }
        )
    } else {
        RestHighLevelClient(RestClient.builder(HttpHost(connUri.host, connUri.port, connUri.scheme)))
    }

    // create our kafka client
    val consumer = createKafkaConsumer()


    // we need to create the index on OpenSearch if it doesn't exist already
    restHighLevelClient.use { openSearchClient ->
        val indexExists = openSearchClient.indices().exists(GetIndexRequest("wikimedia"), RequestOptions.DEFAULT)
        if (!indexExists) {
            val createIndexRequest = CreateIndexRequest("wikimedia")
            openSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT)
        }

        try {
            // subscribe topic
            consumer.subscribe(listOf("wikimedia.recentchange"))

            while (true) {
                val records = consumer.poll(Duration.ofMillis(3000))
                val recordCount = records.count()
                val bulkRequest = BulkRequest()

                println("Received $recordCount records")
                records.forEach {
                    val jsonData = removeLogParams(it.value())
                    // 멱등 컨슈머 구현
                    // 1. id 정의 kafka record coordinates 를 통해서 topic - partition - offset
                    // 2. data 에 id 가 있는 경우 이를 그대로 사용한다.
                    val indexRequest = IndexRequest("wikimedia")
                        .source(jsonData.second, XContentType.JSON)
                        .id(jsonData.first)
                    bulkRequest.add(indexRequest)
                }

                if (bulkRequest.numberOfActions() > 0) {
                    val response = openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT)
                    println("${response.items.size} record processed")

                    // commit offset after batch is consumed.
                    consumer.commitSync()
                    println("offset committed")
                }
            }
        } catch (e: WakeupException) {
            logger.info("Consumer is starting to shut down")
        } catch (e: Exception) {
            logger.error("Unexpected exception in the consumer", e)
        } finally {
            consumer.close() // close the consumer, this will also commit offsets
            openSearchClient.close()
            logger.info("The consumer is now gracefully shut down")
        }
    }

    // main code logic

    // close things
}

fun createKafkaConsumer(): KafkaConsumer<String, String> {
    // create Producer Properties
    val properties = Properties()
    val groupId = "consumer-opensearch-demo"

    // connect to Localhost
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")

    // create consumer configs
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")

    // create a consumer
    return KafkaConsumer<String, String>(properties)
}

fun removeLogParams(json: String): Pair<String, String> {
    return try {
        val rootNode = ObjectMapper().readTree(json)
        rootNode as ObjectNode
        rootNode.remove("log_params")
        Pair(rootNode.get("meta").get("id").asText(), rootNode.toString())
    } catch (e: Exception) {
        throw IllegalArgumentException(e)
    }
}
