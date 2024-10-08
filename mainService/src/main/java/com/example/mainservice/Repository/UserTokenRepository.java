package com.example.mainservice.Repository;

import com.example.mainservice.Model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    List<UserToken> findUserTokensByUsernameAndIpAddress(String username, String ipAddress);

    UserToken findByUsernameAndDeviceTypeAndIpAddress(String username, String deviceType, String ipAddress);
    List<UserToken> findByExpirationTimeBefore(LocalDateTime localDateTime);
    List<UserToken> findUserTokensByUsername(String username);}