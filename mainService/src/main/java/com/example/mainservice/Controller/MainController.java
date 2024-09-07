package com.example.mainservice.Controller;

import com.example.mainservice.Dto.UserEntityDto;
import com.example.mainservice.Model.Course;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.AuthService;
import com.example.mainservice.Service.UserService;
import com.example.mainservice.Service.ViewService;
import com.example.mainservice.Service.impl.JwtDecoderImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    final private ViewService viewService;
    final private UserService userService;
    final private AuthService authService;
    final private JwtDecoderImpl jwtDecoder;


    @GetMapping("/home")
    public String mainPage(Model model, HttpServletRequest request)
    {
        // Get client IP address
        String ipAddress = request.getRemoteAddr();

        log.info("mainPage controller is working");
        List<Course> courses = viewService.getCourses();
        if(SecurityUtil.getSessionUser() != null && !SecurityUtil.getSessionUser().isEmpty()) {
           UserEntityDto userEntityDto = jwtDecoder.decodeToken(authService.getUserTokenByIpAdressAndUsername(ipAddress,SecurityUtil.getSessionUser()).getToken());
           log.info("User: "+ userEntityDto.getUsername()+ " was logged in");
        }
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

    // Search logic for courses by logic or by course id
    @GetMapping("/courses/findCourses")
    public String findCourses(@RequestParam(required = false) String type,
                              @RequestParam(required = false) String searchBar,
                              Model model) {
        if (searchBar == null || searchBar.trim().isEmpty()) {
            return "redirect:/home";
        }

        List<Course> coursesList = viewService.searchCourses(type, searchBar);
        coursesList.forEach(System.out::println);
        model.addAttribute("courses", coursesList);
        return "mainPage";
    }


}
