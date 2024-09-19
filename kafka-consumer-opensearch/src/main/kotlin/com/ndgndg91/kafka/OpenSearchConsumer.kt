package com.ndgndg91.kafka

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.opensearch.client.RequestOptions
import org.opensearch.client.RestClient
import org.opensearch.client.RestHighLevelClient
import org.opensearch.client.indices.CreateIndexRequest
import org.opensearch.client.indices.GetIndexRequest
import java.net.URI


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

    // we need to create the index on OpenSearch if it doesn't exist already
    restHighLevelClient.use {
        val indexExists = it.indices().exists(GetIndexRequest("wikimedia"), RequestOptions.DEFAULT)
        if (!indexExists) {
            val createIndexRequest = CreateIndexRequest("wikimedia")
            it.indices().create(createIndexRequest, RequestOptions.DEFAULT)
        }
    }


    // create our kafka client

    // main code logic

    // close things
}