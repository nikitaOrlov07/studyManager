package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.Homeworks.HomeworkRequest;
import com.example.mainservice.Service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private WebClient.Builder webClientBuilder; // for http request

    @Override
    public Boolean createHomework(HomeworkRequest homeworkRequest) throws IOException {
        String result = webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/create/save")
                        .build()
                )
                .contentType(MediaType.MULTIPART_FORM_DATA) // Set the content type to handle multipart data
                .body(BodyInserters.fromMultipartData(prepareMultipartData(homeworkRequest))) // Properly prepare the multipart data
                .retrieve()
                .bodyToMono(String.class)
                .block();

        Boolean opResult = "Homework was created successfully".equals(result); // if result variable will be null -> won`t throw exception

        return opResult;
    }

    @Override
    public List<HomeworkDto> findHomeworksByAuthorAndStatusAndCourseId(Long authorId, String homeworkStatus, Long courseId) {
        List<HomeworkDto> result = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/getCreatedHomework")
                        .queryParam("authorId", authorId)
                        .queryParam("type", homeworkStatus)
                        .queryParam("courseId", courseId)
                        .build())
                .retrieve()
                .bodyToFlux(HomeworkDto.class)
                .collect(Collectors.toList())
                .block();
        return result;
    }


    // Helper method to prepare multipart data
    private MultiValueMap<String, Object> prepareMultipartData(HomeworkRequest homeworkRequest) throws IOException {
        MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();

        // Add text fields
        multipartData.add("title", homeworkRequest.getTitle());
        multipartData.add("description", homeworkRequest.getDescription());
        multipartData.add("startDate", homeworkRequest.getStartDate());
        multipartData.add("endDate", homeworkRequest.getEndDate());
        multipartData.add("courseId", homeworkRequest.getCourseId());
        multipartData.add("authorId", homeworkRequest.getAuthorId());

        // Add the list of user entity IDs
        for (Long userId : homeworkRequest.getUserEntitiesId()) {
            multipartData.add("userEntitiesId", userId);
        }

        // Add file fields
        for (MultipartFile file : homeworkRequest.getFiles()) {
            if (!file.isEmpty()) {
                multipartData.add("files", new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                });
            }
        }

        return multipartData;
    }

}
