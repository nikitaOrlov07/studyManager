package com.example.userservice.Mapper;

import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Model.UserEntity;
import lombok.extern.slf4j.Slf4j;

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
}

