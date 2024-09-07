package com.example.courseservice.Dto.Course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseCreationRequest {
    private String title;
    private String description;
    private Double price;
    private String language;
    private String tags;
    private String format;
    private String endDate;

    // Security
    private String courseType;
    private String coursePassword;

    // Author
    private String  author;
}
