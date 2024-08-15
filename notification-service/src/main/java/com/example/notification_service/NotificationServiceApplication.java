package com.example.notification_service;

import com.example.notification_service.Dto.UserEntityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    // Define kafka Listener
    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(UserEntityDto userEntityDto)
    {
       // send out an email notification
       log.info("User {} was registered successfully" ,userEntityDto.getId());
    }


}
