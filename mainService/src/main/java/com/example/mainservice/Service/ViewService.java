package com.example.mainservice.Service;

import com.example.mainservice.Model.Course;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ViewService {
    List<Course> getCourses();

    Course findCourse(Long courseId) throws Exception;

    ResponseEntity<Resource> getDownloadLink(Long fileId);

    // view file
    ResponseEntity<Resource> getFileView(Long fileId);


}
