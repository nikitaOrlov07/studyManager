package com.example.userservice.kafka_config;

import com.example.userservice.Dto.UserEntityDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class UserEntityDtoSerializer extends JsonSerializer<UserEntityDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(UserEntityDto value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Serialize the UserEntityDto object to JSON
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeStringField("username", value.getUsername());
        gen.writeStringField("email", value.getEmail());
        gen.writeStringField("password", value.getPassword());
        gen.writeStringField("age", value.getAge());
        gen.writeStringField("town", value.getTown());
        gen.writeNumberField("phoneNumber", value.getPhoneNumber());
        gen.writeStringField("role", value.getRole());

        gen.writeArrayFieldStart("createdCoursesIds");
        for (Long courseId : value.getCreatedCoursesIds()) {
            gen.writeNumber(courseId);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("participatingCourses");
        for (Long courseId : value.getParticipatingCourses()) {
            gen.writeNumber(courseId);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("chatsIds");
        for (Long chatId : value.getChatsIds()) {
            gen.writeNumber(chatId);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("completedHomeworksIds");
        for (Long homeworkId : value.getCompletedHomeworksIds()) {
            gen.writeNumber(homeworkId);
        }
        gen.writeEndArray();

        gen.writeArrayFieldStart("createdHomeworksIds");
        for (Long homeworkId : value.getCreatedHomeworksIds()) {
            gen.writeNumber(homeworkId);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
