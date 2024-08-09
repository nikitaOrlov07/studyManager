package com.example.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer // create Eureka Discovery Server
public class DiscoveryServiceApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(DiscoveryServiceApplication.class,args);
    }
}
