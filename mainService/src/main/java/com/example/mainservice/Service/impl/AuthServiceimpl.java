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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
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
         UserToken existingToken = userTokenRepository.findByUsernameAndDeviceTypeAndIpAddress(loginRequest.getUsername(), loginRequest.getDeviceType(), loginRequest.getIpAddress());

        if (existingToken != null) {
            // Update existing token
            existingToken.setToken(token);
            existingToken.setExpirationTime(LocalDateTime.now().plusHours(1));
            existingToken.setIpAddress(loginRequest.getIpAddress()); // Assume this is added to LoginRequest
            userTokenRepository.save(existingToken);
            log.info("Updated token for user: {} on device: {}", loginRequest.getUsername(), loginRequest.getDeviceType());
        } else {
            // Create new token entry
            UserToken userToken = UserToken.builder()
                    .token(token)
                    .username(loginRequest.getUsername())
                    .expirationTime(LocalDateTime.now().plusHours(1))
                    .ipAddress(loginRequest.getIpAddress())
                    .deviceType(loginRequest.getDeviceType())
                    .build();
            userTokenRepository.save(userToken);
            log.info("New token saved for user: {} on device: {}", loginRequest.getUsername(), loginRequest.getDeviceType());
        }

        return token;
    }

    @Override
    public UserToken getUserTokenByIpAdressAndUsername(String ipAddress, String sessionUser) {
        return userTokenRepository.findUserTokenByUsernameAndIpAddress(sessionUser,ipAddress);
    }

    @Override
    public void deleteUserToken(String username, String remoteAddr) {
      UserToken userToken = getUserTokenByIpAdressAndUsername(username, remoteAddr);
      userTokenRepository.delete(userToken);
    }


}
