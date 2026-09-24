package com.zjnu.demo.service.impl;

import com.zjnu.demo.common.BusinessException;
import com.zjnu.demo.entity.User;
import com.zjnu.demo.mapper.UserMapper;
import com.zjnu.demo.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectAll();
    }

    @Override
    public User findById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在: " + id);
        }
        return user;
    }

    @Override
    @Transactional
    public User create(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        userMapper.insert(user);
        return userMapper.selectById(user.getId());   // 回读，拿到数据库生成的 create_time
    }

    @Override
    @Transactional
    public void update(User user) {
        if (userMapper.selectById(user.getId()) == null) {
            throw new BusinessException("用户不存在: " + user.getId());
        }
        userMapper.update(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (userMapper.selectById(id) == null) {
            throw new BusinessException("用户不存在: " + id);
        }
        userMapper.deleteById(id);
    }

    @Override
    public String exportCsv() {
        List<User> users = userMapper.selectAll();
        StringBuilder sb = new StringBuilder("id,username,email,create_time\n");
        for (User u : users) {
            sb.append(u.getId()).append(',')
              .append(u.getUsername()).append(',')
              .append(u.getEmail() == null ? "" : u.getEmail()).append(',')
              .append(u.getCreateTime()).append('\n');
        }
        return sb.toString();
    }
}
