package com.example.mainservice.Service.impl;

import com.example.mainservice.Model.UserToken;
import com.example.mainservice.Repository.UserTokenRepository;
import com.example.mainservice.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private UserTokenRepository tokenRepository;

    @Override
    public String findTokenByUsername(String username) throws Exception {
        List<UserToken> userTokens = tokenRepository.findUserTokensByUsername(username);
        if (userTokens.isEmpty()) {
            throw new Exception("User not found");
        }

        // Sort tokens by time of last entry (in descending order)
        userTokens.sort((a, b) -> b.getLastLoginTime().compareTo(a.getLastLoginTime()));

        // Return the token of the most recent session
        return userTokens.get(0).getToken();
    }

}
