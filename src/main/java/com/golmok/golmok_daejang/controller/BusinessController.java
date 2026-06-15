package com.golmok.golmok_daejang.controller;

import com.golmok.golmok_daejang.dto.response.BusinessDashboardResponse;
import com.golmok.golmok_daejang.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    // 사업자 대시보드 조회
    @GetMapping("/dashboard")
    public BusinessDashboardResponse getDashboard(@RequestParam("id") String loginId) {
        return businessService.getDashboard(loginId);
    }
}
