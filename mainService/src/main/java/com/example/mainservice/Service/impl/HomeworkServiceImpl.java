package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.Homeworks.HomeworkRequest;
import com.example.mainservice.Dto.StudentAttachments.StudentAttachmentRequest;
import com.example.mainservice.Dto.StudentAttachments.StudentHomeworkAttachmentDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.HomeworkService;
import com.example.mainservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
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
@Slf4j
public class HomeworkServiceImpl implements HomeworkService {

    @Autowired
    private WebClient.Builder webClientBuilder; // for http request
    @Autowired
    private UserService userService;

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
    public List<HomeworkDto> findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(Long authorId, String homeworkStatus, String courseTitle, String homeworkTitle) {

        List<HomeworkDto> result = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/getCreatedHomework")
                        .queryParam("authorId", authorId)
                        .queryParam("type", homeworkStatus)
                        .queryParam("courseTitle", courseTitle)
                        .queryParam("homeworkTitle", homeworkTitle)
                        .build())
                .retrieve()
                .bodyToFlux(HomeworkDto.class)
                .collect(Collectors.toList())
                .block();
        return result;
    }

    @Override
    public List<StudentHomeworkAttachmentDto> findStudentsAttachmentsByHomeworkId(Long homeworkId) {
        List<StudentHomeworkAttachmentDto> studentHomeworkAttachmentDtos =webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/studentAttachments")
                        .queryParam("homeworkId",homeworkId)
                        .build())
                .retrieve()
                .bodyToFlux(StudentHomeworkAttachmentDto.class)
                .collect(Collectors.toList())
                .block();

        return studentHomeworkAttachmentDtos;
    }

    @Override
    public Boolean uploadStudentAttachment(StudentAttachmentRequest studentAttachmentRequest,List<MultipartFile> files) throws IOException {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        // Add JSON data (besides files)
        bodyBuilder.part("homeworkId", studentAttachmentRequest.getHomeworkId().toString());
        bodyBuilder.part("studentId", studentAttachmentRequest.getStudentId().toString());
        // Add files to JSON
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
        String response = webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/upload")
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build())) // Add json
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if ("Homework was upload successfully".equals(response)) {
            log.info(response);
            return true;
        } else {
            log.error(response);
            return false;
        }
    }

    @Override
    public String checkStudentAttachment(Long homeworkId, Long studentAttachmentId , Integer mark , String message, String status) {
        // Security (studentAttachment can only check homework author)
        UserEntityDto userEntityDto = userService.findUserByUsername(SecurityUtil.getSessionUser());
        StudentHomeworkAttachmentDto studentHomeworkAttachmentDto  = findStudentAttachmentById(studentAttachmentId);

        if(userEntityDto == null || !userEntityDto.getCreatedHomeworksIds().contains(studentHomeworkAttachmentDto.getHomework().getId()))
        {
            return "notAllowed"; // Only the person who created the homework can check it.
        }

        String result = webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/checkHomework/{studentAttachmentId}")
                        .queryParam("mark", mark)
                        .queryParam("message", message)
                        .queryParam("status", status)
                        .build(studentAttachmentId)) // add as pathVariable
                .retrieve()
                .bodyToMono(String.class)
                .block();


        if("Student attachment successfully checked".equals(result))
            log.info(result);
        else
            log.error(result);

        return result;
    }

    private StudentHomeworkAttachmentDto findStudentAttachmentById(Long studentAttachmentId) {

        log.info("findStudentAttachmentById with id {} is working" ,studentAttachmentId);
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/getStudentAttachmentById")
                        .queryParam("studentAttachmentId",studentAttachmentId)
                        .build())
                .retrieve()
                .bodyToMono(StudentHomeworkAttachmentDto.class)
                .block();
    }

    @Override
    public StudentHomeworkAttachmentDto findStudentAttachmentsByHomeworkAndStudentId(Long homeworkId, Long userId) {
        StudentHomeworkAttachmentDto studentHomeworkAttachmentDto = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/getStudentAttachment")
                        .queryParam("homeworkId", homeworkId)
                        .queryParam("studentId",userId)
                        .build())
                .retrieve()
                .bodyToMono(StudentHomeworkAttachmentDto.class)
                .block();

        return  studentHomeworkAttachmentDto;
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
    @Override
    public Boolean deleteHomework(Long homeworkId) {
        log.info("Trying to delete homework with id: "+ homeworkId);
        return webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks/delete/"+homeworkId)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

}
