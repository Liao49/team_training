# -*- coding: utf-8 -*-
"""本地 TF-IDF 向量化（实验五 RAG"向量化"环节）。

设计取舍（真实决策，报告中如实说明）：
- 智谱 embedding-3 为付费接口（本账户无资源包，调用返回 429 余额不足）；
- 课程文档规模小（约百个知识块），本地 TF-IDF 即可稳定完成"向量化 → 余弦检索"，
  且零成本、离线可复现、结果确定性可验证；
- 若需替换为商用向量接口（embedding-3 / text-embedding-v3），只需替换本模块的
  vectorize 实现，检索与提示拼接逻辑完全不变。

特征设计：英文/数字连续串作为整体词 + 中文字符 2-gram（对中文检索友好）。
"""
import json
import math
import re
from collections import Counter

_WORD = re.compile(r"[A-Za-z0-9]+")


def tokenize(text: str) -> Counter:
    """文本 → 词频表：英文/数字串整体小写化；其余按字符 2-gram。"""
    compact = re.sub(r"\s+", "", text)
    tokens = [w.lower() for w in _WORD.findall(compact)]
    tokens += [compact[i:i + 2] for i in range(len(compact) - 1)]
    return Counter(tokens)


class TfIdfIndex:
    """稀疏 TF-IDF 索引：build → save/load → search（余弦相似度 top-k）。"""

    def __init__(self):
        self.chunks = []      # [{"id": int, "text": str}]
        self.df = Counter()   # 词 → 出现该词的文档数
        self.docvecs = []     # 每个文档 L2 归一化后的稀疏向量 {token: weight}

    @classmethod
    def build(cls, chunks):
        idx = cls()
        idx.chunks = chunks
        counts = [tokenize(c["text"]) for c in chunks]
        for c in counts:
            idx.df.update(c.keys())
        idx.docvecs = [idx._vec(c) for c in counts]
        return idx

    def _vec(self, counts: Counter) -> dict:
        """TF-IDF 权重并做 L2 归一化（归一化后余弦相似度 = 点积）。"""
        n = len(self.chunks)
        v = {t: (1 + math.log(c)) * (math.log((n + 1) / (self.df.get(t, 0) + 1)) + 1.0)
             for t, c in counts.items()}
        norm = math.sqrt(sum(w * w for w in v.values())) or 1.0
        return {t: w / norm for t, w in v.items()}

    def vectorize(self, text: str) -> dict:
        return self._vec(tokenize(text))

    @staticmethod
    def cosine(a: dict, b: dict) -> float:
        small, big = (a, b) if len(a) <= len(b) else (b, a)
        return sum(w * big.get(t, 0.0) for t, w in small.items())

    def search(self, query: str, k: int = 3):
        """提问向量化 → 与全部文档向量算余弦 → top-k。"""
        q = self.vectorize(query)
        scored = [(self.cosine(q, self.docvecs[i]), i) for i in range(len(self.chunks))]
        scored.sort(reverse=True)
        return [dict(self.chunks[i], score=round(s, 4)) for s, i in scored[:k]]

    def save(self, path: str):
        with open(path, "w", encoding="utf-8") as f:
            json.dump({"chunks": self.chunks, "df": dict(self.df), "docvecs": self.docvecs},
                      f, ensure_ascii=False)

    @classmethod
    def load(cls, path: str):
        with open(path, encoding="utf-8") as f:
            d = json.load(f)
        idx = cls()
        idx.chunks = d["chunks"]
        idx.df = Counter(d["df"])
        idx.docvecs = d["docvecs"]
        return idx
