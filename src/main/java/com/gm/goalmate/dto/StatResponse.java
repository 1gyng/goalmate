package com.gm.goalmate.dto;

import com.gm.goalmate.domain.dailyRecord.Emotion;
import com.gm.goalmate.domain.goal.GoalType;
import com.gm.goalmate.domain.goal.TypeSuccessRate;
import lombok.Builder;

public class StatResponse {

    public static final String UNRECORDED_KEY = "UNRECORDED";
    public static final String UNRECORDED_DESCRIPTION = "기록 없음";

    @Builder
    public record EmotionStat(String name, String description, Long recordedCount, Double successRate) {
        public static EmotionStat init(Emotion emotion) {
            return EmotionStat.builder()
                    .name(emotion.name())
                    .description(emotion.getDescription())
                    .recordedCount(0L)
                    .successRate(0.0)
                    .build();
        }
    }

    @Builder
    public record SuccessRateStat(GoalType type, Double successRate, Long successCount, Long totalCount) {
        public static SuccessRateStat init(GoalType type) {
            return SuccessRateStat.builder()
                    .type(type)
                    .successRate(0.0)
                    .successCount(0L)
                    .totalCount(0L)
                    .build();
        }

        public static SuccessRateStat from(TypeSuccessRate rate) {
            return SuccessRateStat.builder()
                    .type(rate.getType())
                    .successRate(rate.getSuccessRate())
                    .successCount(rate.getSuccessCount())
                    .totalCount(rate.getTotalCount())
                    .build();
        }
    }

    public record CurrentStreak(int currentStreak) {
        public static CurrentStreak init() {
            return new CurrentStreak(0);
        }

        public static CurrentStreak of(int streak) {
            return new CurrentStreak(streak);
        }
    }
}