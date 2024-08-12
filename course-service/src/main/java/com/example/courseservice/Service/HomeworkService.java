package com.example.courseservice.Service;

import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkStatus;
import com.example.courseservice.Model.Homework;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HomeworkService {
    Homework createHomework(HomeworkRequest request);

    String uploadFile(MultipartFile file, Long id) throws Exception;

    String uploadHomework(Long homeworkId, Long studentId, List<MultipartFile> files) throws Exception;

    String checkHomework(Long studentHomeworkAttachmentId, HomeworkStatus homeworkStatus, String message, Integer mark) throws Exception;

    List<Homework> getAllHomeworks(Long studentId);
}
