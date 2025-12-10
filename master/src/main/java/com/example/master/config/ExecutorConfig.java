package com.example.master.config;

import com.example.master.common.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for ExecutorService used for minion task dispatching.
 */
@Configuration
public class ExecutorConfig {

    @Bean
    public ExecutorService minionDispatchExecutor() {
        return new ThreadPoolExecutor(
                AppConstants.THREAD_POOL_SIZE,
                AppConstants.THREAD_POOL_SIZE,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(AppConstants.THREAD_POOL_QUEUE_CAPACITY),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}