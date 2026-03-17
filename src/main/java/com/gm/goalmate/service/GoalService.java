package com.gm.goalmate.service;

import com.gm.goalmate.domain.goal.*;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.domain.user.UserRepository;
import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.dto.GoalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

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
    public void updateGoal(Long goalNum, GoalRequest.Update request, Long userId) {
        Goal goal = findGoalByGoalId(goalNum);
        validateWriter(goal, userId);
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
    public GoalResponse.Result updateGoalResult(Long goalNum, GoalRequest.UpdateResult request, Long userId) {
        Goal goal = findGoalByGoalId(goalNum);
        validateWriter(goal, userId);
        goal.updateResult(request);

        return GoalResponse.Result.builder()
                .result(goal.getResult())
                .build();
    }

    public List<GoalResponse.Item> getTodayGoalItemList(Long userId, GoalType type) {
        LocalDate today = LocalDate.now();
        return getGoalItemsByDate(userId, type, today);
    }

    public GoalResponse.Summary getTodayGoalSummary(Long userId, GoalType type) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = getStartDate(type, today);
        LocalDate dueDate = getDueDate(type, today);
        List<GoalResponse.Item> items = getGoalItemsByPeriod(userId, type, startDate, dueDate);

        return GoalResponse.Summary.builder()
                .items(items)
                .type(type)
                .startDate(startDate)
                .dueDate(dueDate)
                .build();
    }

    private List<GoalResponse.Item> getGoalItemsByDate(Long userId, GoalType type, LocalDate date) {
        LocalDate startDate = getStartDate(type, date);
        LocalDate dueDate = getDueDate(type, date);

        return getGoalItemsByPeriod(userId, type, startDate, dueDate);
    }

    private List<GoalResponse.Item> getGoalItemsByPeriod(Long userId, GoalType type, LocalDate startDate, LocalDate dueDate) {
        List<Goal> goals = goalRepository.findAllByUser_UserIdAndStartDateBetweenAndType(userId, startDate, dueDate, type);
        return GoalResponse.Item.from(goals);
    }

    private Goal findGoalByGoalId(Long goalNum) {
        return goalRepository.findById(goalNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));
    }

    private void validateWriter(Goal goal, Long userId) {
        if (!goal.getUser().getUserId().equals(userId)) { //작성자 = 로그인한 사용자
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
    }

    private LocalDate getStartDate(GoalType type, LocalDate baseDate) {
        return switch (type) {
            case DAILY -> baseDate;
            case WEEKLY -> baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            case MONTHLY -> baseDate.with(TemporalAdjusters.firstDayOfMonth());
            case YEARLY -> baseDate.with(TemporalAdjusters.firstDayOfYear());
        };
    }

    private LocalDate getDueDate(GoalType type, LocalDate baseDate) {
        return switch (type) {
            case DAILY -> baseDate;
            case WEEKLY -> baseDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            case MONTHLY -> baseDate.with(TemporalAdjusters.lastDayOfMonth());
            case YEARLY -> baseDate.with(TemporalAdjusters.lastDayOfYear());
        };
    }
}