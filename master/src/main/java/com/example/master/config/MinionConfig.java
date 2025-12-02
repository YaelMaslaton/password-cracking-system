package com.example.master.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for minion properties.
 */
@Configuration
@EnableConfigurationProperties(MinionProperties.class)
public class MinionConfig {
}