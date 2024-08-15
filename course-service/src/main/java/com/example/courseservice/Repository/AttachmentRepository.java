package com.example.courseservice.Repository;

import com.example.courseservice.Model.Attachment;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Observed
public interface AttachmentRepository  extends JpaRepository<Attachment , Long> {

}
