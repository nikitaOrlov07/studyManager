package com.example.userservice.Repositories;

import com.example.userservice.Model.UserEntity;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Observed
public interface UserEntityRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findFirstByUsername(String username);
    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);
}
