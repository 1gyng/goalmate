package com.gm.goalmate.controller;

import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/calendar")
    public String showCalendar() {
        return "calendar";
    }

    @PostMapping("/goals")
    public ResponseEntity<String> addGoal(@Valid @RequestBody GoalRequest.Add request) {
        goalService.addGoal(request);
        return ResponseEntity.ok("목표가 추가되었습니다!");
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
