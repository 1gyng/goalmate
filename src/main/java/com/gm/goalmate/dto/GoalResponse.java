package com.gm.goalmate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.goalmate.domain.goal.GoalResult;
import com.gm.goalmate.domain.goal.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class GoalResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Calendar {
        private Long id;
        private String task;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate dueDate;
        private GoalType type;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ListSimple {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate dueDate;
        private GoalType type;
        private List<ListItem> items;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ListItem {
        private Long id;
        private String task;
        private GoalResult result;
    }


}
