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
| `SSH_PRIVATE_KEY` | **Secret** | Paste your full vs427 RSA key (from cloud interface) |
| `REMOTE_HOST` | Variable | `vs427.dei.isep.ipp.pt` |
| `REMOTE_PORT` | Variable | `2222` |
| `APP_PORT` | Variable | `2226` |
| `STAGE_DB_URL` | **Secret** | `jdbc:mysql://localhost:3307/vendnet_stage` |
| `STAGE_DB_USER` | **Secret** | `vendnet_user` |
| `STAGE_DB_PASS` | **Secret** | `vendnet_stage_72616f9c70fe` |
| `STAGE_JWT_SECRET` | **Secret** | `pnOFpPXFIpWwgysy9L+RL0zl5O6pN51bAC7c1rtfnnw=` |
| `STAGE_HMAC_SECRET` | **Secret** | `WGAZrMiPTt7scYKRsZwsEDiTivoXrfEzhe6dS3GIjVI=` |

---

### PROD Environment

| Name | Type | Value |
|------|------|-------|
| `SSH_PRIVATE_KEY` | **Secret** | Paste your full vs427 RSA key (from cloud interface) |
| `REMOTE_HOST` | Variable | `vs427.dei.isep.ipp.pt` |
| `REMOTE_PORT` | Variable | `2222` |
| `APP_PORT` | Variable | `2226` |
| `PROD_DB_URL` | **Secret** | `jdbc:mysql://localhost:3306/vendnet` |
| `PROD_DB_USER` | **Secret** | `vendnet_user` |
| `PROD_DB_PASS` | **Secret** | `vendnet_prod_fc31f96b9e612ab7` |
| `PROD_JWT_SECRET` | **Secret** | `d1VhO2kkTEtBhil0kiv0CQoPj5L9Bc+wnSxgDNtJlwaMe1lLckjmLeSoalv5lxkC` |
| `PROD_HMAC_SECRET` | **Secret** | `IA/2u2IhF9BoGHiqpKwghG+R0hxMbZdzpQEWCo1o3PBjrCcXntUaWFF4w/5lkYi0` |

---

## 3. SSH Private Key

The key from your DEI cloud vs427 server. Copy the **entire** content including:
```
-----BEGIN OPENSSH PRIVATE KEY-----
...
-----END OPENSSH PRIVATE KEY-----
```

If you don't have it, open the cloud interface → vs427 → SSH access → copy the RSA private key.

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
