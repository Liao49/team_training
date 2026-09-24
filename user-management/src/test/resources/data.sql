INSERT INTO user (username, password, email) VALUES
('admin', '$2a$10$fF4z9qd9x3Yl/kiYl1WaUu5PtSKUCj/t/56VrISyxh0HReH9/GGmu', 'admin@example.com'),
('zsan',  '$2a$10$qwYLgH/q74G4Eb70H3ETKerq96WU41uTx8l2dlycy0J/Q6vbdM8SC', 'zsan@example.com');

INSERT INTO role (id, code, name) VALUES (1, 'ADMIN', '管理员'), (2, 'USER', '普通用户');

INSERT INTO user_role (user_id, role_id) VALUES (1, 1), (2, 2);
