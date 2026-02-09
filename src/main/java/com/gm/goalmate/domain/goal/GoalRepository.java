package com.gm.goalmate.domain.goal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findAllByUserUserId(Long userId);

    List<Goal> findAllByUser_UserIdAndStartDateBetweenAndType(Long userId, LocalDate startDate, LocalDate dueDate, GoalType type);
}
