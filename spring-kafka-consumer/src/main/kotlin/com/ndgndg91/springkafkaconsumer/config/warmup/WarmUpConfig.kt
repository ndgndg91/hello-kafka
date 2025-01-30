package com.ndgndg91.springkafkaconsumer.config.warmup

import com.ndgndg91.springkafkacommon.WarmUpCommandLineRunner
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.CompletableFuture

@Configuration
class WarmUpConfig {
    private val logger = LoggerFactory.getLogger(WarmUpConfig::class.java)

    @Bean
    fun warmup(): CommandLineRunner {
        return object : WarmUpCommandLineRunner(logger) {
            override fun createTasks(): List<CompletableFuture<Void>> {
                return listOf(
                    CompletableFuture.runAsync { performTask {
                            logger.info("async logic")
                        }
                    }
                )
            }

        }
    }

}