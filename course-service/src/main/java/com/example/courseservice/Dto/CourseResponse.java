package com.example.courseservice.Dto;

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
    private String title;
    private String description;
    private List<Attachment> attachments = new ArrayList<>();
    private List<Long> involvedUserIds = new ArrayList<>(); // for users
    private Long chatId;
}
