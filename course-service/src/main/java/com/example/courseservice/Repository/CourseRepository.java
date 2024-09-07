package com.example.courseservice.Repository;

import com.example.courseservice.Model.Course;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Observed
public interface CourseRepository extends JpaRepository<Course,Long> {

    Course findByTitle(String title);
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Course> searchCourses(String query);
    Course findCourseByCourseKey(String courseKey);
}
