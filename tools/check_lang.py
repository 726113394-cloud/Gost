#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Gost 语言文件一致性校验。

用途：
  1. 六种语言文件的 key 集合必须完全一致（少一个 key 就会有一种语言显示成 key 本身）
  2. 代码里引用的 key 必须存在于语言文件
  3. 反向检查：语言文件里有、但代码里没人用的 key（仅提示，不算失败）

退出码：0 = 通过，1 = 有问题（可直接用于 CI）

用法：
    python tools/check_lang.py
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LANG_DIR = ROOT / "resources" / "lang"
SRC_DIR = ROOT / "main"

LANGUAGES = ["zh_CN", "en_US", "ja_JP", "es_ES", "fr_FR", "de_DE"]

# 取值/发送消息的方法名
I18N_METHODS = ("getMessage", "sendMessage", "sendTitle", "sendActionBar", "getMessageFor",
               "send", "msg", "lang")

# key 形态：小写字母开头，含点号分层，允许数字与连字符
KEY_RE = re.compile(r"^[a-z][a-z0-9-]*(?:\.[a-z0-9-]+)+$")

# 顶层 key: value（不处理嵌套 section，语言文件目前是扁平结构）
LINE_RE = re.compile(r"^([A-Za-z0-9_.-]+):\s*(.*)$")


def load_keys(path):
    keys = {}
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        m = LINE_RE.match(line)
        if m:
            keys[m.group(1)] = m.group(2)
    return keys


# key: "值"  形式；值里不允许出现未转义的双引号
STRICT_RE = re.compile(r'^([A-Za-z][A-Za-z0-9_.-]*):\s*"(.*)"\s*$')


def validate_syntax(path):
    """行级语法校验。

    YAML 里值写坏（例如英文译文里出现未转义的 " ）会让整个文件解析失败，
    而插件侧只会退化成「只剩用户文件的旧键」——玩家看到大量 key。
    这种问题正则数 key 发现不了，必须在这里拦。
    """
    problems = []
    for lineno, raw in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        m = STRICT_RE.match(line)
        if not m:
            problems.append(f"{path.name}:{lineno} 不是 key: \"值\" 形式 -> {line[:70]}")
            continue
        value = m.group(2)
        if '"' in value.replace('\\"', ""):
            problems.append(f"{path.name}:{lineno} 值里有未转义的双引号 -> {line[:70]}")
    return problems


def collect_code_keys():
    """扫描 java 源码，收集 i18n 方法调用里出现的 key 字面量。"""
    found = {}  # key -> set of "file:line"

    call_re = re.compile(r"\b(" + "|".join(I18N_METHODS) + r")\s*\(([^;]*)\)")
    str_re = re.compile(r'"((?:[^"\\]|\\.)*)"')

    for java in SRC_DIR.rglob("*.java"):
        text = java.read_text(encoding="utf-8")
        for lineno, line in enumerate(text.splitlines(), start=1):
            if line.lstrip().startswith("*") or line.lstrip().startswith("//"):
                continue
            for call in call_re.finditer(line):
                for literal in str_re.findall(call.group(2)):
                    if KEY_RE.match(literal):
                        found.setdefault(literal, set()).add(f"{java.name}:{lineno}")
    return found


def main():
    problems = []

    # 1) 语言文件是否存在
    data = {}
    for lang in LANGUAGES:
        path = LANG_DIR / f"{lang}.yml"
        if not path.is_file():
            problems.append(f"缺少语言文件: {path}")
            continue
        data[lang] = load_keys(path)

    if not data:
        print("没有可校验的语言文件")
        return 1

    # 2) 六语言键集一致性
    reference = LANGUAGES[0] if LANGUAGES[0] in data else next(iter(data))
    ref_keys = set(data[reference])
    print(f"基准语言 {reference}: {len(ref_keys)} 个 key")

    for lang, keys in data.items():
        if lang == reference:
            continue
        missing = ref_keys - set(keys)
        extra = set(keys) - ref_keys
        if missing:
            problems.append(f"{lang} 缺少 {len(missing)} 个 key: {', '.join(sorted(missing)[:10])}"
                            + (" ..." if len(missing) > 10 else ""))
        if extra:
            problems.append(f"{lang} 多出 {len(extra)} 个 key: {', '.join(sorted(extra)[:10])}"
                            + (" ..." if len(extra) > 10 else ""))
        if not missing and not extra:
            print(f"  {lang}: 键集一致 ({len(keys)})")

    # 2b) 行级语法校验（防止值写坏导致整份文件解析失败）
    syntax_problems = []
    for lang in LANGUAGES:
        path = LANG_DIR / f"{lang}.yml"
        if path.is_file():
            syntax_problems.extend(validate_syntax(path))
    if syntax_problems:
        problems.extend(syntax_problems)
    else:
        print("  行级语法校验通过 ✓")

    # 3) 代码引用的 key 是否都在语言文件里
    code_keys = collect_code_keys()
    print(f"\n代码中引用的 key: {len(code_keys)} 个")
    undefined = sorted(k for k in code_keys if k not in ref_keys)
    if undefined:
        for key in undefined:
            where = ", ".join(sorted(code_keys[key])[:3])
            problems.append(f"代码引用了未定义的 key: {key}  ({where})")
    else:
        print("  全部已定义 ✓")

    # 4) 未被引用的 key（仅提示）
    unused = sorted(ref_keys - set(code_keys))
    if unused:
        print(f"\n提示：语言文件中有 {len(unused)} 个 key 未被代码引用（可能是历史残留或动态拼接）:")
        for key in unused[:20]:
            print(f"  - {key}")
        if len(unused) > 20:
            print(f"  ... 另有 {len(unused) - 20} 个")

    print()
    if problems:
        print(f"✗ 发现 {len(problems)} 个问题:")
        for p in problems:
            print(f"  - {p}")
        return 1

    print("✓ 语言文件校验通过")
    return 0


if __name__ == "__main__":
    sys.exit(main())
