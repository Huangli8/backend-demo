package com.saygobackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyChecker implements CommandLineRunner {
    @Value("${ai.api-key}")
    private String apiKey;

    @Override
    public void run(String... args) {
        System.out.println(">>> AI_API_KEY from Spring: " + apiKey);
    }
}
