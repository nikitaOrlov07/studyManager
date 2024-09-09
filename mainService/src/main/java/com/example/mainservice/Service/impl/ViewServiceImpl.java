package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Model.Course;
import com.example.mainservice.Service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewServiceImpl implements ViewService {

    private final WebClient.Builder webClientBuilder; // for http request

    @Override
    public List<Course> getCourses() {
        log.info("ViewService getCourses method is working");

        ParameterizedTypeReference<List<Course>> typeReference = new ParameterizedTypeReference<>() {}; // Allows WebClient to correctly deserialize generalized types. In this case, it allows the WebClient to realize that it should convert the response body to List<Course>.

        // HTTP request to course controller
        List<Course> courses = webClientBuilder.build()
                .get()
                .uri("http://course-service/courses")
                .retrieve()
                .bodyToMono(typeReference)
                .doOnSubscribe(s -> log.info("Sending request to course service"))    // Performs an action when a subscription to a reactive stream begins.
                .doOnError(e -> log.error("Error occurred: ", e))                     // Performs an action when an error occurs during stream processing.
                .doOnSuccess(r -> log.info("Received response from course service"))  // This method is called after successful processing, but before the result is passed to the subscriber.
                .block();

        log.info("Courses count: " + (courses != null ? courses.size() : 0));
        return courses;
    }


    @Override
    public Course findCourse(Long courseId) throws Exception {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://course-service/courses/{courseId}", courseId)
                    .retrieve()
                    .bodyToMono(Course.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error fetching course with ID {}: {}", courseId, e.getMessage());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new Exception("Course with ID " + courseId + " not found.");
            }
            throw new RuntimeException("Error fetching course", e);
        }
    }



        /// For files

        // download file
        @Override
        public ResponseEntity<Resource> getDownloadLink(Long fileId) {
            log.info("getDownloadLing service method is working");
            return webClientBuilder.build()
                    .get()
                    .uri("http://course-service/files/download/{fileId}", fileId)
                    .retrieve()
                    .toEntity(Resource.class)
                    .block();
        }
        // view file
        @Override
        public ResponseEntity<Resource> getFileView(Long fileId, String username) {
            return webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("course-service")
                            .path("/files/view")
                            .queryParam("fileId",fileId)
                            .queryParam("username", username)
                            .build())
                    .retrieve()
                    .toEntity(Resource.class)
                    .block();
        }


    @Override
    public List<Course> searchCourses(String type, String searchBar) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/courses/search")
                        .queryParam("type", type)
                        .queryParam("searchBar", searchBar)
                        .build())
                .retrieve()
                .bodyToFlux(Course.class)
                .collectList()
                .block();
    }

    @Override
    public List<UserEntityDto> getInvolvedUsers(List<Long> involvedUserIds) {
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("user-service")
                        .path("/users/findUsersByIds")
                        .queryParam("usersIds", involvedUserIds)
                        .build())
                .retrieve()
                .bodyToFlux(UserEntityDto.class)
                .collectList()
                .block();
    }

    @Override
    public List<HomeworkDto> findHomeworksByUser(Long userId, String type) {
        return (List<HomeworkDto>) webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("course-service")
                        .path("/homeworks")
                        .queryParam("studentId", userId)
                        .queryParam("type", type) // Add type parameter to the query
                        .build())
                .retrieve()
                .bodyToFlux(HomeworkDto.class) // Use bodyToFlux to handle a list of objects
                .collectList(); // Collect results into a List
    }


}
