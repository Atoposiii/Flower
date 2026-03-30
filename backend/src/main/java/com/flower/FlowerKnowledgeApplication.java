package com.flower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class FlowerKnowledgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowerKnowledgeApplication.class, args);
    }
}