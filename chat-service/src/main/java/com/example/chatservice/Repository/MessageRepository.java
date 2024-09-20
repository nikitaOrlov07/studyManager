package com.example.chatservice.Repository;

import com.example.chatservice.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {
}
