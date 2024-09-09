package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.LoginRequest;
import com.example.mainservice.Entity.UserToken;
import com.example.mainservice.Repository.UserTokenRepository;
import com.example.mainservice.Service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceimpl implements AuthService {

    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private UserTokenRepository userTokenRepository;

    @Override
    public String login(LoginRequest loginRequest) {
        log.info("Service login method is working");
        try {
            ResponseEntity<String> response = webClientBuilder.build()
                    .post()
                    .uri("http://authentication-service/authentication/login")
                    .body(BodyInserters.fromObject(loginRequest))
                    .retrieve().toEntity(String.class)
                    .block();

            if (response.getBody() == null || response.getBody().isEmpty()) {
                log.error("response body is empty or null");
                throw new RuntimeException("Authentication failed: Empty response");
            }
            log.info("response body is not empty");

            if (response.getBody().equalsIgnoreCase("Wrong username or password")) {
                return "Wrong data";
            }
            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(response.getBody());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse JSON response", e);
                throw new RuntimeException("Failed to parse authentication response", e);
            }

            String token = jsonNode.get("token").asText();

            // Check if token already exists for this user and device type
            UserToken userToken = userTokenRepository.findByUsernameAndDeviceTypeAndIpAddress(
                    loginRequest.getUsername(), loginRequest.getDeviceType(), loginRequest.getIpAddress());

            if (userToken != null) {
                // Update existing token
                userToken.setToken(token);
                userToken.setExpirationTime(LocalDateTime.now().plusHours(1));
                userToken.setSessionCount(userToken.getSessionCount() + 1);
                userToken.setLastLoginTime(LocalDateTime.now());
            } else {
                // Create new token entry
                userToken = UserToken.builder()
                        .token(token)
                        .username(loginRequest.getUsername())
                        .expirationTime(LocalDateTime.now().plusHours(1))
                        .ipAddress(loginRequest.getIpAddress())
                        .deviceType(loginRequest.getDeviceType())
                        .sessionCount(1)
                        .lastLoginTime(LocalDateTime.now())
                        .build();
            }
            userTokenRepository.save(userToken);
            log.info("Token updated for user: {} on device: {}. Active sessions: {}",
                    loginRequest.getUsername(), loginRequest.getDeviceType(), userToken.getSessionCount());

            return token;
        }
    catch (WebClientResponseException.Conflict e) {
        log.error("Authentication failed: Conflict");
        return "Wrong data";
    } catch (WebClientResponseException e) {
        log.error("Authentication failed: " + e.getStatusCode(), e);
        throw new RuntimeException("Authentication failed: " + e.getStatusCode());
    }
    }

    @Override
    public UserToken getUserTokenByIpAdressAndUsername(String ipAddress, String sessionUser) {
        return userTokenRepository.findUserTokenByUsernameAndIpAddress(sessionUser,ipAddress);
    }

    @Override
    public void deleteUserToken(String username, String remoteAddr) {
        UserToken userToken = getUserTokenByIpAdressAndUsername(remoteAddr, username);
        if (userToken != null) {
            userToken.setSessionCount(userToken.getSessionCount() - 1);
            if (userToken.getSessionCount() <= 0) {
                userTokenRepository.delete(userToken);
                log.info("User token deleted for user: {} from IP: {}", username, remoteAddr);
            } else {
                userTokenRepository.save(userToken);
                log.info("Session count decremented for user: {} from IP: {}. Remaining sessions: {}",
                        username, remoteAddr, userToken.getSessionCount());
            }
        } else {
            log.warn("No token found for username: {} and IP: {}", username, remoteAddr);
        }
    }
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<UserToken> expiredTokens = userTokenRepository.findByExpirationTimeBefore(now);
        userTokenRepository.deleteAll(expiredTokens);
        log.info("Cleaned up {} expired tokens", expiredTokens.size());
    }


}
