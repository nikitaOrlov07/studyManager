package com.example.mainservice.Controller;

import com.example.mainservice.Dto.Homeworks.Enums.StudentAttachmentStatus;
import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.Homeworks.HomeworkRequest;
import com.example.mainservice.Dto.StudentAttachments.StudentAttachmentRequest;
import com.example.mainservice.Dto.StudentAttachments.StudentHomeworkAttachmentDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Model.Course;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.CourseService;
import com.example.mainservice.Service.HomeworkService;
import com.example.mainservice.Service.UserService;
import com.example.mainservice.Service.ViewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/homeworks")
@RequiredArgsConstructor
@Slf4j
public class HomeworkController {

    private final ViewService viewService;
    private final UserService userService;
    private final CourseService courseService;
    private final HomeworkService homeworkService;

    // Create homework page
    @GetMapping("/create/course/{courseId}")
    public String createHomeworkPage(@PathVariable Long courseId,
                                     Model model) throws Exception {
        Course course = viewService.findCourse(courseId);
        String username = SecurityUtil.getSessionUser();
        if(course == null || username == null || username.isEmpty())
        {
            log.error("Course not found");
            return "redirect:/home?error";
        }

        UserEntityDto userEntityDto = userService.findUserByUsername(username);
        if(!course.getAuthorId().equals(userEntityDto.getId()))
        {
            return "redirect:/home?notAllowed";
        }
        List<UserEntityDto> users = userService.findUsersByIds(course.getInvolvedUserIds());

        HomeworkRequest homeworkRequest = new HomeworkRequest();
        homeworkRequest.setCourseId(course.getId());
        homeworkRequest.setAuthorId(userEntityDto.getId());

        model.addAttribute("course",course);
        model.addAttribute("homeworkCreationRequest",homeworkRequest);
        model.addAttribute("involvedUsers", users);

        return "homeworkCreation";
    }


    /// Homeworks page
    // For teacher
    @GetMapping("/teacherPage")  // Get teacher page
    public String getTeacherPage(Model model,
                                 @RequestParam(value = "type",required = false ) String homeworkStatus,
                                 @RequestParam(value = "courseTitle",required = false) String courseTitle ,
                                 @RequestParam(value=  "homeworkTitle" , required = false) String homeworkTitle)
    {
      String username = SecurityUtil.getSessionUser();
      if(username == null || username.isEmpty() || userService.findUserByUsername(username).getCreatedCoursesIds().isEmpty())
      {
          log.error("The user does not have permission to access the teacher's page ");
          return "redirect:/home?notAllowed";
      }
      if(homeworkStatus == null || homeworkStatus.isEmpty())
      {
          homeworkStatus = "All";
      }
      UserEntityDto userEntityDto = userService.findUserByUsername(SecurityUtil.getSessionUser());
      List<HomeworkDto> createdHomeworks =  homeworkService.findHomeworksByAuthorAndStatusAndCourseIdAndCourseTitle(userEntityDto.getId(),homeworkStatus,courseTitle,homeworkTitle);
      log.info("User created homeworks size is :"+createdHomeworks.size());

        model.addAttribute("pageType","teacher"); // for view
        model.addAttribute("homeworks",createdHomeworks);

      return "homeworksPage";

    }
    // For student
    @GetMapping
    public String getHomeworksPage(Model model ,
                                   @RequestParam(value = "type",required = false) String type)
    {
        String username = SecurityUtil.getSessionUser();
        if(username == null || username.isEmpty())
        {
            log.error("Unauthorized user trying to reach \"getHomeworks\" page");
            return "redirect:/home?notAllowed";
        }
        UserEntityDto user = userService.findUserByUsername(username);

        if(type == null || type.isEmpty())
        {
            type= "All";
        }

        List<HomeworkDto> homeworks = viewService.findHomeworksByUser(user.getId(),type);
        model.addAttribute("homeworks",homeworks);
        model.addAttribute("type",type);
        model.addAttribute("user",user);

        model.addAttribute("pageType","student"); // for view

        return "homeworksPage";
    }

    /// Detail pages of homeworks
    // For student
    @GetMapping("/{homeworkId}")
    public String getHomeworkDetailPageForUser(@PathVariable Long homeworkId ,
                                        Model model)
    {
        String username = SecurityUtil.getSessionUser();
        if(username == null || username.isEmpty())
        {
            return "redirect:/home?notAllowed";
        }
        UserEntityDto user = userService.findUserByUsername(username);
        HomeworkDto homeworkDto = viewService.findHomeworkByHomeworkId(homeworkId);
        StudentHomeworkAttachmentDto studentAttachment = homeworkService.findStudentAttachmentsByHomeworkAndStudentId(homeworkId,user.getId());

        // Check that the user ID is present in at least one of the possible lists.
        if (!(homeworkDto.getGradedHomeworkUserEntitiesId().contains(user.getId()) ||
                homeworkDto.getAcceptedHomeworkEntitiesId().contains(user.getId()) ||
                homeworkDto.getUserEntitiesId().contains(user.getId()) ||
                homeworkDto.getRejectedHomeworkUserEntitiesId().contains(user.getId()) ||
                homeworkDto.getSubmitHomeworkUserEntitiesId().contains(user.getId()))) {
            return "redirect:/home?notAllowed";
        }

        if(studentAttachment != null)
        {
            log.info("Attachment status :"+ studentAttachment.getStatus());
            log.info("Attachment id "+ studentAttachment.getId());
            log.info("Attachment upload date "+ studentAttachment.getUploadedDate());

        }

        log.info("getHomeworkDetailPage is working");
        model.addAttribute("user", user); // maybe not needed
        model.addAttribute("homework",homeworkDto);
        model.addAttribute("attachment", studentAttachment);

        model.addAttribute("pageType","student");
        return "homeworkDetailPage";
    }
    // For teacher
    @GetMapping("/teacherPage/{homeworkId}")
    public String getHomeworkDetailPageForTeacher(@PathVariable Long homeworkId,
                                                  Model model)
    {
        HomeworkDto homeworkDto = viewService.findHomeworkByHomeworkId(homeworkId);
        List<StudentHomeworkAttachmentDto> studentsAttachments = homeworkService.findStudentsAttachmentsByHomeworkId(homeworkId);
        List<UserEntityDto> notSubmittedUsers = userService.findUsersByIds(homeworkDto.getUserEntitiesId());
        log.info("getHomeworkDetailPage is working");

        if(studentsAttachments == null)
        {
            log.error("studentsAttachments is null");
        }
        if(studentsAttachments != null) {
            log.info("student attachments is empty: " + studentsAttachments.isEmpty());
            log.info("students attachments size is: " + studentsAttachments.size());
        }

        model.addAttribute("homework",homeworkDto);
        model.addAttribute("homeworkAttachments",studentsAttachments);
        model.addAttribute("pageType","teacher");
        model.addAttribute("notSubmittedUsers",notSubmittedUsers);

        return "homeworkDetailPage";
    }
    // Check studentAttachment
    @PostMapping("/teacherPage/checkHomeworks/{studentAttachmentId}")
    public String checkStudentAttachment(@PathVariable Long studentAttachmentId,
                                         @RequestParam(value="mark",required = false) Integer mark,
                                         @RequestParam(value = "message",required = false)  String message,
                                         @RequestParam("homeworkId") Long homeworkId,
                                         @RequestParam(value = "status",required = false) String status )

    {
        log.info("checkStudentAttachment Controller method is working with studentAttachmentId: " + studentAttachmentId);
        homeworkService.checkStudentAttachment(homeworkId,studentAttachmentId , mark , message, status);
        return "redirect:/homeworks/teacherPage/"+homeworkId+"?homeworkChecked";
    }
    //// Homework activities
    /// For teacher
    // Saving homework
    @PostMapping("/create/save")
    public String saveHomework(@Valid @ModelAttribute("homeworkCreationRequest") HomeworkRequest homeworkRequest,
                               BindingResult bindingResult,
                               @RequestParam("courseId") Long courseId,
                               Model model) throws Exception {

        if (bindingResult.hasErrors()) {
            model.addAttribute("involvedUsers", userService.findUsersByIds(viewService.findCourse(courseId).getInvolvedUserIds()));
            model.addAttribute("course", viewService.findCourse(courseId));
            return "homeworkCreation";
        }

        Boolean result = homeworkService.createHomework(homeworkRequest);

        if (result == null || !result) {
            log.error("Homework creation failed");
            return "redirect:/home?error";
        }

        log.info("Controller method \"saveHomework\" is working correctly");
        return "redirect:/courses/" + courseId + "?successHomeworkCreated";
    }
    // Check homework

    /// For student
    // Upload the homework
    @PostMapping("/upload")
    public String submitHomeworkForStudent(@ModelAttribute("studentAttachment") StudentAttachmentRequest studentAttachmentRequest,
                                           @RequestParam(value = "files" ,required = false) List<MultipartFile> files,
                                           Model model) throws IOException {

        UserEntityDto currentUser = userService.findUserByUsername(SecurityUtil.getSessionUser());

        if(currentUser == null || !currentUser.getHomeworkIds().contains(studentAttachmentRequest.getHomeworkId()))
        {
          log.error("User can`t submit homework");
          return "redirect:/home?notAllowed";
        }
        log.info("User can upload homework");

        if(files == null || files.isEmpty())
        {
            model.addAttribute("studentAttachment", studentAttachmentRequest);
            model.addAttribute("pageType","student");
            return "homeworkDetailPage";
        }

        studentAttachmentRequest.setStudentId(currentUser.getId());
        Boolean result = homeworkService.uploadStudentAttachment(studentAttachmentRequest,files);

        if(!result)
        {
            log.error("Attachment was not uploaded successfully");
            return "redirect:/home?error";
        }
        log.info("Attachment was uploaded successfully");

        return "redirect:/homeworks/"+studentAttachmentRequest.getHomeworkId();

    }
}
