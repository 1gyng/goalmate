package com.gm.goalmate.domain.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findAllByUserUserId(Long userId);

    List<Goal> findAllByUser_UserIdAndDateRange_StartDateBetweenAndType(Long userId, LocalDate startDate, LocalDate dueDate, GoalType type);

    @Query(value = "SELECT COALESCE(d.emotion, 'UNRECORDED') AS name, ROUND(SUM(CASE WHEN g.result = 'SUCCESS' THEN 1 ELSE 0 END) * 100.0 / COUNT(g.goal_id), 0) AS success_rate FROM goal g " +
            "LEFT JOIN daily_record d ON g.user_id = d.user_id AND COALESCE(g.result_date, g.due_date) = d.record_date WHERE g.user_id = :userId " +
            "AND COALESCE(g.result_date, g.due_date) BETWEEN :first AND :last GROUP BY name", nativeQuery = true)
    List<EmotionSuccessRate> findSuccessRateGroupByEmotion(Long userId, LocalDate first, LocalDate last);

    @Query(value = "SELECT g.type AS type, ROUND(SUM(CASE WHEN g.result = 'SUCCESS' THEN 1 ELSE 0 END) * 100.0 / COUNT(g.goal_id), 1) AS success_rate, " +
            "SUM(CASE WHEN g.result = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, COUNT(g.goal_id) as total_count FROM goal g " +
            "WHERE g.user_id = :userId AND ((g.type != 'YEARLY' AND g.due_date BETWEEN :first AND :last) " +
            "OR (g.type = 'YEARLY' AND YEAR(g.due_date) = YEAR(:first))) GROUP BY type", nativeQuery = true)
    List<TypeSuccessRate> findSuccessRateGroupByType(Long userId, LocalDate first, LocalDate last);

    @Query(value = "SELECT DISTINCT DATE(result_date) AS success_date FROM goal WHERE user_id = :userId AND result = 'SUCCESS' ORDER BY success_date DESC", nativeQuery = true)
    List<SuccessDate> findSuccessDate(Long userId);
}
