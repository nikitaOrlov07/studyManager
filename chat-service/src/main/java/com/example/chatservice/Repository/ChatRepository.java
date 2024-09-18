package com.example.chatservice.Repository;

import com.example.chatservice.Model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {

    List<Chat> findByParticipantsIdsContains(Long userId);

}
