package com.example.courseservice.Service;

import com.example.courseservice.Dto.UserEntity.UserEntityResponse;

import java.util.List;

public interface UserService {
    List<UserEntityResponse> findUsersById(List<Long> userIds);



    void updateUserItems(String type, String action, Long id, Long userId);

    UserEntityResponse findUsersByUsername(String username);
}
