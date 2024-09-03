package com.example.userservice.Controller;

import com.example.userservice.Dto.Login.LoginRequestDto;
import com.example.userservice.Dto.Registration.RegistrationDto;
import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.JwtTokenConf.JwtTokenProvider;
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

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class Controller {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/getCurrentUser")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            // Здесь вы можете получить дополнительную информацию о пользователе
            return ResponseEntity.ok(currentUserName);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }

    @GetMapping("/get") // working
    public UserEntityDto getUserByUserId(@RequestParam Long userId)
    {
         return  userService.findUserById(userId);
    }

    // registration
    @PostMapping("/save")
    public String saveUser(@ModelAttribute RegistrationDto registrationDto)
    {
        log.info("UserService \"saveUser\" controller method is working");
        // check existing email
        UserEntity existingUserByEmail = userService.findByEmail(registrationDto.getEmail());
        if(existingUserByEmail != null && existingUserByEmail.getEmail() != null && !existingUserByEmail.getEmail().isEmpty()) {
            return "user with this email is already exists";
        }
        System.out.println(registrationDto);
        // check existing username
        UserEntity existingUserByUsername = userService.findUserByUsername(registrationDto.getUsername());
        if(existingUserByUsername != null && existingUserByUsername.getUsername() != null && !existingUserByUsername.getUsername().isEmpty()) {
            return "user with this username is already exists";
        }

        userService.save(UserEntityMapper.registrationDtoToUserEntityDto(registrationDto));
        return "User was successfully saved";
    }

    // Method to verify the passed login and password with Authentication Service

    @PostMapping("/checkAuthentication")
    public Boolean checkLoginData(@ModelAttribute LoginRequestDto loginRequestDto) {
        UserEntity user = userService.findUserByUsername(loginRequestDto.getUsername());
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword());
    }

    @GetMapping("/{username}")
    public UserEntityDto getUserByUsername(@PathVariable String username) {
        UserEntity user = userService.findUserByUsername(username);
        if (user == null) {
            return null;
        }
        return UserEntityMapper.userEntityToUserEntityDto(user);
    }

}
