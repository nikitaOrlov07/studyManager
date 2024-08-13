package com.example.userservice.Controller;

import com.example.userservice.Dto.UserEntityDto;
import com.example.userservice.Mapper.UserEntityMapper;
import com.example.userservice.Model.UserEntity;
import com.example.userservice.Security.SecurityUtil;
import com.example.userservice.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class Controller {
    private final UserService userService;
    @GetMapping("/getCurrent")
    public UserEntityDto getCurrentUser()
    {
        UserEntity userEntity = userService.findUserByUsername(SecurityUtil.getSessionUser());
        return UserEntityMapper.userEntityToUserEntityDto(userEntity);
    }

    @GetMapping("/get") // working
    public UserEntityDto getUserByUserId(@RequestParam Long userId)
    {
         return  userService.findUserById(userId);
    }

    // registry
    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserEntityDto userEntityDto)
    {
        // check existing email
        UserEntity existingUserByEmail = userService.findByEmail(userEntityDto.getEmail());
        if(existingUserByEmail != null && existingUserByEmail.getEmail() != null && !existingUserByEmail.getEmail().isEmpty()) {
            return "user with this email is already exists";
        }
        // check existing username
        UserEntity existingUserByUsername = userService.findUserByUsername(userEntityDto.getUsername());
        if(existingUserByUsername != null && existingUserByUsername.getUsername() != null && !existingUserByEmail.getUsername().isEmpty()) {
            return "user with this username is already exists";
        }
        userService.save(userEntityDto);
        return "User was successfully saved";
    }

}
