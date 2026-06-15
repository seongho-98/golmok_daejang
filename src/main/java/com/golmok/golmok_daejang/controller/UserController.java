package com.golmok.golmok_daejang.controller;

import com.golmok.golmok_daejang.dto.ApiResponse;
import com.golmok.golmok_daejang.dto.request.DogamSaveRequest;
import com.golmok.golmok_daejang.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 프로필 조회 (개인)
    @GetMapping("/api/users/profile")
    public ApiResponse<?> getProfile(@RequestParam("id") String loginId) {
        return ApiResponse.ok(userService.getProfile(loginId));
    }

    // 도감 저장
    @PostMapping("/api/dogam")
    public ApiResponse<?> saveDogam(@RequestBody DogamSaveRequest req) {
        userService.saveDogam(req);
        return ApiResponse.ok();
    }
}
