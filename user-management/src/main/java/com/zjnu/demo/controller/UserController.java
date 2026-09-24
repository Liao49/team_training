package com.zjnu.demo.controller;

import com.zjnu.demo.common.Result;
import com.zjnu.demo.entity.User;
import com.zjnu.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "user-controller", description = "用户管理接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "查询全部用户")
    @GetMapping
    public Result<List<User>> list() {
        return Result.ok(userService.findAll());
    }

    @Operation(summary = "导出用户列表 CSV")
    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {
        String csv = userService.exportCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @Operation(summary = "按 ID 查询用户")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        return Result.ok(user);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<User> create(@RequestBody @Valid User user) {
        return Result.ok(userService.create(user));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.update(user);
        return Result.ok(null);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok(null);
    }
}
