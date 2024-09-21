package com.example.mainservice.Repository;

import com.example.mainservice.Entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    UserToken findUserTokenByUsernameAndIpAddress(String username, String ipAddress);

    UserToken findByUsernameAndDeviceTypeAndIpAddress(String username, String deviceType, String ipAddress);
    List<UserToken> findByExpirationTimeBefore(LocalDateTime localDateTime);
    Optional<UserToken> findUserTokenByUsername(String username);
}