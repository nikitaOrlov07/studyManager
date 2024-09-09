package com.example.courseservice.Mappers;

import com.example.courseservice.Dto.Attachment.AttachmentDto;
import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
public class HomeworkMapper {
    public static Homework homeworkRequestToHomework(HomeworkRequest homeworkRequest)
    {
        Homework homework = Homework.builder()
                .title(homeworkRequest.getTitle())
                .description(homeworkRequest.getDescription())
                .startDate(homeworkRequest.getStartDate())
                .endDate(homeworkRequest.getEndDate())
    //          .userEntitiesId(homeworkRequest.getUserEntitiesId())
                .build();
        return  homework;
    }
    public static HomeworkResponse convertToHomeworkResponse(Homework homework) {
        HomeworkResponse response = new HomeworkResponse();
        response.setId(homework.getId());
        response.setTitle(homework.getTitle());
        response.setDescription(homework.getDescription());
        response.setStartDate(homework.getStartDate());
        response.setEndDate(homework.getEndDate());
        response.setUserEntitiesId(new ArrayList<>(homework.getUserEntitiesId()));
        response.setAuthorId(homework.getAuthorId());

        // Map each Attachment to AttachmentDto objects
        response.setAttachmentList(homework.getAttachmentList().stream()
                .map(attachment -> new AttachmentDto(
                        attachment.getId(),
                        attachment.getFileName(),
                        attachment.getFileType(),
                        attachment.getDownloadUrl(),
                        attachment.getViewUrl(),
                        attachment.getAccessType()
                ))
                .collect(Collectors.toList()));

        // Map each  StudentAttachments object to StudentAttachmentsDtoObject
        response.setStudentAttachments(homework.getStudentAttachments().stream()
                 .map(HomeworkMapper::studentHomeworkAttachmentToDto)
                 .collect(Collectors.toList()));

        return response;
    }
    public static StudentHomeworkAttachmentDto studentHomeworkAttachmentToDto(StudentHomeworkAttachment studentHomeworkAttachment)
    {
        StudentHomeworkAttachmentDto studentHomeworkAttachmentDto = StudentHomeworkAttachmentDto.builder()
                .id(studentHomeworkAttachment.getId())
                .homework(studentHomeworkAttachment.getHomework())
                .studentId(studentHomeworkAttachment.getStudentId())
                .uploadedDate(studentHomeworkAttachment.getUploadedDate())
                .status(studentHomeworkAttachment.getStatus())
                .mark(studentHomeworkAttachment.getMark())
                .message(studentHomeworkAttachment.getMessage())
                .checkedDate(studentHomeworkAttachment.getCheckedDate())
                .build();

        studentHomeworkAttachmentDto.setAttachments(studentHomeworkAttachment.getAttachments().stream()
                .map(attachment -> new AttachmentDto(
                        attachment.getId(),
                        attachment.getFileName(),
                        attachment.getFileType(),
                        attachment.getDownloadUrl(),
                        attachment.getViewUrl(),
                        attachment.getAccessType()
                ))
                .collect(Collectors.toList()));

        return studentHomeworkAttachmentDto;
    }
}
