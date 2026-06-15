package com.golmok.golmok_daejang.controller;

import com.golmok.golmok_daejang.dto.ApiResponse;
import com.golmok.golmok_daejang.dto.request.BusinessSignupRequest;
import com.golmok.golmok_daejang.dto.request.BusinessVerifyRequest;
import com.golmok.golmok_daejang.dto.request.LoginRequest;
import com.golmok.golmok_daejang.dto.request.UserSignupRequest;
import com.golmok.golmok_daejang.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 개인 로그인
    @PostMapping("/login/user")
    public ApiResponse<?> loginUser(@RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.loginUser(req));
    }

    // 사업자 로그인
    @PostMapping("/login/business")
    public ApiResponse<?> loginBusiness(@RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.loginBusiness(req));
    }

    // 개인 회원가입
    @PostMapping("/signup/user")
    public ApiResponse<?> signupUser(@RequestBody UserSignupRequest req) {
        return ApiResponse.ok(authService.signupUser(req));
    }

    // 사업자 본인확인
    @PostMapping("/verify/business")
    public ApiResponse<?> verifyBusiness(@RequestBody BusinessVerifyRequest req) {
        return ApiResponse.ok(authService.verifyBusiness(req));
    }

    // 사업자 회원가입
    @PostMapping("/signup/business")
    public ApiResponse<?> signupBusiness(@RequestBody BusinessSignupRequest req) {
        return ApiResponse.ok(authService.signupBusiness(req));
    }
}
