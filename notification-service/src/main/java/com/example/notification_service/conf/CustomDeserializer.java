package com.example.notification_service.conf;

import com.example.notification_service.Dto.UserEntityDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomDeserializer implements Deserializer<Object> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            // Десериализуем в Map
            Map<String, Object> map = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});

            // Helper function to safely convert to Long
            Function<Object, Long> toLong = (obj) -> {
                if (obj instanceof Integer) {
                    return ((Integer) obj).longValue();
                } else if (obj instanceof Long) {
                    return (Long) obj;
                } else if (obj instanceof String) {
                    return Long.parseLong((String) obj);
                }
                return null;
            };

            // Helper function to safely convert List to List<Long>
            Function<Object, List<Long>> toListOfLong = (obj) -> {
                if (obj instanceof List) {
                    return ((List<?>) obj).stream()
                            .map(toLong)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }
                return new ArrayList<>();
            };

            return new UserEntityDto(
                    toLong.apply(map.get("id")),
                    (String) map.get("username"),
                    (String) map.get("email"),
                    (String) map.get("password"),
                    (String) map.get("age"),
                    (String) map.get("town"),
                    toLong.apply(map.get("phoneNumber")),
                    (String) map.get("role"),
                    toListOfLong.apply(map.get("createdCoursesIds")),
                    toListOfLong.apply(map.get("participatingCourses")),
                    toListOfLong.apply(map.get("chatsIds")),
                    toListOfLong.apply(map.get("completedHomeworksIds")),
                    toListOfLong.apply(map.get("createdHomeworksIds"))
            );

        } catch (Exception e) {
            throw new SerializationException("Error deserializing JSON message", e);
        }
    }
}