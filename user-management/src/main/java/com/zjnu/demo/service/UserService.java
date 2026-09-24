package com.zjnu.demo.service;

import com.zjnu.demo.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User create(User user);

    void update(User user);

    void delete(Long id);
}
