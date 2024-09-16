package com.example.courseservice.Service.impl;

import com.example.courseservice.Dto.Homework.Enums.HomeworkStatus;
import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentAttachmentRequest;
import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Mappers.UsersMapper;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import com.example.courseservice.Dto.UserEntity.UserEntityDto;
import com.example.courseservice.Mappers.HomeworkMapper;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Repository.HomeworkRepository;
import com.example.courseservice.Repository.StudentAttachmentRepository;
import com.example.courseservice.Service.AttachmentService;
import com.example.courseservice.Service.CourseService;
import com.example.courseservice.Service.HomeworkService;
import com.example.courseservice.Service.UserService;
import org.springframework.data.jpa.domain.Specification;
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
import java.util.Collections;
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
        private final StudentAttachmentRepository studentHomeworkAttachmentRepository;
        private final UserService userService;
        private final HomeworkMapper mapper;


        @Override
        public Homework createHomework(HomeworkRequest request) {


            Homework homework = mapper.homeworkRequestToHomework(request); // make this method non-static in HomeworkMapper class, because i need to use CourseService

            UserEntityResponse author = userService.findUsersById(Arrays.asList(request.getAuthorId())).get(0);

            homework.setAuthorId(author.getId());
            homework.setStatus(HomeworkStatus.Unchecked);

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
        public String uploadFile(MultipartFile file, Long courseId, Long userId) throws Exception {
            log.info("Uploading file for homework is working");
            Homework homework = homeworkRepository.findById(courseId).get();
            UserEntityResponse userEntityDto = userService.findUsersById(Arrays.asList(userId)).get(0);

            if (userEntityDto == null) {
                return "error";
            }

            Attachment attachment = attachmentService.saveAttachment(file, homework.getCourse(), homework, UsersMapper.responseToDto(userEntityDto));

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
            if (uploadDate.isAfter(endDate)) {
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
            if (savedStudentHomeworkAttachment == null)
                log.error("Error in saving homework Attachment with id: " + studentUploadedHomework.getId());

            // Update homework information
            homeworkRepository.save(homework);

            // Http request to update user (student)
            log.info("Start to update user information is user Service");
            Boolean result = webClientBuilder.build()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("user-service")
                            .path("/users/submit/homeworks/" + homework.getId())
                            .queryParam("userId", student.getId())
                            .build())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            if (result) {
                log.info("User information for uploading homework was changed successfully");
            } else {
                log.error("User information for uploading homework was not changed successfully");
                throw new Exception("Error in user service");
            }

            return "Homework was upload successfully";
        }


        @Override
        public List<HomeworkResponse> getCreatedHomeworksByIds(List<Long> homeworksIds) {
            List<HomeworkResponse> homeworkResponses = homeworksIds.stream()
                    .map(id -> mapper.convertToHomeworkResponse(homeworkRepository.findById(id).get()))
                    .collect(Collectors.toList());

            return homeworkResponses;
        }

        @Override
        @Transactional
        public HomeworkResponse getHomeworkById(Long homeworkId) {
            Homework homework = homeworkRepository.findById(homeworkId).get();
            if (homework == null)
                return null;
            HomeworkResponse response = mapper.convertToHomeworkResponse(homework);
            return response;
        }



        @Override
        @Transactional(readOnly = true)
        public List<HomeworkResponse> findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(Long authorId, String homeworkStatus, String courseTitle, String homeworkTitle) {
            Specification<Homework> spec = Specification.where(null); // interface that is used in JPA (Java Persistence API) to create dynamic SQL queries using a filter criterion.

            log.info("Homework status: "+ homeworkStatus);

            if (authorId != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("authorId"), authorId));
            }
            /*
            (root, query, cb) -> ...: This is a lambda function, which is an implementation of the Specification interface.
            Parameters:
            root - an object representing the root entity of the query (in this case Homework).
            query - the query itself.
            cb - CriteriaBuilder, which is used to build the query conditions.
             */
            if (homeworkStatus != null  && !homeworkStatus.equals("All")) {
                HomeworkStatus status = HomeworkStatus.valueOf(homeworkStatus);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
            } else if (homeworkStatus != null  && homeworkStatus.equals("All")) {
                spec = spec.and((root, query, cb) -> cb.or(
                        cb.equal(root.get("status"), HomeworkStatus.Checked),
                        cb.equal(root.get("status"), HomeworkStatus.Unchecked)
                ));
            }

            if (courseTitle != null && !courseTitle.isEmpty()) {
                log.info("Course Title: " + courseTitle);
                Course course = courseService.findByTitle(courseTitle);
                if (course != null) {
                    log.info("Course id: " + course.getId());
                    spec = spec.and((root, query, cb) -> cb.equal(root.get("course"), course));
                }
            }

            if (homeworkTitle != null && !homeworkTitle.isEmpty()) {
                spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + homeworkTitle.toLowerCase() + "%"));
            }

            List<Homework> homeworks = homeworkRepository.findAll(spec); // repositories must extend JpaSpecificationExecutor<Homework>

            if (homeworks.isEmpty()) {
                log.info("No homeworks found matching the criteria");
                return Collections.emptyList();
            }

            log.info("Found {} homeworks matching the criteria", homeworks.size());
            return homeworks.stream().map(mapper::convertToHomeworkResponse).collect(Collectors.toList());
        }


        @Override
        public String checkHomework(Long studentAttachmentId, Integer mark, String message, String status) {
            // StudentAttachment changing
            StudentHomeworkAttachment studentAttachment = studentHomeworkAttachmentRepository.findById(studentAttachmentId).get();
            if(studentAttachment == null)
            {
                log.error("Required student attachment was not found");
                return "the required student attachment was not found";
            }
            Homework homework = homeworkRepository.findById(studentAttachment.getHomework().getId()).get();
            if(homework == null || !homework.getSubmitHomeworkUserEntitiesId().contains(studentAttachment.getStudentId()))
            {
                log.error("Homework error");
                return "Homework error";
            }

            studentAttachment.setMark(mark);

            // Set checked date
            LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            studentAttachment.setCheckedDate(date.format(formatter));

            // Set status and update homework information
            homework.getSubmitHomeworkUserEntitiesId().remove(studentAttachment.getStudentId());
            if(status.equals("reject"))
            {
                studentAttachment.setStatus(StudentAttachmentStatus.Rejected);
                homework.getRejectedHomeworkUserEntitiesId().add(studentAttachment.getStudentId());
            }
            else if(status.equals("accept"))
            {
               if(mark == null)
               {
                   log.info("Set student attachment status -> completed");
                   studentAttachment.setStatus(StudentAttachmentStatus.Completed);
                   homework.getAcceptedHomeworkEntitiesId().add(studentAttachment.getStudentId());
               }
               else
               {
                   log.info("Set student attachment status -> rated");
                   studentAttachment.setStatus(StudentAttachmentStatus.Graded);
                   homework.getGradedHomeworkUserEntitiesId().add(studentAttachment.getStudentId());
               }
            }
            else
            {
                log.error("Wrong student attachment status (can be only \"accepted\" or \"rejected\")");
                return "Wrong status";
            }
            studentAttachment.setMessage(message);
            // Set homework status checked (if homework does not have unchecked student attachments)
            if(homework.getUserEntitiesId().isEmpty() && homework.getSubmitHomeworkUserEntitiesId().isEmpty())
            {
                homework.setStatus(HomeworkStatus.Checked);
            }
            else
                homework.setStatus(HomeworkStatus.Unchecked);


            // Update all information
            studentHomeworkAttachmentRepository.save(studentAttachment);
            homeworkRepository.save(homework);

            return "Student attachment successfully checked";
        }

        @Override
        @Transactional(readOnly = true)
        public List<HomeworkResponse> getHomeworks(Long studentId, String type) {
            List<Homework> homeworks = new ArrayList<>();
            log.info("type: " + type);

            switch (type) {
                case "Submitted":
                    homeworks.addAll(homeworkRepository.findBySubmitHomeworkUserEntitiesIdContaining(studentId));
                    break;
                case "Checked":
                    homeworks.addAll(homeworkRepository.findByGradedHomeworkUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findByAcceptedHomeworkEntitiesIdContaining(studentId));
                    break;
                case "Rejected":
                    homeworks.addAll(homeworkRepository.findByRejectedHomeworkUserEntitiesIdContaining(studentId));
                    break;
                case "Assigned":
                    homeworks.addAll(homeworkRepository.findAllByUserEntitiesIdContaining(studentId));
                    break;
                case "All":
                    homeworks.addAll(homeworkRepository.findBySubmitHomeworkUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findByGradedHomeworkUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findByRejectedHomeworkUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findAllByUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findByAcceptedHomeworkEntitiesIdContaining(studentId));
                    break;
                default:
                    homeworks.addAll(homeworkRepository.findBySubmitHomeworkUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findByGradedHomeworkUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findByRejectedHomeworkUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findAllByUserEntitiesIdContaining(studentId));
                    homeworks.addAll(homeworkRepository.findByAcceptedHomeworkEntitiesIdContaining(studentId));
                    break;
            }

            if (homeworks.isEmpty()) {
                log.info("Homework list is empty");
                return null;
            }

            return homeworks.stream()
                    .distinct() // Remove duplicates
                    .map(mapper::convertToHomeworkResponse)
                    .collect(Collectors.toList());
        }
    }
