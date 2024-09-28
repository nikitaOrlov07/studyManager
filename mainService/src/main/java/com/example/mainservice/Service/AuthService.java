package com.example.mainservice.Service;

import com.example.mainservice.Dto.LoginRequest;
import com.example.mainservice.Model.UserToken;

public interface AuthService {
    String login(LoginRequest loginRequest);

    UserToken getUserTokenByIpAddressAndUsername(String ipAddress, String sessionUser);
    void deleteSpecificUserToken(String username, String ipAddress, String deviceType);
}
