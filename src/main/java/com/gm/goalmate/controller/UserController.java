package com.gm.goalmate.controller;

import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.UserRequest;
import com.gm.goalmate.dto.UserResponse;
import com.gm.goalmate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/join")
    public String joinGetRedirect() {
        return "redirect:/";
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody UserRequest.Join request) {
        userService.join(request);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다!"));
    }

    @GetMapping("/login")
    public String loginGetRedirect() {
        return "redirect:/";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequest.Login request, HttpServletRequest httpServletRequest) {
        User loginUser = userService.login(request);

        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("loginUser", loginUser);

        return ResponseEntity.ok(Map.of("message", "로그인 성공"));
    }

    @GetMapping("/join/check-id/{loginId}")
    public ResponseEntity<UserResponse.LoginIdCheck> checkLoginId(@PathVariable String loginId) {
        UserResponse.LoginIdCheck loginIdCheck = userService.checkLoginId(loginId);
        return ResponseEntity.ok(loginIdCheck);
    }
}
