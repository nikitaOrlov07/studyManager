package com.example.courseservice.Config.webClientConf;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    //  is used for automatic load balancing when working with distributed systems and services. It is embedded in the client side to provide load balancing between multiple instances of a service.
    // Automatically creates client side LoadBalancing
    public WebClient.Builder webClientBuilder()
    {
        return  WebClient.builder(); // create a bean of WebClient with "webClientBuilder" name
    }
}
