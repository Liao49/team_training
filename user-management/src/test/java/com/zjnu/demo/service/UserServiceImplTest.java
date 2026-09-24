package com.zjnu.demo.service;

import com.zjnu.demo.common.BusinessException;
import com.zjnu.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional                       // 测试自动回滚，不污染数据库
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void create_should_return_generated_id() {
        User user = new User();
        user.setUsername("test01");
        user.setPassword("123456");
        user.setEmail("t01@example.com");

        User created = userService.create(user);

        assertNotNull(created.getId());
        assertEquals("test01", created.getUsername());
    }

    @Test
    void create_duplicate_username_should_throw() {
        User user = new User();
        user.setUsername("admin");          // 与初始化数据冲突
        user.setPassword("123456");

        assertThrows(BusinessException.class, () -> userService.create(user));
    }

    @Test
    void findAll_should_return_seeded_users() {
        var users = userService.findAll();

        assertTrue(users.size() >= 2);     // 初始化数据 admin / zsan
        assertTrue(users.stream().anyMatch(u -> "admin".equals(u.getUsername())));
    }

    @Test
    void findById_not_exist_should_throw() {
        assertThrows(BusinessException.class, () -> userService.findById(99999L));
    }

    @Test
    void update_should_modify_email() {
        User user = userService.findById(1L);
        user.setEmail("admin-new@example.com");

        userService.update(user);

        assertEquals("admin-new@example.com", userService.findById(1L).getEmail());
    }

    @Test
    void delete_should_remove_user() {
        userService.delete(2L);            // 删除初始化用户 zsan

        assertThrows(BusinessException.class, () -> userService.findById(2L));
    }
}
