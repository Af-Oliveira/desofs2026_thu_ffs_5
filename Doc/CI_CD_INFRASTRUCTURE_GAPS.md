# CI/CD Infrastructure Gaps — Resolution Guide

> **Project:** VendNet — Vending Machine Network  
> **Phase:** 2 Sprint 1 — CI/CD Security Pipeline  
> **Date:** 2026-05-15  

---

## Overview

Three acceptance criteria cannot be fulfilled through code alone — they require configuration in the GitHub repository settings. This document provides step-by-step instructions for each.

| # | Story | Acceptance Criterion | Configuration Location |
|---|-------|---------------------|----------------------|
| 1 | US-SAST-003 | Branch protection rule: PR cannot merge if SonarQube quality gate fails | GitHub → Settings → Rules → Rulesets |
| 2 | US-SCA-002 | GitHub Dependency Graph populated + Dependabot alerts enabled | GitHub → Settings → Code security |
| 3 | US-SECRET-002 | Audit log of GitHub Actions secret access enabled | GitHub → Settings → Audit log (org-level) |

---

## 1. US-SAST-003 — Branch Protection Rule (SonarQube Quality Gate)

**Requirement:** PR cannot merge if SonarQube quality gate fails (0 new Critical/Blocker issues, coverage ≥80%, duplication ≤3%).

**Why this is GitHub-only:** Branch protection rules are enforced at the repository/org level on the GitHub platform. They cannot be defined in `.github/workflows/*.yml` files. The pipeline already runs SonarQube on every PR (Stage 6 in `ci-security.yml`); this setting blocks merges when the scan fails.

### Step-by-Step

1. Go to the repository on GitHub.
2. Click **Settings** → **Rules** → **Rulesets**.
3. Click **New ruleset** → **New branch ruleset**.
4. Configure:

| Field | Value |
|-------|-------|
| **Ruleset Name** | `main-branch-protection` |
| **Enforcement status** | Active |
| **Target branches** | Add target → Include default branch (`main`) |

5. Under **Branch protections**, enable:

| Rule | Setting |
|------|---------|
| **Require a pull request before merging** | Checked. Require approvals: **1**. Dismiss stale reviews: Checked. |
| **Require status checks to pass before merging** | Checked. Require branches to be up to date: Checked. |
| **Status checks that are required** | Search and add: `SonarQube Cloud - Quality Gate`, `Build & Unit Test`, `SAST - SpotBugs + Semgrep`, `SCA - Dependency Check + Maven Enforcer`, `ArchUnit - Layered Architecture Validation`, `Integration Tests + Abuse Cases + IAST` |
| **Block force pushes** | Checked |
| **Require code scanning results** | Optional — if CodeQL or Semgrep SARIF uploads are used, select the relevant tool. |

6. Click **Create**.

### Verification

```bash
# Create a branch with a deliberate quality gate violation
git checkout -b test-quality-gate main
echo " " >> vendnet/src/main/java/pt/isep/desofs/vendnet/VendnetApplication.java
git add . && git commit -m "test: trigger quality gate failure"
git push origin test-quality-gate
# Open a PR — the merge button will be blocked until all checks pass
```

---

## 2. US-SCA-002 — GitHub Dependency Graph + Dependabot

**Requirement:** GitHub Dependency Graph enabled and populated from `pom.xml`. Dependabot alerts enabled for the repository.

**Why this is GitHub-only:** The dependency graph is a GitHub platform feature that parses `pom.xml` (or `build.gradle`, `package.json`, etc.) and builds a dependency tree. Dependabot uses this graph to alert on known CVEs. Neither feature can be activated through code.

### Step-by-Step

#### 2.1 Enable Dependency Graph

1. Go to the repository on GitHub.
2. Click **Settings** → **Code security** (under "Security" section).
3. Under **Dependency graph**:
   - It should be **enabled by default** for public repositories.
   - For private repositories, verify it is enabled.
4. Confirm it works: go to **Insights** → **Dependency graph**. After a few minutes, you should see the full Maven dependency tree parsed from `pom.xml`.

#### 2.2 Enable Dependabot Alerts

1. Same page: **Settings** → **Code security**.
2. Under **Dependabot alerts**:
   - Click **Enable**.
   - These alert on known CVEs in your dependencies.
3. Under **Dependabot security updates**:
   - Click **Enable**.
   - This auto-opens PRs to bump vulnerable dependencies to patched versions.

#### 2.3 Create Dependabot Configuration (Optional — Auto Version Bumps)

Create `.github/dependabot.yml`:

```yaml
version: 2
updates:
  - package-ecosystem: "maven"
    directory: "/vendnet"
    schedule:
      interval: "weekly"
      day: "monday"
      time: "09:00"
      timezone: "Europe/Lisbon"
    open-pull-requests-limit: 10
    labels:
      - "dependencies"
      - "security"
    reviewers:
      - "team-vendnet"
```

### Verification

1. Go to **Security** → **Dependabot** → **Dependabot alerts**.
2. Any CVEs in your dependencies will appear here.
3. Go to **Insights** → **Dependency graph** — verify the tree matches `pom.xml`.

---

## 3. US-SECRET-002 — Audit Log for GitHub Actions Secrets

**Requirement:** Audit log of secret access enabled (GitHub Enterprise or org-level).

**Why this is GitHub-only:** This requires a GitHub **organization account** with **GitHub Enterprise** or **GitHub Team (with audit log API)**. The audit log tracks who accessed/modified repository secrets and environment variables. It is not configurable at the repository level for free-tier accounts.

### Step-by-Step

#### 3.1 If You Have a GitHub Organization

1. Go to the **organization** (not the repository) on GitHub.
2. Click **Settings** → **Audit log**.
3. Under **Audit log**, verify events are being recorded. Look for:
   - `repo.secret_scanning_alert` — when a secret is detected
   - `org.update_secret` — when a secret is modified
   - `workflow.run` — when a workflow accesses `${{ secrets.XXX }}`
4. To export the audit log:
   ```bash
   gh api orgs/YOUR_ORG/audit-log --paginate
   ```
5. Set up log streaming to a SIEM (Splunk, Azure Sentinel, Datadog) under **Settings** → **Audit log** → **Log streaming** (Enterprise-only).

#### 3.2 If You Have a Personal or Free Account

Personal accounts and free-tier organizations **do not have access to the audit log**. The fallback approach:

1. **Enable secret scanning push protection** (Settings → Code security → Secret scanning → Push protection: Enable). This blocks commits containing secrets before they reach the repository.
2. **Review the workflow run logs** after each CI run — GitHub records which encrypted secrets were accessed by each job:
   - Go to **Actions** → select a workflow run → click any job.
   - The "Set up job" step lists `***` for each secret accessed.
3. **Use GitHub's security overview** (Settings → Code security → Security overview) to see secret scanning alerts.

### Verification

```bash
# Check if your org has audit log access
gh api orgs/YOUR_ORG/audit-log --paginate 2>&1 | head -5
# Returns events if available, or "Not Found" if not available

# Check recent secret scanning alerts
gh api repos/YOUR_ORG/vendnet/secret-scanning/alerts --paginate 2>&1 | head -10
```

---

## Summary

| Gap | Action | Where | Effort |
|-----|--------|-------|--------|
| SonarQube branch protection | Create ruleset requiring status checks before merge | Repo Settings → Rules → Rulesets | 5 minutes |
| Dependency Graph + Dependabot | Enable from repo settings | Repo Settings → Code security | 2 minutes |
| Secret audit log | Enable if org/enterprise; use push protection as fallback | Org Settings → Audit log | 5 minutes (org) / N/A (personal) |

---

## Post-Resolution Checklist

After completing all three configurations, verify:

- [ ] Open a PR: SonarQube quality gate appears as a required check and blocks merge on failure
- [ ] **Insights → Dependency graph** shows the full Maven tree
- [ ] **Security → Dependabot** lists any open CVE alerts
- [ ] Push a commit with `password=secret123` — secret scanning push protection blocks it
- [ ] Run `gh workflow run ci-security.yml` — workflow accesses `${{ secrets.SONAR_TOKEN }}` without exposing it in logs

---

*This document should be updated whenever new branch protection rules, dependency tools, or audit requirements are added to the CI/CD pipeline.*
