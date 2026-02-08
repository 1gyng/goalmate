package com.gm.goalmate.domain.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findAllByUserUserId(Long userId);

    @Modifying
    @Query(value = "UPDATE Goal g SET g.status = 'TODO' WHERE g.startDate > :today AND g.status != 'TODO'")
    void updateStatusToTodo(@Param("today") LocalDate today);

    @Modifying
    @Query(value = "UPDATE Goal g SET g.status = 'IN_PROGRESS' WHERE g.startDate <= :today AND g.dueDate >= :today AND g.status != 'IN_PROGRESS'")
    void updateStatusToInProgress(@Param("today") LocalDate today);

    @Modifying
    @Query(value = "UPDATE Goal g SET g.status = 'COMPLETED' WHERE g.dueDate < :today AND g.status != 'COMPLETED'")
    void updateStatusToCompleted(@Param("today") LocalDate today);

    List<Goal> findAllByUserUserIdAndStartDateBetweenAndType(Long userId, LocalDate startDate, LocalDate dueDate, GoalType type);
}
