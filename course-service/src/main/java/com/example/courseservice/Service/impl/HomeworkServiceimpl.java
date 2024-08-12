package com.example.courseservice.Service.impl;

import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import com.example.courseservice.Dto.UserEntity;
import com.example.courseservice.Mappers.HomeworkMapper;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Repository.HomeworkRepository;
import com.example.courseservice.Repository.StudentHomeworkRepository;
import com.example.courseservice.Service.AttachmentService;
import com.example.courseservice.Service.HomeworkService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.courseservice.Dto.Homework.HomeworkStatus;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeworkServiceimpl implements HomeworkService {

    private final WebClient.Builder webClientBuilder;  // for HTTP requests
    private final HomeworkRepository homeworkRepository;
    private final AttachmentService attachmentService;
    private final StudentHomeworkRepository studentHomeworkRepository;


    @Override
    public Homework createHomework(HomeworkRequest request) {
        System.out.println(request);
        Homework homework = HomeworkMapper.homeworkRequestToHomework(request);
        System.out.println(homework);
        // HTTP request to get User author
        UserEntity author = UserEntity.builder()
                .id(2L)
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .createdHomeworks(Arrays.asList(homework))
                .chats(new ArrayList<>())
                .build(); // temporary
        // HTTP request to user service to find users in the group
        UserEntity user = UserEntity.builder()
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

        UserEntity user = UserEntity.builder()
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
        UserEntity student = UserEntity.builder()
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
                .status(HomeworkStatus.Submitted)
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
        student.getCompletedHomeworks().add(studentHomework);

        studentHomeworkRepository.save(studentHomework);
        // Http request to update user (student)
        return "Homework was upload successfully";
    }

    @Override
    public String checkHomework(Long  studentHomeworkAttachmentId, HomeworkStatus homeworkStatus, String message, Integer mark) throws Exception {
        StudentHomeworkAttachment studentHomeworkAttachment = studentHomeworkRepository.findById(studentHomeworkAttachmentId)
                .orElseThrow(() -> new Exception("Homework not found"));
        Homework homework = studentHomeworkAttachment.getHomework();

        // HTTP request to take current user and compare  him to authorId
        UserEntity author = UserEntity.builder()
                .id(2L)
                .username("username")
                .password("password")
                .email("sdsdsd")
                .role("user")
                .createdHomeworks(Arrays.asList(homework))
                .chats(new ArrayList<>())
                .build(); // temporary
        /*if(!author.getId().equals(homework.getAuthorId()))
        {
            throw new Exception("You can`t check homework");
        }
         */
        // HTTP request to take user who complete his homework
        UserEntity student = UserEntity.builder()
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
        if(homeworkStatus != null)
            studentHomeworkAttachment.setStatus(homeworkStatus);

        studentHomeworkAttachment.setCheckedDate(getCurrentDate());
        studentHomeworkRepository.save(studentHomeworkAttachment);
        return "homework was successfully checked";
    }

    @Override
    public List<Homework> getAllHomeworks(Long studentId) {
        return homeworkRepository.findAllByStudentId(studentId);
    }

    private String getCurrentDate()
    {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        return today.format(formatter);
    }
}
