package com.example.courseservice.Repository;

import com.example.courseservice.Dto.Homework.Enums.HomeworkStatus;
import com.example.courseservice.Model.Course;
import com.example.courseservice.Model.Homework;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Observed
public interface HomeworkRepository extends JpaRepository<Homework,Long>, JpaSpecificationExecutor<Homework> {
    // Query to find all Homework where the userEntitiesId list contains the given studentId



    List<Homework> findAllByUserEntitiesIdContaining(Long studentId);

    // Find homeworks for students by status and studentId
    List<Homework> findBySubmitHomeworkUserEntitiesIdContaining(Long userId);
    List<Homework> findByGradedHomeworkUserEntitiesIdContaining(Long userId);
    List<Homework> findByRejectedHomeworkUserEntitiesIdContaining(Long userId);
    List<Homework> findByAcceptedHomeworkEntitiesIdContaining(Long userId);

    // Find homeworks for  teacher by their id and homework status
    List<Homework> findByAuthorIdAndStatusAndCourse(Long authorId, HomeworkStatus status, Course course); // if status value is null -> Spring will search only by authorId

    Homework findByTitle(String title);
}
