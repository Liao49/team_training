# -*- coding: utf-8 -*-
"""知识库构建（实验五步骤 3 前半）：课程实践方案 → 切块 → TF-IDF 向量化 → index.json。"""
import re

from vectorizer import TfIdfIndex

HEADING = re.compile(r"^(实验[一二三四五六七八九十]+|第[一二三四五六七八九十]+部分|\d+\.(\d+\.?)?\s|[一二三四五六七八九十]+、)")


def split_chunks(text: str, size: int = 450):
    """先按标题行切段，段内按行贪心装包到约 size 字；chunk 首行保留标题便于溯源。"""
    sections, title, buf = [], "", []
    for raw in text.splitlines():
        line = raw.strip()
        if not line or "PAGEREF" in line:
            continue
        if HEADING.match(line) and len(line) < 40:
            if buf:
                sections.append((title, buf))
            title, buf = line, []
        else:
            buf.append(line)
    if buf:
        sections.append((title, buf))

    chunks = []
    for t, lines in sections:
        pack = ""
        for ln in lines:
            if pack and len(pack) + len(ln) + 1 > size:
                chunks.append((t + "\n" + pack).strip())
                pack = ln
            else:
                pack = (pack + "\n" + ln) if pack else ln
        if pack.strip():
            chunks.append((t + "\n" + pack).strip())
    return [{"id": i, "text": c} for i, c in enumerate(chunks)]


if __name__ == "__main__":
    with open("knowledge/课程实践方案.txt", encoding="utf-8") as f:
        text = f.read()
    chunks = split_chunks(text)
    index = TfIdfIndex.build(chunks)
    index.save("knowledge/index.json")

    lens = [len(c["text"]) for c in chunks]
    print(f"切块完成：{len(chunks)} 个知识块（最短 {min(lens)} 字 / 最长 {max(lens)} 字 / 平均 {sum(lens) // len(lens)} 字）")
    print(f"词表规模：{len(index.df)} 个特征词；向量已 L2 归一化（余弦 = 点积）")
    print("已保存：knowledge/index.json")
    print()
    print('自检：检索 "实验五要求我们完成哪些内容" top-5 命中 →')
    for h in index.search("实验五要求我们完成哪些内容", k=5):
        print(f"  [chunk {h['id']}] score={h['score']}  {h['text'][:46]}...")
