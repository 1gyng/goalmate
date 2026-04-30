package com.gm.goalmate.controller;

import com.gm.goalmate.dto.UserRequest;
import com.gm.goalmate.dto.UserResponse;
import com.gm.goalmate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<Void> join(@Valid @RequestBody UserRequest.Join request) {
        userService.join(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/login")
    public String loginGetRedirect() {
        return "redirect:/";
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse.LoginInfo> login(@Valid @RequestBody UserRequest.Login request, HttpServletRequest httpServletRequest) {
        UserResponse.LoginSession loginUser = userService.login(request);

        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("loginUser", loginUser.getId());

        return ResponseEntity.ok(
                UserResponse.LoginInfo.builder()
                        .nickname(loginUser.getNickname())
                        .build());
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/join/check-id/{loginId}")
    public ResponseEntity<UserResponse.LoginIdCheck> checkLoginId(@PathVariable String loginId) {
        UserResponse.LoginIdCheck loginIdCheck = userService.checkLoginId(loginId);
        return ResponseEntity.ok(loginIdCheck);
    }
}
