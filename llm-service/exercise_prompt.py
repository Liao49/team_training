# -*- coding: utf-8 -*-
"""提示工程小练习（实验五步骤 1）：给定需求 → 稳定输出 JSON。

对比两种 System 提示的输出稳定性：
- 严格提示：字段定义 + 少样本示例 + "只输出 JSON" 约束 + temperature=0；
- 宽松提示：无结构约束，temperature=0.7。
"""
import json

from config import client, CHAT_MODEL

SYSTEM_STRICT = """你是课程助教。将学生的需求解析为 JSON，且只输出 JSON，不要输出任何其他文字。
字段定义：
{"type": "功能需求" | "缺陷修复" | "其他", "模块": <string>, "优先级": "高" | "中" | "低"}
示例：
输入：登录页面点击按钮没反应
输出：{"type": "缺陷修复", "模块": "登录", "优先级": "高"}"""

SYSTEM_LOOSE = "你是课程助教，请分析学生的需求。"

DEMANDS = ["希望支持导出用户列表", "首页图片加载太慢了", "下周想加个深色模式"]


def parse_demand(demand: str, system: str, temperature: float) -> str:
    """调用对话接口，返回模型原始输出文本。"""
    resp = client.chat.completions.create(
        model=CHAT_MODEL,
        messages=[{"role": "system", "content": system},
                  {"role": "user", "content": demand}],
        temperature=temperature,
    )
    return resp.choices[0].message.content


def parse_demand_json(demand: str) -> dict:
    """严格提示下的结构化解析：输出经 json.loads 验证，解析失败自动重试一次。"""
    for attempt in (1, 2):
        out = parse_demand(demand, SYSTEM_STRICT, 0)
        try:
            return json.loads(out)
        except json.JSONDecodeError:
            if attempt == 2:
                raise
            print(f"  [重试] 第 {attempt} 次输出非法 JSON：{out}")


if __name__ == "__main__":
    print("== 1) 严格提示（字段定义 + 少样本 + 只输出JSON + temperature=0）")
    print("      3 个需求 × 3 次重复调用，验证稳定性 ==")
    for d in DEMANDS:
        outs = [parse_demand(d, SYSTEM_STRICT, 0) for _ in range(3)]
        parsed = []
        for o in outs:
            try:
                parsed.append(json.loads(o))
            except json.JSONDecodeError:
                parsed.append(None)
        all_ok = all(p is not None for p in parsed)
        stable = "3 次输出一致" if len(set(outs)) == 1 else "3 次输出不一致!"
        status = "全部为合法 JSON" if all_ok else "存在非法 JSON!"
        print(f"  {d}")
        print(f"    -> {json.dumps(parsed[0], ensure_ascii=False)}   [{status}；{stable}]")

    print()
    print("== 2) 宽松提示（无结构约束 + temperature=0.7），同样需求的输出 ==")
    for d in DEMANDS[:2]:
        out = parse_demand(d, SYSTEM_LOOSE, 0.7)
        show = out if len(out) <= 60 else out[:57] + "..."
        try:
            json.loads(out)
            ok = "是"
        except json.JSONDecodeError:
            ok = "否"
        print(f"  {d}")
        print(f"    -> {show}   [可直接 json.loads 解析？{ok}]")
