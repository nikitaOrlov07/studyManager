package com.example.mainservice.Service;

import com.example.mainservice.Dto.User.RegistrationDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    String saveUser(RegistrationDto registrationDto);

    UserEntityDto findUserByUsername(String sessionUser);

    List<UserEntityDto> findUsersByIds(List<Long> involvedUserIds);

    List<UserEntityDto> findUsersByUsernames(String query);
}
