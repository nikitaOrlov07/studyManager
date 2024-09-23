package com.example.mainservice.Service.impl;

import com.example.mainservice.Model.UserToken;
import com.example.mainservice.Repository.UserTokenRepository;
import com.example.mainservice.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private UserTokenRepository tokenRepository;

    @Override
    public String findTokenByUsername(String username) throws Exception {
        UserToken userToken = tokenRepository.findUserTokenByUsername(username).orElseThrow(() -> new Exception("User not found"));
        return userToken.getToken();
    }
}
