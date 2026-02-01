package com.gm.goalmate.domain.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalResult {
    NONE("미선택"),
    SUCCESS("달성"),
    FAILED("미달성");

    private final String description;
}
