package com.example.userservice.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// setting configuration for database tracing
// creates a bean of the ObservedAspect class This is a class from the Spring Observability framework that integrates with aspects to collect trace and metrics data
@Configuration
public class ObservationConfig {
    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry)
    {
        return  new ObservedAspect(observationRegistry);
    }
}
