package com.example.courseservice.Config.webClientConf;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    //  is used for automatic load balancing when working with distributed systems and services. It is embedded in the client side to provide load balancing between multiple instances of a service.
    // Automatically creates client side LoadBalancing
    public WebClient.Builder webClientBuilder()
    {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(500 * 1024 * 1024)) // 500MB
                .build(); // Configures the maximum size of data that can be loaded into memory for processing.

        return  WebClient.builder()
                .exchangeStrategies(exchangeStrategies); // create a bean of WebClient with "webClientBuilder" name
    }
}
