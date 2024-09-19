package com.example.courseservice.Mappers;

import com.example.courseservice.Dto.Attachment.AttachmentDto;
import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import com.example.courseservice.Service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeworkMapper {

    private  final CourseService courseService;

    public Homework homeworkRequestToHomework(HomeworkRequest homeworkRequest)
    {
        Course course = null;
        if(homeworkRequest.getCourseId() != null)
        {
            course = courseService.findCourseById(homeworkRequest.getCourseId());
        }
        Homework homework = Homework.builder()
                .title(homeworkRequest.getTitle())
                .description(homeworkRequest.getDescription())
                .startDate(homeworkRequest.getStartDate())
                .endDate(homeworkRequest.getEndDate())
                .userEntitiesId(homeworkRequest.getUserEntitiesId())
                .course(course)
                .authorId(homeworkRequest.getAuthorId())
                .build();

        return  homework;
    }
    public  HomeworkResponse convertToHomeworkResponse(Homework homework) {
        HomeworkResponse response = new HomeworkResponse();
        response.setId(homework.getId());
        response.setTitle(homework.getTitle());
        response.setDescription(homework.getDescription());
        response.setStartDate(homework.getStartDate());
        response.setEndDate(homework.getEndDate());
        response.setAuthorId(homework.getAuthorId());
        response.setStatus(homework.getStatus());

        response.setUserEntitiesId(new ArrayList<>(homework.getUserEntitiesId()));
        response.setSubmitHomeworkUserEntitiesId(new ArrayList<>(homework.getSubmitHomeworkUserEntitiesId()));
        response.setRejectedHomeworkUserEntitiesId(new ArrayList<>(homework.getRejectedHomeworkUserEntitiesId()));
        response.setGradedHomeworkUserEntitiesId(new ArrayList<>(homework.getGradedHomeworkUserEntitiesId()));
        response.setAcceptedHomeworkEntitiesId(new ArrayList<>(homework.getAcceptedHomeworkEntitiesId()));

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

    public  static StudentHomeworkAttachmentDto studentHomeworkAttachmentToDto(StudentHomeworkAttachment studentHomeworkAttachment)
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
