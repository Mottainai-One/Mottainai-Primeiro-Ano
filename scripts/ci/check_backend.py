"""Fail if packaging or test execution was silently skipped."""
from pathlib import Path
import xml.etree.ElementTree as ET

target = Path("back-end/jdbc/mottainai-jdbc/target")
wars = list(target.glob("*.war"))
if not wars or not all(war.stat().st_size for war in wars):
    raise SystemExit("No non-empty WAR was produced")
executed = 0
for path in (target / "surefire-reports").glob("TEST-*.xml"):
    suite = ET.parse(path).getroot()
    if int(suite.get("errors", 0)) or int(suite.get("failures", 0)):
        raise SystemExit("JUnit contains failures or errors")
    executed += int(suite.get("tests", 0)) - int(suite.get("skipped", 0))
if executed < 1:
    raise SystemExit("At least one JUnit test must execute; skipped tests do not count")
if not (target / "site/jacoco/jacoco.xml").is_file():
    raise SystemExit("JaCoCo XML report was not generated")
print(f"WAR generated, {executed} tests executed, JaCoCo report generated.")
