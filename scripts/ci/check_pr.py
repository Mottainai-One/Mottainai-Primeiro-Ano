"""Validate PR metadata from the event file, never interpolate it into a shell."""
import json
import os
import re
from pathlib import Path


def validate(title, body):
    errors = []
    if not re.fullmatch(r"(?:feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(?:\([\w./-]+\))?!?: \S.+", title):
        errors.append("Use a Conventional Commits title, e.g. ci: add backend checks")
    body = re.sub(r"<!--.*?-->", "", body, flags=re.S)
    body = re.sub(r"```.*?```|~~~.*?~~~", "", body, flags=re.S)
    for heading in ("Description", "Changes Made", "Validation"):
        match = re.search(rf"^## {heading}\s*\n(.*?)(?=^## |\Z)", body, re.M | re.S)
        content = match[1] if match else ""
        content = re.sub(r"^\s*- \[[ xX]\].*$|^---$", "", content, flags=re.M).strip()
        if len(content) < 20 or any(placeholder in content.lower() for placeholder in (
            "provide a clear", "describe your", "add details here", "todo", "tbd",
        )):
            errors.append(f"Fill in {heading} with concrete changes or validation evidence")
    return errors


if __name__ == "__main__":
    pr = json.loads(Path(os.environ["GITHUB_EVENT_PATH"]).read_text(encoding="utf-8"))["pull_request"]
    errors = validate(pr["title"], pr.get("body") or "")
    if errors:
        raise SystemExit("\n".join(errors))
    print("PR title and description are valid.")
