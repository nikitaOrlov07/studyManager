package com.example.mainservice.Service.impl;

import com.example.mainservice.Dto.UserEntityDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtDecoderImpl {

    @Value("${jwt.secret}") // this annotation does not work with static fields
    private  String secret;

    public UserEntityDto decodeToken(String token) {
        // Token decryption
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token);

        Claims claims = claimsJws.getBody();

        // Converting decrypted data into a UserEntityDto object
        UserEntityDto user = new UserEntityDto();
        user.setId(Long.valueOf(claims.get("id").toString()));
        user.setUsername(claims.getSubject());
        System.out.println();
        user.setEmail(claims.get("email").toString());
        user.setAge(claims.get("age").toString());
        user.setTown(claims.get("town").toString());
        user.setPhoneNumber(Long.valueOf(claims.get("phoneNumber").toString()));
        user.setRole(claims.get("role").toString());

        // Converting fields that are lists
        user.setCreatedCoursesIds((List<Long>) claims.get("createdCoursesIds"));
        user.setParticipatingCourses((List<Long>) claims.get("participatingCourses"));
        user.setChatsIds((List<Long>) claims.get("chatsIds"));
        user.setCompletedHomeworksIds((List<Long>) claims.get("completedHomeworksIds"));
        user.setCreatedHomeworksIds((List<Long>) claims.get("createdHomeworksIds"));

        return user;
    }
}