package com.example.mainservice.Dto.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
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
    private Long  authorId;

    // Course ID key
    private String courseIdentifier;
}

