package com.gm.goalmate.dto;

import lombok.Getter;

public class GoalRequest {

    @Getter
    public static class Add {
        private String task;
    }
}
