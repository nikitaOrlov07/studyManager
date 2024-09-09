package com.example.mainservice.Controller;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.User.UserEntityDto;
import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.UserService;
import com.example.mainservice.Service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/homeworks")
@RequiredArgsConstructor
@Slf4j
public class HomeworkController {

    private final ViewService viewService;
    private final UserService userService;

    // Method for getting all user homework
    @GetMapping
    public String getHomeworkPage(Model model , @RequestParam("userId") Long userId, @RequestParam(value = "type",required = false) String type)
    {
        String username = SecurityUtil.getSessionUser();
        if(username == null || username.isEmpty())
        {
            log.error("Unauthorized user trying to reach \"getHomeworks\" page");
            return "redirect:/home?notAllowed";
        }
        UserEntityDto user = userService.findUserByUsername(username);
        if(!user.getId().equals(userId))
        {
            log.error("User: "+ user.getId() + "trying to reach homeworks for student: "+ userId);
            return "redirect:/home?notAllowed";
        }
        if(type == null || type.isEmpty())
        {
            type= "All";
        }
        List<HomeworkDto> homeworks = viewService.findHomeworksByUser(userId,type);
        model.addAttribute("homeworks",homeworks);
        model.addAttribute("type",type);
        model.addAttribute("user",user);

        return "homeworksPage";
    }

}
