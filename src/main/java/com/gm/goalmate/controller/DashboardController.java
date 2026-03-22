package com.gm.goalmate.controller;

import com.gm.goalmate.config.LoginUser;
import com.gm.goalmate.domain.dailyRecord.Emotion;
import com.gm.goalmate.domain.goal.GoalType;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.service.DailyRecordService;
import com.gm.goalmate.service.GoalService;
import com.gm.goalmate.service.StatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final GoalService goalService;
    private final StatService statService;

    public DashboardController(GoalService goalService, DailyRecordService dailyRecordService, StatService statService) {
        this.goalService = goalService;
        this.statService = statService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@LoginUser User user, Model model) {
        model.addAttribute("emotions", Emotion.values());
        model.addAttribute("dailyGoals", goalService.getTodayGoalItemList(user.getUserId(), GoalType.DAILY));
        model.addAttribute("emotionStat", statService.getMonthlyEmotionStat(user.getUserId()));

        return "dashboard";
    }
}
