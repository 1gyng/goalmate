package com.gm.goalmate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserResponse {
    @Getter
    @AllArgsConstructor
    public static class LoginIdCheck {
        private String loginId; //검사한 loginId
        private String message;
    }
}
