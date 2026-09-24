-- 建库建表与初始化数据（实验一）
CREATE DATABASE IF NOT EXISTS team_training DEFAULT CHARSET utf8mb4;
USE team_training;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 密文）',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB COMMENT = '用户表';

-- 初始化数据（密码为 BCrypt 加密后的密文）
INSERT INTO `user` (username, password, email) VALUES
('admin', '$2a$10$fF4z9qd9x3Yl/kiYl1WaUu5PtSKUCj/t/56VrISyxh0HReH9/GGmu', 'admin@example.com'),
('zsan',  '$2a$10$qwYLgH/q74G4Eb70H3ETKerq96WU41uTx8l2dlycy0J/Q6vbdM8SC', 'zsan@example.com');

SELECT id, username, password, email, create_time FROM `user`;
