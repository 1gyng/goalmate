package com.gm.goalmate.dto;

import com.gm.goalmate.domain.dailyRecord.Emotion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class DailyRecordRequest {

    @Getter
    @Setter
    public static class Add {
        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 50, message = "50자 이내로 입력해주세요.")
        private String content;

        @NotNull(message = "감정을 선택해주세요.")
        private Emotion emotion;

        @NotNull(message = "작성할 날짜를 선택해주세요.")
        private LocalDate recordDate;
    }

    @Getter
    @Setter
    public static class Update {
        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 50, message = "50자 이내로 입력해주세요.")
        private String content;

        @NotNull(message = "감정을 선택해주세요.")
        private Emotion emotion;
    }
}
