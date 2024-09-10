package com.example.mainservice.Service;

import com.example.mainservice.Dto.User.RegistrationDto;
import com.example.mainservice.Dto.User.UserEntityDto;

import java.util.List;

public interface UserService {
    String saveUser(RegistrationDto registrationDto);

    UserEntityDto findUserByUsername(String sessionUser);

    List<UserEntityDto> findUsersByIds(List<Long> involvedUserIds);
}
