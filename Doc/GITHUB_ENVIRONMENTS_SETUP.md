# GitHub Environment Configuration Guide

> **Project:** VendNet — Vending Machine Network  
> **Purpose:** Configure GitHub Environments for DEV / STAGING / PROD deployment gates  
> **Prerequisite:** You must be a repository admin to create environments

---

## 1. Create Environments

Go to: **GitHub → Repo → Settings → Environments**

Create three environments in this order:

| Environment | Port | Protection Rules | Trigger |
|-------------|------|-------------------|---------|
| **DEV** | 8280 | No restrictions | Auto-deploy on push to `develop` |
| **STAGING** | 8180 | Required reviewers (optional) | Auto-deploy on push to `main` after all tests pass |
| **PROD** | 8080 | Required reviewers: 1+ | Manual trigger only (`workflow_dispatch`) |

> **Port convention:** DEV=8280, STAGING=8180, PROD=8080 — each environment runs on a distinct port for isolation on the same host.

---

## 2. Environment Architecture

All three environments share the same host (`vs427.dei.isep.ipp.pt`) but use **separate Docker networks and ports**:

```
vs427.dei.isep.ipp.pt
├── DEV (port 8280)
│   └── vendnet-dev          → H2 in-memory, no external DB needed
│
├── STAGING (port 8180)
│   ├── vendnet-stage-mysql  → port 3307 (mapped from 3306)
│   └── vendnet-stage        → connects to stage-mysql via Docker network
│
└── PROD (port 8080)
    ├── vendnet-prod-mysql   → port 3306 (standard MySQL)
    └── vendnet-prod          → connects to prod-mysql via Docker network
```

**Key isolation properties:**
- Each environment uses its own Docker network (`vendnet-net`, `vendnet-stage`, `vendnet-prod`)
- Each environment uses its own JWT secret and HMAC key (never shared across envs)
- PROD runs with `--security-opt no-new-privileges:true --cap-drop ALL` (hardening)
- PROD's MySQL has `restart: always`; STAGING and DEV use `restart: unless-stopped`

---

## 3. Environment Secrets & Variables

### DEV Environment

No secrets required. DEV uses H2 in-memory database and mock secrets.

| Name | Type | Value |
|------|------|-------|
| `REMOTE_HOST` | Variable | `vs427.dei.isep.ipp.pt` |
| `REMOTE_PORT` | Variable | `2222` |
| `APP_PORT` | Variable | `8280` |

---

### STAGING Environment

| Name | Type | Value |
|------|------|-------|
| `SSH_PRIVATE_KEY` | **Secret** | Paste your full vs427 RSA key (from cloud interface) |
| `REMOTE_HOST` | Variable | `vs427.dei.isep.ipp.pt` |
| `REMOTE_PORT` | Variable | `2222` |
| `APP_PORT` | Variable | `8180` |
| `STAGE_DB_URL` | **Secret** | `jdbc:mysql://vendnet-stage-mysql:3306/vendnet_stage` |
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
| `APP_PORT` | Variable | `8080` |
| `PROD_DB_URL` | **Secret** | `jdbc:mysql://vendnet-prod-mysql:3306/vendnet` |
| `PROD_DB_USER` | **Secret** | `vendnet_user` |
| `PROD_DB_PASS` | **Secret** | `vendnet_prod_fc31f96b9e612ab7` |
| `PROD_JWT_SECRET` | **Secret** | `d1VhO2kkTEtBhil0kiv0CQoPj5L9Bc+wnSxgDNtJlwaMe1lLckjmLeSoalv5lxkC` |
| `PROD_HMAC_SECRET` | **Secret** | `IA/2u2IhF9BoGHiqpKwghG+R0hxMbZdzpQEWCo1o3PBjrCcXntUaWFF4w/5lkYi0` |
| `PROD_DB_ROOT_PASSWORD` | **Secret** | (choose a strong password) |

---

## 4. SSH Private Key

The key from your DEI cloud vs427 server. Copy the **entire** content including:
```
-----BEGIN OPENSSH PRIVATE KEY-----
...
-----END OPENSSH PRIVATE KEY-----
```

If you don't have it, open the cloud interface → vs427 → SSH access → copy the RSA private key.

### Database Connectivity

| Env | App→DB URL | MySQL Port (host) |
|-----|------------|-------------------|
| DEV | `jdbc:h2:mem:vendnet_dev` (in-memory, no external DB) | — |
| STAGING | `jdbc:mysql://vendnet-stage-mysql:3306/vendnet_stage` (Docker network) | 3307 |
| PROD | `jdbc:mysql://vendnet-prod-mysql:3306/vendnet` (Docker network) | 3306 |

> **Note:** STAGING and PROD apps connect to their MySQL via Docker network (container name resolution), NOT via `localhost`. The host-side ports (3307/3306) are only for direct MySQL access/debugging.

---

## 5. Verification

After configuring, run a manual workflow dispatch to verify:

```bash
# Trigger DEV deployment
gh workflow run ci-security.yml --ref develop

# Trigger STAGING deployment
gh workflow run ci-security.yml --ref main

# Trigger PROD deployment (manual only)
gh workflow run ci-security.yml --ref main
```

Check: **Actions → CI/CD Pipeline → latest run → Deploy to [ENV]**

Verify each environment:
```bash
curl http://vs427.dei.isep.ipp.pt:8280/actuator/health   # DEV
curl http://vs427.dei.isep.ipp.pt:8180/actuator/health     # STAGING
curl http://vs427.dei.isep.ipp.pt:8080/actuator/health     # PROD
```

---

## 6. Notes

- **Repository secrets** apply to all environments. Use **environment secrets** when values differ per environment (e.g., different DB passwords for STAGING vs PROD).
- **PROD** secrets must differ from STAGING — never share JWT secrets or DB passwords between environments.
- **PROD** deployments use Docker security hardening (`no-new-privileges`, `cap-drop ALL`).
- Each environment gets its own Docker network for full container isolation.
- `application-dev.properties` is gitignored — each developer creates their own local copy.
- Use `application-stage.properties` and `application-prod.properties` as templates.