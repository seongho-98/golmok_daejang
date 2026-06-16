package com.golmok.golmok_daejang.controller;

import com.golmok.golmok_daejang.dto.response.BusinessDashboardResponse;
import com.golmok.golmok_daejang.dto.request.BusinessAgentRequest;
import com.golmok.golmok_daejang.dto.response.BusinessAgentContent;
import com.golmok.golmok_daejang.dto.response.BusinessAgentResponse;
import com.golmok.golmok_daejang.service.BusinessService;
import com.golmok.golmok_daejang.service.FoundryAgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;
    private final FoundryAgentService foundryAgentService;

    // 사업자 대시보드 조회
    @GetMapping("/dashboard")
    public BusinessDashboardResponse getDashboard(@RequestParam("id") String loginId) {
        return businessService.getDashboard(loginId);
    }

    // Foundry Agent로 사업 정보 분석 요청
    @PostMapping("/analyze")
    public ResponseEntity<BusinessAgentResponse> analyzeBusinessInfo(@Valid @RequestBody BusinessAgentRequest request) {
        try {
            BusinessAgentContent agentResponse = foundryAgentService.sendToFoundryAgent(
                    request.getBusinessName(),
                    request.getBusinessAddress()
            );

            BusinessAgentResponse response = BusinessAgentResponse.builder()
                    .businessName(request.getBusinessName())
                    .businessAddress(request.getBusinessAddress())
                    .agentResponse(agentResponse)
                    .success(true)
                    .message("Agent 분석이 완료되었습니다")
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            BusinessAgentResponse response = BusinessAgentResponse.builder()
                    .businessName(request.getBusinessName())
                    .businessAddress(request.getBusinessAddress())
                    .success(false)
                    .message("Agent 분석 중 오류 발생: " + e.getMessage())
                    .build();

            return ResponseEntity.status(500).body(response);
        }
    }
}
