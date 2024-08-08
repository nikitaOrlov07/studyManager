package com.example.mainservice.Model;

import java.util.List;

public class Course {
    private Long id ;
    private String title;
    private  String description;
    private String creationDate;

    // can be some file
    private List<Attachment> attachment;

    // users
    private  List<User> users;
}
