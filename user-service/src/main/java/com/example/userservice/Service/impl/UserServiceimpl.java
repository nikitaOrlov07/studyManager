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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceimpl implements UserService {

    @Autowired
    private UserEntityRepository userRepository;
    @Autowired
    private KafkaTemplate<String,UserEntityDto> kafkaTemplate;
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
        kafkaTemplate.send("notificationTopic",UserEntityMapper.userEntityToUserEntityDto(userEntity));
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


}
