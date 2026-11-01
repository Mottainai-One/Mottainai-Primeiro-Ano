"""Validate populated landing files and local references without fetching URLs."""
from html.parser import HTMLParser
from pathlib import Path
import os
import re
import subprocess
from urllib.parse import unquote, urlsplit

ROOT = Path("front-end/landing").resolve()


def check_path(source, reference):
    url = urlsplit(reference.strip())
    if url.scheme or url.netloc or not url.path:
        return
    local = unquote(url.path)
    target = (ROOT / local.lstrip("/") if local.startswith("/") else source.parent / local).resolve()
    if not target.is_relative_to(ROOT) or not target.exists():
        raise ValueError(f"Broken or out-of-root reference in {source.relative_to(ROOT)}: {reference}")
    if target.is_file() and not target.stat().st_size:
        raise ValueError(f"Reference points to an empty placeholder: {reference}")


class References(HTMLParser):
    def __init__(self, source):
        super().__init__()
        self.source = source

    def handle_starttag(self, tag, attrs):
        for key, value in attrs:
            if value and key in {"src", "href", "poster"}:
                check_path(self.source, value)
            if value and key == "srcset" and not value.startswith("data:"):
                for candidate in value.split(","):
                    check_path(self.source, candidate.strip().split()[0])


def main():
    files = [p for p in ROOT.rglob("*") if p.is_file() and p.suffix in {".html", ".css", ".js"}
             and p.read_text(encoding="utf-8").strip()]
    if not files:
        message = "Landing contains only placeholders; no HTML/CSS/JavaScript content to validate yet."
        print(message)
        if os.environ.get("GITHUB_STEP_SUMMARY"):
            with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as summary:
                summary.write(message + "\n")
        return
    for path in files:
        content = path.read_text(encoding="utf-8")
        if path.suffix == ".html":
            subprocess.run(["node", "node_modules/htmlhint/bin/htmlhint", str(path)], check=True)
            References(path).feed(content)
        elif path.suffix == ".css":
            subprocess.run(["node", "node_modules/stylelint/bin/stylelint.mjs", str(path)], check=True)
            for match in re.finditer(r"url\(\s*['\"]?([^)'\"]+)['\"]?\s*\)", content):
                check_path(path, match[1].strip())
            for match in re.finditer(r"@import\s+['\"]([^'\"]+)['\"]", content):
                check_path(path, match[1])
        else:
            subprocess.run(["node", "--check", str(path)], check=True)
    print(f"Validated {len(files)} populated landing files.")


if __name__ == "__main__":
    main()
