package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.UserEntityDto;
import com.example.mainservice.Dto.course.CourseCreationRequest;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.AuthService;
import com.example.mainservice.Service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public String createCourse(CourseCreationRequest request, List<MultipartFile> files) throws IOException {


        request.setAuthor(SecurityUtil.getSessionUser());
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        // Добавляем CourseCreationRequest как JSON часть
        bodyBuilder.part("courseData", request, MediaType.APPLICATION_JSON);

        // Add files
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    bodyBuilder.part("files", new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    });
                }
            }
        }

        // HTTP request
        ResponseEntity<String> response = webClientBuilder.build()
                .post()
                .uri("http://course-service/courses")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && response.getBody() != null) {
            log.info("Response from createCourse endpoint: {}", response.getBody());
            return response.getBody();
        } else {
            log.error("Failed to create course: Empty or null response");
            throw new RuntimeException("Failed to create course: Empty or null response");
        }
    }

}
