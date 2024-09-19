package com.example.courseservice.Service.impl;

import com.example.courseservice.Dto.UserEntity.UserEntityDto;
import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Repository.AttachmentRepository;
import com.example.courseservice.Service.AttachmentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    @Override
    public Attachment saveAttachment(MultipartFile file, Course course, Homework homework , UserEntityDto user, String fileStatus) throws Exception {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if(fileName.contains("..")) {
                throw new Exception("Filename contains invalid path sequence" + fileName);
            }
            log.info("Saving attachment method is working");
            Attachment attachment = new Attachment(fileName,
                    file.getContentType(),
                    file.getBytes(),
                    null,
                    null
            );
            attachment.setCreator(user.getUsername());

            if(course != null)
                attachment.setCourse(course);
            if(homework != null)
                attachment.setHomework(homework);

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            attachment.setTimestamp(currentDateTime.format(formatter));
            attachment.setAccessType(fileStatus);

            return attachmentRepository.save(attachment);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Could not save File: "+fileName);
        }
    }
    @Transactional
    @Override
    public void updateAttachmentUrls(Long id, String downloadUrl, String viewUrl) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
        attachment.setDownloadUrl(downloadUrl);
        attachment.setViewUrl(viewUrl);
        attachmentRepository.save(attachment);

    }

    @Transactional
    @Override
    public Attachment getAttachment(Long fileId) throws Exception {
        return attachmentRepository.findById(fileId).orElseThrow(()-> new Exception("File not found with id "+ fileId));
    }
    @Override
    public boolean canMakeOperationsWithAttachment(UserEntityResponse userEntityDto, Attachment attachment) {
        // Check if the user is authorized (assuming you have a method to check authorization)
        if(userEntityDto == null)
        { return false;}
        // Check if the user is the creator of the attachment
        if (userEntityDto.getUsername().equals(attachment.getCreator())) {
            return true;
        }

        // Check if the user is involved in the course
        Course course = attachment.getCourse();
        if (course != null && course.getInvolvedUserIds().contains(userEntityDto.getId())) {
            return true;
        }

        // Check if the user is in homework.getUserEntities
        Homework homework = attachment.getHomework();
        if (homework != null && homework.getUserEntitiesId().contains(userEntityDto.getId())) {
            return true;
        }

        // Check if the user is the author of the homework
        if (homework != null && homework.getAuthorId().equals(userEntityDto.getId())) {
            return true;
        }

        // If none of the conditions are met, return false
        return false;
    }


}
