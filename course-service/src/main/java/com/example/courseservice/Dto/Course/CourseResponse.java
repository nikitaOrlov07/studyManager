package com.example.courseservice.Dto.Course;

import com.example.courseservice.Model.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String language;
    private String tags;
    private String format;
    private String creationDate;
    private String endDate;
    private List<Attachment> attachments = new ArrayList<>();
    private List<Long> involvedUserIds = new ArrayList<>(); // for users
    private Long chatId;
    // Security
    private String courseType;
    private String coursePassword;

    // Author
    private Long authorId;
}
