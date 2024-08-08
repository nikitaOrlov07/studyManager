package com.example.courseservice.Service;

import com.example.courseservice.Dto.CourseRequest;
import com.example.courseservice.Dto.CourseResponse;
import com.example.courseservice.Model.Course;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();

    Course createCourse(CourseRequest courseRequest);

    CourseResponse getCourse(Long courseId);

    String uploadFile(MultipartFile file, Long id) throws Exception;

    void deleteCourse(Long courseId);
}
