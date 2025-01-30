package com.ndgndg91.springkafkacommon

import org.slf4j.Logger
import org.springframework.boot.CommandLineRunner
import java.util.concurrent.CompletableFuture

abstract class WarmUpCommandLineRunner(
    private val logger: Logger
): CommandLineRunner {

    override fun run(vararg args: String?) {
        logger.info("Starting WarmUpCommandLineRunner")
        args.forEach { logger.info(it ?: "")}

        val tasks = createTasks()
        CompletableFuture.allOf(*tasks.toTypedArray()).join()

        logger.info("Completed WarmUpCommandLineRunner")
    }

    abstract fun createTasks(): List<CompletableFuture<Void>>

    protected fun performTask(task: () -> Unit): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            try {
                task()
            } catch (e: Exception) {
                logger.error("Error in warmup phase", e)
            }
        }
    }
}