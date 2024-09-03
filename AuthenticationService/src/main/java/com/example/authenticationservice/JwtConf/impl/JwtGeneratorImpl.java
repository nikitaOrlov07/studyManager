package com.example.authenticationservice.JwtConf.impl;

import com.example.authenticationservice.Dto.UserDto;
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
    @Override
    public Map<String, String> generateToken(UserDto user) {
        String jwtToken="";
        jwtToken = Jwts.builder()
                .setSubject(user.getUsername()) // Sets the user as the subject of the token
                .setIssuedAt(new Date())        // Sets the token creation time
                .signWith(SignatureAlgorithm.HS256, secret) // Signs the token using the HS256 algorithm and the secret key
                .compact();   // Completes the token creation process and returns it as a string.
        Map<String, String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        jwtTokenGen.put("message", message);
        return jwtTokenGen;
    }
}
