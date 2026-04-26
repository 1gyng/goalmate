package com.gm.goalmate.controller;

import com.gm.goalmate.config.LoginUser;
import com.gm.goalmate.domain.goal.GoalResult;
import com.gm.goalmate.domain.goal.GoalType;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.dto.GoalResponse;
import com.gm.goalmate.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
        model.addAttribute("dailyGoals", goalService.getTodayGoalSummary(user.getUserId(), GoalType.DAILY));
        model.addAttribute("weeklyGoals", goalService.getTodayGoalSummary(user.getUserId(), GoalType.WEEKLY));
        model.addAttribute("monthlyGoals", goalService.getTodayGoalSummary(user.getUserId(), GoalType.MONTHLY));
        model.addAttribute("yearlyGoals", goalService.getTodayGoalSummary(user.getUserId(), GoalType.YEARLY));

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

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(goalId).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(params = "type")
    public ResponseEntity<Slice<GoalResponse.Detail>> getListGoalsByType(@LoginUser User user,
                                                                        @PageableDefault(size = 5, sort = "dateRange.dueDate", direction = Sort.Direction.DESC) Pageable pageable,
                                                                        @RequestParam("type") GoalType type,
                                                                        @RequestParam(value = "result",required = false) GoalResult result) {
        Slice<GoalResponse.Detail> goals = goalService.getGoalsByType(user.getUserId(), type, result, pageable);
        return ResponseEntity.ok(goals);
    }

/*    @GetMapping(/{goalNum})
    public ResponseEntity<?> getGoal(@PathVariable Long goalNum, @LoginUser User user) {
        return ResponseEntity.ok().build();
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id, @LoginUser User user) {
        goalService.deleteGoal(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateGoal(@PathVariable Long id, @Valid @RequestBody GoalRequest.Update request, @LoginUser User user) {
        goalService.updateGoal(id, request, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/result")
    public ResponseEntity<GoalResponse.Result> updateGoalResult(@PathVariable Long id, @Valid @RequestBody GoalRequest.UpdateResult request, @LoginUser User user) {
        GoalResponse.Result result = goalService.updateGoalResult(id, request, user.getUserId());
        return ResponseEntity.ok(result);
    }

}
