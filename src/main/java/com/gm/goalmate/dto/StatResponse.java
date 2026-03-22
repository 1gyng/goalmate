package com.gm.goalmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class StatResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class EmotionStat {
        String name;
        String description;
        long count;
        double successRate;
    }
}
