package com.example.chatservice.Service;

import java.util.List;

public interface UserService {
    Boolean changeChatIds(List<Long> userIds, Long id, String operationType);
}
