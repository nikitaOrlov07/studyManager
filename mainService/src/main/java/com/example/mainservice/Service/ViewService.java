package com.example.mainservice.Service;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.StudentAttachments.StudentHomeworkAttachmentDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Model.Course;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ViewService {
    List<Course> getCourses();

    Course findCourse(Long courseId) throws Exception;


    // download file
    ResponseEntity<Resource> getDownloadLink(Long fileId, String username);

    // view file
    ResponseEntity<Resource> getFileView(Long fileId, String username);


    List<Course> searchCourses(String type, String searchBar);

    List<UserEntityDto> getInvolvedUsers(List<Long> involvedUserIds);

    List<HomeworkDto> findHomeworksByUser(Long userId, String type);

    HomeworkDto findHomeworkByHomeworkId(Long homeworkId);

}
