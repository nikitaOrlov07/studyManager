package com.example.authenticationservice.Controller;

import com.example.authenticationservice.Dto.LoginRequestDto;
import com.example.authenticationservice.Dto.UserEntityDto;
import com.example.authenticationservice.Exceptions.UserNotFoundException;
import com.example.authenticationservice.Exceptions.WrongDataException;
import com.example.authenticationservice.JwtConf.JwtGenerator;
import com.example.authenticationservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@Slf4j
public class UserAuthenticationController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtGenerator jwtGenerator;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("User AuthenticationService Controller method is working");
        try {
            if(loginRequestDto.getUsername() == null || loginRequestDto.getPassword() == null) {
                throw new UserNotFoundException("UserName or Password is Empty");
            }
            UserEntityDto userData = userService.getUserByNameAndPassword(loginRequestDto.getUsername(),loginRequestDto.getPassword());
            log.info("Generation token for user: " + userData.getUsername());
            return new ResponseEntity<>(jwtGenerator.generateToken(userData), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            log.error("\"UserNotFoundException\" was thrown");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (WrongDataException e)
        {
            log.error("\"WrongDataException\" was thrown");
            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }
    }
}
