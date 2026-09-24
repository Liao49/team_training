# team-training-user-mgmt

《团队规范实训》课程项目：用户管理系统（前后端分离）。

## 项目结构

- `user-management/`：Spring Boot 3 + MyBatis + MySQL 后端，提供 RESTful API
- `frontend/`：Vue 3 + Element Plus 前端（Vite 构建，开发端口 8081）

## 本地运行

```bash
# 1. 初始化数据库（MySQL 8.0）
mysql -uroot -p < user-management/sql/init.sql

# 2. 启动后端（端口 8080，数据库密码通过环境变量注入）
cd user-management
set DB_PASSWORD=你的数据库密码
mvn spring-boot:run

# 3. 启动前端（端口 8081，代理 /api 到 8080）
cd ../frontend
npm install
npm run dev
```

## 接口清单

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users | 查询全部用户（order=id\|username 指定排序） |
| GET | /api/users/{id} | 按 ID 查询用户 |
| POST | /api/users | 新增用户（用户名/密码/邮箱非空校验，密码 BCrypt 加密） |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |
| GET | /api/users/export | 导出用户列表 CSV |

接口文档：启动后端后访问 http://localhost:8080/swagger-ui.html（springdoc 自动生成）。

## CI

推送到 main 或发起 PR 时，GitHub Actions 自动执行 `mvn -B verify`（编译 + 单元测试），
测试使用内存数据库 H2（MySQL 模式），无需外部 MySQL。
