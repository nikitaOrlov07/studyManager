package com.example.userservice.Controller;

import com.example.userservice.Dto.Login.LoginRequestDto;
import com.example.userservice.Dto.Registration.RegistrationDto;
import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Mapper.UserEntityMapper;
import com.example.userservice.Model.UserEntity;
import com.example.userservice.Security.SecurityUtil;
import com.example.userservice.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class Controller {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/get") // working
    public UserEntityDto getUserByUserId(@RequestParam Long userId) {
        return userService.findUserById(userId);
    }

    // registration
    @PostMapping("/save")
    public String saveUser(@ModelAttribute RegistrationDto registrationDto) {
        log.info("UserService \"saveUser\" controller method is working");
        // check existing email
        UserEntity existingUserByEmail = userService.findByEmail(registrationDto.getEmail());
        if (existingUserByEmail != null && existingUserByEmail.getEmail() != null && !existingUserByEmail.getEmail().isEmpty()) {
            return "user with this email is already exists";
        }
        System.out.println(registrationDto);
        // check existing username
        UserEntity existingUserByUsername = userService.findUserByUsername(registrationDto.getUsername());
        if (existingUserByUsername != null && existingUserByUsername.getUsername() != null && !existingUserByUsername.getUsername().isEmpty()) {
            return "user with this username is already exists";
        }

        userService.save(UserEntityMapper.registrationDtoToUserEntityDto(registrationDto));
        return "User was successfully saved";
    }


    // Method to verify the passed login and password with Authentication Service
    @PostMapping("/checkAuthentication")
    public Boolean checkLoginData(@ModelAttribute LoginRequestDto loginRequestDto) {
        log.info("User Service \"checkAuthentication \" controller method is working ");
        UserEntity user = userService.findUserByUsername(loginRequestDto.getUsername());
        if (user == null) {
            return false;
        }
        Boolean result = passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword());
        log.info("Password correct: " + result);
        return result;
    }

    @GetMapping("/{username}")
    public UserEntityDto getUserByUsername(@PathVariable String username) {
        UserEntity user = userService.findUserByUsername(username);
        if (user == null) {
            return null;
        }
        return UserEntityMapper.userEntityToUserEntityDto(user);
    }

    /// Method for finding course involved users
    @GetMapping("/findUsersByIds")
    public List<UserEntityDto> findUsersByIds(@RequestParam List<Long> usersIds) {
        return userService.findUsersByIds(usersIds);
    }

    /// Method for joining or leaving course
    @GetMapping("/action/{action}")
    public Boolean actionCourse(@PathVariable("action") String action,
                                @RequestParam("courseId") Long courseId,
                                @RequestParam("username") String username) {
        log.info("UserService \"Action Course\" method is working");
        Boolean result = userService.courseAction(action, courseId, username);
        log.info("Operation status: " + result);
        return result;
    }

    /// Method for updating user information when user creates a new course or a new homework (or a new studentAttachment)
    @PostMapping("/{action}/{type}/{id}")
    public Boolean updateItemsInformation(@PathVariable("type") String type,     // type can be homework or course
                                          @PathVariable("id") Long id,
                                          @PathVariable("action") String action, // action can be "create","update", "submit" , "check"
                                          @RequestParam("userId") Long userId)
    {
        log.info("createOrDeleteItems controller method is working");

        log.info("Type: "+type);
        log.info("Action: "+ action);
        log.info("UserId: "+ userId);
        log.info("Item id: " + id);

        userService.updateCreatedItems(action,type,id,userId);
        return true;
    }
    @PostMapping("/assignHomeworks")
    public void assignHomework(@RequestParam("usersId") List<Long> userEntities,
                               @RequestParam("homeworkId") Long homeworkId,
                               @RequestParam("type") String type) // type can be submitted or assigned
    {
      log.info("assignHomework controller method is working");
      userService.assignHomeworks(userEntities, homeworkId , type);
    }
    @PostMapping("/chats/addChatsIds/{operationType}")
    public boolean addChatId(@PathVariable(value="operationType",required = false) String operationType, @RequestParam(value = "usersIds", required = false) List<Long> userIds ,@RequestParam(value = "chatId",required = false) Long chatId )
    {
        log.info("\"addChatId\" Controller method is working");
        return userService.addChatIds(userIds,chatId,operationType);
    }

}
