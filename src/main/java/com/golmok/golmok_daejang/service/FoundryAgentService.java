package com.golmok.golmok_daejang.service;

import com.golmok.golmok_daejang.dto.response.BusinessAgentContent;
import com.golmok.golmok_daejang.dto.response.BusinessTypeRecommendationData;
import com.golmok.golmok_daejang.dto.response.PersonalityAnalysisData;
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

    public PersonalityAnalysisData analyzePersonality(BusinessTypeRecommendationData recommendation) {
        try {
            String endpoint = dotenv.get("PERSONALITY_ANALYSIS_ENDPOINT");
            String azureOpenaiKey = dotenv.get("AZURE_OPENAI_API_KEY");
            String apiVersion = dotenv.get("FOUNDRY_API_VERSION");

            if (endpoint == null || azureOpenaiKey == null) {
                throw new RuntimeException(".env 파일에서 필요한 환경 변수를 찾을 수 없습니다");
            }

            String resolvedApiVersion = (apiVersion == null || apiVersion.isBlank())
                    ? DEFAULT_API_VERSION : apiVersion;
            String fullUrl = appendApiVersion(endpoint + "/responses", resolvedApiVersion);

            String instructions = """
                    [Role]
                    너는 금융 데이터 및 결제 거래 내역 분석 전문가이자 데이터 기반 심리 분석가야.
                    사용자의 소비 패턴을 깊이 있게 추론하여, 사용자의 성격 유형을 형용사 형태로 업종별 가게명을 보고 3가지씩 추론해줘

                    - 다른 잡담은 하지 말고 반드시 아래의 JSON 포맷으로만 답변해 줘.
                    - 만약 업종이 비어있으면 업종 추천, 다른 입력된 데이터를 참고해서 성격 유형 추론해서 꼭 아래 형식에 맞춰서 줘.
                    - 업종명에는 진짜 업종명을 입력해줘
                    {
                      "A업종명": ["형용사1", "형용사2", "형용사3"],
                      "B업종명": ["형용사1", "형용사2", "형용사3"],
                      "C업종명": ["형용사1", "형용사2", "형용사3"]
                    }
                    """;

            String input = String.format(
                    "A업종: %s, 가게명: %s\nB업종: %s, 가게명: %s\nC업종: %s, 가게명: %s",
                    recommendation.getTypeA(), recommendation.getStoresA(),
                    recommendation.getTypeB(), recommendation.getStoresB(),
                    recommendation.getTypeC(), recommendation.getStoresC()
            );

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-4.1-mini");
            requestBody.addProperty("instructions", instructions);
            requestBody.addProperty("input", input);

            log.info("성격 분석 요청 Endpoint: {}", fullUrl);
            log.info("성격 분석 요청 본문: {}", input);

            String response = webClientBuilder
                    .baseUrl(fullUrl)
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
                                        String message = "성격 분석 오류 응답 - status="
                                                + clientResponse.statusCode()
                                                + ", body=" + errorBody;
                                        log.error(message);
                                        return Mono.error(new RuntimeException(message));
                                    })
                    )
                    .bodyToMono(String.class)
                    .doOnError(error -> log.error("성격 분석 요청 실패: ", error))
                    .block();

            log.info("성격 분석 응답: {}", response);
            return parsePersonalityResponse(response, recommendation);

        } catch (Exception e) {
            log.error("성격 분석 통신 중 오류 발생", e);
            throw new RuntimeException("성격 분석 통신 실패: " + e.getMessage(), e);
        }
    }

    private PersonalityAnalysisData parsePersonalityResponse(String rawResponse, BusinessTypeRecommendationData recommendation) {
        log.info("parsePersonalityResponse 시작 - typeA={}, typeB={}, typeC={}",
                recommendation.getTypeA(), recommendation.getTypeB(), recommendation.getTypeC());

        JsonObject root = JsonParser.parseString(rawResponse).getAsJsonObject();
        JsonArray output = root.getAsJsonArray("output");

        if (output == null || output.isEmpty()) {
            log.error("Azure 응답에 output 배열이 없습니다. rawResponse={}", rawResponse);
            throw new RuntimeException("Azure 응답에 output 배열이 없습니다");
        }

        for (JsonElement outputElement : output) {
            JsonObject outputObject = outputElement.getAsJsonObject();
            String elementType = outputObject.has("type") ? outputObject.get("type").getAsString() : "";
            log.info("output element type={}", elementType);

            if (!"message".equals(elementType) || !outputObject.has("content")) continue;

            for (JsonElement contentElement : outputObject.getAsJsonArray("content")) {
                JsonObject contentObject = contentElement.getAsJsonObject();
                String contentType = contentObject.has("type") ? contentObject.get("type").getAsString() : "";
                log.info("content element type={}", contentType);

                if (!"output_text".equals(contentType) || !contentObject.has("text")) continue;

                String text = normalizeJsonText(contentObject.get("text").getAsString());
                log.info("파싱할 텍스트: {}", text);

                JsonObject parsed = JsonParser.parseString(text).getAsJsonObject();

                // Azure가 업종명을 키로 직접 반환: {"의류": [...], "카페": [...], ...}
                java.util.LinkedHashMap<String, List<String>> personality = new java.util.LinkedHashMap<>();
                for (java.util.Map.Entry<String, JsonElement> entry : parsed.entrySet()) {
                    List<String> adjs = new ArrayList<>();
                    if (entry.getValue().isJsonArray()) {
                        for (JsonElement el : entry.getValue().getAsJsonArray()) {
                            if (!el.isJsonNull()) adjs.add(el.getAsString());
                        }
                    }
                    log.info("업종({}) 형용사: {}", entry.getKey(), adjs);
                    personality.put(entry.getKey(), adjs);
                }

                log.info("최종 personality 맵: {}", personality);
                return PersonalityAnalysisData.builder().personality(personality).build();
            }
        }

        log.error("output 배열에서 output_text를 찾지 못했습니다");
        throw new RuntimeException("응답에서 output_text를 찾을 수 없습니다");
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

