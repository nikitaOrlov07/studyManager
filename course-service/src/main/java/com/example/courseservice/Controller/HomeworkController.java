package com.example.courseservice.Controller;

import com.example.courseservice.Config.exceptions.ResourceNotFoundException;
import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Dto.Homework.HomeworkResponse;
import com.example.courseservice.Dto.Homework.Enums.StudentAttachmentStatus;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentAttachmentRequest;
import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import com.example.courseservice.Service.HomeworkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.example.courseservice.Service.StudentAttachmentService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/homeworks")
public class HomeworkController {
    private final HomeworkService homeworkService;
    private final StudentAttachmentService  studentAttachmentService;
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
                        homeworkService.uploadFile(file, homework.getId(),homework.getAuthorId());
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
    public ResponseEntity<?> uploadHomework(@RequestParam("homeworkId") Long homeworkId ,
                                            @RequestParam("studentId") Long studentId,
                                            @RequestParam(value = "files" , required = false) List<MultipartFile> files)
    {
        StudentAttachmentRequest studentAttachmentRequest = StudentAttachmentRequest.builder()
                .homeworkId(homeworkId)
                .studentId(studentId)
                .files(files)
                .build();

        log.info("Homework id is :" + homeworkId);
        log.info("Student id is :" + studentId);

        try {
            String str = homeworkService.uploadHomework(studentAttachmentRequest);
            return ResponseEntity.ok(str);
        }
        catch (Exception e) {
            log.error("Failed to upload");
            log.error(String.valueOf(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading homework: " + e.getMessage());
        }
    }
    // Homework author (teacher) can check the homework
    @PostMapping("/checkHomework/{studentAttachmentId}")
    public ResponseEntity<?> checkHomework(@PathVariable Long studentAttachmentId,
                                           @RequestParam(value = "mark", required = false) Integer mark,
                                           @RequestParam(value = "message" , required = false) String message,
                                           @RequestParam(value = "status") String status) {

        try {
            String result = homeworkService.checkHomework(studentAttachmentId,mark,message,status);
            log.info("Status: "+status + "\nstudentAttachmentId: "+studentAttachmentId);

            if(mark != null)
                log.info("Mark: "+mark);
            if(message != null)
                log.info("Message: "+ message);

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
                                                         @RequestParam(value = "type") String homeworkStatus,
                                                         @RequestParam(value = "courseTitle",required = false) String courseTitle,
                                                         @RequestParam(value = "homeworkTitle",required = false) String homeworkTitle )
    {
        log.info("Course service \"getHomeworksForTeacher\" controller method is working");

        log.info("Homework status: " + homeworkStatus);
        log.info("Author id: "+ authorId);
        log.info("Homework status: "+ homeworkStatus);

        return homeworkService.findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(authorId , homeworkStatus,courseTitle,homeworkTitle);
    }


    /// StudentAttachments
    @GetMapping("/getStudentAttachment")
    public StudentHomeworkAttachmentDto findStudentHomeworkAttachmentDtoByHomeworkIdAndStudentID(@RequestParam("homeworkId") Long homeworkId,
                                                                                                 @RequestParam("studentId")  Long studentId)
    {
        log.info("Course Service \"findStudentHomeworkAttachmentDto\" method is working");
        StudentHomeworkAttachmentDto studentHomeworkAttachmentDto = studentAttachmentService.findStudentAttachmentsByHomeworkIdAndStudentId(homeworkId,studentId);
        return studentHomeworkAttachmentDto;
    }

    @GetMapping("/studentAttachments")
    public List<StudentHomeworkAttachmentDto> findStudentsAttachmentsByHomeworkId(@RequestParam("homeworkId") String homeworkIdStr)
    {
        log.info("Course Service \"findStudentsAttachmentsByHomeworkId\" method is working");
        Long homeworkId = Long.parseLong(homeworkIdStr);
        List<StudentHomeworkAttachmentDto> studentHomeworkAttachmentDtos = studentAttachmentService.findHomeworkAttachmentsByHomeworkId(homeworkId);
        return studentHomeworkAttachmentDtos;
    }

    @GetMapping("/getStudentAttachmentById")
    public StudentHomeworkAttachmentDto findStudentAttachmentById(@RequestParam Long studentAttachmentId)
    {
        log.info("Course Service \"findStudentAttachmentById \" controller method is working");
        return studentAttachmentService.findStudentAttachmentById(studentAttachmentId);
    }
    // Delete homework
    @PostMapping("/delete/{homeworkId}")
    public Boolean deleteHomework(@PathVariable("homeworkId") Long homeworkId)
    {
        log.info("Course Service \"deleteHomework \" controller method is working");

        try {
            Boolean result = homeworkService.deleteHomework(homeworkId);
            if(result)
            {
                log.info("Homework with id {} was deleted successfully" , homeworkId);
            }
            else
                log.error("Error occurred while deleting homework with id :" + homeworkId);

            return result;
        }
        catch (ResourceNotFoundException e)
        {
            log.error("Error occurred while deleting homework:  "+ e.getMessage());
            return false;
        }

    }
}
