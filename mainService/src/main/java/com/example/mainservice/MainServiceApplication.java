package com.example.mainservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }

}
