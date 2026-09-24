package com.zjnu.demo.service;

import com.zjnu.demo.common.BusinessException;
import com.zjnu.demo.entity.User;
import com.zjnu.demo.mapper.UserMapper;
import com.zjnu.demo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder encoder;        // BCryptPasswordEncoder
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String rawPassword) {
        User user = userMapper.selectByUsername(username);
        // 登录失败统一提示，避免用户名枚举
        if (user == null || !encoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        List<String> roles = userMapper.selectRolesByUserId(user.getId());
        return jwtUtil.generate(user.getId(), user.getUsername(), roles);
    }

    /** /api/users/me：按用户名回查当前登录用户（密码字段序列化时已隐藏） */
    public User loadByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
}
