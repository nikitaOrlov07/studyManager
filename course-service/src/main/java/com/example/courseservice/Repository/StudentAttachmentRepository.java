package com.example.courseservice.Repository;

import com.example.courseservice.Model.Homework;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Observed
public interface StudentAttachmentRepository extends JpaRepository<StudentHomeworkAttachment, Long> {
    StudentHomeworkAttachment findByStudentIdAndHomework(Long studentId, Homework homework);
    List<StudentHomeworkAttachment> findAllByHomework(Homework homework);
}
