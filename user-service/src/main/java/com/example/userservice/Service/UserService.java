package com.example.userservice.Service;

import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Model.UserEntity;

import java.util.List;

public interface UserService {
    UserEntityDto findUserById(Long userId);

    UserEntity findUserByUsername(String sessionUser);

    void save(UserEntityDto userEntityDto);

    UserEntity findByEmail(String email);

    List<UserEntityDto> findUsersByIds(List<Long> usersIds);

    Boolean courseAction(String action, Long courseId, String username);

    void updateCreatedItems(String action, String type, Long id,Long userId);

    void assignHomeworks(List<Long> userEntities, Long homeworkId, String type);

    boolean addChatIds(List<Long> usersIds,Long chatId, String operationType);
}
