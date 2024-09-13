package com.example.mainservice.Service;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.Homeworks.HomeworkRequest;
import com.example.mainservice.Dto.StudentAttachments.StudentAttachmentRequest;
import com.example.mainservice.Dto.StudentAttachments.StudentHomeworkAttachmentDto;

import java.io.IOException;
import java.util.List;

public interface HomeworkService {
    Boolean createHomework(HomeworkRequest homeworkRequest) throws IOException;

    List<HomeworkDto>  findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(Long authorId, String homeworkStatus, Long courseId, String courseTitle);

    List<StudentHomeworkAttachmentDto> findStudentsAttachmentsByHomeworkId(Long homeworkId);

    Boolean uploadStudentAttachment(StudentAttachmentRequest studentAttachmentRequest);
}
