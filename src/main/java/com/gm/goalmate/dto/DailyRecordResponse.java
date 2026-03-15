package com.gm.goalmate.dto;

import com.gm.goalmate.domain.dailyRecord.DailyRecord;
import com.gm.goalmate.domain.dailyRecord.Emotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyRecordResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RecordCheck {
        private Long id;
        private LocalDate recordDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Detail {
        private LocalDate recordDate;
        private String content;
        private Emotion emotion;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Detail from(DailyRecord dailyRecord) {
            return Detail.builder()
                    .content(dailyRecord.getContent())
                    .emotion(dailyRecord.getEmotion())
                    .recordDate(dailyRecord.getRecordDate())
                    .createdAt(dailyRecord.getCreatedAt())
                    .updatedAt(dailyRecord.getUpdatedAt())
                    .build();
        }
    }

}
