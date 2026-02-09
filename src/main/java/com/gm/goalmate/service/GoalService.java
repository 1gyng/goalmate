package com.gm.goalmate.service;

import com.gm.goalmate.domain.goal.*;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.domain.user.UserRepository;
import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.dto.GoalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long addGoal(GoalRequest.Add request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Goal goal = Goal.builder()
                .type(request.getType())
                .task(request.getTask())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .user(user)
                .build();

        return goalRepository.save(goal).getGoalId();
    }

    @Transactional
    public void deleteGoal(Long goalNum, Long userId) {
        Goal goal = findGoalByGoalId(goalNum);
        validateWriter(goal, userId);

        goalRepository.delete(goal);
    }

    @Transactional
    public void updateGoal(Long goalNum, GoalRequest.Update request) {
        Goal goal = findGoalByGoalId(goalNum);
        goal.update(request);
    }

    public List<GoalResponse.Calendar> getCalendarGoals(Long userId) {
        List<Goal> goals = goalRepository.findAllByUserUserId(userId);
        return goals.stream()
                .map(goal -> GoalResponse.Calendar.builder()
                        .id(goal.getGoalId())
                        .task(goal.getTask())
                        .startDate(goal.getStartDate())
                        .dueDate(goal.getDueDate())
                        .type(goal.getType())
                        .build())
                .toList();
    }

    @Transactional
    public GoalResult updateGoalResult(Long goalNum, GoalRequest.UpdateResult request, Long userId) {
        Goal goal = findGoalByGoalId(goalNum);
        validateWriter(goal, userId);
        goal.updateResult(request);

        return goal.getResult();
    }

    public GoalResponse.ListSimple getTodayGoalsByType(Long userId, GoalType type) {
        LocalDate startDate = getStartDate(type);
        LocalDate dueDate = getDueDate(type);

        List<Goal> goals = goalRepository.findAllByUser_UserIdAndStartDateBetweenAndType(userId, startDate, dueDate, type);
        List<GoalResponse.ListItem> items = mapToListItems(goals);

        return GoalResponse.ListSimple.builder()
                .items(items)
                .type(type)
                .startDate(startDate)
                .dueDate(dueDate)
                .build();
    }

    private List<GoalResponse.ListItem> mapToListItems(List<Goal> goals) {
        return goals.stream()
                .map(goal -> GoalResponse.ListItem.builder()
                        .id(goal.getGoalId())
                        .task(goal.getTask())
                        .result(goal.getResult())
                        .build())
                .toList();
    }
    private Goal findGoalByGoalId(Long goalNum) {
        return goalRepository.findById(goalNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));
    }

    private void validateWriter(Goal goal, Long userId) {
        if(!goal.getUser().getUserId().equals(userId)) { //작성자 = 로그인한 사용자
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
    }

    private LocalDate getStartDate(GoalType type) {
        LocalDate today = LocalDate.now();

        return switch (type) {
            case DAILY -> today;
            case WEEKLY -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            case MONTHLY -> today.with(TemporalAdjusters.firstDayOfMonth());
            case YEARLY -> today.with(TemporalAdjusters.firstDayOfYear());
        };
    }

    private LocalDate getDueDate(GoalType type) {
        LocalDate today = LocalDate.now();

        return switch (type) {
            case DAILY -> today;
            case WEEKLY -> today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            case MONTHLY -> today.with(TemporalAdjusters.lastDayOfMonth());
            case YEARLY -> today.with(TemporalAdjusters.lastDayOfYear());
        };
    }
}