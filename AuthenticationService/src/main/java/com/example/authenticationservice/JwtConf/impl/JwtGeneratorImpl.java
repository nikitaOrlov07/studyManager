package com.example.authenticationservice.JwtConf.impl;

import com.example.authenticationservice.Dto.UserEntityDto;
import com.example.authenticationservice.JwtConf.JwtGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtGeneratorImpl implements JwtGenerator {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${app.jwttoken.message}")
    private String message;
    @Value("${jwt.expiration}")
    private Long expiration;
    @Override
    public Map<String, Object> generateToken(UserEntityDto user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        String jwtToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .claim("age", user.getAge())
                .claim("town", user.getTown())
                .claim("phoneNumber", user.getPhoneNumber())
                .claim("role", user.getRole())
                .claim("createdCoursesIds", user.getCreatedCoursesIds())
                .claim("participatingCourses", user.getParticipatingCourses())
                .claim("chatsIds", user.getChatsIds())
                .claim("completedHomeworksIds", user.getCompletedHomeworksIds())
                .claim("createdHomeworksIds", user.getCreatedHomeworksIds())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        Map<String, Object> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        jwtTokenGen.put("message", message);
        return jwtTokenGen;
    }
}
