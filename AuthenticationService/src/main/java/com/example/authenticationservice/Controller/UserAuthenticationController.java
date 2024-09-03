package com.example.authenticationservice.Controller;

import com.example.authenticationservice.Dto.LoginRequestDto;
import com.example.authenticationservice.Dto.UserDto;
import com.example.authenticationservice.Exceptions.UserNotFoundException;
import com.example.authenticationservice.JwtConf.JwtGenerator;
import com.example.authenticationservice.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
public class UserAuthenticationController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtGenerator jwtGenerator;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            if(loginRequestDto.getUsername() == null || loginRequestDto.getPassword() == null) {
                throw new UserNotFoundException("UserName or Password is Empty");
            }
            UserDto userData = userService.getUserByNameAndPassword(loginRequestDto.getUsername(),loginRequestDto.getPassword());
            return new ResponseEntity<>(jwtGenerator.generateToken(userData), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
