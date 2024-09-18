package com.example.chatservice.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<Long> participantsIds = new ArrayList<Long>();

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "chat",fetch = FetchType.EAGER , cascade = CascadeType.ALL, orphanRemoval = true) // orphanRemoval child entities should be automatically deleted if they are no longer associated with the parent entity.
    private List<Message> messages = new ArrayList<>();

    private Long courseId; // will be not null if this is course chat

}
