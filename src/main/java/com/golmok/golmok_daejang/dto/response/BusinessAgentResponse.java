package com.golmok.golmok_daejang.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessAgentResponse {

    private String businessName;
    private String businessAddress;
    private BusinessAgentContent agentResponse;
    private boolean success;
    private String message;
}

