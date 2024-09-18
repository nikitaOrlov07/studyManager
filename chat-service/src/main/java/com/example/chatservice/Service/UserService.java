package com.example.chatservice.Service;

import java.util.List;

public interface UserService {
    Boolean saveChatIds(List<Long> userIds, Long id, String operationType);
}
