package com.example.userservice.Mapper;

import com.example.userservice.Dto.Registration.RegistrationDto;
import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Model.UserEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class UserEntityMapper {
    public static UserEntityDto userEntityToUserEntityDto(UserEntity userEntity) {
        return UserEntityDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .age(userEntity.getAge())
                .town(userEntity.getTown())
                .phoneNumber(userEntity.getPhoneNumber())
                .role(userEntity.getRole())
                .createdCoursesIds(userEntity.getCreatedCoursesIds())
                .participatingCourses(userEntity.getParticipatingCourses())
                .chatsIds(userEntity.getChatsIds())
                .completedHomeworksIds(userEntity.getCompletedHomeworksIds())
                .createdHomeworksIds(userEntity.getCreatedHomeworksIds())
                .build();
    }
    
    public static UserEntity userEntityDtoToUserEntity(UserEntityDto userEntityDto) {
        return UserEntity.builder()
                .id(userEntityDto.getId())
                .username(userEntityDto.getUsername())
                .email(userEntityDto.getEmail())
                .password(userEntityDto.getPassword())
                .age(userEntityDto.getAge())
                .town(userEntityDto.getTown())
                .phoneNumber(userEntityDto.getPhoneNumber())
                .role(userEntityDto.getRole())
                .createdCoursesIds(userEntityDto.getCreatedCoursesIds())
                .participatingCourses(userEntityDto.getParticipatingCourses())
                .chatsIds(userEntityDto.getChatsIds())
                .completedHomeworksIds(userEntityDto.getCompletedHomeworksIds())
                .createdHomeworksIds(userEntityDto.getCreatedHomeworksIds())
                .build();
    }
    public static UserEntityDto registrationDtoToUserEntityDto(RegistrationDto registrationDto) {
        return UserEntityDto.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .town(registrationDto.getTown())
                .phoneNumber(registrationDto.getPhoneNumber())
                .age(registrationDto.getAge())
                .role("USER")
                .createdCoursesIds(new ArrayList<>())
                .participatingCourses(new ArrayList<>())
                .chatsIds(new ArrayList<>())
                .completedHomeworksIds(new ArrayList<>())
                .createdHomeworksIds(new ArrayList<>())
                .build();
    }

}

