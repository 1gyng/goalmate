package com.gm.goalmate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class UserRequest {
    @Getter
    @Setter
    public static class Join {
        @NotBlank(message = "아이디를 입력해주세요.")
        private String loginId;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
        @NotBlank(message = "닉네임을 입력해주세요.")
        private String nickname;
    }

    @Getter
    @Setter
    public static class Login {
        @NotBlank(message = "아이디를 입력해주세요.")
        private String loginId;
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
    }
}
