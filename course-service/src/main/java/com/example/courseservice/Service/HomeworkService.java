package com.example.courseservice.Service;

import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentAttachmentRequest;
import com.example.courseservice.Model.Homework;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HomeworkService {
    Homework createHomework(HomeworkRequest request);

    @Transactional
    String uploadFile(MultipartFile file, Long courseId, Long username) throws Exception;

    String uploadHomework(StudentAttachmentRequest studentAttachmentRequest) throws Exception;



    List<HomeworkResponse> getHomeworks(Long studentId, String type);


    List<HomeworkResponse> getCreatedHomeworksByIds(List<Long> homeworksIds);

    HomeworkResponse getHomeworkById(Long homeworkId);


    List<HomeworkResponse>  findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(Long authorId, String homeworkStatus, String courseTitle, String homeworkTitle);


    String checkHomework(Long studentAttachmentId, Integer mark, String message, String status);

    Boolean deleteHomework(Long homeworkId);

    List<HomeworkResponse> getHomeworksByCourseId(Long courseId);
}
