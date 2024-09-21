package com.example.chatservice.conf.JwtConfiguration;

import com.example.chatservice.Dto.UserEntityDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public UserEntityDto decodeToken(String token) {
        try {
            log.info("Started to decode token");
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            UserEntityDto user = new UserEntityDto();
            user.setId(Long.valueOf(claims.get("id").toString()));
            user.setUsername(claims.getSubject());
            user.setEmail(claims.get("email").toString());
            user.setAge(claims.get("age").toString());
            user.setTown(claims.get("town").toString());
            user.setPhoneNumber(claims.get("phoneNumber").toString());
            user.setRole(claims.get("role").toString());

            user.setCreatedCoursesIds((List<Long>) claims.get("createdCoursesIds"));
            user.setParticipatingCourses((List<Long>) claims.get("participatingCourses"));
            user.setChatsIds((List<Long>) claims.get("chatsIds"));
            user.setCompletedHomeworksIds((List<Long>) claims.get("completedHomeworksIds"));
            user.setCreatedHomeworksIds((List<Long>) claims.get("createdHomeworksIds"));

            return user;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature");
            throw new RuntimeException("Invalid JWT signature");
        }
    }

    public boolean validateToken(String token) {
        try {
            log.info("Started to validate token");
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            log.info("Token is valid");
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        log.info("Started to extract username");
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }
}