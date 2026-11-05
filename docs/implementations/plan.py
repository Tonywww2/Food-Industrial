#!/usr/bin/env python3
"""实装计划工具：新建计划、重建索引、检查链接。

用法（在任意目录运行均可）：
    python docs/implementations/plan.py new  <slug> "<标题>" [--kind system|content]
                                              [--stage <阶段>] [--deps I05,I08]
                                              [--docs ../additives/15-batch-4.md ...]
                                              [--packages <代码位置>]
    python docs/implementations/plan.py index
    python docs/implementations/plan.py check

- new   ：取下一个编号，按模板生成 I<NN>-<slug>.md，并重建索引。
- index ：从各计划的表头重建 README.md 中两段自动生成的表格。
- check ：检查编号唯一、依赖存在且无环、状态合法、所有相对链接指向存在的文件。

只用 Python 3 标准库。
"""
import argparse
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
README = os.path.join(HERE, "README.md")
PLAN_RE = re.compile(r"^I(\d{2,3})-[a-z0-9-]+\.md$")
LINK_RE = re.compile(r"\[([^\]]+)\]\(([^)#\s]+)(#[^)]*)?\)")
STATUSES = ("未开始", "进行中", "已完成", "搁置", "阻塞")
STAGES = ("A 基础", "B 物料", "C 框架", "D 设备", "E 内容", "F 联动与验证")
MARKERS = {
    "index": ("<!-- plan-index:start -->", "<!-- plan-index:end -->"),
    "docs": ("<!-- doc-map:start -->", "<!-- doc-map:end -->"),
    "graph": ("<!-- dep-graph:start -->", "<!-- dep-graph:end -->"),
}


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def write(path, text):
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)


def plan_files():
    return sorted(f for f in os.listdir(HERE) if PLAN_RE.match(f))


def parse_plan(name):
    text = read(os.path.join(HERE, name))
    lines = text.splitlines()
    title = lines[0].lstrip("# ").strip() if lines else name
    plan_id, _, plan_title = title.partition(" ")
    header = {}
    for line in lines[1:]:
        if line.startswith("## "):
            break
        m = re.match(r"^\|\s*([^|]+?)\s*\|\s*(.*?)\s*\|\s*$", line)
        if m and m.group(1) not in ("项", "---"):
            header[m.group(1)] = m.group(2)
    deps = re.findall(r"I\d{2,3}", header.get("依赖", ""))
    docs = [href for _, href, _ in LINK_RE.findall(header.get("原文档", ""))]
    body = re.sub(r"```.*?```", "", text, flags=re.S)
    body = re.sub(r"`[^`\n]*`", "", body)
    refs = [href for _, href, _ in LINK_RE.findall(body)
            if href.endswith(".md") and not PLAN_RE.match(os.path.basename(href))]
    return {
        "file": name,
        "id": plan_id,
        "title": plan_title,
        "status": header.get("状态", ""),
        "stage": header.get("阶段", ""),
        "deps": deps,
        "docs": docs,
        "refs": refs,
        "text": text,
    }


def replace_block(text, key, body):
    start, end = MARKERS[key]
    pattern = re.compile(re.escape(start) + r".*?" + re.escape(end), re.S)
    if not pattern.search(text):
        sys.exit(f"README.md 缺少标记 {start} … {end}")
    return pattern.sub(lambda _: f"{start}\n{body}\n{end}", text)


def doc_label(href):
    rel = os.path.normpath(os.path.join(HERE, href))
    root = os.path.normpath(os.path.join(HERE, "..", ".."))
    return os.path.relpath(rel, root).replace(os.sep, "/")


def build_index(plans):
    rows = ["| 编号 | 计划 | 阶段 | 状态 | 依赖 |", "| --- | --- | --- | --- | --- |"]
    for p in plans:
        deps = "、".join(p["deps"]) or "—"
        rows.append(f"| [{p['id']}]({p['file']}) | {p['title']} | {p['stage']} | {p['status']} | {deps} |")
    return "\n".join(rows)


def build_doc_map(plans):
    mapping = {}
    for p in plans:
        for href in p["refs"]:
            mapping.setdefault(href, []).append(p["id"])
    rows = ["| 设计文档 | 引用它的计划 |", "| --- | --- |"]
    for href in sorted(mapping, key=doc_label):
        ids = "、".join(f"[{i}]({next(q['file'] for q in plans if q['id'] == i)})" for i in sorted(set(mapping[href])))
        rows.append(f"| [{doc_label(href)}]({href}) | {ids} |")
    return "\n".join(rows)


def build_graph(plans):
    lines = ["```mermaid", "flowchart LR"]
    for p in plans:
        lines.append(f"  {p['id']}[\"{p['id']} {p['title']}\"]")
    for p in plans:
        for d in p["deps"]:
            lines.append(f"  {d} --> {p['id']}")
    lines.append("```")
    return "\n".join(lines)


def cmd_index(_args=None):
    plans = [parse_plan(f) for f in plan_files()]
    text = read(README)
    text = replace_block(text, "index", build_index(plans))
    text = replace_block(text, "docs", build_doc_map(plans))
    text = replace_block(text, "graph", build_graph(plans))
    write(README, text)
    print(f"索引已更新：{len(plans)} 份计划")


def cmd_check(_args=None):
    plans = [parse_plan(f) for f in plan_files()]
    problems = []
    ids = {}
    for p in plans:
        if not re.fullmatch(r"I\d{2,3}", p["id"]) or not p["file"].startswith(p["id"] + "-"):
            problems.append(f"{p['file']}：标题编号与文件名不一致")
        if p["id"] in ids:
            problems.append(f"{p['file']}：编号与 {ids[p['id']]} 重复")
        ids[p["id"]] = p["file"]
        if p["status"] not in STATUSES:
            problems.append(f"{p['file']}：状态“{p['status']}”不在 {STATUSES} 中")
        if p["stage"] not in STAGES:
            problems.append(f"{p['file']}：阶段“{p['stage']}”不在 {STAGES} 中")
        if not p["docs"]:
            problems.append(f"{p['file']}：原文档一栏没有链接")
    for p in plans:
        for d in p["deps"]:
            if d not in ids:
                problems.append(f"{p['file']}：依赖 {d} 不存在")
    graph = {p["id"]: p["deps"] for p in plans}
    state = {}

    def visit(node, stack):
        state[node] = 1
        for nxt in graph.get(node, []):
            if state.get(nxt) == 1:
                problems.append("依赖成环：" + " → ".join(stack + [node, nxt]))
            elif state.get(nxt) is None:
                visit(nxt, stack + [node])
        state[node] = 2

    for node in graph:
        if state.get(node) is None:
            visit(node, [])
    for name in plan_files() + ["README.md"]:
        body = re.sub(r"```.*?```", "", read(os.path.join(HERE, name)), flags=re.S)
        body = re.sub(r"`[^`\n]*`", "", body)
        for _, href, _ in LINK_RE.findall(body):
            if re.match(r"^[a-z]+://", href):
                continue
            if not os.path.exists(os.path.normpath(os.path.join(HERE, href))):
                problems.append(f"{name}：链接目标不存在 {href}")
    if problems:
        print("\n".join(problems))
        sys.exit(1)
    print(f"检查通过：{len(plans)} 份计划")


def cmd_new(args):
    if not re.fullmatch(r"[a-z0-9-]+", args.slug):
        sys.exit("slug 只能用小写字母、数字和连字符")
    if args.stage not in STAGES:
        sys.exit(f"阶段必须是 {STAGES} 之一")
    numbers = [int(PLAN_RE.match(f).group(1)) for f in plan_files()]
    plan_id = f"I{(max(numbers) + 1 if numbers else 1):02d}"
    name = f"{plan_id}-{args.slug}.md"
    template = read(os.path.join(HERE, f"_template-{args.kind}.md"))
    docs = "、".join(f"[{doc_label(d)}]({d})" for d in args.docs) or "（填写）"
    deps = "、".join(x.strip() for x in args.deps.split(",") if x.strip()) or "—"
    text = (template.replace("{{ID}}", plan_id).replace("{{TITLE}}", args.title)
            .replace("{{STAGE}}", args.stage).replace("{{DEPS}}", deps)
            .replace("{{DOCS}}", docs).replace("{{PACKAGES}}", args.packages or "（填写）"))
    write(os.path.join(HERE, name), text)
    print(f"已创建 {name}")
    cmd_index()


def main():
    parser = argparse.ArgumentParser(description="实装计划工具")
    sub = parser.add_subparsers(dest="cmd", required=True)
    new = sub.add_parser("new", help="新建计划")
    new.add_argument("slug")
    new.add_argument("title")
    new.add_argument("--kind", choices=("system", "content"), default="system")
    new.add_argument("--stage", default="E 内容")
    new.add_argument("--deps", default="")
    new.add_argument("--docs", nargs="*", default=[])
    new.add_argument("--packages", default="")
    sub.add_parser("index", help="重建 README 索引")
    sub.add_parser("check", help="检查计划")
    args = parser.parse_args()
    {"new": cmd_new, "index": cmd_index, "check": cmd_check}[args.cmd](args)


if __name__ == "__main__":
    main()
