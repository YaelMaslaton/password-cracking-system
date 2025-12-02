package com.example.master.config;

import com.example.master.client.MinionClient;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ExecutorService minionDispatchExecutor(@Autowired(required = false) MinionClient minionClient) {
        int minionCount = 3; // Default for tests
        if (minionClient != null) {
            try {
                minionCount = minionClient.getMinionCount();
            } catch (Exception e) {
                // Use default if configuration is not available
            }
        }
        return new ThreadPoolExecutor(
            minionCount,
            minionCount,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}