package com.saygobackend.ai.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saygobackend.ai.AiTripClient;
import com.saygobackend.ai.model.AiTripPlan;
import com.saygobackend.dto.GenerateTripPlanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Primary //优先调用openAi client
public class OpenAiTripClient implements AiTripClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.base-url}")
    private String baseUrl;

    public OpenAiTripClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public List<AiTripPlan> generatePlans(GenerateTripPlanRequest req, int planCount, List<String> planTypes) {
        try {
            String prompt = buildPrompt(req, planCount, planTypes);
            log.info("调用AI生成行程，目的地={}, 人数={}, 日期={}~{}",
                    req.getDestination(), req.getPeople(), req.getStartDate(), req.getEndDate());
            log.info("使用AI BaseURL={}, Key前缀={}", baseUrl, apiKey.substring(0, 5));
//            // 1. 调用 DeepSeek / OpenAI
//            String response = webClient.post()
//                    .uri(baseUrl + "/chat/completions")
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                    .bodyValue("""
//                        {
//                          "model": "deepseek-chat",
//                          "messages": [
//                            {"role": "system", "content": "你是一个旅行规划助手"},
//                            {"role": "user", "content": "%s"}
//                          ],
//                          "temperature": 0.7
//                        }
//                        """.formatted(prompt))
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            // 2. 提取 JSON 部分
//            String json = extractJsonFromResponse(response);
//
//            // 3. 反序列化成 List<AiTripPlan>
//            return objectMapper.readValue(json, new TypeReference<List<AiTripPlan>>() {});

            // 请求体用 Map 构造，避免 JSON 拼接问题
            Map<String, Object> body = Map.of(
                    "model", "deepseek-chat",
                    "messages", List.of(
                            Map.of("role", "system", "content", "你是一个旅行规划助手"),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "temperature", 0.7
            );

            String response = webClient.post()
                    .uri(baseUrl + "/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("AI原始响应: {}", response);

            String json = extractJsonFromResponse(response);

            log.info("提取到的JSON: {}", json);

            return objectMapper.readValue(json, new TypeReference<List<AiTripPlan>>() {});
        } catch (Exception e) {
            log.error("AI生成旅行方案失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI生成旅行方案失败", e);
        }
    }

    private String buildPrompt(GenerateTripPlanRequest req, int planCount, List<String> planTypes) {
        return String.format("""
            根据以下用户需求生成旅行方案：
            - 目的地: %s
            - 出行日期: %s 到 %s
            - 人数: %d 人
            - 预算范围: %s - %s 元
            - 兴趣标签: %s

            要求：
            1. 生成 %d 套不同风格的行程方案（如：%s）。
            2. 每个方案包含: 方案类型，总预算，每天的行程（活动：类型、描述、费用、时间段）。
            3. 请严格输出 JSON，符合以下结构：
            [
              {
                "planType": "性价比",
                "totalEstimatedCost": 5000,
                "days": [
                  {
                    "dayNumber": 1,
                    "notes": "第一天: 抵达 + 城市游览",
                    "activities": [
                      {
                        "activityType": "交通",
                        "description": "抵达机场并前往酒店",
                        "estimatedCost": 300,
                        "timeSlot": "morning"
                      }
                    ]
                  }
                ]
              }
            ]
            """,
                req.getDestination(),
                req.getStartDate(),
                req.getEndDate(),
                req.getPeople(),
                req.getBudgetMin(),
                req.getBudgetMax(),
                req.getInterestTags(),
                planCount,
                planTypes
        );
    }

    /**
     * 简单从返回里提取 JSON（假设 AI 输出被包裹在 content 字段）
     */
    private String extractJsonFromResponse(String response) throws Exception {
        var tree = objectMapper.readTree(response);
        String content = tree.at("/choices/0/message/content").asText();
        // 如果 AI 多说了别的，把 JSON 提取出来
        int start = content.indexOf("[");
        int end = content.lastIndexOf("]");
        return content.substring(start, end + 1);
    }
}
