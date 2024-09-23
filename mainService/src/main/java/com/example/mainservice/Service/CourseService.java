package com.example.mainservice.Service;

import com.example.mainservice.Dto.course.CourseCreationRequest;
import com.example.mainservice.Dto.course.Course;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    String createCourse(CourseCreationRequest request, List<MultipartFile> files) throws IOException;
    Boolean action(Long courseId, String username, String action);

    List<Course> searchCreatedCoursesByTitleAndAuthor(String courseTitle, Long id);

    List<Course> searchParticipatedCoursesByTitleAndUserId(String courseTitle, Long id);
}
