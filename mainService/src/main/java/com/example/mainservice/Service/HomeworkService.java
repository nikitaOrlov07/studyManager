package com.example.mainservice.Service;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.Homeworks.HomeworkRequest;
import com.example.mainservice.Dto.StudentAttachments.StudentAttachmentRequest;
import com.example.mainservice.Dto.StudentAttachments.StudentHomeworkAttachmentDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface HomeworkService {
    Boolean createHomework(HomeworkRequest homeworkRequest) throws IOException;

    List<HomeworkDto>  findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(Long authorId, String homeworkStatus, String courseTitle, String homeworkTitle);

    List<StudentHomeworkAttachmentDto> findStudentsAttachmentsByHomeworkId(Long homeworkId);

    Boolean uploadStudentAttachment(StudentAttachmentRequest studentAttachmentRequest, List<MultipartFile> files) throws IOException;

    String checkStudentAttachment(Long homeworkId, Long studentAttachmentId , Integer mark , String message , String status);

    StudentHomeworkAttachmentDto findStudentAttachmentsByHomeworkAndStudentId(Long homeworkId, Long userId);

    Boolean deleteHomework(Long homeworkId);


    List<HomeworkDto> findHomeworksByCourseId(Long id);
}
