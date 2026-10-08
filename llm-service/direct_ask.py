# -*- coding: utf-8 -*-
"""无 RAG 直接问答演示（实验五：幻觉风险对比）。"""
from llm_client import ask

if __name__ == "__main__":
    q = "《团队规范项目实训》的实验五要求我们完成哪些内容？"
    print(">>> ask(" + repr(q) + ")      # 不带资料，直接问答")
    print()
    print(ask(q))
    print()
    q2 = "这门课一共要交几份实验报告？"
    print(">>> ask(" + repr(q2) + ")")
    print()
    print(ask(q2))
