package com.example.mainservice.Controller;

import com.example.mainservice.Dto.RegistrationDto;
import com.example.mainservice.Service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class AuthController {
    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // For registration
    @GetMapping("/register")
    public String getRegisterForm(Model model)
    {
        RegistrationDto user = new RegistrationDto();
        model.addAttribute("user", user); // we add empty object into a View ,
        // but if we don`t do it --> we will get an error
        return"register";
    }
    @PostMapping("/register/save")
    public String saveUser(@ModelAttribute @Valid  RegistrationDto user,
                           BindingResult bindingResult,
                           Model model)
    {
        if(bindingResult.hasErrors())
        {
            log.error("There are binding errors: {}", bindingResult.getAllErrors());
            model.addAttribute("user", user);
            return "register";
        }
        String userSavingStatus = userService.saveUser(user);

        if(userSavingStatus.equals("user with this email is already exists"))
        {
            log.error("This email already exists");
            return "redirect:/register?existingEmail";
        }
        if(userSavingStatus.equals("user with this username is already exists"))
        {
            log.error("This username already exists");
            return "redirect:/register?existingUsername";
        }
        else
        {
            return "redirect:/login&successfullyRegistered";
        }
    }
    @GetMapping("/login")
    public String loginPage()
    {
        return "login";
    }
}
