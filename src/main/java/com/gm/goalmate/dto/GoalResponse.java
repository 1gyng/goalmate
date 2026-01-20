package com.gm.goalmate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.goalmate.domain.goal.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class GoalResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SimpleInfo {
        private Long id; //goalId
        private String task;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate dueDate;
        private GoalType type;
    }
}
