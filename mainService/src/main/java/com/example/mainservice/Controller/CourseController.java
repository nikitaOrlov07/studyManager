package com.example.mainservice.Controller;

import com.example.mainservice.Dto.course.CourseCreationRequest;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    // Create Course
    @GetMapping("/create")
    public String getCreateCoursePage(Model model)
    {
        if(SecurityUtil.getSessionUser() == null || SecurityUtil.getSessionUser().isEmpty())
        {
            return "redirect:/login";
        }
        model.addAttribute("course",new CourseCreationRequest());
        return "course-create";
    }
    @PostMapping("/create/save")
    public String saveCourse(@RequestPart("courseData") @Valid CourseCreationRequest request,
                             @RequestPart(value = "files", required = false) List<MultipartFile> files,
                             BindingResult bindingResult,
                             Model model) throws IOException {
        if(bindingResult.hasErrors())
        {
            model.addAttribute("course", request);
            return "course-create";
        }
        log.info("Course save controller is working");
        log.info("Files size :" + files.size());
        String result = courseService.createCourse(request, files);
        if(result.equals("Course with title " + request.getTitle() + " already exists"))
        {
            return "redirect:/courses/create?existingTitle";
        }
        return "redirect:/home?courseCreatedSuccessfully";
    }
    // Join Course
    @PostMapping("/{courseId}/{action}")
    public String joinCourse(@PathVariable("action") String action,
                             @PathVariable("courseId") Long courseId)
    {
        String username = SecurityUtil.getSessionUser();
        if(username == null)
        {
            return "redirect:/home?notAllowed";
        }

        Boolean result = courseService.action(courseId, username,action);

        if(result.equals(false))
        {
            return "redirect:/home?error";
        }

        return "redirect:/courses/"+courseId+"?success"+action; // action can be join or leave
    }

}
