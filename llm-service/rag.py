# -*- coding: utf-8 -*-
"""RAG 核心（实验五步骤 3 后半）：检索 top-k → 拼入提示 → ask() → 引用编号校验。"""
import re

from llm_client import ask
from vectorizer import TfIdfIndex

INDEX = TfIdfIndex.load("knowledge/index.json")


def retrieve(question: str, k: int = 5):
    """提问向量化 → 余弦相似度排序 → top-k 资料块。

    课程文档仅 16 块，取 top-5 以保证"目标 + 步骤要点"这类跨块信息能全部进入上下文。
    """
    return INDEX.search(question, k)


def rag_ask(question: str):
    """完整 RAG 问答：返回 (回答, 命中资料, 有效引用编号)。"""
    hits = retrieve(question)
    context = "\n\n".join(f"[{i + 1}] {h['text']}" for i, h in enumerate(hits))
    answer = ask(question, context)
    # 输出校验：提取 [n] 引用，编号超出资料范围的视为无效
    cited = sorted({int(n) for n in re.findall(r"\[(\d)\]", answer)})
    valid = [n for n in cited if 1 <= n <= len(hits)]
    return answer, hits, valid


if __name__ == "__main__":
    q = "《团队规范项目实训》的实验五要求我们完成哪些内容？"
    answer, hits, valid = rag_ask(q)
    print(">>> rag_ask(" + repr(q) + ")")
    print()
    print("回答：")
    print(answer)
    print()
    print(f"命中资料 top-{len(hits)}（有效引用编号 {valid}）：")
    for i, h in enumerate(hits):
        print(f"  [{i + 1}] chunk {h['id']}  score={h['score']}  {h['text'][:46]}...")
