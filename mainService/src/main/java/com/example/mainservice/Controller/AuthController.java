package com.example.mainservice.Controller;

import com.example.mainservice.Dto.LoginRequest;
import com.example.mainservice.Dto.User.RegistrationDto;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.AuthService;
import com.example.mainservice.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
public class AuthController {

    private UserService userService;
    private AuthService authService;


    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }


    // For registration
    @GetMapping("/register")
    public String getRegisterForm(Model model)
    {
        if(SecurityUtil.getSessionUser() != null )
        {
            return "redirect:/home?notAllowed"; // Authenticated users do not have access to the registration page
        }
        RegistrationDto user = new RegistrationDto();
        model.addAttribute("registrationDto", user);
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
            model.addAttribute("registrationDto", user);
            return "register";
        }
        String userSavingStatus = userService.saveUser(user);

        if(userSavingStatus.equals("user with this username is already exists"))
        {
            log.error("This username already exists");
            return "redirect:/register?existingUsername";
        }
        if(userSavingStatus.equals("user with this email is already exists"))
        {
            log.error("This email already exists");
            return "redirect:/register?existingEmail";
        }
        else
        {
            return "redirect:/login?successfullyRegistered";
        }
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/users/login")
    public String loginUser(@ModelAttribute LoginRequest loginRequest, HttpServletRequest request, HttpSession session) {
        // Get client IP address
        String ipAddress = request.getRemoteAddr();
        loginRequest.setIpAddress(ipAddress);

        // Get User-Agent string
        String userAgentString = request.getHeader("User-Agent");

        // Initialize UserAgentAnalyzer (consider making this a bean for better performance)
        UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                .build();

        // Parse User-Agent
        nl.basjes.parse.useragent.UserAgent userAgent = uaa.parse(userAgentString);

        // Extract device information
        String deviceBrand = userAgent.getValue("DeviceBrand");
        String deviceName = userAgent.getValue("DeviceName");
        String operatingSystem = userAgent.getValue("OperatingSystemName");
        String browserName = userAgent.getValue("AgentName");

        // Combine information to create a device identifier
        String deviceInfo = String.format("%s %s (%s, %s)", deviceBrand, deviceName, operatingSystem, browserName);
        loginRequest.setDeviceType(deviceInfo);

        String token = authService.login(loginRequest);
        if (token != null) {
            session.setAttribute("user", loginRequest.getUsername());
            session.setAttribute("token", token);

            // Create Authentication object
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            Authentication auth = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), null, authorities);

            // Set Authentication in SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(auth);

            return "redirect:/home";
        }
        return "redirect:/login?error=true";
    }
    @PostMapping("/users/logout")
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String username = SecurityUtil.getSessionUser();
        if (username != null) {
            // Delete token logic by device and location
            authService.deleteUserToken(username,request.getRemoteAddr());
            // To clear the security context (SecurityContextHolder). The logout method terminates the current authentication session by removing authentication data from the security context, and logs out of the system.
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            // The user session is invalidated, which means it will be invalidated. Calling request.getSession(false) returns the current session if it exists, or null if there is no session. If a session is found, session.invalidate() is called, which invalidates the session by destroying all of its attributes.
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return "redirect:/login?logout";
        }
        return "redirect:/home?error";
    }
}
