package com.zjnu.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应 user 表（map-underscore-to-camel-case 自动映射 createTime/create_time）
 */
@Data
public class User {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过 50")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 100, message = "密码长度不能超过 100")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)   // 序列化时隐藏密码，反序列化正常绑定
    private String password;

    @Size(max = 100, message = "邮箱长度不能超过 100")
    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
