package com.gm.goalmate.domain.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findAllByUserUserId(Long userId);

    List<Goal> findAllByUser_UserIdAndStartDateBetweenAndType(Long userId, LocalDate startDate, LocalDate dueDate, GoalType type);

    @Query(value = "SELECT COALESCE(d.emotion, 'UNRECORDED') AS name, ROUND(SUM(IF(g.result = 'SUCCESS',1, 0)) / COUNT(g.goal_id), 1) AS success_rate FROM goal g " +
            "LEFT JOIN daily_record d ON g.user_id = d.user_id AND COALESCE(g.result_date, g.due_date) = d.record_date WHERE g.user_id = :userId " +
            "AND COALESCE(g.result_date, g.due_date) BETWEEN :first AND :last GROUP BY name", nativeQuery = true)
    List<EmotionSuccessRate> findSuccessRateGroupByEmotion(Long userId, LocalDate first, LocalDate last);
}
