package com.gm.goalmate.service;

import com.gm.goalmate.domain.goal.Goal;
import com.gm.goalmate.domain.goal.GoalRepository;
import com.gm.goalmate.domain.goal.GoalStatus;
import com.gm.goalmate.dto.GoalRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Transactional
    public void addGoal(GoalRequest.Add request) {
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
                .writerId(1L) //수정필요
                .build();

        goalRepository.save(goal);
    }
}
