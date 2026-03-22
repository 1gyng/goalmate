package com.gm.goalmate.dto;

import com.gm.goalmate.domain.goal.GoalResult;
import com.gm.goalmate.domain.goal.GoalStatus;
import com.gm.goalmate.domain.goal.GoalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class GoalRequest {

    @Getter
    @Setter
    public static class Add {
        @NotBlank(message = "목표를 입력해주세요.")
        private String task;

        @NotNull(message = "구분을 선택해주세요.")
        private GoalType type;

        @NotNull(message = "시작일을 선택해주세요")
        private LocalDate startDate;

        @NotNull(message = "마감일을 선택해주세요")
        private LocalDate dueDate;
    }

    @Getter
    @Setter
    public static class Update {
        @NotBlank(message = "목표를 입력해주세요.")
        private String task;

        @NotNull(message = "구분을 선택해주세요.")
        private GoalType type;

        @NotNull(message = "시작일을 선택해주세요")
        private LocalDate startDate;

        @NotNull(message = "마감일을 선택해주세요")
        private LocalDate dueDate;
    }

    @Getter
    @Setter
    public static class UpdateResult {
        @NotNull(message = "결과를 선택해주세요.")
        private GoalResult result;

        @NotNull(message = "실제 목표 완료일을 선택해주세요.")
        private LocalDate resultDate;
    }
}
