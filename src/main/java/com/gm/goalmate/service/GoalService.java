package com.gm.goalmate.service;

import com.gm.goalmate.domain.goal.Goal;
import com.gm.goalmate.domain.goal.GoalRepository;
import com.gm.goalmate.domain.goal.GoalStatus;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.domain.user.UserRepository;
import com.gm.goalmate.dto.GoalRequest;
import com.gm.goalmate.dto.GoalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
    public GoalResponse.Goals addGoal(GoalRequest.Add request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if(request.getDueDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        GoalStatus status;
        LocalDate today = LocalDate.now();
        if(today.isBefore(request.getStartDate())) {
            status = GoalStatus.TODO;
        } else if (today.isAfter(request.getDueDate())) {
            status = GoalStatus.COMPLETED;
        } else {
            status = GoalStatus.IN_PROGRESS;
        }

        Goal goal = Goal.builder()
                .type(request.getType())
                .task(request.getTask())
                .status(status)
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .user(user)
                .build();

        goalRepository.save(goal);

        return GoalResponse.Goals.builder()
                .id(goal.getGoalId())
                .title(goal.getTask())
                .start(goal.getStartDate())
                .end(goal.getDueDate().plusDays(1))
                .build();
    }

    @Transactional
    public void deleteGoal(Long goalNum, Long userId) {
        Goal goal = goalRepository.findById(goalNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 목표를 찾을 수 없습니다."));

        if(!goal.getUser().getUserId().equals(userId)) { //작성자 = 로그인한 사용자
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        goalRepository.delete(goal);
    }

    public void updateGoal(Long goalNum, GoalRequest.Update request) {

    }

    public List<GoalResponse.Goals> getGoalsByUserId(Long userId) {
        List<Goal> goals = goalRepository.findAllByUserUserId(userId);
        return goals.stream()
                .map(goal -> GoalResponse.Goals.builder()
                        .id(goal.getGoalId())
                        .title(goal.getTask())
                        .start(goal.getStartDate())
                        .end(goal.getDueDate().plusDays(1))
                        .build())
                .collect(Collectors.toList());
    }
}
