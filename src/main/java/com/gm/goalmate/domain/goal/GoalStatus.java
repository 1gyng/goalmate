package com.gm.goalmate.domain.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalStatus {
    TODO("할일"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String description;
}