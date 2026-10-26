from pathlib import Path
import subprocess
import sys
import tempfile
import unittest


SCRIPT = Path("scripts/ci/check_backend.py").resolve()


class BackendReportTests(unittest.TestCase):
    def run_guard(self, tests, skipped=0, failures=0, war=True):
        with tempfile.TemporaryDirectory() as directory:
            target = Path(directory) / "back-end/jdbc/mottainai-jdbc/target"
            (target / "surefire-reports").mkdir(parents=True)
            (target / "site/jacoco").mkdir(parents=True)
            if war:
                (target / "application.war").write_bytes(b"test fixture")
            (target / "site/jacoco/jacoco.xml").write_text("<report/>", encoding="utf-8")
            (target / "surefire-reports/TEST-fixture.xml").write_text(
                f'<testsuite tests="{tests}" skipped="{skipped}" failures="{failures}" errors="0"/>',
                encoding="utf-8")
            return subprocess.run([sys.executable, str(SCRIPT)], cwd=directory,
                                  capture_output=True, text=True).returncode

    def test_nonzero_executed_tests_and_war_pass(self):
        self.assertEqual(0, self.run_guard(2))

    def test_zero_or_all_skipped_tests_fail(self):
        self.assertNotEqual(0, self.run_guard(0))
        self.assertNotEqual(0, self.run_guard(2, skipped=2))

    def test_failures_or_missing_war_fail(self):
        self.assertNotEqual(0, self.run_guard(2, failures=1))
        self.assertNotEqual(0, self.run_guard(2, war=False))
