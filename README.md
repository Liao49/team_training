# team-training-user-mgmt

《团队规范实训》课程项目：用户管理系统（前后端分离 + LLM 智能问答）。

## 项目结构

- `user-management/`：Spring Boot 3 + MyBatis + MySQL 后端，提供 RESTful API
- `frontend/`：Vue 3 + Element Plus 前端（Vite 构建，开发端口 8081）
- `llm-service/`：Python FastAPI 服务，实验五 RAG 问答（端口 8000）

## 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 17 |
| Maven | 3.9 |
| MySQL | 8.0 |
| Node.js | 18+ |
| Python | 3.10+（依赖：fastapi、uvicorn、openai、scikit 无需，纯标准实现） |

## 环境变量

后端启动前需设置（密钥不写入代码库）：

```bash
set DB_PASSWORD=你的MySQL密码
set JWT_SECRET=任意不少于32字符的密钥串
```

llm-service 需设置智谱 API Key（ZHIPU_API_KEY），从 https://open.bigmodel.cn 控制台获取。

## 数据库初始化

```bash
mysql -uroot -p < user-management/sql/init.sql       # 建库建表 + 初始用户
mysql -uroot -p < user-management/sql/exp4_rbac.sql  # 实验四：角色与权限表
```

演示账号：admin（管理员，密码 admin@2026）/ zsan（普通用户，密码 zsan@2026）。

## 启动步骤

```bash
# 1. 后端（端口 8080）
cd user-management
set DB_PASSWORD=你的数据库密码
set JWT_SECRET=你的JWT密钥
mvn spring-boot:run

# 2. 前端（端口 8081，/api 代理到 8080）
cd frontend
npm install
npm run dev

# 3. llm-service（实验五，端口 8000，不启动则智能问答接口降级报错）
cd llm-service
pip install fastapi uvicorn openai
python -m uvicorn server:app --host 127.0.0.1 --port 8000
```

说明：`llm-service/knowledge/index.json` 为检索索引，可用 `python build_index.py` 从课程文档重建。

## 接口清单

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录，返回 JWT |
| GET | /api/users/me | 当前登录用户信息 |
| GET | /api/users | 查询全部用户（order=id\|username 指定排序） |
| GET | /api/users/{id} | 按 ID 查询用户 |
| POST | /api/users | 新增用户（仅 ADMIN，密码 BCrypt 加密） |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户（仅 ADMIN） |
| GET | /api/users/export | 导出用户列表 CSV |
| POST | /api/ask | 智能问答（实验五，限流 5 次/分钟/用户） |

接口文档：启动后端后访问 http://localhost:8080/swagger-ui.html（springdoc 自动生成）。

## CI

推送到 main 或发起 PR 时，GitHub Actions 自动执行 `mvn -B verify`（编译 + 单元测试），
测试使用内存数据库 H2（MySQL 模式），无需外部 MySQL。
