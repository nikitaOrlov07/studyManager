package com.example.courseservice.Repository;

import com.example.courseservice.Model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository  extends JpaRepository<Attachment , Long> {

}
