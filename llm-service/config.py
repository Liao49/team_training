# -*- coding: utf-8 -*-
"""LLM 客户端配置（实验五）。API Key 只从环境变量读取，不写入代码。"""
import os
import winreg

BASE_URL = "https://open.bigmodel.cn/api/paas/v4"   # 智谱 BigModel 的 OpenAI 兼容端点
CHAT_MODEL = "glm-4-flash"                           # 免费聊天模型，满足课程问答场景


def load_api_key() -> str:
    # 优先读环境变量；读不到再从注册表用户环境取（setx 写入的位置）
    key = os.environ.get("ZHIPU_API_KEY")
    if key:
        return key
    with winreg.OpenKey(winreg.HKEY_CURRENT_USER, "Environment") as k:
        key, _ = winreg.QueryValueEx(k, "ZHIPU_API_KEY")
    return key


from openai import OpenAI

client = OpenAI(api_key=load_api_key(), base_url=BASE_URL)
