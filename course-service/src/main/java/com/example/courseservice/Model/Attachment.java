package com.example.courseservice.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType;
    private String creator;
    private String downloadUrl;
    private String viewUrl;
    private String timestamp;


    // that a class property must be mapped to a larger object in the database.
    @Lob
    @Column(columnDefinition = "oid")
    @JsonIgnore
    private byte[] data;

    public Attachment(String fileName, String fileType, byte[] data, String downloadUrl ,String viewUrl) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.downloadUrl=downloadUrl;
        this.viewUrl= viewUrl;
    }
    @ToString.Exclude
    @JsonBackReference // to eliminate recursion
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @ToString.Exclude
    @JsonBackReference // to eliminate recursion
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "homework_id", referencedColumnName = "id")
    private Homework homework;

}