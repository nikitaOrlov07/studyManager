package com.example.mainservice.Controller;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.Homeworks.HomeworkRequest;
import com.example.mainservice.Dto.Homeworks.StudentHomeworkAttachmentDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Model.Course;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.CourseService;
import com.example.mainservice.Service.HomeworkService;
import com.example.mainservice.Service.UserService;
import com.example.mainservice.Service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // Method for getting all user homework
    @GetMapping
    public String getHomeworkPage(Model model ,  @RequestParam(value = "type",required = false) String type)
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

        return "homeworksPage";
    }

    // Homework detail page (for student)
    @GetMapping("/{homeworkId}")
    public String getHomeworkDetailPage(@PathVariable Long homeworkId ,
                                        Model model)
    {
        String username = SecurityUtil.getSessionUser();
        if(username == null || username.isEmpty())
        {
            return "redirect:/home?notAllowed";
        }
        UserEntityDto user = userService.findUserByUsername(username);
        HomeworkDto homeworkDto = viewService.findHomeworkByHomeworkId(homeworkId);
        StudentHomeworkAttachmentDto studentAttachments = viewService.findStudentAttachmentsByHomeworkAndStudentId(homeworkDto.getId(),user.getId());

        // Check that the user ID is present in at least one of the possible lists.
        if (!(homeworkDto.getGradedHomeworkUserEntitiesId().contains(user.getId()) ||
                homeworkDto.getUserEntitiesId().contains(user.getId()) ||
                homeworkDto.getRejectedHomeworkUserEntitiesId().contains(user.getId()) ||
                homeworkDto.getSubmitHomeworkUserEntitiesId().contains(user.getId()))) {
            return "redirect:/home?notAllowed";
        }

        log.info("getHomeworkDetailPage is working");
        model.addAttribute("user", user); // maybe not needed
        model.addAttribute("homework",homeworkDto);
        model.addAttribute("studentAttachments", studentAttachments);
        return "homeworkDetailPage";
    }

    // Create homework page
    @GetMapping("/create/course/{courseId}")
    public String createHomeworkPage(@PathVariable Long courseId,
                                     Model model) throws Exception {
        Course course = viewService.findCourse(courseId);
        System.out.println(course.getAuthor());
        String username = SecurityUtil.getSessionUser();
        if(course == null || username == null || username.isEmpty())
        {
            log.error("Course not found");
            return "redirect:/home?error";
        }
        UserEntityDto userEntityDto = userService.findUserByUsername(username);
        if(!course.getAuthor().equals(userEntityDto.getId().toString()))
        {
            return "redirect:/home?notAllowed";
        }
        model.addAttribute("course",course);
        model.addAttribute("homeworkCreationRequest", new HomeworkRequest());
        return "homeworkCreation";
    }
    @PostMapping("/create/save")
    public String saveHomework(@ModelAttribute("homework") HomeworkRequest homeworkRequest)
    {
      return null;
    }
}
