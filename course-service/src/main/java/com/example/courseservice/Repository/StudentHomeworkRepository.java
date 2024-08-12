package com.example.courseservice.Repository;

import com.example.courseservice.Model.StudentHomeworkAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentHomeworkRepository extends JpaRepository<StudentHomeworkAttachment, Long> {
}
