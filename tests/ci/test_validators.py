import unittest
from scripts.ci.check_pr import validate


class PullRequestTests(unittest.TestCase):
    body = """## Description
Add automated build and JDBC integration checks.
## Changes Made
- Add Java 21, JUnit and PostgreSQL validation.
## Validation
Executed two JUnit tests against PostgreSQL 16.
"""

    def test_valid_metadata(self):
        self.assertEqual([], validate("ci: add backend checks", self.body))

    def test_invalid_title(self):
        self.assertTrue(validate("Updated stuff", self.body))

    def test_portuguese_evidence_is_not_a_placeholder(self):
        self.assertEqual([], validate("ci: add checks", self.body.replace(
            "Executed two JUnit tests against PostgreSQL 16.", "Todos os testes passaram com PostgreSQL 16.")))

    def test_missing_or_hidden_evidence(self):
        self.assertTrue(validate("ci: add checks", ""))
        self.assertTrue(validate("ci: add checks", "<!--" + self.body + "-->"))
        self.assertTrue(validate("ci: add checks", self.body.replace(
            "Executed two JUnit tests against PostgreSQL 16.", "- [x] Tests executed")))
