package com.example.courseservice.Config.JwtTokenConf;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;

    @PostConstruct
    public void init() {
        log.info("JWT Secret length: " + (jwtSecret != null ? jwtSecret.length() : "null"));
        log.info("JWT Expiration: " + jwtExpirationInMs);
    }

    public String generateToken(Authentication authentication) {
        log.info("Generating token for user: " + authentication.getName());
        log.info("JWT Secret length at token generation: " + (jwtSecret != null ? jwtSecret.length() : "null"));

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            // log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            // log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            // log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            // log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            // log.error("JWT claims string is empty.");
        }
        return false;
    }
}