package com.example.mainservice.Controller;

import com.example.mainservice.Dto.UserEntityDto;
import com.example.mainservice.Model.Course;
import com.example.mainservice.Service.UserService;
import com.example.mainservice.Service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    final private ViewService viewService;
    final private UserService userService;


    @GetMapping("/home")
    public String mainPage(Model model)
    {
        log.info("mainPage controller is working");
        List<Course> courses = viewService.getCourses();
        UserEntityDto userEntityDto = userService.getCurrentUserFromUserService();

        if(userEntityDto != null)
            System.out.println(userEntityDto.getUsername());
        if(userEntityDto == null)
            System.out.println("userEntityDto is null");
        model.addAttribute("courses", courses);
        return "mainPage";
    }
    @GetMapping("/courses/{courseId}")
    public String detailPage(@PathVariable Long courseId,
                             Model model) throws Exception {
        Course course = viewService.findCourse(courseId);
        model.addAttribute("course", course);
        log.info("Detail Page Main controller method is working");
        System.out.println(course.getAttachments().isEmpty());
        return "detailPage";
    }


}
