package com.example.notification_service.conf;

import com.example.notification_service.Dto.UserEntityDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomDeserializer implements Deserializer<Object> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            // Десериализуем в Map
            Map<String, Object> map = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});

            // Здесь вы можете создать объект нужного вам класса на основе данных из map
            // Например:
            return new UserEntityDto(
                    (Long) map.get("id"),
                    (String) map.get("username"),
                    (String) map.get("email"),
                    (String) map.get("password"),
                    (String) map.get("age"),
                    (String) map.get("town"),
                    (Long) map.get("phoneNumber"),
                    (String) map.get("role"),
                    ((List<String>) map.get("createdCoursesIds")).stream().map(Long::parseLong).collect(Collectors.toList()),
                    ((List<String>) map.get("participatingCourses")).stream().map(Long::parseLong).collect(Collectors.toList()),
                    ((List<String>) map.get("chatsIds")).stream().map(Long::parseLong).collect(Collectors.toList()),
                    ((List<String>) map.get("completedHomeworksIds")).stream().map(Long::parseLong).collect(Collectors.toList()),
                    ((List<String>) map.get("createdHomeworksIds")).stream().map(Long::parseLong).collect(Collectors.toList())
            );

        } catch (Exception e) {
            throw new SerializationException("Error deserializing JSON message", e);
        }
    }
}