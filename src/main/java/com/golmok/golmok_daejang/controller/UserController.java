package com.golmok.golmok_daejang.controller;

import com.golmok.golmok_daejang.dto.ApiResponse;
import com.golmok.golmok_daejang.dto.request.DogamSaveRequest;
import com.golmok.golmok_daejang.dto.response.BusinessTypeRecommendationData;
import com.golmok.golmok_daejang.service.FoundryAgentService;
import com.golmok.golmok_daejang.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(name = "User", description = "개인 사용자 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final FoundryAgentService foundryAgentService;

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/api/users/profile")
    public ApiResponse<?> getProfile(
            @Parameter(description = "로그인 아이디", example = "user01") @RequestParam("id") String loginId) {
        return ApiResponse.ok(userService.getProfile(loginId));
    }

    @Operation(summary = "도감 저장")
    @PostMapping("/api/dogam")
    public ApiResponse<?> saveDogam(@RequestBody DogamSaveRequest req) {
        userService.saveDogam(req);
        return ApiResponse.ok();
    }

    @Operation(summary = "업종 동선 추천 + AI 성격 분석",
               description = "추천 업종/가게 목록과 업종별 성격 형용사 3가지를 함께 반환합니다.")
    @GetMapping("/api/users/recommend-business-types")
    public ApiResponse<?> recommendBusinessTypesWithPersonality(
            @Parameter(description = "주민번호", example = "9812021234123") @RequestParam("residentNumber") String residentNumber) {
        BusinessTypeRecommendationData recommendation = userService.recommendBusinessTypes(residentNumber);

        try {
            Map<String, List<String>> personality = foundryAgentService.analyzePersonality(recommendation).getPersonality();
            return ApiResponse.ok(personality);
        } catch (Exception e) {
            log.error("성격 분석 실패", e);
            return ApiResponse.ok(Collections.emptyMap());
        }
    }

    @Operation(summary = "업종 동선 추천 (A → B → C)",
               description = "최근 3개월 내 같은 요일/시간대 거래 기반으로 A업종(최다) → B업종 → C업종 순서와 각 업종의 가게 목록을 반환합니다.")
    @GetMapping("/api/users/recommend-business-types/v2")
    public ApiResponse<?> recommendBusinessTypes(
            @Parameter(description = "주민번호", example = "9812021234123") @RequestParam("residentNumber") String residentNumber) {
        return ApiResponse.ok(userService.recommendBusinessTypes(residentNumber));
    }
}
