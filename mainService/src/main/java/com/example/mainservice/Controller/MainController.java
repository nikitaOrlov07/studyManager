package com.example.mainservice.Controller;

import com.example.mainservice.Model.Course;
import com.example.mainservice.Service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    final private ViewService viewService;

    @GetMapping("/home")
    public String mainPage(Model model)
    {
        List<Course> courses = viewService.getCourses();
        model.addAttribute("courses", courses);
        return "mainPage";
    }
    @GetMapping("/courses/{courseId}")
    public String detailPage(@PathVariable Long courseId,
                             Model model) throws Exception {
        Course course = viewService.findCourse(courseId);
        model.addAttribute("course", course);
        log.info("Detail Page Main controller method is working");
        return "detailPage";
    }


}
