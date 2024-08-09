package com.example.mainservice.Service;

import com.example.mainservice.Model.Course;

import java.util.List;

public interface ViewService {
    List<Course> getCourses();

    Course findCourse(Long courseId) throws Exception;
}
