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
    public static HomeworkResponse homeworkToHomeworkResponse(Homework homework) {
        log.info("Homework response mapper is working");
        List<AttachmentDto> attachmentDtos = homework.getAttachmentList().stream()
                .map(attachment -> new AttachmentDto(
                        attachment.getDownloadUrl(),
                        attachment.getViewUrl()
                ))
                .collect(Collectors.toList());
        List<StudentHomeworkAttachmentDto> studentHomeworkAttachmentDtos = homework.getStudentAttachments().stream()
                .map(HomeworkMapper::studentHomeworkAttachmentToDto)
                .collect(Collectors.toList());

        HomeworkResponse homeworkResponse = HomeworkResponse.builder()
                .id(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .startDate(homework.getStartDate())
                .endDate(homework.getEndDate())
                .userEntitiesId(new ArrayList<>(homework.getUserEntitiesId()))
                .attachmentList(attachmentDtos)
                .studentAttachments(studentHomeworkAttachmentDtos)
                .authorId(homework.getAuthorId())
                .build();
        return homeworkResponse;
    }
    public static StudentHomeworkAttachmentDto studentHomeworkAttachmentToDto(StudentHomeworkAttachment studentHomeworkAttachment)
    {
        StudentHomeworkAttachmentDto studentHomeworkAttachmentDto = StudentHomeworkAttachmentDto.builder()
                .id(studentHomeworkAttachment.getId())
                .homework(studentHomeworkAttachment.getHomework())
                .studentId(studentHomeworkAttachment.getStudentId())
                .attachments(studentHomeworkAttachment.getAttachments())
                .uploadedDate(studentHomeworkAttachment.getUploadedDate())
                .status(studentHomeworkAttachment.getStatus())
                .mark(studentHomeworkAttachment.getMark())
                .message(studentHomeworkAttachment.getMessage())
                .checkedDate(studentHomeworkAttachment.getCheckedDate())
                .build();
        return studentHomeworkAttachmentDto;
    }
}
