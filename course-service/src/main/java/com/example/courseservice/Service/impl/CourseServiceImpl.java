package com.example.courseservice.Service.impl;

import com.example.courseservice.Config.ResourceNotFoundException;
import com.example.courseservice.Dto.Course.CourseRequest;
import com.example.courseservice.Dto.Course.CourseResponse;
import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Mappers.UsersMapper;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Repository.CourseRepository;
import com.example.courseservice.Service.AttachmentService;
import com.example.courseservice.Service.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final AttachmentService attachmentService;
    private final WebClient.Builder webClientBuilder; // for HTTP requests

    @Override
    public List<Course> getAllCourses() {
        log.info("getAllCourses service method is working ");
        return courseRepository.findAll();
    }

    @Override
    public Course createCourse(CourseRequest courseRequest) {
        // Get creation date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        String formattedDate = currentDate.format(formatter);
        // Http request to userService to find a current user
        UserEntityResponse userEntityResponse = webClientBuilder.build()
                .get()
                .uri("http://user-service/users/getCurrent")
                .retrieve().bodyToMono(UserEntityResponse.class)
                .block();
        System.out.println(userEntityResponse);
        // Http request to Chat service to create a new chat for current course
        // Chat chat =

        Course course = Course.builder()
                .title(courseRequest.getTitle())
                .description(courseRequest.getDescription())
       //       .chatId(chat.getId())
                .creationDate(formattedDate)
                .authorId(userEntityResponse.getId())
                .build();
        Course savedCourse = courseRepository.save(course);

        if(savedCourse == null)
            log.error("Course creation failed");
        else
            log.info("Course was created successfully");
        return savedCourse;
    }

    @Override
    public CourseResponse getCourseResponse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course with ID " + courseId + " not found."));

        return CourseResponse.builder()
                .title(course.getTitle())
                .description(course.getDescription())
                .involvedUserIds(course.getInvolvedUserIds())
                .chatId(course.getChatId())
                .attachments(course.getAttachments())
                .build();
    }

    @Transactional
    @Override
    public String  uploadFile(MultipartFile file, Long courseId) throws Exception {
        Course course = courseRepository.findById(courseId).get();
        // Find User sending HTTP request to users service
        UserEntityResponse userEntityResponse = webClientBuilder.build()
                .get()
                .uri("http://user-service/users/getCurrent")
                .retrieve().bodyToMono(UserEntityResponse.class)
                .block(); //  if will be null -> program wont stop ?

        System.out.println("Ids: "+ userEntityResponse.getCreatedCoursesIds());
        if(userEntityResponse == null || course == null) {
           return "error";
        }

      Attachment attachment = attachmentService.saveAttachment(file, course,null, UsersMapper.responseToDto(userEntityResponse)); // upload course attachments

        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(String.valueOf(attachment.getId()))
                .toUriString();
        String viewUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/view/")
                .path(String.valueOf(attachment.getId()))
                .toUriString();

        log.info("Download URL: " + downloadUrl);
        log.info("View URL: " + viewUrl);



        attachmentService.updateAttachmentUrls(attachment.getId(), downloadUrl, viewUrl);


        return "file successfully uploaded";
    }

    // Course delete logic -> working
    @Override
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).get();
        if(course == null)
            throw new ResourceNotFoundException("Course with ID " + courseId + " not found.");
        courseRepository.delete(course);
    }

    @Override
    public List<Course> getCourseByIds(List<Long> courseIds) {
        List<Course> courses = courseIds.stream()
                .map(id -> courseRepository.findById(id).get())
                .collect(Collectors.toList());
        return  courses;
    }


}
