package com.gm.goalmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserResponse {
    @Getter
    @AllArgsConstructor
    public static class LoginIdCheck {
        private String loginId; //검사한 loginId
        private String message;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LoginSession {
        private Long id;
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LoginInfo {
        private String nickname;
    }
}
