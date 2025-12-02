package com.example.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Master Service.
 * This service coordinates distributed password cracking by splitting work
 * among multiple Minion services and collecting their results.
 */
@SpringBootApplication
@EnableScheduling
public class MasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterApplication.class, args);
    }
}