package com.example.courseservice.Service.impl;

import com.example.courseservice.Dto.Homework.Enums.HomeworkStatus;
import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import com.example.courseservice.Dto.UserEntity.UserEntityDto;
import com.example.courseservice.Mappers.HomeworkMapper;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Repository.HomeworkRepository;
import com.example.courseservice.Repository.StudentHomeworkRepository;
import com.example.courseservice.Service.AttachmentService;
import com.example.courseservice.Service.CourseService;
import com.example.courseservice.Service.HomeworkService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.courseservice.Dto.Homework.Enums.StudentAttachmentStatus;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeworkServiceimpl implements HomeworkService {

    private final WebClient.Builder webClientBuilder;  // for HTTP requests
    private final CourseService courseService;
    private final HomeworkRepository homeworkRepository;
    private final AttachmentService attachmentService;
    private final StudentHomeworkRepository studentHomeworkRepository;


    @Override
    public Homework createHomework(HomeworkRequest request) {
        System.out.println(request);
        Homework homework = HomeworkMapper.homeworkRequestToHomework(request);
        System.out.println(homework);
        // HTTP request to get User author
        UserEntityDto author = UserEntityDto.builder()
                .id(2L)
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .createdHomeworks(Arrays.asList(HomeworkMapper.convertToHomeworkResponse(homework)))
                .chats(new ArrayList<>())
                .build(); // temporary
        // HTTP request to user service to find users in the group
        UserEntityDto user = UserEntityDto.builder()
                .id(1L)
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .chats(new ArrayList<>())
                .build(); // temporary

        homework.setUserEntitiesId(Arrays.asList(user.getId()));
        homework.setAuthorId(author.getId());

        homeworkRepository.save(homework);
        return homework;
    }

    @Transactional
    @Override
    public String  uploadFile(MultipartFile file, Long courseId) throws Exception {
        log.info("Uploading file for homework is working");
        Homework homework = homeworkRepository.findById(courseId).get();
        // Find User sending HTTP request to users service

        UserEntityDto user = UserEntityDto.builder()
                .id(1L)
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .chats(new ArrayList<>())
                .build(); // temporary
        if(user == null) {
            return "error";
        }
        Attachment attachment = attachmentService.saveAttachment(file, homework.getCourse(),homework, user);
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(String.valueOf(attachment.getId()))
                .toUriString();
        String viewUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/view/")
                .path(String.valueOf(attachment.getId()))
                .toUriString();

        log.info("Download URL: " + downloadUrl);
        log.info("View URL: " + viewUrl);

        attachmentService.updateAttachmentUrls(attachment.getId(), downloadUrl, viewUrl);
        return "file successfully uploaded";
    }

    @Transactional
    public String uploadHomework(Long homeworkId, Long studentId, List<MultipartFile> files) throws Exception {
        log.info("Uploading homework for student");

        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new Exception("Homework not found"));

        // HTTP request to userService to find user by userId
        UserEntityDto student = UserEntityDto.builder()
                .id(studentId)
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .completedHomeworks(new ArrayList<>())
                .chats(new ArrayList<>())
                .build(); // temporary


        if (student == null || !homework.getUserEntitiesId().contains(studentId)) {
            throw new Exception("Student is not allowed to submit this homework");
        }

        // check if this job has been submitted before (working good)
        boolean homeworkAlreadySubmitted = student.getCompletedHomeworks().stream()
                .anyMatch(attachment -> attachment.getHomework().getId().equals(homeworkId));

        if (homeworkAlreadySubmitted) {
            throw new Exception("Homework has already been submitted by this student");
        }
       StudentHomeworkAttachment studentHomework = StudentHomeworkAttachment.builder()
                .homework(homework)
                .studentId(studentId)
                .status(StudentAttachmentStatus.Submitted)
                .uploadedDate(getCurrentDate())
                .build();

        List<Attachment> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            Attachment attachment = attachmentService.saveAttachment(file, homework.getCourse(), homework, student);

            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/download/")
                    .path(String.valueOf(attachment.getId()))
                    .toUriString();
            String viewUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/view/")
                    .path(String.valueOf(attachment.getId()))
                    .toUriString();

            log.info("Download URL: " + downloadUrl);
            log.info("View URL: " + viewUrl);

            attachmentService.updateAttachmentUrls(attachment.getId(), downloadUrl, viewUrl);
            attachments.add(attachment);
        }

        studentHomework.setAttachments(attachments);
        student.getCompletedHomeworks().add(HomeworkMapper.studentHomeworkAttachmentToDto(studentHomework));

        studentHomeworkRepository.save(studentHomework);
        // Http request to update user (student)
        return "Homework was upload successfully";
    }

    @Override
    public String checkHomework(Long  studentHomeworkAttachmentId, StudentAttachmentStatus studentAttachmentStatus, String message, Integer mark) throws Exception {
        StudentHomeworkAttachment studentHomeworkAttachment = studentHomeworkRepository.findById(studentHomeworkAttachmentId)
                .orElseThrow(() -> new Exception("Homework not found"));
        Homework homework = studentHomeworkAttachment.getHomework();

        // HTTP request to take current user and compare  him to authorId
        UserEntityDto author = UserEntityDto.builder()
                .id(2L)
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .createdHomeworks(Arrays.asList(HomeworkMapper.convertToHomeworkResponse(homework)))
                .chats(new ArrayList<>())
                .build(); // temporary
        /*if(!author.getId().equals(homework.getAuthorId()))
        {
            throw new Exception("You can`t check homework");
        }
         */
        // HTTP request to take user who complete his homework
        UserEntityDto student = UserEntityDto.builder()
                .id(studentHomeworkAttachment.getStudentId())
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .completedHomeworks(new ArrayList<>())
                .chats(new ArrayList<>())
                .build(); // temporary

        // Change studentAttachment
        if(message != null)
            studentHomeworkAttachment.setMessage(message);
        if(mark != null)
           studentHomeworkAttachment.setMark(mark);
        if(studentAttachmentStatus != null)
            studentHomeworkAttachment.setStatus(studentAttachmentStatus);

        studentHomeworkAttachment.setCheckedDate(getCurrentDate());
        studentHomeworkRepository.save(studentHomeworkAttachment);
        return "homework was successfully checked";
    }



    @Override
    public List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByIds(List<Long> studentAttachmentsIds) {
        List<StudentHomeworkAttachmentDto> studentHomeworkAttachmentsDtos = studentAttachmentsIds.stream()
                .map(id -> HomeworkMapper.studentHomeworkAttachmentToDto(studentHomeworkRepository.findById(id).get()))
                .collect(Collectors.toList());

        return studentHomeworkAttachmentsDtos;
    }

    @Override
    public List<HomeworkResponse> getCreatedHomeworksByIds(List<Long> homeworksIds)
    {
        List<HomeworkResponse> homeworkResponses = homeworksIds.stream()
                .map(id -> HomeworkMapper.convertToHomeworkResponse(homeworkRepository.findById(id).get()))
                .collect(Collectors.toList());

        return homeworkResponses;
    }

    @Override
    public HomeworkResponse getHomeworkById(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).get();
        if(homework == null)
            return null;
        HomeworkResponse response = HomeworkMapper.convertToHomeworkResponse(homework);
        return  response ;
    }

    @Override
    public StudentHomeworkAttachmentDto findStudentAttachmentsByHomeworkIdAndStudentId(Long homeworkId, Long studentId) {
       Homework homework = homeworkRepository.findById(homeworkId).get();
       if(homework == null)
       {
           log.error("Homework is null");
           return null;
       }
       StudentHomeworkAttachment  studentHomeworkAttachment = studentHomeworkRepository.findByStudentIdAndHomework(studentId,homework);
       if(studentHomeworkAttachment == null)
       {
           log.error("StudentHomeworkAttachment is null");
           return null;
       }

       return HomeworkMapper.studentHomeworkAttachmentToDto(studentHomeworkAttachment);
    }

    @Override
    public List<HomeworkResponse> getHomeworksByAuthorIdAndHomeworkStatusAndCourse(Long authorId, String homeworkStatus , Long courseId) {

         List<Homework> homeworks = null;
         Course course = null;

         if(courseId != null)
         {
             course = courseService.findCourseById(courseId);
         }


         if(homeworkStatus.equals("All"))
         {
             homeworks = homeworkRepository.findByAuthorIdAndHomeworkStatusAndCourse(authorId,null,course);
         }
         if(homeworkStatus.equals("Checked"))
         {
             homeworks =homeworkRepository.findByAuthorIdAndHomeworkStatusAndCourse(authorId, HomeworkStatus.Checked,course);
         }
        if(homeworkStatus.equals("Unchecked"))
        {
            homeworks = homeworkRepository.findByAuthorIdAndHomeworkStatusAndCourse(authorId, HomeworkStatus.Unchecked,course);
        }

        if(homeworks == null || homeworks.isEmpty())
            return null;

        return homeworks.stream().map(HomeworkMapper::convertToHomeworkResponse).collect(Collectors.toList());
    }

    private String getCurrentDate()
    {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        return today.format(formatter);
    }
    @Override
    @Transactional(readOnly = true)
    public List<HomeworkResponse> getHomeworks(Long studentId, String type) {
        List<Homework> homeworks;

        switch (type) {
            case "Submitted":
                homeworks = homeworkRepository.findBySubmitHomeworkUserEntitiesIdContaining(studentId);
                break;
            case "Graded":
                homeworks = homeworkRepository.findByGradedHomeworkUserEntitiesIdContaining(studentId);
                break;
            case "Rejected":
                homeworks = homeworkRepository.findByRejectedHomeworkUserEntitiesIdContaining(studentId);
                break;
            case "All":
            default:
                homeworks = homeworkRepository.findAllByUserEntitiesIdContaining(studentId);
                break;
        }

        if (homeworks == null || homeworks.isEmpty()) {
            log.info("Homework list is empty");
            return null;
        }

        return homeworks.stream()
                .map(HomeworkMapper::convertToHomeworkResponse)
                .collect(Collectors.toList());
    }
}
