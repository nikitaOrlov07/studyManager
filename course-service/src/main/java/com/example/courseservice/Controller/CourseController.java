package com.example.courseservice.Controller;

import com.example.courseservice.Config.ResourceNotFoundException;
import com.example.courseservice.Dto.Course.CourseRequest;
import com.example.courseservice.Dto.Course.CourseResponse;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    // For Main Page working
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Course> getCourseList()
    {
        log.info("getCourseList Controller method is working");
        return courseService.getAllCourses();
    }
    // For Course Creation working
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createCourse(@ModelAttribute CourseRequest request)
    {

        log.info("createCourse Controller method is working");

        Course course = courseService.createCourse(request);
        // file saving
        if(request.getFiles() != null) {
            for (MultipartFile file : request.getFiles()) {
                if (!file.isEmpty()) {
                    try {
                        courseService.uploadFile(file, course.getId());
                    } catch (Exception e) {
                        log.error("Error uploading file", e);
                    }
                }
            }
        }
        return "course was created successfully";
    }
    // update logic
    /*
    
     */
    // For Detail page working
    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public CourseResponse findCourse(@PathVariable Long courseId) {
        log.info("findCourse Controller method is working");
        CourseResponse courseResponse = courseService.getCourse(courseId);
        if (courseResponse == null) {
            throw new ResourceNotFoundException("Course with ID " + courseId + " not found.");
        }
        return courseResponse;
    }
    // Delete Course logic
    @PostMapping("/delete/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCourse(@PathVariable Long courseId) {
        log.info("deleteCourse Controller method is working");
        // HTTP request to userService and delete this course  from users courses
        /*

         */
        courseService.deleteCourse(courseId);
      return "course was successfully deleted";
    }


}
