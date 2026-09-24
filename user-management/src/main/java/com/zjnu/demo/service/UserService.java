package com.zjnu.demo.service;

import com.zjnu.demo.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User create(User user);

    void update(User user);

    void delete(Long id);

    /**
     * 导出全部用户为 CSV 文本（实验三：feature/user-export）
     */
    String exportCsv();
}
