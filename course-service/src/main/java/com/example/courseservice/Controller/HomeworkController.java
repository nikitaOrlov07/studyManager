package com.example.courseservice.Controller;

import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.Homework.Enums.StudentAttachmentStatus;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Service.HomeworkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/homeworks")
public class HomeworkController {
    private final HomeworkService homeworkService;

    // Create homework Entity
    @PostMapping("/create/save")
    @ResponseStatus(HttpStatus.CREATED)
    public String createHomework(@ModelAttribute HomeworkRequest request) {


        Homework homework = homeworkService.createHomework(request);
        System.out.println("From controller:" + request);

        // File saving
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (MultipartFile file : request.getFiles()) {
                if (!file.isEmpty()) {
                    try {
                        homeworkService.uploadFile(file, homework.getId());
                    } catch (Exception e) {
                        log.error("Error uploading file", e);
                    }
                }
            }
        }
        return "Homework was created successfully";
    }

    // Student can upload
    @PostMapping("/upload")
    public ResponseEntity<?> uploadHomework(
            @RequestParam("homeworkId") Long homeworkId,
            @RequestParam("studentId") Long studentId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            String str = homeworkService.uploadHomework(homeworkId, studentId, files);
            return ResponseEntity.ok(str);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading homework: " + e.getMessage());
        }
    }
    // Homework author (teacher) can check the homework
    @PostMapping("/checkHomework/{studentAttachmentId}")
    public ResponseEntity<?> checkHomework(
            @PathVariable Long studentAttachmentId,
            @RequestParam(required = false) StudentAttachmentStatus studentAttachmentStatus,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) Integer mark) {

        try {
            String result = homeworkService.checkHomework(studentAttachmentId, studentAttachmentStatus, message, mark);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid homework status provided");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // Get homework
    @GetMapping("/{homeworkId}")
    public HomeworkResponse getHomeworkById(@PathVariable Long homeworkId)
    {
        log.info("Course Service \"getHomeworkById\" method is working");
        HomeworkResponse homeworkResponse = homeworkService.getHomeworkById(homeworkId);
        log.info("Homework title: " + homeworkResponse.getTitle());
        return homeworkResponse;
    }
    @GetMapping("/studentAttachments")
    public StudentHomeworkAttachmentDto findStudentHomeworkAttachmentDto(@RequestParam("homeworkId") Long homeworkId,
                                                                         @RequestParam("studentId")  Long studentId)
    {
        log.info("Course Service \"findStudentHomeworkAttachmentDto\" method is working");
        StudentHomeworkAttachmentDto studentHomeworkAttachmentDto = homeworkService.findStudentAttachmentsByHomeworkIdAndStudentId(homeworkId,studentId);
        return studentHomeworkAttachmentDto;
    }
    // Get students homework (for homeworks page)
    @GetMapping
    @Transactional(readOnly = true) // readOnly means that the annotated method will only perform a read operation
    public List<HomeworkResponse> getHomeworksForStudents(@RequestParam("studentId") Long studentId,
                                                          @RequestParam(value = "type") String type)
    {
        log.info("Course service \"getHomeworks\" controller method is working with type: " + type + " for studentId: "+ studentId);
        return homeworkService.getHomeworks(studentId,type);
    }
    // Get homeworks by authorId
    @GetMapping("/getCreatedHomework")
    public List<HomeworkResponse> getHomeworksForTeacher(@RequestParam("authorId") Long authorId,
                                                         @RequestParam(value = "type",required = false) String homeworkStatus,
                                                         @RequestParam(value = "courseId",required = false) Long courseId)
    {
        log.info("Course service \"getHomeworksForTeacher\" controller method is working");
        return homeworkService.getHomeworksByAuthorIdAndHomeworkStatusAndCourse(authorId , homeworkStatus,courseId);
    }

}
