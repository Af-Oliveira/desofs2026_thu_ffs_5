# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — JaCoCo Coverage Summary (vendnet-ci-cd.yml, lines 313-337)
# Parses the JaCoCo XML report and prints LINE, BRANCH, and INSTRUCTION
# coverage percentages to the build log.
# ─────────────────────────────────────────────────────────────────────────────

import sys
import xml.etree.ElementTree as ET

tree = ET.parse(sys.argv[1])
root = tree.getroot()
for counter in root.findall('counter'):
    ctype = counter.get('type', '')
    missed = int(counter.get('missed', 0))
    covered = int(counter.get('covered', 0))
    total = missed + covered
    pct = (covered / total * 100) if total > 0 else 0
    if ctype in ('LINE', 'BRANCH', 'INSTRUCTION'):
        print(f'  {ctype:12s}: {covered}/{total} = {pct:.2f}%')
