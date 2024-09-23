package com.example.courseservice.Mappers;

import com.example.courseservice.Dto.Course.CourseResponse;
import com.example.courseservice.Model.Course;

public class CourseMapper {
    public static CourseResponse getCourseResponseFromCourse(Course course)
    {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .language(course.getLanguage())
                .tags(String.join(", ", course.getTags()))
                .format(course.getFormat())
                .creationDate(course.getCreationDate())
                .endDate(course.getEndDate())
                .attachments(course.getAttachments())
                .involvedUserIds(course.getInvolvedUserIds())
                .chatId(course.getChatId())
                .courseType(course.getCourseType())
                .courseIdentifier(course.getCourseKey())
                .coursePassword(course.getCoursePassword())
                .authorId(course.getAuthorId())
                .build();
    }
}
