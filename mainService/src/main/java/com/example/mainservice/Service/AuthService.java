package com.example.mainservice.Service;

import com.example.mainservice.Dto.LoginRequest;
import com.example.mainservice.Model.UserToken;

public interface AuthService {
    String login(LoginRequest loginRequest);

    UserToken getUserTokenByIpAdressAndUsername(String ipAddress, String sessionUser);

    void deleteUserToken(String username, String remoteAddr);
}
