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

## CI

推送到 main 或发起 PR 时，GitHub Actions 自动执行 `mvn -B verify`（编译 + 单元测试），
测试使用内存数据库 H2（MySQL 模式），无需外部 MySQL。
