package com.zjnu.demo.controller;

import com.zjnu.demo.common.Result;
import com.zjnu.demo.service.AskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 学习搭子智能问答（实验五）：任意登录用户可用（RBAC 不限角色），
 * 未登录由 Security 过滤链统一 401。
 */
@Tag(name = "ask-controller", description = "智能问答接口（实验五）")
@RestController
@RequestMapping("/api")
public class AskController {

    private final AskService askService;

    public AskController(AskService askService) { this.askService = askService; }

    /** 问答请求体：参数校验防止脏数据进入 LLM 链路 */
    public record AskRequest(@NotBlank(message = "问题不能为空")
                             @Size(max = 500, message = "问题长度不能超过500字")
                             String question) {}

    @Operation(summary = "学习搭子智能问答（RAG，基于课程资料作答并标注出处）")
    @PostMapping("/ask")
    public Result<Map<String, Object>> ask(@RequestBody @Valid AskRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> data = askService.ask((String) auth.getPrincipal(), req.question());
        return Result.ok(data);
    }
}
