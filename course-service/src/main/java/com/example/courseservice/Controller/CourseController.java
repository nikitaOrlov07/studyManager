package com.example.courseservice.Controller;

import com.example.courseservice.Config.exceptions.CourseCreationException;
import com.example.courseservice.Config.exceptions.ResourceNotFoundException;
import com.example.courseservice.Dto.Course.CourseCreationRequest;
import com.example.courseservice.Dto.Course.CourseResponse;
import com.example.courseservice.Mappers.CourseMapper;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<CourseResponse> getCourseList()
    {
        log.info("getCourseList Controller method is working");
        return courseService.getAllCourses().stream()
                .map(CourseMapper::getCourseResponseFromCourse)
                .collect(Collectors.toList());
    }
    // For Course Creation working
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createCourse(@RequestPart("courseData") CourseCreationRequest request,
                               @RequestPart(value = "files", required = false) List<MultipartFile> files)
    {

        log.info("createCourse Controller method is working");
        Course course;
        try{course = courseService.createCourse(request);}
        catch (CourseCreationException e)
        {
            return e.getMessage();
        }
        // file saving
        if(files == null || files.isEmpty())
            log.info("File list is empty");
        else
            log.info("Files size :" + files.size());
        if(files != null) {
            for (MultipartFile file : files ) {
                if (!file.isEmpty()) {
                    try {
                        courseService.uploadFile(file, course.getId(),request.getAuthor());
                    } catch (Exception e) {
                        log.error("Error uploading file", e);
                    }
                }
            }
        }
        return "course was created successfully";
    }

    // For Detail page working
    @GetMapping("/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    public CourseResponse courseDetail(@PathVariable Long courseId) {
        log.info("findCourse Controller method is working");
        CourseResponse courseResponse = courseService.getCourseResponse(courseId);
        if (courseResponse == null) {
            throw new ResourceNotFoundException("Course with ID " + courseId + " not found.");
        }
        return courseResponse;
    }
    //  Search logic
    @GetMapping ("/search")
    public List<CourseResponse> searchCourse(@RequestParam String type,
                                             @RequestParam String searchBar)
    {
        log.info("Course controller \"search method\" is working \n");
        log.info("Type: "+type + '\n' + "Query: "+ searchBar);
        return courseService.findCourses(type,searchBar);
    }
    // Join Course logic
    @GetMapping("/action/{action}")
    public Boolean actionCourse(@PathVariable("action") String action ,
                              @RequestParam("courseId") Long courseId,
                              @RequestParam("username") String username)
    {
        log.info("CourseServive \"actionCourse\" controller method is working");
        return courseService.actionCourse(courseId,username,action);
    }

    //// Find courses for CabinetPage
    // Created
    @GetMapping("/search/{type}")
    public List<CourseResponse> searchCourses(@PathVariable String type,
                                              @RequestParam(value = "courseTitle") String courseTitle ,
                                              @RequestParam(value = "userId") Long userId)
    {
        log.info("CourseServive \"searchUsersCourses\" controller method is working with parameters: courseTitle: {} and authorId: {}",courseTitle,userId);
        List<CourseResponse> courseResponses = new ArrayList<>();
        if(type.equals("created"))
        {
            courseResponses = courseService.searchCreatedCourses(courseTitle,userId);
        }
        if(type.equals("participating"))
        {
            courseResponses = courseService.searchParticipatedCourses(courseTitle,userId);
        }
        return courseResponses;
    }
    // Delete Course logic
    @PostMapping("/delete")
    public Boolean deleteCourse(@RequestParam Long courseId)
    {
        log.info("Course service \"deleteCourse\" controller method is working for course with id: " + courseId);

            Boolean result = courseService.deleteCourse(courseId);
            if(true == result)
                log.info("Course was successfully deleted");
            else
                log.error("Error occurred deleting course");

            return  result;

        /*
        catch (Exception e) {
            log.error("Exception: {} occurred while deleting course with id {}", e, courseId);
            return false;
        }
        */
    }



}
