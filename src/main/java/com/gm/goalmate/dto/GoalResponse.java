package com.gm.goalmate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gm.goalmate.domain.goal.Goal;
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
    public static class Summary {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate startDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate dueDate;
        private GoalType type;
        private List<Item> items;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String task;
        private GoalResult result;

        public static Item from(Goal goal) {
            return Item.builder()
                    .id(goal.getGoalId())
                    .task(goal.getTask())
                    .result(goal.getResult())
                    .build();
        }

        public static List<Item> from(List<Goal> goals) {
            return goals.stream()
                    .map(Item::from)
                    .toList();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Result {
        private GoalResult result;
    }

}
