package com.example.userservice.Service.impl;

import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Mapper.UserEntityMapper;
import com.example.userservice.Model.UserEntity;
import com.example.userservice.Repositories.UserEntityRepository;
import com.example.userservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceimpl implements UserService {

    @Autowired
    private UserEntityRepository userRepository;
    @Autowired
    private KafkaTemplate<String,HashMap<String,UserEntityDto>> kafkaTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

        userEntity.setPassword(passwordEncoder.encode(userEntityDto.getPassword()));

        // Set registration date attribute
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        userEntity.setRegistrationDate(date.format(formatter));
        // Save user
        userRepository.save(userEntity);
        HashMap<String,UserEntityDto> kafkaData = new HashMap<String, UserEntityDto>();
        kafkaData.put("register", UserEntityMapper.userEntityToUserEntityDto(userEntity));
        kafkaTemplate.send("notificationTopic",kafkaData);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserEntityDto> findUsersByIds(List<Long> usersIds) {
        return userRepository.findByIdIn(usersIds)
                .stream()
                .map(UserEntityMapper::userEntityToUserEntityDto)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean courseAction(String action, Long courseId, String username) {
        UserEntity userEntity = findUserByUsername(username);
        if(action.equals("join"))
        {
            userEntity.getParticipatingCourses().add(courseId);
            userRepository.save(userEntity);
            return true;
        }
        if(action.equals("leave"))
        {
            userEntity.getParticipatingCourses().remove(courseId);
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    @Override
    public void updateCreatedItems(String action, String type, Long id,Long userId) {
        UserEntity userEntity = userRepository.findById(userId).get();
        if(userEntity == null)
        {
            throw new NoSuchElementException("User not found");
        }
        if(action.equals("create"))
        {
            if(type.equals("courses"))
            {
                userEntity.getCreatedCoursesIds().add(id);
                log.info("User created course");
            }
            if(type.equals("homeworks"))
            {
                userEntity.getCreatedHomeworksIds().add(id);
                log.info("User created homework");
            }
        }
        else if(action.equals("delete"))
        {
            if(type.equals("courses"))
            {
                userEntity.getCreatedCoursesIds().remove(id);
                log.info("User delete course");
            }
            if(type.equals("homeworks"))
            {
                userEntity.getCreatedHomeworksIds().remove(id);
                log.info("User delete homework");
            }
        }
        else if(action.equals("submit") && type.equals("homeworks"))
        {
            userEntity.getHomeworksIds().remove(id);
            userEntity.getCompletedHomeworksIds().add(id);
            log.info("Homework was successfully submitted");
        }
        userRepository.save(userEntity);
    }

    @Override
    public void assignHomeworks(List<Long> userEntities, Long homeworkId, String type) {
        List<UserEntity> users = userRepository.findAllById(userEntities);
        if (type.equals("assign")) {
            for (UserEntity user : users) {
                if (!user.getHomeworksIds().contains(homeworkId)) {
                    user.getHomeworksIds().add(homeworkId);
                }
            }
        }
        if(type.equals("submit"))
        {
            for (UserEntity user : users) {
                if (!user.getHomeworksIds().contains(homeworkId)) {
                    user.getHomeworksIds().remove(homeworkId);
                    user.getCompletedHomeworksIds().add(homeworkId);
                }
            }
        }
        userRepository.saveAll(users);
    }

    @Override
    public boolean changeChatIds(List<Long> usersIds,Long chatId, String operationType) { // operationType can be "delete" or add chatId  to users list
        if(usersIds == null || usersIds.isEmpty())
        {
            log.error("usersIds list is empty");
            return false;
        }
        log.info("Chat id: "+ chatId + "operationType: "+ operationType);
        UserEntity user = null;
        for (Long userId : usersIds){
            user = userRepository.findById(userId).get();
            if(user == null)
            {
                log.error("User with id: {} was not found ",userId);
                continue;
            }
            if(operationType.equals("add")) {
                user.getChatsIds().add(chatId);
                log.info("Chat with id : {} was successfully added to user chats",chatId);
            }
            if(operationType.equals("delete"))
            {
                if(user.getChatsIds().contains(chatId))
                {
                    user.getChatsIds().remove(chatId);
                    log.info("Chat with id : {} was successfully removed from user chats",chatId);
                }
            }
            userRepository.save(user);
        }
        return true;
    }

    @Override
    public List<UserEntityDto> searchUsersByTitle(String query) {
        List<UserEntity> userEntities =  userRepository.searchUserByQuery(query);
        if(userEntities == null || userEntities.size() == 0)
        {
          log.error("user search result for the query {} is empty",query);
          return  null;
        }
        else {
            return userEntities.stream().map(UserEntityMapper::userEntityToUserEntityDto).collect(Collectors.toList());
        }
    }

    @Override
    public Boolean deleteHomework(Long homeworkId) {
        // Find in created homeworks
        UserEntity authorEntity = userRepository.findByCreatedHomeworksIdsContaining(homeworkId);
        authorEntity.getCreatedHomeworksIds().remove(homeworkId);
        userRepository.save(authorEntity);
        // Find in homeworks ids
        List<UserEntity> userEntities = userRepository.findAllByHomeworksIdsContaining(homeworkId);
        if(userEntities != null && !userEntities.isEmpty())
        {
            for(UserEntity userEntity: userEntities)
            {
                log.info("Size of users who need to do homework with id: {} is : {}",homeworkId, userEntities.size());
                userEntity.getHomeworksIds().remove(homeworkId);
                userRepository.save(userEntity);
            }
        }
        // Find in completing homeworks
        userEntities = userRepository.findAllByCompletedHomeworksIdsContaining(homeworkId);
        if(userEntities != null && !userEntities.isEmpty())
        {
            for(UserEntity userEntity: userEntities)
            {
                log.info("Size of users who complete homework with id: {} is : {}",homeworkId, userEntities.size());
                userEntity.getCompletedHomeworksIds().remove(homeworkId);
                userRepository.save(userEntity);
            }
        }
        log.info("homework with id {} was deleted from all users lists",homeworkId);
        return true;
    }
}
