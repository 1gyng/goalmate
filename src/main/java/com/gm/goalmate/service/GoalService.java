package com.gm.goalmate.service;

import com.gm.goalmate.domain.goal.Goal;
import com.gm.goalmate.domain.goal.GoalRepository;
import com.gm.goalmate.domain.goal.GoalResult;
import com.gm.goalmate.domain.goal.GoalStatus;
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
    public Long addGoal(GoalRequest.Add request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Goal goal = Goal.builder()
                .type(request.getType())
                .task(request.getTask())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .user(user)
                .today(LocalDate.now())
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

    public List<GoalResponse.SimpleInfo> getGoalsByUserId(Long userId) {
        List<Goal> goals = goalRepository.findAllByUserUserId(userId);
        return goals.stream()
                .map(goal -> GoalResponse.SimpleInfo.builder()
                        .id(goal.getGoalId())
                        .task(goal.getTask())
                        .startDate(goal.getStartDate())
                        .dueDate(goal.getDueDate())
                        .type(goal.getType())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 0시 0분 0초(자정)에 실행
    public void updateStatus() {
        LocalDate today = LocalDate.now();
        goalRepository.updateStatusToTodo(today);
        goalRepository.updateStatusToInProgress(today);
        goalRepository.updateStatusToCompleted(today);
    }

    @Transactional
    public String updateGoalResult(Long goalNum, GoalRequest.UpdateResult request) {
        Goal goal = findGoalByGoalId(goalNum);
        goal.updateResult(request);

        if(goal.getResult() == GoalResult.SUCCESS) {
            return "목표 달성! 다음 목표도 파이팅 해봐요✨";
        } else
            return "저장 완료. 이번의 아쉬움은 다음에 채워봐요💪";
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
}