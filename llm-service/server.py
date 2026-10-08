# -*- coding: utf-8 -*-
"""FastAPI 网关服务（实验五步骤 4）：POST /ask → RAG 问答，供 Spring Boot 转调。

启动：python -m uvicorn server:app --host 127.0.0.1 --port 8000
"""
import hashlib

from fastapi import FastAPI
from pydantic import BaseModel, Field

from rag import rag_ask

app = FastAPI(title="llm-service", version="1.0.0")


class AskBody(BaseModel):
    question: str = Field(min_length=1, max_length=600)


@app.get("/health")
def health():
    return {"status": "ok", "service": "llm-service"}


@app.post("/ask")
def ask(body: AskBody):
    q = body.question
    # Spring Boot 侧 PromptGuard 已用 <user_input> 包裹；此处解包恢复原始问题再拼提示
    prefix, suffix = "<user_input>\n", "\n</user_input>"
    if q.startswith(prefix) and q.endswith(suffix):
        q = q[len(prefix):-len(suffix)].strip()
    # 日志脱敏：只记长度与哈希，不落原始问题内容
    digest = hashlib.sha256(q.encode("utf-8")).hexdigest()[:8]
    print(f"[ask] len={len(q)} hash={digest}")
    answer, hits, valid = rag_ask(q)
    return {"answer": answer,
            "sources": [{"id": h["id"], "text": h["text"]} for h in hits],
            "cited": valid}
