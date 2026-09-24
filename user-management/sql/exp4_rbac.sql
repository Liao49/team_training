-- 实验四：RBAC 权限建模（角色/用户角色/权限/角色权限）
USE team_training;

DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `permission`;
DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
    `id`   BIGINT      NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码：ADMIN / USER',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`code`)
) ENGINE = InnoDB COMMENT = '角色表';

CREATE TABLE `user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE = InnoDB COMMENT = '用户-角色关联表';

CREATE TABLE `permission` (
    `id`   BIGINT      NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL COMMENT '权限编码：如 user:delete',
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`code`)
) ENGINE = InnoDB COMMENT = '权限表';

CREATE TABLE `role_permission` (
    `role_id`       BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `permission_id`),
    CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
    CONSTRAINT `fk_rp_perm` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`)
) ENGINE = InnoDB COMMENT = '角色-权限关联表';

INSERT INTO role(code, name) VALUES ('ADMIN', '管理员'), ('USER', '普通用户');
INSERT INTO permission(code, name) VALUES
('user:list',   '查询用户'),
('user:create', '新增用户'),
('user:update', '更新用户'),
('user:delete', '删除用户');
-- ADMIN 拥有全部权限，USER 仅查询
INSERT INTO role_permission(role_id, permission_id) VALUES (1, 1), (1, 2), (1, 3), (1, 4), (2, 1);
-- 用户-角色：admin(1)->ADMIN，zsan(2)->USER
INSERT INTO user_role(user_id, role_id) VALUES (1, 1), (2, 2);

SELECT u.username, r.code AS role_code, r.name AS role_name
FROM user u JOIN user_role ur ON u.id = ur.user_id JOIN role r ON ur.role_id = r.id;
