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
            // Deserialize Map<String, Map<String, Object>>
            Map<String, Map<String, Object>> outerMap = objectMapper.readValue(data, new TypeReference<Map<String, Map<String, Object>>>() {});

            HashMap<String, UserEntityDto> result = new HashMap<>();

            for (Map.Entry<String, Map<String, Object>> entry : outerMap.entrySet()) {
                String key = entry.getKey();
                Map<String, Object> userMap = entry.getValue();

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

                UserEntityDto userDto = new UserEntityDto(
                        toLong.apply(userMap.get("id")),
                        (String) userMap.get("username"),
                        (String) userMap.get("email"),
                        (String) userMap.get("password"),
                        (String) userMap.get("age"),
                        (String) userMap.get("town"),
                        (String) userMap.get("phoneNumber"),
                        (String) userMap.get("role"),
                        toListOfLong.apply(userMap.get("createdCoursesIds")),
                        toListOfLong.apply(userMap.get("participatingCourses")),
                        toListOfLong.apply(userMap.get("chatsIds")),
                        toListOfLong.apply(userMap.get("completedHomeworksIds")),
                        toListOfLong.apply(userMap.get("createdHomeworksIds"))
                );

                result.put(key, userDto);
            }

            return result;

        } catch (Exception e) {
            throw new SerializationException("Error deserializing JSON message", e);
        }
    }
}