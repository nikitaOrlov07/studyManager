package com.example.courseservice.Service;

import com.example.courseservice.Dto.Course.CourseCreationRequest;
import com.example.courseservice.Dto.Course.CourseResponse;
import com.example.courseservice.Model.Course;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();

    Course createCourse(CourseCreationRequest courseCreationRequest);

    CourseResponse getCourseResponse(Long courseId);

    String uploadFile(MultipartFile file, Long id, String author) throws Exception;

    void deleteCourse(Long courseId);


    List<Course> getCourseByIds(List<Long> courseIds);

    List<CourseResponse> findCourses(String type, String searchBar);

    Boolean actionCourse(Long courseId, String username, String action);

    Course findCourseById(Long courseId);

    Course findByTitle(String courseTitle);

    List<CourseResponse> searchCreatedCourses(String courseTitle, Long authorId);

    List<CourseResponse> searchParticipatedCourses(String courseTitle, Long userId);
}
