package com.zjnu.demo.controller;

import com.zjnu.demo.common.Result;
import com.zjnu.demo.entity.User;
import com.zjnu.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "auth-controller", description = "认证与个人信息接口")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    /** 登录请求体：参数校验防止脏数据进入认证流程 */
    public record LoginRequest(@NotBlank(message = "用户名不能为空") String username,
                               @NotBlank(message = "密码不能为空") String password) {}

    @Operation(summary = "登录（成功返回 JWT）")
    @PostMapping("/auth/login")
    public Result<Map<String, String>> login(@RequestBody @Valid LoginRequest req) {
        String token = authService.login(req.username(), req.password());
        return Result.ok(Map.of("token", token));
    }

    @Operation(summary = "查询当前登录用户信息")
    @GetMapping("/users/me")
    public Result<User> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = authService.loadByUsername((String) auth.getPrincipal());
        return Result.ok(user);
    }
}
