# -*- coding: utf-8 -*-
"""LLM 客户端配置（实验五）。

密钥安全约定：
- ZHIPU_API_KEY 只从环境变量读取；终端会话未继承时从 HKCU 用户环境（setx 写入处）读取；
- 密钥绝不写入代码、日志或 git 仓库，报告截图全程脱敏。
"""
import os
import winreg

BASE_URL = "https://open.bigmodel.cn/api/paas/v4"   # 智谱 BigModel 的 OpenAI 兼容端点
CHAT_MODEL = "glm-4-flash"                           # 免费聊天模型，满足课程问答场景


def load_api_key() -> str:
    key = os.environ.get("ZHIPU_API_KEY")
    if key:
        return key
    with winreg.OpenKey(winreg.HKEY_CURRENT_USER, "Environment") as k:
        key, _ = winreg.QueryValueEx(k, "ZHIPU_API_KEY")
    return key


from openai import OpenAI

client = OpenAI(api_key=load_api_key(), base_url=BASE_URL)
