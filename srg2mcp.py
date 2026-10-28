#!/usr/bin/env python3
"""Troca os nomes SRG (func_123_a / field_123_b) do codigo pelos nomes MCP (stable_22).
Uso: python3 tools/srg2mcp.py <pasta_para_procurar_csv> <pasta_do_codigo>"""
import csv, os, re, sys

root, src = sys.argv[1], sys.argv[2]

def find(name):
    best = None
    for d, _, files in os.walk(root):
        if name in files:
            p = os.path.join(d, name)
            if best is None or ("stable" in p and "stable" not in best):
                best = p
    return best

mapping = {}
for n in ("methods.csv", "fields.csv"):
    p = find(n)
    if not p:
        sys.exit("nao achei " + n + " em " + root)
    print("usando", p)
    with open(p, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            mapping[row["searge"]] = row["name"]
print(len(mapping), "nomes no mapa")

rx = re.compile(r"\b(?:func|field)_\d+_[A-Za-z0-9_]+\b")
changed = missing = 0
unknown = set()
for d, _, files in os.walk(src):
    for fn in files:
        if not fn.endswith(".java"):
            continue
        path = os.path.join(d, fn)
        with open(path, encoding="utf-8") as f:
            text = f.read()
        def sub(m):
            global changed
            k = m.group(0)
            if k in mapping:
                changed += 1
                return mapping[k]
            unknown.add(k)
            return k
        new = rx.sub(sub, text)
        if new != text:
            with open(path, "w", encoding="utf-8") as f:
                f.write(new)
print(changed, "trocas feitas;", len(unknown), "nomes sem mapa:", sorted(unknown)[:20])
