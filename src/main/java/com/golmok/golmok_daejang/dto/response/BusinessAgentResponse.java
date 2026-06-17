package com.golmok.golmok_daejang.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessAgentResponse {

    private String businessName;
    private String businessAddress;
    private BusinessAgentContent agentResponse;
    private List<BusinessImageGenerationPayload> imageGenerationPayloads;
    private BusinessSearchPayload searchPayload;
    private boolean success;
    private String message;
}

