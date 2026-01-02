package com.gm.goalmate.controller;

import com.gm.goalmate.config.LoginUser;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> addGoal(@Valid @RequestBody GoalRequest.Add request, @LoginUser User user) {
        goalService.addGoal(request, user.getUserId());
        return ResponseEntity.ok("목표가 추가되었습니다!");
    }

    @DeleteMapping("/goals/{goalNum}")
    public ResponseEntity<String> deleteGoal(@PathVariable Long goalNum, @LoginUser User user) {
        goalService.deleteGoal(goalNum, user.getUserId());
        return ResponseEntity.ok("목표가 삭제되었습니다!");
    }

    @PutMapping("/goals/{goalNum}")
    public ResponseEntity<String> updateGoal(@PathVariable Long goalNum, @Valid @RequestBody GoalRequest.Update request) {
        goalService.updateGoal(goalNum, request);
        return ResponseEntity.ok("목표가 수정되었습니다!");
    }

    @GetMapping("/goals")
    public String showGoalList() {
        return "goal-list";
    }

}
