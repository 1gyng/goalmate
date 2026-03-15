package com.gm.goalmate.controller;

import com.gm.goalmate.config.LoginUser;
import com.gm.goalmate.domain.dailyRecord.Emotion;
import com.gm.goalmate.domain.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(@LoginUser User user, Model model) {
        model.addAttribute("emotions", Emotion.values());

        return "dashboard";
    }
}
