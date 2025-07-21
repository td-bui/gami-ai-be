package com.project.gamiai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class JasyptTestConfig {

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @PostConstruct
    public void printDecryptedPassword() {
        System.out.println("Decrypted DB password: " + dbPassword);
    }
}