package com.example.courseservice.Repository;

import com.example.courseservice.Model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HomeworkRepository extends JpaRepository<Homework,Long> {
    // Query to find all Homework where the userEntitiesId list contains the given studentId
    @Query("SELECT h FROM Homework h WHERE :studentId MEMBER OF h.userEntitiesId")
    List<Homework> findAllByStudentId(@Param("studentId") Long studentId);

}
