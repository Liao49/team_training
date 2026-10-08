package com.zjnu.demo.service;

import com.zjnu.demo.common.BusinessException;
import com.zjnu.demo.common.PromptGuard;
import com.zjnu.demo.common.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;

/**
 * 智能问答服务（实验五）：把 AI 能力作为"功能模块"接入既有工程。
 * 1. 复用实验四 PromptGuard：拦截提示注入 + &lt;user_input&gt; 定界符包裹；
 * 2. 每用户限流 5 次/分钟（大模型调用成本防护）；
 * 3. 转调 Python RAG 服务（FastAPI），30s 读超时；
 * 4. 日志脱敏：只记问题长度与哈希，不落原始内容；异常统一降级提示。
 */
@Service
public class AskService {

    private static final Logger log = LoggerFactory.getLogger(AskService.class);

    private final RateLimiter limiter = new RateLimiter(5, Duration.ofMinutes(1));
    private final RestClient rest;

    public AskService(@Value("${llm.base-url:http://127.0.0.1:8000}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(30_000);
        this.rest = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> ask(String username, String question) {
        if (!limiter.tryAcquire(username)) {
            throw new BusinessException("提问过于频繁，请一分钟后再试");
        }
        String safe = PromptGuard.sanitize(question);     // 注入拦截 + 定界符包裹
        // 日志脱敏：只记长度与哈希，避免用户问题内容落日志
        log.info("ask: user={}, len={}, hash={}", username,
                question.length(), Integer.toHexString(question.hashCode()));
        try {
            return rest.post().uri("/ask")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("question", safe))
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.warn("llm-service unavailable: {}", e.getMessage());
            throw new BusinessException("智能问答服务暂时不可用，请稍后再试");
        }
    }
}
