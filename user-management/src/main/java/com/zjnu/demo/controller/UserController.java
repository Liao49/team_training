package com.zjnu.demo.controller;

import com.zjnu.demo.common.Result;
import com.zjnu.demo.entity.User;
import com.zjnu.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "user-controller", description = "用户管理接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "查询全部用户（order=id|username 指定排序）")
    @GetMapping
    public Result<List<User>> list(@RequestParam(defaultValue = "id") String order) {
        List<User> users = userService.findAll();
        if ("username".equals(order)) {
            users.sort(java.util.Comparator.comparing(User::getUsername));
        }
        return Result.ok(users);
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
