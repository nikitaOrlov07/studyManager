package com.example.userservice.kafka_config;

import com.example.userservice.Dto.UserEntityDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

public class UserEntityDtoSerializer implements Serializer<HashMap<String,UserEntityDto>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, HashMap<String,UserEntityDto> kafkaData) { // String value is type of notification (can be register , log in or delete account)
        try {
            return objectMapper.writeValueAsBytes(kafkaData);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing UserEntityDto", e);
        }
    }
}