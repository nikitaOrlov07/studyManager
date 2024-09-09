package com.example.courseservice.Repository;

import com.example.courseservice.Model.Homework;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Observed
public interface StudentHomeworkRepository extends JpaRepository<StudentHomeworkAttachment, Long> {
    StudentHomeworkAttachment findByStudentIdAndHomework(Long studentId, Homework homework);
}
