package com.golmok.golmok_daejang.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAgentRequest {

    @NotBlank(message = "사업장명은 필수입니다")
    private String businessName;

    @NotBlank(message = "사업자주소는 필수입니다")
    private String businessAddress;
}

