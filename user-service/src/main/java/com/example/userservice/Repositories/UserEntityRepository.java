package com.example.userservice.Repositories;

import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Model.UserEntity;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Observed
public interface UserEntityRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findFirstByUsername(String username);
    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);
    List<UserEntity> findByIdIn(List<Long> ids);

    @Query("SELECT u FROM UserEntity u WHERE u.username LIKE %:query%")
    List<UserEntity> searchUserByQuery(@Param("query") String query);

    //// Delete homework
    // Find homework in "createdHomeworks" list
    UserEntity findByCreatedHomeworksIdsContaining(Long homeworkId);
    //  Find homework in "homeworksIds" list
    List<UserEntity> findAllByHomeworksIdsContaining(Long homeworkId);
    //  Find homework in "completingHomeworks" list
    List<UserEntity> findAllByCompletedHomeworksIdsContaining(Long homeworkId);

    // Method for course deleting
    List<UserEntity> findAllByParticipatingCoursesContaining(Long courseId);
}
