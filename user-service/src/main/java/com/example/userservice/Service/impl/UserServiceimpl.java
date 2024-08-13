package com.example.userservice.Service.impl;

import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Mapper.UserEntityMapper;
import com.example.userservice.Model.UserEntity;
import com.example.userservice.Repositories.UserEntityRepository;
import com.example.userservice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceimpl implements UserService {

    @Autowired
    UserEntityRepository userRepository;


    @Override
    public UserEntityDto findUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserEntityMapper.userEntityToUserEntityDto(userEntity);
    }

    @Override
    public UserEntity findUserByUsername(String sessionUser) {
        UserEntity userEntity = userRepository.findByUsername(sessionUser);
        return userEntity;
    }

    @Override
    public void save(UserEntityDto userEntityDto) {
        UserEntity userEntity = UserEntityMapper.userEntityDtoToUserEntity(userEntityDto);
        userRepository.save(userEntity);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
