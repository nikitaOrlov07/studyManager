package com.example.mainservice.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long id ;
    private String title;
    private  String description;
    private Double price;
    private String language;
    private List<String> tags;
    private String format;

    // Dates
    private String creationDate;
    private String endDate;


    // can be some file
    private List<Attachment> attachments = new ArrayList<>() ;

    // users
    private  List<UserEntity> userEntities = new ArrayList<>();    // confirmed users
    private  List<UserEntity> instructor = new ArrayList<>();    // course authors
    private  List<UserEntity> userRequests = new ArrayList<>();    // user

    private Long chatId;
}
