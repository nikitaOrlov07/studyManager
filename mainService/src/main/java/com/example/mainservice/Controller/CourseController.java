package com.example.mainservice.Controller;

import com.example.mainservice.Dto.course.CourseCreationRequest;
import com.example.mainservice.Security.SecurityConfig;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
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
public class CourseController {

    private final CourseService courseService;

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

        String result = courseService.createCourse(request, files);
        if(result.equals("Course with title " + request.getTitle() + " already exists"))
        {
            return "redirect:/courses/create?existingTitle";
        }
        return "redirect:/home?courseCreatedSuccessfully";
    }
}
