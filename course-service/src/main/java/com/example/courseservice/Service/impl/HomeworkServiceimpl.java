package com.example.courseservice.Service.impl;

import com.example.courseservice.Dto.Homework.Enums.HomeworkStatus;
import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentAttachmentRequest;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Mappers.UsersMapper;
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
import com.example.courseservice.Service.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.courseservice.Dto.Homework.Enums.StudentAttachmentStatus;

import java.time.LocalDateTime;
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
        private final StudentHomeworkRepository studentHomeworkAttachmentRepository;
        private final UserService userService;
        private final HomeworkMapper mapper;


        @Override
        public Homework createHomework(HomeworkRequest request) {


            Homework homework = mapper.homeworkRequestToHomework(request); // make this method non-static in HomeworkMapper class, because i need to use CourseService

            UserEntityResponse author = userService.findUsersById(Arrays.asList(request.getAuthorId())).get(0);

            homework.setAuthorId(author.getId());
            Homework savedHomework = homeworkRepository.save(homework);

            userService.updateUserItems("homeworks", "create", savedHomework.getId(), author.getId());

            if (request.getUserEntitiesId() != null && !request.getUserEntitiesId().isEmpty()) {
                webClientBuilder.build()
                        .post()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("http")
                                .host("user-service")
                                .path("/users/assignHomeworks")
                                .queryParam("usersId", request.getUserEntitiesId())
                                .queryParam("homeworkId", savedHomework.getId())
                                .queryParam("type", "assign")
                                .build())
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
            }

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
    @Override
    public String uploadHomework(StudentAttachmentRequest studentAttachmentRequest) throws Exception {
        log.info("Uploading homework for student");

        Homework homework = homeworkRepository.findById(studentAttachmentRequest.getHomeworkId())
                .orElseThrow(() -> new Exception("Homework not found"));

        // HTTP request to userService to find user by userId
        UserEntityDto student = UsersMapper.responseToDto(userService.findUsersById(Arrays.asList(studentAttachmentRequest.getStudentId())).get(0));
        log.info("mapper is working good ");
        if (student == null || !homework.getUserEntitiesId().contains(studentAttachmentRequest.getStudentId())) {
            log.error("User cannot upload this homework");
            throw new Exception("Student is not allowed to submit this homework");
        }
        log.info("Student is allowed to upload homework");

        // check if this job has been submitted before (working good)
        boolean homeworkAlreadySubmitted = student.getCompletedHomeworks().stream()
                .anyMatch(attachment -> attachment.getHomework().getId().equals(studentAttachmentRequest.getHomeworkId()));

        log.info("The student has not taken this assignment before");

        if (homeworkAlreadySubmitted) {
            log.error("Homework already submitted");
            throw new Exception("Homework has already been submitted by this student");
        }

        // Get homework endDate and attachment upload date in LocalDateFormat
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate uploadDate = LocalDate.now();
        LocalDate endDate = LocalDate.parse(homework.getEndDate(), formatter);

        // Set studentAttachment status
        StudentAttachmentStatus status = StudentAttachmentStatus.Submitted;
        if(uploadDate.isAfter(endDate))
        {
             status = StudentAttachmentStatus.Late;
        }
       log.info("Create student attachment object");
       StudentHomeworkAttachment studentUploadedHomework = StudentHomeworkAttachment.builder()
                .homework(homework)
                .studentId(student.getId())
                .status(status)
                .uploadedDate(uploadDate.format(formatter))
                .build();

        List<Attachment> attachments = new ArrayList<>();

        log.info("Saving files");
        for (MultipartFile file : studentAttachmentRequest.getFiles()) {
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

        studentUploadedHomework.setAttachments(attachments);
        student.getCompletedHomeworks().add(HomeworkMapper.studentHomeworkAttachmentToDto(studentUploadedHomework));
        // homework entity changing
        homework.getUserEntitiesId().remove(student.getId());
        homework.getSubmitHomeworkUserEntitiesId().add(student.getId());

        StudentHomeworkAttachment savedStudentHomeworkAttachment = studentHomeworkAttachmentRepository.save(studentUploadedHomework);
        if(savedStudentHomeworkAttachment== null)
            log.error("Error in saving homework Attachment with id: "+ studentUploadedHomework.getId());

        // Update homework information
        homeworkRepository.save(homework);

        // Http request to update user (student)
        log.info("Start to update user information is user Service");
        Boolean result = webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("user-service")
                        .path("/users/submit/homeworks/"+homework.getId())
                        .queryParam("userId",student.getId())
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if(result) {
            log.info("User information for uploading homework was changed successfully");
        }
        else {
            log.error("User information for uploading homework was not changed successfully");
            throw new Exception("Error in user service");
        }

        return "Homework was upload successfully";
    }

    @Override
    public String checkHomework(Long  studentHomeworkAttachmentId, StudentAttachmentStatus studentAttachmentStatus, String message, Integer mark) throws Exception {
        StudentHomeworkAttachment studentHomeworkAttachment = studentHomeworkAttachmentRepository.findById(studentHomeworkAttachmentId)
                .orElseThrow(() -> new Exception("Homework not found"));
        Homework homework = studentHomeworkAttachment.getHomework();

        // HTTP request to take current user and compare  him to authorId
        UserEntityDto author = UserEntityDto.builder()
                .id(2L)
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .createdHomeworks(Arrays.asList(mapper.convertToHomeworkResponse(homework)))
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
        studentHomeworkAttachmentRepository.save(studentHomeworkAttachment);
        return "homework was successfully checked";
    }



    @Override
    public List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByIds(List<Long> studentAttachmentsIds) {
        List<StudentHomeworkAttachmentDto> studentHomeworkAttachmentsDtos = studentAttachmentsIds.stream()
                .map(id -> HomeworkMapper.studentHomeworkAttachmentToDto(studentHomeworkAttachmentRepository.findById(id).get()))
                .collect(Collectors.toList());

        return studentHomeworkAttachmentsDtos;
    }

    @Override
    public List<HomeworkResponse> getCreatedHomeworksByIds(List<Long> homeworksIds)
    {
        List<HomeworkResponse> homeworkResponses = homeworksIds.stream()
                .map(id -> mapper.convertToHomeworkResponse(homeworkRepository.findById(id).get()))
                .collect(Collectors.toList());

        return homeworkResponses;
    }

    @Override
    @Transactional
    public HomeworkResponse getHomeworkById(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId).get();
        if(homework == null)
            return null;
        HomeworkResponse response = mapper.convertToHomeworkResponse(homework);
        return  response ;
    }

    @Override
    @Transactional
    public StudentHomeworkAttachmentDto findStudentAttachmentsByHomeworkIdAndStudentId(Long homeworkId, Long studentId) {

       Homework homework = homeworkRepository.findById(homeworkId).get();
       if(homework == null)
       {
           log.error("Homework is null");
           return null;
       }
       StudentHomeworkAttachment  studentHomeworkAttachment = studentHomeworkAttachmentRepository.findByStudentIdAndHomework(studentId,homework);
       if(studentHomeworkAttachment == null)
       {
           log.error("StudentHomeworkAttachment is null");
           return null;
       }

       return mapper.studentHomeworkAttachmentToDto(studentHomeworkAttachment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeworkResponse> findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(Long authorId, String homeworkStatus , Long courseId, String courseTitle) {

         List<Homework> homeworks = null;

         Course course = null;

         if(courseId != null)
         {
             course = courseService.findCourseById(courseId);
         }
         if(courseTitle != null && !courseTitle.isEmpty())
         {
             course = courseService.findByTitle(courseTitle);
         }


         if(homeworkStatus.equals("Checked"))
         {
             homeworks =   homeworkRepository.findByAuthorIdAndStatusAndCourse(authorId,HomeworkStatus.Checked,course);
         }
         else if(homeworkStatus.equals("Unchecked"))
         {
             homeworks =   homeworkRepository.findByAuthorIdAndStatusAndCourse(authorId,HomeworkStatus.Unchecked,course);
         }
         else {
             homeworks = homeworkRepository.findByAuthorIdAndStatusAndCourse(authorId,null,course);
         }

        if(homeworks == null || homeworks.isEmpty())
            return null;

        log.info("User created homeworks: " + homeworks);
        return homeworks.stream().map(mapper::convertToHomeworkResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByHomeworkId(Long homeworkId) {
            if(homeworkId == null)
            {return null;}

            Homework homework = homeworkRepository.findById(homeworkId).get();

            if(homework == null)
            {
                return null;
            }

            List<StudentHomeworkAttachment> studentHomeworkAttachments = studentHomeworkAttachmentRepository.findAllByHomework(homework);

            if(studentHomeworkAttachments == null || studentHomeworkAttachments.isEmpty())
                return null;

            return studentHomeworkAttachments.stream().map(HomeworkMapper::studentHomeworkAttachmentToDto).collect(Collectors.toList());
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
                .map(mapper::convertToHomeworkResponse)
                .collect(Collectors.toList());
    }
}
