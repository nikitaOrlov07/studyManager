package com.example.courseservice.Dto.Homework;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkRequest {
    // basic information

    private String title;
    private String description;
    private String startDate;
    private String endDate;

    // who should do
    private List<Long> userEntitiesId = new ArrayList<Long>();

    // homework Attachment
    private List<MultipartFile> files = new ArrayList<>();    // what to do (teacher make thsi attachments)

    private Long courseId;
    private Long authorId;

}
