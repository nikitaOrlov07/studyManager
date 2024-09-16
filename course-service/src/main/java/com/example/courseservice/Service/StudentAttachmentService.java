package com.example.courseservice.Service;

import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentAttachmentService {
    @Transactional
    StudentHomeworkAttachmentDto findStudentAttachmentsByHomeworkIdAndStudentId(Long homeworkId, Long studentId);

    List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByIds(List<Long> studentAttachmentsIds);

    @Transactional
    List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByHomeworkId(Long homeworkId);

    StudentHomeworkAttachmentDto findStudentAttachmentById(Long studentAttachmentId);
}
