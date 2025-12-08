package com.gm.goalmate.controller;

import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.service.GoalService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping("/goals/new")
    public String showGoalForm() {
        return "goal-form";
    }

    @PostMapping("/goals")
    public String addGoal(@RequestBody GoalRequest.Add request) {
        return "";
    }

    @DeleteMapping("/goals")
    public String deleteGoal() {
        return "";
    }

    @GetMapping("/goals")
    public String showGoalList() {
        return "goal-list";
    }

}
