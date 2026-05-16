# GitHub Environment Configuration Guide

> **Project:** VendNet — Vending Machine Network  
> **Purpose:** Configure GitHub Environments for DEV / STAGING / PROD deployment gates  
> **Prerequisite:** You must be a repository admin to create environments

---

## 1. Create Environments

Go to: **GitHub → Repo → Settings → Environments**

Create three environments in this order:

| Environment | Protection Rules | Trigger |
|-------------|-----------------|---------|
| **DEV** | No restrictions | Auto-deploy on push to `develop` |
| **STAGING** | Required reviewers (optional) | Auto-deploy on push to `main` after all tests pass |
| **PROD** | Required reviewers: 1+ | Manual trigger only (`workflow_dispatch`) |

---

## 2. Environment Secrets & Variables

### DEV Environment

No secrets required. DEV uses H2 in-memory database and mock secrets.

| Name | Type | Value |
|------|------|-------|
| _(none needed)_ | — | DEV uses `application-dev.properties` (local defaults) |

---

### STAGING Environment

| Name | Type | Value |
|------|------|-------|
| `SSH_PRIVATE_KEY` | **Secret** | RSA private key for `root@vs427.dei.isep.ipp.pt` (see section 3) |
| `REMOTE_HOST` | Variable | `vs427.dei.isep.ipp.pt` |
| `REMOTE_PORT` | Variable | `2222` |
| `APP_PORT` | Variable | `2226` |
| `STAGE_DB_URL` | **Secret** | `jdbc:mysql://localhost:3307/vendnet_stage` |
| `STAGE_DB_USER` | **Secret** | `vendnet_user` |
| `STAGE_DB_PASS` | **Secret** | `stage_vendnet_pass` |
| `STAGE_JWT_SECRET` | **Secret** | Generate: `openssl rand -base64 32` |
| `STAGE_HMAC_SECRET` | **Secret** | Generate: `openssl rand -base64 32` |

---

### PROD Environment

| Name | Type | Value |
|------|------|-------|
| `SSH_PRIVATE_KEY` | **Secret** | RSA private key for `root@vs427.dei.isep.ipp.pt` |
| `REMOTE_HOST` | Variable | `vs427.dei.isep.ipp.pt` |
| `REMOTE_PORT` | Variable | `2222` |
| `APP_PORT` | Variable | `2226` |
| `PROD_DB_URL` | **Secret** | Production MySQL JDBC URL |
| `PROD_DB_USER` | **Secret** | Production MySQL username |
| `PROD_DB_PASS` | **Secret** | Production MySQL password |
| `PROD_JWT_SECRET` | **Secret** | Generate: `openssl rand -base64 64` |
| `PROD_HMAC_SECRET` | **Secret** | Generate: `openssl rand -base64 64` |

---

## 3. How to Generate Required Values

### JWT Secret (256-bit minimum for prod)

```bash
openssl rand -base64 32   # 256 bits — STAGING
openssl rand -base64 64   # 512 bits — PROD
```

### HMAC Webhook Secret

```bash
openssl rand -base64 32
```

### SSH Private Key

The key you already have for vs427. Copy the **entire** content including `-----BEGIN OPENSSH PRIVATE KEY-----` and `-----END OPENSSH PRIVATE KEY-----`.

If you don't have the key:
```bash
# On vs427, generate a dedicated CI key
ssh-keygen -t rsa -b 4096 -f /root/.ssh/ci-deploy -N ""
cat /root/.ssh/ci-deploy.pub >> /root/.ssh/authorized_keys
cat /root/.ssh/ci-deploy    # Copy this to GitHub secret
```

### Database URLs

| Env | URL |
|-----|-----|
| DEV | `jdbc:h2:mem:vendnet_dev` (in-memory, no config needed) |
| STAGING | `jdbc:mysql://localhost:3307/vendnet_stage` |
| PROD | `jdbc:mysql://localhost:3306/vendnet` |

---

## 4. Verification

After configuring, run a manual workflow dispatch to verify:

```bash
# Trigger PROD deployment
gh workflow run ci-security.yml --ref main
```

Check: **Actions → CI/CD Pipeline → latest run → Deploy to PROD**

---

## 5. Notes

- **Repository secrets** apply to all environments. Use **environment secrets** when values differ per environment (e.g., different DB passwords for STAGING vs PROD).
- **PROD** secrets must differ from STAGING — never share JWT secrets or DB passwords between environments.
- The pipeline currently uses placeholder deploy scripts (echo commands). Update the `deploy-*` jobs with actual SSH deploy logic when the vs427 server is fully configured.
- `application-dev.properties` is gitignored — each developer creates their own local copy. Use `application-stage.properties` and `application-prod.properties` as templates.
