# Self-Hosted GitHub Actions Runner Setup

> **Purpose:** Deploy jobs fail because GitHub cloud runners can't resolve `vs427.dei.isep.ipp.pt` (internal DEI DNS). A self-hosted runner inside the DEI network solves this.

---

## 1. Create the Runner in GitHub

1. Go to: **GitHub → Af-Oliveira/desofs2026_thu_ffs_5 → Settings → Actions → Runners**
2. Click **New self-hosted runner**
3. Select **Linux** as the OS
4. Copy the setup commands shown (they include a unique token)

---

## 2. Set Up the Runner on the DEI VM

SSH into `vs427.dei.isep.ipp.pt` (port 2222, user `root`) and run:

```bash
# Create a directory for the runner
mkdir -p /opt/actions-runner && cd /opt/actions-runner

# Download the runner
curl -o actions-runner-linux-x64-2.334.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.334.0/actions-runner-linux-x64-2.334.0.tar.gz

# Verify hash (optional but recommended)
echo "6d507c8e4096edec0c2ff3805b87d685b998eb21bb2a5c3c103cc3bc4f3e15c3  actions-runner-linux-x64-2.334.0.tar.gz" | shasum -a 256 -c

# Extract
tar xzf actions-runner-linux-x64-2.334.0.tar.gz
```

### Configure the runner
```bash
# Run the configure script with the token from GitHub
./config.sh --url https://github.com/Af-Oliveira/desofs2026_thu_ffs_5 --token <YOUR_TOKEN_FROM_GITHUB>
```

- Leave name as default or use `dei-vs427-runner`
- For labels: press Enter (default) or add `dei,vs427`
- For work folder: press Enter (default `_work`)

### Install as a systemd service (runs on boot, survives SSH logout)
```bash
sudo ./svc.sh install
sudo ./svc.sh start
sudo ./svc.sh status
```

### Verify
```bash
# Check it's running
sudo ./svc.sh status
# Should show: "status	Active: active (running)"

# Check it appears in GitHub
# GitHub → Settings → Actions → Runners → should show "Idle"
```

---

## 3. Update the Workflow

The three deploy jobs (`deploy-dev`, `deploy-stage`, `deploy-prod`) need `runs-on: self-hosted` instead of `runs-on: ubuntu-latest`.

Change this in `.github/workflows/ci-security.yml` (already updated):

```yaml
deploy-dev:
  name: Deploy to DEV
  runs-on: self-hosted    # <-- was ubuntu-latest
  ...

deploy-stage:
  name: Deploy to STAGING
  runs-on: self-hosted    # <-- was ubuntu-latest
  ...

deploy-prod:
  name: Deploy to PROD
  runs-on: self-hosted    # <-- was ubuntu-latest
  ...
```

---

## 4. Environment Variables for Self-Hosted Runner

Since the runner is **on** vs427, deploy jobs should use localhost SSH, not the external gateway:

Go to **GitHub → Settings → Environments → DEV / STAGING / PROD** and update:

| Variable | Old value | New value |
|----------|-----------|-----------|
| `REMOTE_HOST` | `vs427.dei.isep.ipp.pt` | `localhost` |
| `REMOTE_PORT` | `2222` (external SSH gateway) | `22` (internal SSH) |

> Without this change, the deploy will try to connect to `vs427.dei.isep.ipp.pt:2222` via the external gateway, which loops back through the internet instead of using the local network.

---

## 5. Important Notes

- The runner VM must have Java 17, Maven, Docker, docker-compose installed
- The runner needs **outbound** internet access to reach `github.com`
- No DNS needed — all deploy targets are `localhost` when runner is on vs427
- The `SSH_PRIVATE_KEY` secret must be usable from the runner VM (key auth to localhost)
- For PROD: the runner needs 1+ reviewer approval configured in the PROD environment
- External SSH gateway (`vsgate-ssh.dei.isep.ipp.pt:10427 → vs427:2222`) is separate and unaffected
- Label the runner with `dei` or `vs427` to target it specifically in other workflows
