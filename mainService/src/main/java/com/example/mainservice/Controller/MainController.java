package com.example.mainservice.Controller;

import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Dto.course.Course;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.AuthService;
import com.example.mainservice.Service.CourseService;
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
    final private CourseService courseService;


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
           log.info("User role is "+userEntityDto.getRole());
        }
        model.addAttribute("courses", courses);
        return "mainPage";
    }
    @GetMapping("/courses/{courseId}")
    public String detailPage(@PathVariable Long courseId,
                             Model model) throws Exception
    {
        // Get course information
        Course course = courseService.findCourse(courseId);
        log.info("CourseId is: " + course.getId());
        model.addAttribute("course", course);
        // Find involved users
        List<UserEntityDto> involvedUsers = viewService.getInvolvedUsers(course.getInvolvedUserIds());

        log.info("Involved users: "+ involvedUsers.size());

        model.addAttribute("involvedUsers",involvedUsers);

        log.info("Course type: " + course.getCourseType());
        log.info("Course authorId is :" + course.getAuthorId());


        // Get current user information
        UserEntityDto userEntityDto = null;
        if(SecurityUtil.getSessionUser() != null && !SecurityUtil.getSessionUser().isEmpty())
        {
            userEntityDto = userService.findUserByUsername(SecurityUtil.getSessionUser());
            log.info("Current user: "+ userEntityDto.getUsername());
        }


        model.addAttribute("user",userEntityDto);
        log.info("Detail Page Main controller method is working");

        // Find course author information
        UserEntityDto courseAuthor =  userService.findUserById(course.getAuthorId());

        model.addAttribute("courseAuthor",courseAuthor);
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
        if(searchBar != null)
        {
            log.info("Search query is :" + searchBar);
            log.info("Search type is :" + type);
        }
        List<Course> coursesList = viewService.searchCourses(type, searchBar);
        coursesList.forEach(System.out::println);
        model.addAttribute("courses", coursesList);
        return "mainPage";
    }
    // Cabinet Page
    @GetMapping("/cabinet")
    public String getCabinetPage(@RequestParam(required = false, value = "createdCourseTitle") String createdCourseTitle,
                                 @RequestParam(required = false, value = "participatingCourseTitle") String participatingCourseTitle,
                                 @RequestParam(required = false, value = "userTitle") String userTitle,
                                 Model model) {
        UserEntityDto currentUserEntity = userService.findUserByUsername(SecurityUtil.getSessionUser());
        if (currentUserEntity == null) {
            return "redirect:/home?notAllowed";
        }

        if (currentUserEntity.getRole().equals("ADMIN")) {
            List<UserEntityDto> foundUsers = userService.findUsersByUsernames(userTitle);
            model.addAttribute("foundUsers", foundUsers);
        }

        List<Course> createdCourses = null;
        if (!currentUserEntity.getCreatedCoursesIds().isEmpty()) {
            createdCourses = courseService.searchCreatedCoursesByTitleAndAuthor(createdCourseTitle, currentUserEntity.getId());
            model.addAttribute("createdCourses", createdCourses);
        }

        List<Course> participatingCourses = null;
        if (!currentUserEntity.getParticipatingCourses().isEmpty()) {
            participatingCourses = courseService.searchParticipatedCoursesByTitleAndUserId(participatingCourseTitle, currentUserEntity.getId());
            model.addAttribute("participatedCourses", participatingCourses);
        }

        model.addAttribute("currentUser", currentUserEntity);
        return "cabinet";
    }
}
