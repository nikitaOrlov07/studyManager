package com.example.mainservice.config.JwtTokenConf;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String authHeader = httpRequest.getHeader("Authorization");

        if ("OPTIONS".equals(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
        } else {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ServletException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();
                request.setAttribute("claims", claims);
                request.setAttribute("blog", request.getParameter("id"));
            } catch (Exception e) {
                throw new ServletException("Invalid token");
            }
        }

        filterChain.doFilter(request, response);
    }
}