package com.example.mainservice.Controller;

import com.example.mainservice.Model.Course;
import com.example.mainservice.Service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    final private ViewService viewService;

    @GetMapping("/mainPage")
    public String mainPage()
    {
        List<Course> courses = viewService.getCourses();


        return "mainPage";
    }

}
