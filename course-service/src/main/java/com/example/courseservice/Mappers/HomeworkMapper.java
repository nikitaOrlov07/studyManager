package com.example.courseservice.Mappers;

import com.example.courseservice.Dto.Homework.HomeworkRequest;
import com.example.courseservice.Model.Homework;

public class HomeworkMapper {
    public static Homework homeworkRequestToHomework(HomeworkRequest homeworkRequest)
    {
        Homework homework = Homework.builder()
                .title(homeworkRequest.getTitle())
                .description(homeworkRequest.getDescription())
                .startDate(homeworkRequest.getStartDate())
                .endDate(homeworkRequest.getEndDate())
    //          .userEntitiesId(homeworkRequest.getUserEntitiesId())
                .build();
        return  homework;
    }
}
