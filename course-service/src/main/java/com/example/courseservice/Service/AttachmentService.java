package com.example.courseservice.Service;

import com.example.courseservice.Dto.UserEntity;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Course;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file, Course course, UserEntity user) throws Exception;

    @Transactional
    void updateAttachmentUrls(Long id, String downloadUrl, String viewUrl);

    Attachment getAttachment(Long fileId) throws Exception;
}
