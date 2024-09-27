package com.example.courseservice.Service;

import com.example.courseservice.Dto.UserEntity.UserEntityDto;
import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.Homework;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    public Attachment saveAttachment(MultipartFile file, Course course, Homework homework , UserEntityDto user, String fileStatus) throws Exception;

    @Transactional
    void updateAttachmentUrls(Long id, String downloadUrl, String viewUrl);

    Attachment getAttachment(Long fileId) throws Exception;

    boolean canMakeOperationsWithAttachment(UserEntityResponse userEntityDto, Attachment attachment);

    void deleteFile(Attachment attachment);
}
