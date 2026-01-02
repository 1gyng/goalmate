package com.gm.goalmate.controller;

import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.UserRequest;
import com.gm.goalmate.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody UserRequest.Join request) {
        userService.join(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserRequest.Login request, HttpServletRequest httpServletRequest) {
        User loginUser = userService.login(request);

        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("loginUser", loginUser);

        return ResponseEntity.ok("로그인 성공");
    }

}
