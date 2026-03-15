package com.gm.goalmate.controller;

import com.gm.goalmate.config.LoginUser;
import com.gm.goalmate.domain.goal.GoalType;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.dto.GoalResponse;
import com.gm.goalmate.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping("/calendar")
    public String showCalendar(@LoginUser User user) {
        return "calendar";
    }

    @GetMapping("/today")
    public String showList(@LoginUser User user, Model model) {
        model.addAttribute("dailyGoals", goalService.getTodayGoalsByType(user.getUserId(), GoalType.DAILY));
        model.addAttribute("weeklyGoals", goalService.getTodayGoalsByType(user.getUserId(), GoalType.WEEKLY));
        model.addAttribute("monthlyGoals", goalService.getTodayGoalsByType(user.getUserId(), GoalType.MONTHLY));
        model.addAttribute("yearlyGoals", goalService.getTodayGoalsByType(user.getUserId(), GoalType.YEARLY));

        return "today-goal";
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse.Calendar>> getCalendarGoals(@LoginUser User user) {
        List<GoalResponse.Calendar> goals = goalService.getCalendarGoals(user.getUserId());
        return ResponseEntity.ok(goals);
    }

    @PostMapping
    public ResponseEntity<Void> addGoal(@Valid @RequestBody GoalRequest.Add request, @LoginUser User user) {
        Long goalId = goalService.addGoal(request, user.getUserId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{goalNum}").buildAndExpand(goalId).toUri();
        return ResponseEntity.created(location).build();
    }

/*    @GetMapping(/{goalNum})
    public ResponseEntity<?> getGoal(@PathVariable Long goalNum, @LoginUser User user) {
        return ResponseEntity.ok().build();
    }*/

    @DeleteMapping("/{goalNum}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalNum, @LoginUser User user) {
        goalService.deleteGoal(goalNum, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{goalNum}")
    public ResponseEntity<Void> updateGoal(@PathVariable Long goalNum, @Valid @RequestBody GoalRequest.Update request, @LoginUser User user) {
        goalService.updateGoal(goalNum, request, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{goalNum}/result")
    public ResponseEntity<GoalResponse.Result> updateGoalResult(@PathVariable Long goalNum, @Valid @RequestBody GoalRequest.UpdateResult request, @LoginUser User user) {
        GoalResponse.Result result = goalService.updateGoalResult(goalNum, request, user.getUserId());
        return ResponseEntity.ok(result);
    }

}
