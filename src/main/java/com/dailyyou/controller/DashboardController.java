package com.dailyyou.controller;

import com.dailyyou.entity.User;
import com.dailyyou.service.DiaryService;
import com.dailyyou.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    private final DiaryService diaryService;
    private final UserService userService;

    public DashboardController(DiaryService diaryService, UserService userService) {
        this.diaryService = diaryService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        
        String username = principal.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("entries", diaryService.getUserEntries(user));
        return "dashboard"; // Points to templates/dashboard.html
    }
}
