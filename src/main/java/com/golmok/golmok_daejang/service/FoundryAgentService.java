package com.golmok.golmok_daejang.service;

import com.golmok.golmok_daejang.dto.response.BusinessAgentContent;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoundryAgentService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String DEFAULT_API_VERSION = "2025-05-15-preview";
    private final WebClient.Builder webClientBuilder;

    /**
     * Foundry Agent로 비즈니스 정보를 전송하고 응답을 받아옵니다.
     * @param businessName 사업장명
     * @param businessAddress 사업자주소
     * @return Agent의 응답
     */
    public BusinessAgentContent sendToFoundryAgent(String businessName, String businessAddress) {
        try {
            String foundryEndpoint = dotenv.get("FOUNDRY_AGENT_ENDPOINT");
            String azureOpenaiKey = dotenv.get("AZURE_OPENAI_API_KEY");
            String apiVersion = dotenv.get("FOUNDRY_API_VERSION");

            if (foundryEndpoint == null || azureOpenaiKey == null) {
                throw new RuntimeException(".env 파일에서 필요한 환경 변수를 찾을 수 없습니다");
            }

            String resolvedApiVersion = (apiVersion == null || apiVersion.isBlank())
                    ? DEFAULT_API_VERSION
                    : apiVersion;
            String endpointWithVersion = appendApiVersion(foundryEndpoint, resolvedApiVersion);

            // Agents OpenAI protocol(/responses)에 맞는 요청 구성
            JsonObject requestBody = buildResponsesRequest(businessName, businessAddress);

            log.info("Foundry Agent에 요청을 보냅니다. Endpoint: {}", endpointWithVersion);
            log.info("요청 본문: {}", requestBody);

            // WebClient를 사용한 비동기 요청
            String response = webClientBuilder
                    .baseUrl(endpointWithVersion)
                    .build()
                    .post()
                    .header("Content-Type", "application/json")
                    .header("api-key", azureOpenaiKey)
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(errorBody -> {
                                        String message = "Foundry Agent 오류 응답 - status="
                                                + clientResponse.statusCode()
                                                + ", body=" + errorBody;
                                        log.error(message);
                                        return Mono.error(new RuntimeException(message));
                                    })
                    )
                    .bodyToMono(String.class)
                    .doOnError(error -> log.error("Foundry Agent 요청 실패: ", error))
                    .block();

            log.info("Foundry Agent 응답: {}", response);
            BusinessAgentContent content = extractContentDto(response);
            log.info("Foundry Agent content(DTO): {}", content);
            System.out.println("[FoundryAgentContentDTO] " + content);
            return content;

        } catch (Exception e) {
            log.error("Foundry Agent 통신 중 오류 발생", e);
            throw new RuntimeException("Agent 통신 실패: " + e.getMessage(), e);
        }
    }

    /**
     * OpenAI Responses 형식의 요청 메시지를 구성합니다.
     */
    private JsonObject buildResponsesRequest(String businessName, String businessAddress) {
        JsonObject request = new JsonObject();

        String prompt = String.format("다음 사업 정보를 분석해주세요.\n사업장명: %s\n사업자주소: %s",
                businessName, businessAddress);

        request.addProperty("input", prompt);

        return request;
    }

    private String appendApiVersion(String endpoint, String apiVersion) {
        String separator = endpoint.contains("?") ? "&" : "?";
        return endpoint + separator + "api-version=" + apiVersion;
    }

    private BusinessAgentContent extractContentDto(String rawResponse) {
        try {
            JsonObject root = JsonParser.parseString(rawResponse).getAsJsonObject();
            JsonArray output = root.getAsJsonArray("output");

            if (output == null) {
                return BusinessAgentContent.builder().text(rawResponse).build();
            }

            for (JsonElement outputElement : output) {
                JsonObject outputObject = outputElement.getAsJsonObject();
                String type = outputObject.has("type") ? outputObject.get("type").getAsString() : "";

                if (!"message".equals(type) || !outputObject.has("content")) {
                    continue;
                }

                JsonArray contentArray = outputObject.getAsJsonArray("content");
                for (JsonElement contentElement : contentArray) {
                    JsonObject contentObject = contentElement.getAsJsonObject();
                    String contentType = contentObject.has("type") ? contentObject.get("type").getAsString() : "";

                    if (!"output_text".equals(contentType) || !contentObject.has("text")) {
                        continue;
                    }

                    String text = contentObject.get("text").getAsString();
                    String normalizedText = normalizeJsonText(text);
                    try {
                        JsonElement parsedText = JsonParser.parseString(normalizedText);
                        if (parsedText.isJsonObject()) {
                            return mapJsonToContent(parsedText.getAsJsonObject(), normalizedText);
                        }
                    } catch (Exception ignored) {
                        // Agent가 plain text를 반환하면 텍스트 필드로 반환합니다.
                    }

                    return BusinessAgentContent.builder().text(text).build();
                }
            }

            return BusinessAgentContent.builder().text(rawResponse).build();
        } catch (Exception e) {
            log.warn("Foundry Agent content 파싱 실패. raw 텍스트를 반환합니다.", e);
            return BusinessAgentContent.builder().text(rawResponse).build();
        }
    }

    private BusinessAgentContent mapJsonToContent(JsonObject payload, String rawText) {
        List<String> featureKeywords = readStringArray(payload,
                "특징키워드", "특징_키워드", "featureKeywords", "feature_keywords");
        List<String> characterNames = readStringArray(payload,
                "캐릭터명", "캐릭터명_3가지", "characterNames", "character_names");

        String text = payload.has("text") && !payload.get("text").isJsonNull()
                ? payload.get("text").getAsString()
                : rawText;

        return BusinessAgentContent.builder()
                .featureKeywords(featureKeywords)
                .characterNames(characterNames)
                .text(text)
                .build();
    }

    private List<String> readStringArray(JsonObject payload, String... candidateKeys) {
        JsonArray array = null;
        for (String key : candidateKeys) {
            if (payload.has(key) && payload.get(key).isJsonArray()) {
                array = payload.getAsJsonArray(key);
                break;
            }
        }

        if (array == null) {
            return Collections.emptyList();
        }

        List<String> values = new ArrayList<>();
        for (JsonElement element : array) {
            if (!element.isJsonNull()) {
                values.add(element.getAsString());
            }
        }
        return values;
    }

    private String normalizeJsonText(String text) {
        String trimmed = text == null ? "" : text.trim();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }

        String withoutStartFence = trimmed.replaceFirst("^```(?:json)?\\s*", "");
        return withoutStartFence.replaceFirst("\\s*```$", "").trim();
    }
}

