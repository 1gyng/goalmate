package com.gm.goalmate.service;

import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.domain.user.UserRepository;
import com.gm.goalmate.dto.UserRequest;
import com.gm.goalmate.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void join(UserRequest.Join request) {
        validateDuplicateLoginId(request.getLoginId());

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);
    }

    @Transactional
    public UserResponse.LoginSession login(UserRequest.Login request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        return UserResponse.LoginSession.builder()
                .id(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }

    public UserResponse.LoginIdCheck checkLoginId(String loginId) {
        validateDuplicateLoginId(loginId);
        return new UserResponse.LoginIdCheck(loginId, "사용할 수 있는 아이디입니다.");
    }

    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalStateException("사용할 수 없는 아이디입니다.");
        }
    }
}
