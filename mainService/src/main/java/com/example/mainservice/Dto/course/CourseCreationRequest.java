package com.example.mainservice.Dto.course;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreationRequest {
    @NotEmpty(message = "You must enter a course title")
    private String title;
    @NotEmpty(message = "You must enter a course description")
    private String description;
    private Double price;
    @NotEmpty(message = "You must enter a course language")
    private String language;
    private String  tags; // will be listed comma-separated, and in the course service we will do string parsing (convert to List<String>)
    private String format;
    private String endDate;


    // Security
    @NotEmpty(message = "You must enter a course type")
    private String courseType;
    private String coursePassword;

    // Author
    private String  author;
}
