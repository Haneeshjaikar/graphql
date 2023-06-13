package com.demo.example.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.neobank.*"})
public class DomainBffApplication {
    public static void main(String[] args) {
        SpringApplication.run(DomainBffApplication.class, args);
    }
}