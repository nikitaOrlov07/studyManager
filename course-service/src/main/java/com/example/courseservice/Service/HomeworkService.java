package com.example.courseservice.Service;

import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.Homework.Enums.StudentAttachmentStatus;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentAttachmentRequest;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Model.Homework;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HomeworkService {
    Homework createHomework(HomeworkRequest request);

    String uploadFile(MultipartFile file, Long id) throws Exception;

    String uploadHomework(StudentAttachmentRequest studentAttachmentRequest) throws Exception;

    String checkHomework(Long studentHomeworkAttachmentId, StudentAttachmentStatus studentAttachmentStatus, String message, Integer mark) throws Exception;

    List<HomeworkResponse> getHomeworks(Long studentId, String type);

    List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByIds(List<Long> studentAttachmentsIds);

    List<HomeworkResponse> getCreatedHomeworksByIds(List<Long> homeworksIds);

    HomeworkResponse getHomeworkById(Long homeworkId);

    StudentHomeworkAttachmentDto findStudentAttachmentsByHomeworkIdAndStudentId(Long homeworkId, Long studentId);

    List<HomeworkResponse>  findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(Long authorId, String homeworkStatus , Long courseId, String courseTitle);

    List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByHomeworkId(Long homeworkId);
}
