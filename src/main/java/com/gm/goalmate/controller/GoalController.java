package com.gm.goalmate.controller;

import com.gm.goalmate.config.LoginUser;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.dto.GoalResponse;
import com.gm.goalmate.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping("/calendar")
    public String showCalendar(@LoginUser User user) {
        return "calendar";
    }

    @GetMapping("/goals")
    public ResponseEntity<List<GoalResponse.SimpleInfo>> getGoalsByUserId(@LoginUser User user) {
        List<GoalResponse.SimpleInfo> goals = goalService.getGoalsByUserId(user.getUserId());
        return ResponseEntity.ok(goals);
    }

    @PostMapping("/goals")
    public ResponseEntity<?> addGoal(@Valid @RequestBody GoalRequest.Add request, @LoginUser User user) {
        Long goalId = goalService.addGoal(request, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", goalId,"message", "목표가 추가되었습니다!"));
    }

    @DeleteMapping("/goals/{goalNum}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long goalNum, @LoginUser User user) {
        goalService.deleteGoal(goalNum, user.getUserId());
        return ResponseEntity.ok(Map.of("message", "목표가 삭제되었습니다!"));
    }

    @PutMapping("/goals/{goalNum}")
    public ResponseEntity<?> updateGoal(@PathVariable Long goalNum, @Valid @RequestBody GoalRequest.Update request) {
        goalService.updateGoal(goalNum, request);
        return ResponseEntity.ok(Map.of("message", "목표가 수정되었습니다!"));
    }

}
