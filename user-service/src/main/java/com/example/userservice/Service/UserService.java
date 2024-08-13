package com.example.userservice.Service;

import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Model.UserEntity;

public interface UserService {
    UserEntityDto findUserById(Long userId);

    UserEntity findUserByUsername(String sessionUser);

    void save(UserEntityDto userEntityDto);

    UserEntity findByEmail(String email);
}
