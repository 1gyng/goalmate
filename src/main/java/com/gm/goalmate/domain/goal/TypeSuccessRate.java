package com.gm.goalmate.domain.goal;

public interface TypeSuccessRate {
    GoalType getType();
    Double getSuccessRate();
    Long getSuccessCount();
    Long getTotalCount();
}
