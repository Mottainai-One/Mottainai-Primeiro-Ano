"""Exercise real linters on temporary fixtures even while the landing is empty."""
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch

from scripts.ci import check_frontend


class FrontendTests(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.root = Path(self.directory.name).resolve()
        self.root_patch = patch.object(check_frontend, "ROOT", self.root)
        self.root_patch.start()
        self.addCleanup(self.root_patch.stop)

    def write(self, name, content):
        path = self.root / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")
        return path

    def test_valid_page_styles_and_script(self):
        self.write("index.html", '<!DOCTYPE html><html><head><title>Test</title>'
                   '<link rel="stylesheet" href="style.css"></head><body>'
                   '<script src="app.js"></script></body></html>')
        self.write("style.css", "body { color: #123456; }\n")
        self.write("app.js", "const message = 'test';\n")
        check_frontend.main()

    def test_html_error_fails(self):
        self.write("index.html", '<!DOCTYPE html><html><head><title>Test</title>'
                   '</head><body><div></body></html>')
        with self.assertRaises(subprocess.CalledProcessError):
            check_frontend.main()

    def test_css_error_fails(self):
        self.write("style.css", "body { color: #zzzzzz; }\n")
        with self.assertRaises(subprocess.CalledProcessError):
            check_frontend.main()

    def test_javascript_error_fails(self):
        self.write("app.js", "const = ;\n")
        with self.assertRaises(subprocess.CalledProcessError):
            check_frontend.main()

    def test_missing_empty_and_escaping_paths_fail(self):
        source = self.write("index.html", "placeholder")
        self.write("empty.css", "")
        for reference in ("missing.png", "empty.css", "../outside.txt"):
            with self.subTest(reference=reference), self.assertRaises(ValueError):
                check_frontend.check_path(source, reference)

    def test_external_urls_and_fragments_are_not_fetched(self):
        source = self.root / "index.html"
        for reference in ("https://example.com/image.png", "//example.com/a", "#section", "mailto:a@example.com"):
            check_frontend.check_path(source, reference)
