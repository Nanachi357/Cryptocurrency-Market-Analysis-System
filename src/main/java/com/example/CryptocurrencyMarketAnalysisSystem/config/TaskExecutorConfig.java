package com.example.CryptocurrencyMarketAnalysisSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class TaskExecutorConfig {

    @Bean(name = "applicationTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Sets the core number of threads
        executor.setCorePoolSize(2);

        // Sets the maximum allowed number of threads
        executor.setMaxPoolSize(2);

        // Sets the capacity of the task queue
        executor.setQueueCapacity(500);

        // Sets the prefix for the names of threads
        executor.setThreadNamePrefix("Binance-");

        // Initializes the ThreadPoolTaskExecutor instance
        executor.initialize();
        return executor;
    }
}