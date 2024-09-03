package com.example.mainservice.Repository;

import com.example.mainservice.Entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByToken(String token);
    Optional<UserToken> findByUsername(String username);
    void deleteByUsername(String username);
}