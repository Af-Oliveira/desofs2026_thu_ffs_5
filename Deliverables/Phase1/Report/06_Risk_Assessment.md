# 6. Risk Assessment

## 6.1 Methodology

### Selected Methodology: OWASP Risk Rating

### Likelihood Factors

| Factor | Description | Scale |
|--------|-------------|-------|
| Skill Level | Technical ability required by the threat agent: 1 = no skills, 5 = some technical knowledge, 9 = expert penetration tester or security researcher | 0-9 |
| Motive | Reward or incentive driving the attack: 1 = low or no reward, 5 = possible reward, 9 = high financial, reputational, or strategic reward | 0-9 |
| Opportunity | Level of access or resources required to execute the attack: 1 = requires full internal access, 5 = requires some access, 9 = no access required | 0-9 |
| Size (threat agent) | Size of the group capable of carrying out the attack: 1 = developers only, 3 = authenticated users, 6 = partners, 9 = anonymous internet users | 0-9 |
| Ease of Discovery | How easy it is to discover the vulnerability: 1 = practically impossible, 4 = difficult, 7 = easy, 9 = automated tools available | 0-9 |
| Ease of Exploit | How easy it is to exploit the vulnerability once found: 1 = theoretical, 4 = difficult, 7 = easy, 9 = automated tools available | 0-9 |
| Awareness | How widely known the vulnerability or attack technique is: 1 = unknown, 4 = hidden, 7 = obvious, 9 = publicly known with documented exploits | 0-9 |
| Intrusion Detection | Likelihood of detecting an active exploitation attempt: 1 = active real-time detection and alerting, 4 = logged without alerting, 9 = not logged at all | 0-9 |

### Impact Factors

| Factor | Description | Scale |
|--------|-------------|-------|
| Financial Damage | Direct monetary loss to the organisation: 1 = less than cost to fix the bug, 4 = minor effect on profit, 7 = significant financial damage, 9 = bankruptcy | 0-9 |
| Reputation Damage | Harm to the organisation's brand or public trust: 1 = minimal, 4 = major accounts lost, 7 = brand damage, 9 = brand destruction | 0-9 |
| Non-Compliance | Degree to which the threat violates regulatory or contractual obligations: 1 = minor violation, 4 = clear violation, 7 = high-profile violation, 9 = felony | 0-9 |
| Privacy Violation | Extent of personal data exposed: 1 = single individual, 4 = hundreds of people, 7 = thousands of people, 9 = millions of people | 0-9 |
| Loss of Confidentiality | Amount and sensitivity of information disclosed: 1 = minimal non-sensitive data, 4 = critical data partially disclosed, 7 = extensive critical data, 9 = all data disclosed | 0-9 |
| Loss of Integrity | Degree to which data can be corrupted or manipulated: 1 = minimal corruption, 4 = seriously corrupt data, 7 = extensive damage, 9 = all data tampered | 0-9 |
| Loss of Availability | Extent of service disruption: 1 = minimal secondary service disruption, 4 = interrupted primary service, 7 = primary service unavailable, 9 = all services unavailable | 0-9 |

### Risk Levels

| Risk Score | Level | Color |
|------------|-------|-------|
| 0–3 | Low | |
| 4–6 | Medium | |
| 7–8 | High | |
| 9+ | Critical | |

> **Scoring note:** Likelihood (L) and Impact (I) are each the arithmetic mean of their respective factor scores, rounded to the nearest integer (0–9). Risk Score = (L × I) / 9, normalised to a 0–9 scale. Risk Level is determined by the OWASP matrix: **Critical** if L ∈ [6,9] AND I ∈ [6,9]; **High** if (L ∈ [6,9] AND I ∈ [3,5]) OR (L ∈ [3,5] AND I ∈ [6,9]); **Medium** if L ∈ [3,5] AND I ∈ [3,5]; **Low** if either axis < 3.

---

## 6.2 Risk Assessment Results

| Threat ID | Threat Description | Likelihood (L) | Impact (I) | Risk Score (L×I/9) | Risk Level | Justification |
|-----------|--------------------|:--------------:|:----------:|:------------------:|:----------:|---------------|
| T-01 | Credential stuffing / account takeover | 7 | 5 | 3.9 | High | External attackers have automated tools and large breach databases (L=7); impact is limited to individual account compromise without lateral movement (I=5). |
| T-02 | Purchase request `unitPrice` parameter manipulation | 5 | 5 | 2.8 | Medium | Any authenticated customer can attempt this (L=5); financial impact is per-transaction but bounded by catalog pricing controls (I=5). |
| T-03 | Purchase repudiation / fraudulent chargeback | 5 | 4 | 2.2 | Medium | Malicious customers have clear motive but the attack relies on absent non-repudiation evidence rather than a technical exploit (L=5); per-transaction financial exposure is moderate (I=4). |
| T-04 | Horizontal IDOR — read other customer's purchase history | 7 | 5 | 3.9 | High | UUID-based IDs reduce discoverability slightly but the technique is well-known and any authenticated user can attempt it (L=7); exposes PII and purchase history but no write access (I=5). |
| T-05 | Brute-force login flood causes victim account lockout | 5 | 4 | 2.2 | Medium | Account lockout mechanism is documented but can be triggered by any external attacker knowing a valid username (L=5); DoS is limited to individual accounts, not system-wide (I=4). |
| T-06 | JWT `role` claim tampered to escalate to Administrator | 7 | 8 | 6.2 | Critical | JWT manipulation is a publicly documented attack requiring moderate skill; `alg:none` variants are trivial (L=7); successful exploitation grants unrestricted administrative access to all system functions (I=8). |
| T-07 | Operator credential theft via spear-phishing | 6 | 5 | 3.3 | High | Spear-phishing is widely practised against operational staff and requires only social-engineering skill (L=6); Operator access enables inventory manipulation and machine telemetry access but not system-wide control (I=5). |
| T-08 | Falsified stock restock submission (phantom inventory) | 5 | 4 | 2.2 | Medium | Requires insider Operator position and deliberate intent; audit logging exists to detect anomalies (L=5); financial and operational impact is limited to supply-chain cost distortion (I=4). |
| T-09 | Denial of stock update causing inventory discrepancy | 4 | 4 | 1.8 | Medium | Requires an Operator willing to exploit audit-log gaps and actively dispute responsibility (L=4); impact is operational confusion without direct system compromise (I=4). |
| T-10 | Cross-machine IDOR — unauthorised telemetry access | 6 | 5 | 3.3 | High | Any authenticated Operator can iterate machine UUIDs without special tooling (L=6); exposes GPS coordinates and operational patterns across the full fleet (I=5). |
| T-11 | Operator calls Administrator-only endpoint via missing `@PreAuthorize` | 5 | 6 | 3.3 | High | Depends on a misconfigured annotation; a malicious Operator who discovers the gap can exploit it immediately (L=5); successful access grants admin-level operations on users or pricing (I=6). |
| T-12 | Administrator account takeover (no MFA documented) | 7 | 8 | 6.2 | Critical | No MFA means credential phishing or reuse is sufficient; Admin accounts are high-value targets with documented tooling (L=7); full administrative access including user management, pricing, and backup controls (I=8). |
| T-13 | Rogue Administrator reassigns user roles / suspends accounts | 5 | 7 | 3.9 | High | Requires a malicious or compromised Admin with existing access (L=5); role manipulation can cover attack tracks, escalate colluding accounts, and disrupt legitimate users (I=7). |
| T-14 | Administrator denies pricing change or backup trigger | 4 | 4 | 1.8 | Medium | Exploits audit-log gaps rather than a technical vulnerability; requires a deliberate insider (L=4); impact is accountability loss and dispute resolution difficulty (I=4). |
| T-15 | Mass data exfiltration via compromised Administrator account | 6 | 9 | 6.0 | Critical | Compromised Admin credentials provide legitimate API access to all data endpoints without triggering anomalous behaviour (L=6); exposes entire user base, purchase history, and audit logs with severe GDPR and reputational consequences (I=9). |
| T-16 | Disk exhaustion via repeated on-demand backup generation | 4 | 5 | 2.2 | Medium | Requires Admin access and deliberate abuse; rotation policies exist but their enforcement is unconfirmed (L=4); file-write failures affect all logging and reporting subsystems (I=5). |
| T-17 | OS command execution via `ProcessBuilder` abuse from Admin account | 6 | 9 | 6.0 | Critical | Command injection into `ProcessBuilder` is a known technique; any attacker with Admin access can attempt it (L=6); arbitrary OS command execution under the application service account can achieve full host compromise (I=9). |
| T-18 | Rogue machine presents stolen mTLS client certificate | 4 | 6 | 2.7 | High | Requires physical or logical access to extract a certificate and private key from a device (L=4); allows submission of fabricated telemetry or sales events as a legitimate machine (I=6). |
| T-19 | Compromised firmware sends falsified sales events | 4 | 7 | 3.1 | High | Supply-chain compromise requires significant capability but is plausible for nation-state or organised criminal actors (L=4); false revenue records and virtual stock depletion have direct financial and operational impact (I=7). |
| T-20 | Machine operator denies erroneous telemetry / sales data origin | 4 | 4 | 1.8 | Medium | Limited to dispute scenarios; mTLS certificate ties data to machine identity making outright denial difficult (L=4); impact is operational accountability loss rather than active exploitation (I=4). |
| T-21 | Compromised firmware harvests pushed configuration and price data | 4 | 4 | 1.8 | Medium | Requires firmware compromise; price and slot data is sensitive but not a primary system control (L=4); competitive intelligence exposure with limited direct operational impact (I=4). |
| T-22 | Compromised machine floods telemetry endpoint | 5 | 6 | 3.3 | High | A compromised machine with a valid mTLS certificate can flood without additional capability (L=5); sustained high-frequency requests can exhaust backend threads and affect all users (I=6). |
| T-23 | VM mTLS cert used to access non-telemetry endpoints | 4 | 6 | 2.7 | High | Requires a compromised machine; relies on absent machine-role scoping in the API (L=4); accessing user or pricing endpoints with machine credentials bypasses human RBAC controls (I=6). |
| T-24 | Spoofed payment confirmation webhook bypasses HMAC | 7 | 8 | 6.2 | Critical | HMAC key leakage via source code or brute force is a well-documented attack; callback URLs are often discoverable (L=7); successful forgery triggers product dispensing without payment, causing direct financial loss (I=8). |
| T-25 | Payment callback payload modified FAILED→COMPLETED | 5 | 7 | 3.9 | High | Requires network-level TLS compromise which is non-trivial (L=5); a single tampered callback converts a declined payment into an authorised sale with direct financial and integrity impact (I=7). |
| T-26 | Payment Gateway disputes having sent a confirmation | 4 | 5 | 2.2 | Medium | Dispute arises from system failure or Gateway-side bug rather than active attack; idempotency handling is the primary mitigation gap (L=4); financial loss per transaction but limited without systemic exploitation (I=5). |
| T-27 | Payment Gateway outage blocks all card/mobile sales | 5 | 7 | 3.9 | High | Third-party outages are beyond VendNet control; no circuit-breaker or fallback is documented (L=5); complete inability to process card or mobile payments affects all revenue-generating transactions (I=7). |
| T-28 | JWT `alg:none` signature-bypass attack | 8 | 8 | 7.1 | Critical | The `alg:none` attack is publicly documented with automated tools; any token holder can attempt it without additional knowledge (L=8); successful bypass allows arbitrary identity and role forgery across all protected endpoints (I=8). |
| T-29 | JWT HMAC secret brute-force (weak signing key) | 5 | 7 | 3.9 | High | Offline `hashcat` attacks against HS256 are well-known but success depends on key entropy; captured tokens are obtainable from intercepted traffic (L=5); recovery of the signing secret enables unlimited JWT forgery for any identity (I=7). |
| T-30 | JWT payload `role` claim tampered after secret recovery | 6 | 8 | 5.3 | Critical | Once the signing secret is recovered (T-29), forging Admin-role tokens requires only standard tooling (L=6); full administrative privilege with all associated data access and operational control (I=8). |
| T-31 | Auth events logged without IP / user-agent context | 5 | 4 | 2.2 | Medium | Incomplete logging is a passive gap exploited during post-incident investigation rather than an active attack vector (L=5); prevents forensic attribution of account compromise scope (I=4). |
| T-32 | Username enumeration via differential login responses | 5 | 4 | 2.2 | Medium | Differential error responses are a well-known pattern; any unauthenticated caller can perform enumeration (L=5); primarily a precursor attack that enables more targeted credential stuffing (I=4). |
| T-33 | PII readable in JWT payload (base64, unencrypted) | 5 | 2 | 1.1 | Low | JWT payloads are only accessible to token holders; HTTPS prevents interception in transit (L=5); exposure is limited to data the token holder has already authenticated with (I=2). |
| T-34 | BCrypt-amplified login flood exhausts Tomcat thread pool | 7 | 5 | 3.9 | High | Automated HTTP flooding requires only a publicly accessible endpoint and standard tooling (L=7); BCrypt amplification makes even moderate concurrency effective at saturating the thread pool (I=5). |
| T-35 | Suspended account's unexpired JWT remains valid (no revocation list) | 5 | 6 | 3.3 | High | Any account holder whose JWT has not expired can exploit this gap; the window size equals the token TTL (L=5); continues to grant API access after an account suspension intended to revoke it (I=6). |
| T-36 | Missing method-level `@PreAuthorize` on application service layer | 5 | 6 | 3.3 | High | Internal callers bypassing the HTTP filter chain is a Spring Security misconfiguration common in large codebases (L=5); bypasses role checks for sensitive data operations invoked from scheduled tasks or event listeners (I=6). |
| T-37 | Stolen Operator JWT used from unrecognised location | 5 | 5 | 2.8 | Medium | Post-credential-theft JWT reuse is straightforward; no geo-anomaly or device detection is documented (L=5); impact is bounded by Operator-role permissions on inventory and telemetry (I=5). |
| T-38 | IDOR — stock update on machine outside Operator's assigned fleet | 6 | 5 | 3.3 | High | Any authenticated Operator can substitute a machine UUID in the API path; per-machine assignment enforcement is not confirmed (L=6); enables inventory manipulation across the full machine fleet (I=5). |
| T-39 | SQL injection via inventory search query parameters | 7 | 9 | 7.0 | Critical | SQL injection is one of the most widely exploited vulnerability classes with extensive automated tooling (L=7); native-query path injection can read, modify, or delete any table in the MySQL instance (I=9). |
| T-40 | Stock update audit log missing Operator identity and slot detail | 5 | 4 | 2.2 | Medium | Requires a malicious Operator actively exploiting the audit gap; the attack vector is passive (L=5); prevents forensic investigation of phantom-inventory incidents (I=4). |
| T-41 | Full fleet GPS/config enumeration via unbounded `GET /machines` | 5 | 4 | 2.2 | Medium | Any authenticated Operator can issue the request; response filtering by assignment is unconfirmed (L=5); exposes physical location and slot configuration of all machines, enabling targeted physical attacks (I=4). |
| T-42 | Malformed slot data triggers unhandled domain exception | 4 | 2 | 0.9 | Low | Boundary-value fuzzing requires an authenticated Operator and is easily detectable; domain validation catches most cases (L=4); impact is a transient 500 error with automatic transaction rollback (I=2). |
| T-43 | Mass-assignment on inventory endpoint silently modifies product pricing | 5 | 6 | 3.3 | High | Jackson mass-assignment is a common Spring Boot misconfiguration discoverable through API exploration (L=5); an Operator who discovers this can set prices to zero, causing immediate revenue loss (I=6). |
| T-44 | JWT replay attack repeats purchase on behalf of victim | 5 | 5 | 2.8 | Medium | Requires obtaining a valid victim JWT; the window is limited by the JWT expiry (L=5); creates a fraudulent purchase record against the victim's account with per-transaction financial impact (I=5). |
| T-45 | TOCTOU race condition causes overselling (negative stock) | 5 | 6 | 3.3 | High | Concurrent request submission is trivially achievable with automated clients; no pessimistic locking is confirmed (L=5); negative stock violates a core domain invariant and can cause dispense-without-stock incidents (I=6). |
| T-46 | Client-supplied `unitPrice` bypasses server-side catalog validation | 8 | 7 | 6.2 | Critical | Any authenticated customer can submit a crafted purchase payload; the attack requires only a standard HTTP client (L=8); purchases at near-zero cost constitute direct, scalable financial fraud (I=7). |
| T-47 | Missing payment `transactionRef` prevents chargeback dispute resolution | 5 | 6 | 3.3 | High | The gap is exploited during chargeback proceedings rather than at transaction time (L=5); absent cross-reference evidence causes VendNet to lose payment-processor disputes, resulting in systematic financial loss (I=6). |
| T-48 | IDOR on `GET /sales/{saleId}` exposes other customers' purchases | 6 | 5 | 3.3 | High | UUID-based IDs reduce guessability but UUIDs can be observed from one's own transactions; ownership check is absent (L=6); exposes personal purchase history including product, amount, and timestamp (I=5). |
| T-49 | Purchase flood exhausts Payment Gateway API quota | 6 | 5 | 3.3 | High | Any authenticated Customer can submit purchases at high frequency; rate limiting is undocumented (L=6); daily quota exhaustion blocks all legitimate card and mobile sales for the remainder of the quota window (I=5). |
| T-50 | Unauthenticated `POST /sales` processed (missing security filter) | 8 | 7 | 6.2 | Critical | A missing Spring Security `requestMatcher` entry is a configuration error discoverable via API fuzzing by any anonymous caller (L=8); allows anonymous purchase initiation, triggering payment gateway calls and stock decrements without authentication (I=7). |
| T-51 | Telemetry packet replay injects stale data as current readings | 5 | 4 | 2.2 | Medium | Requires captured mTLS telemetry packets; anti-replay timestamps are undocumented (L=5); stale stock readings may trigger unnecessary restocking dispatch but do not compromise system integrity (I=4). |
| T-52 | Compromised machine injects false stock-depletion telemetry | 5 | 6 | 3.3 | High | Any machine with a valid mTLS cert can submit crafted telemetry; plausibility checks are undocumented (L=5); systematic false depletion causes repeated unnecessary Operator dispatch and supply-chain cost (I=6). |
| T-53 | Telemetry source `MachineId` not recorded in audit log | 4 | 4 | 1.8 | Medium | Passive audit gap exploitable during post-incident investigation; requires a prior telemetry manipulation event (L=4); prevents attribution of falsified telemetry to a specific machine (I=4). |
| T-54 | Telemetry response exposes GPS coordinates and activity patterns | 5 | 4 | 2.2 | Medium | Any authenticated Operator can query telemetry; data minimisation by role is undocumented (L=5); location and schedule data enables physical targeting of high-value machines (I=4). |
| T-55 | Coordinated VM fleet floods telemetry database | 5 | 7 | 3.9 | High | Requires multiple compromised machines but each uses valid mTLS certificates; per-machine rate limiting is undocumented (L=5); telemetry table growth exhausts shared MySQL disk, impacting all data stores (I=7). |
| T-56 | Malicious telemetry payload exploits server-side deserialisation | 5 | 7 | 3.9 | High | Requires firmware compromise; Java 17 mitigates Log4Shell but other deserialisation paths exist (L=5); successful exploitation could yield remote code execution or heap exhaustion (I=7). |
| T-57 | Non-Administrator triggers backup/rotation via RBAC misconfiguration | 5 | 6 | 3.3 | High | A missing `@PreAuthorize` annotation is discoverable through API enumeration (L=5); triggers OS-level `ProcessBuilder` execution under the application service account without Admin authorisation (I=6). |
| T-58 | Path traversal via report type parameter escapes `/var/vendnet/` sandbox | 6 | 8 | 5.3 | Critical | URL-encoded traversal bypass against whitelist-before-canonicalisation is a well-documented technique (L=6); arbitrary directory creation and file writes outside the sandbox can establish persistence or overwrite system configuration (I=8). |
| T-59 | OS command injection via unsanitised `ProcessBuilder` parameters | 6 | 9 | 6.0 | Critical | Shell-metacharacter injection into backup labels is straightforward; sanitisation completeness is unconfirmed (L=6); arbitrary OS command execution as the application service account with potential for full host compromise (I=9). |
| T-60 | OS file operations not correlated to requesting Admin in audit log | 4 | 4 | 1.8 | Medium | Passive logging gap; HTTP request context to file-operation correlation requires deliberate implementation (L=4); prevents post-incident attribution of T-58/T-59 exploitation (I=4). |
| T-61 | Backup files world-readable due to permission misconfiguration | 5 | 6 | 3.3 | High | Deployment misconfiguration is common; AES-256 encryption protects content but key co-location is undocumented (L=5); read access to encrypted backups combined with key discovery enables full database restoration by attacker (I=6). |
| T-62 | Disk exhaustion from silent backup rotation failure | 4 | 5 | 2.2 | Medium | Cron job failures are silent by default; alerting on rotation health is undocumented (L=4); gradual disk fill causes audit log and backup write failures with delayed detection (I=5). |
| T-63 | Application-to-OS privilege escalation via `ProcessBuilder` injection | 6 | 9 | 6.0 | Critical | Follows directly from T-59; if the service account has `sudo` rights or SUID binaries exist (common in containerised deployments), escalation to root is trivial (L=6); full OS control including persistent backdoor installation (I=9). |
| T-64 | Suspended Admin's unexpired JWT authorises pricing changes | 5 | 7 | 3.9 | High | JWT statelessness means suspension does not revoke access until `exp`; the gap is inherent to the design (L=5); continued pricing and configuration changes from a suspended account undermine incident containment (I=7). |
| T-65 | Rogue Admin sets all product prices to zero across full catalog | 6 | 8 | 5.3 | Critical | Any Admin (compromised or malicious) can iterate product UUIDs from `GET /products` and batch-submit zero-price updates (L=6); all vending machines immediately dispense products for free, causing catastrophic revenue loss (I=8). |
| T-66 | Price change not attributed to specific Admin in audit log | 5 | 4 | 2.2 | Medium | Passive audit gap; the attack is obscuring accountability rather than gaining access (L=5); prevents forensic attribution of pricing manipulation incidents (I=4). |
| T-67 | Config endpoint response reveals internal infrastructure details | 5 | 6 | 3.3 | High | Requires Admin access; Spring's `Environment` serialisation is a documented information leak in misconfigured Boot apps (L=5); exposure of DB credentials, internal URLs, or JWT secret key name provides a blueprint for escalated attacks (I=6). |
| T-68 | Rapid config update flood causes excessive DB write contention | 5 | 4 | 2.2 | Medium | Requires a compromised Admin JWT; per-session rate limiting is undocumented (L=5); DB write contention degrades management operations but is unlikely to cause complete service failure (I=4). |
| T-69 | Operator accesses pricing endpoint via overly permissive annotation | 5 | 6 | 3.3 | High | A `hasAnyRole` misconfiguration is discoverable through normal API usage (L=5); Operator can modify product pricing with immediate revenue impact, identical to Admin pricing abuse (I=6). |
| T-70 | Direct DB access using leaked application credentials | 6 | 9 | 6.0 | Critical | Credentials exposed in Git history or verbose logs are a common finding; direct MySQL access bypasses all application-layer controls (L=6); complete read/write access to all tables including users, sales, and audit records (I=9). |
| T-71 | Direct SQL manipulation bypasses application audit and business rules | 6 | 9 | 6.0 | Critical | Follows from T-70; raw SQL execution bypasses domain invariants, audit logging, and RBAC simultaneously (L=6); arbitrary data modification including role elevation, sale record alteration, and audit log deletion (I=9). |
| T-72 | SQL injection modifies user roles, prices, or sale records | 7 | 9 | 7.0 | Critical | Automated SQL injection tools (sqlmap) can discover and exploit parameterisation gaps with minimal skill (L=7); DML injection enables role escalation, financial record manipulation, and data destruction (I=9). |
| T-73 | No DB-level audit trail for direct-access operations | 5 | 6 | 3.3 | High | MySQL binary log enablement is undocumented; direct-access operations are completely invisible to the application audit system (L=5); prevents detection and attribution of insider DB access or post-exploitation activity (I=6). |
| T-74 | Full table extraction via SQL injection UNION attack | 7 | 9 | 7.0 | Critical | UNION-based injection is automated by sqlmap; any vulnerable query endpoint exposes the full database schema (L=7); complete extraction of user credentials (BCrypt hashes), purchase history, and PII in a single request (I=9). |
| T-75 | MySQL error messages in HTTP response expose schema structure | 5 | 4 | 2.2 | Medium | Unhandled exceptions in default Spring Boot config include stack traces with SQL fragments (L=5); schema disclosure assists subsequent SQL injection attacks but is not exploitable in isolation (I=4). |
| T-76 | DB connection pool exhaustion via time-delayed query injection | 5 | 6 | 3.3 | High | `SLEEP()`-based injection is detectable by parameterised query enforcement but native query paths may remain vulnerable (L=5); HikariCP pool exhaustion blocks all database operations system-wide (I=6). |
| T-77 | Excessive DB account privileges enable DDL operations | 6 | 9 | 6.0 | Critical | `ALL PRIVILEGES` grants are a common deployment default; exploitation follows from T-72 or T-70 (L=6); schema-level operations (`DROP TABLE`, `CREATE USER`) can destroy all data or create persistent backdoor DB accounts (I=9). |
| T-78 | OS attacker reads backup archive as application service account | 5 | 6 | 3.3 | High | Requires OS-level shell access (e.g., via T-63); `700` directory permissions limit access to the service account (L=5); AES-256 protects content if the key is stored separately; key storage is undocumented (I=6). |
| T-79 | Audit log file tampered to erase security event evidence | 5 | 7 | 3.9 | High | OS-level access permits truncation; HMAC checksums detect modification if verified but active verification schedule is undocumented (L=5); erasure of audit evidence prevents detection and attribution of all prior malicious activity (I=7). |
| T-80 | Symlink attack on report directory writes data outside sandbox | 4 | 7 | 3.1 | High | Requires OS-level write access to `/var/vendnet/reports/`; `NOFOLLOW_LINKS` option in `Files.createDirectories()` is unconfirmed (L=4); symlink target receives application data, potentially overwriting sensitive OS files (I=7). |
| T-81 | File system operations not linked to requesting user in audit log | 4 | 4 | 1.8 | Medium | HTTP request context to file-operation correlation is an implementation gap rather than an exploitable vulnerability (L=4); prevents attribution of file-based attacks to the initiating Admin account (I=4). |
| T-82 | Report files with customer PII served beyond Admin scope | 5 | 7 | 3.9 | High | URL structure is derivable from documented directory patterns; endpoint role enforcement is unconfirmed (L=5); exposes customer PII and purchase history to Operator or anonymous callers with GDPR implications (I=7). |
| T-83 | Disk exhaustion from silent report/log accumulation | 4 | 5 | 2.2 | Medium | Rotation job failures are silent; disk exhaustion is a slow process but inevitable without alerting (L=4); `ENOSPC` errors propagate to audit logging and backup writes causing cascading file operation failures (I=5). |
| T-84 | Path traversal writes to privileged OS directory enabling persistence | 6 | 9 | 6.0 | Critical | Chained from T-58; writing to `/etc/cron.d/` or init directories via traversal establishes OS-level persistence that survives application restarts (L=6); persistent backdoor with OS-level execution on every cron trigger (I=9). |
| T-85 | Credential interception via TLS downgrade / SSL stripping | 5 | 5 | 2.8 | Medium | Requires network-position (MITM); minimum TLS version enforcement in Spring Boot configuration is unconfirmed (L=5); plaintext credential capture enables account takeover but requires network access as a prerequisite (I=5). |
| T-86 | HTTP parameter pollution injects duplicate price field | 4 | 2 | 0.9 | Low | Jackson default behaviour (last-value wins) is documented; server-side catalog lookup is the primary control (L=4); even if the duplicate field reaches the backend, server-side validation should reject non-catalog prices (I=2). |
| T-87 | Credentials captured in application DEBUG-level logs | 5 | 5 | 2.8 | Medium | DEBUG logging in production is a common misconfiguration; BCrypt protects stored passwords but plaintext in logs is unprotected (L=5); credentials in log files are accessible to operations staff with log-read access (I=5). |
| T-88 | Authenticated HTTP flood exhausts Tomcat thread pool | 7 | 5 | 3.9 | High | Any holder of a valid JWT can initiate; automated tooling is widely available; per-account rate limiting is undocumented (L=7); complete API unavailability for all users during the flood (I=5). |
| T-89 | JWT leaked in verbose error response or access log | 5 | 5 | 2.8 | Medium | Error response body content is partially controlled by Spring Boot defaults; access log format is configuration-dependent (L=5); leaked JWT allows session hijacking until token expiry (I=5). |
| T-90 | API response modified via TLS cert mismatch on client side | 4 | 5 | 2.2 | Medium | Requires client-side certificate validation failure and network MITM positioning; server-side TLS is correctly configured (L=4); modified responses could alter prices or purchase confirmations seen by the customer (I=5). |
| T-91 | JPA query missing `userId` filter returns all customers' records | 5 | 6 | 3.3 | High | A missing predicate in a Spring Data repository method exposes all records to any authenticated caller (L=5); bulk disclosure of all customers' purchase history constitutes a mass privacy violation (I=6). |
| T-92 | Unbounded response payload from unpaginated endpoint exhausts heap | 5 | 5 | 2.8 | Medium | No pagination is an API design gap; legitimate usage can trigger it accidentally (L=5); OOM errors cause JVM crash or GC thrashing, impacting all concurrent requests (I=5). |
| T-93 | Stock update payload tampered by proxy-level MITM | 4 | 5 | 2.2 | Medium | Corporate proxy TLS interception requires a trusted CA installation on the Operator client, which is a non-trivial precondition (L=4); inventory levels corrupted for specific machines only (I=5). |
| T-94 | Operator endpoint flood causes DB write contention | 5 | 4 | 2.2 | Medium | Requires a stolen Operator JWT; flooding inventory endpoints creates write contention but other system components are not affected (L=5); inventory management performance degradation for legitimate Operators (I=4). |
| T-95 | Machine log response exposes security audit events to Operator role | 5 | 4 | 2.2 | Medium | Content filtering by role is undocumented; any authenticated Operator can call the log endpoint (L=5); Admin activity and security incidents visible to Operators who should not have that access (I=4). |
| T-96 | Admin JWT exfiltrated via XSS in companion web UI | 5 | 7 | 3.9 | High | XSS in admin UI with JWT in localStorage is a well-known attack pattern; CSP documentation is absent (L=5); Admin JWT exfiltration grants full administrative access until token expiry (I=7). |
| T-97 | Admin config request body modified via TLS misconfiguration | 4 | 5 | 2.2 | Medium | Requires MITM and client-side certificate validation failure in the Admin tool (L=4); modified configuration could disable security controls such as lockout or rate limiting (I=5). |
| T-98 | Admin endpoint flooded via compromised Admin session | 5 | 5 | 2.8 | Medium | Requires a compromised Admin JWT; flooding management endpoints creates DB write contention (L=5); administrative operations become unresponsive but the application continues serving end-users (I=5). |
| T-99 | Audit log response modified in transit to conceal attacker activity | 4 | 5 | 2.2 | Medium | Requires network MITM on the Admin client segment and TLS compromise; HMAC checksums on stored files partially mitigate (L=4); sanitised audit log prevents security team from detecting ongoing attack (I=5). |
| T-100 | `GET /admin/users` response serialises BCrypt password hashes | 5 | 7 | 3.9 | High | Missing `@JsonIgnore` is a common Spring Boot serialisation oversight discoverable via normal Admin API usage (L=5); offline BCrypt cracking of all user hashes enables mass account takeover (I=7). |
| T-101 | Stolen mTLS cert used from attacker cloud infrastructure | 4 | 6 | 2.7 | High | Physical or logical extraction of a certificate requires significant effort; CRL/OCSP revocation is undocumented (L=4); cloud-based attacker can submit falsified sales events for any machine ID at scale (I=6). |
| T-102 | Compromised firmware sends false sales events (inflated quantity) | 5 | 7 | 3.9 | High | Firmware-level compromise requires supply-chain access but the backend cannot distinguish authentic from fabricated events (L=5); inflated sale records create false revenue figures and deplete virtual inventory without physical stock movement (I=7). |
| T-103 | Coordinated telemetry flood from multiple compromised VMs | 4 | 7 | 3.1 | High | Requires a fleet of compromised machines; each uses a valid mTLS cert bypassing authentication controls (L=4); 10,000+ req/s from 50 machines saturates the Tomcat thread pool, causing sales processing failures (I=7). |
| T-104 | Config push tampered on legacy machine without mTLS enforcement | 4 | 6 | 2.7 | High | ARP spoofing on a machine network segment is achievable with local network access (L=4); price list zeroing or slot assignment corruption on affected machines causes immediate revenue loss at those locations (I=6). |
| T-105 | Price list and slot config stored in plaintext on VM device | 5 | 4 | 2.2 | Medium | Physical machine access is required; this is primarily a business intelligence confidentiality risk (L=5); complete pricing strategy visible to competitor with physical access to a single machine (I=4). |
| T-106 | Payment amount tampered in outbound request via SSRF | 5 | 7 | 3.9 | High | SSRF exploitation requires identifying a vulnerable VendNet endpoint; outbound SSRF protection is undocumented (L=5); payment authorisations for reduced amounts (e.g., £0.01) that are approved by the Gateway constitute direct financial fraud (I=7). |
| T-107 | Payment request body captured in DEBUG-level application log | 4 | 4 | 1.8 | Medium | DEBUG logging in production is a configuration error; no raw card data is included (tokenised only) (L=4); transaction metadata and tokenised card references in logs could assist replay or enumeration attacks (I=4). |
| T-108 | Forged payment confirmation webhook bypasses HMAC validation | 7 | 8 | 6.2 | Critical | HMAC key recovery from source code leaks or short-key brute force is well-documented; callback URLs are discoverable via reconnaissance (L=7); a single forged webhook triggers product dispensing and sale record creation without any payment (I=8). |
| T-109 | Payment status modified FAILED→COMPLETED in callback payload | 5 | 7 | 3.9 | High | Requires TLS compromise on the callback path; HMAC must cover the full payload body to detect field-level modification (L=5); converts a declined payment into an authorised sale, causing per-transaction financial loss (I=7). |
| T-110 | Second-order SQL injection via stored malicious input in native query | 5 | 7 | 3.9 | High | Input stored safely via parameterised INSERT is later concatenated into a native query by a reporting job; testing of native query paths is typically incomplete (L=5); delayed execution can drop tables or escalate privileges when the report job runs (I=7). |
| T-111 | N+1 Hibernate query pattern exhausts DB connection pool | 5 | 5 | 2.8 | Medium | N+1 is triggered by normal API usage without special tooling; lazy loading strategy is undocumented (L=5); HikariCP pool exhaustion blocks all DB operations but is resolved by request completion (I=5). |
| T-112 | Over-broad JPA query returns records beyond authorised scope | 5 | 6 | 3.3 | High | A missing `userId` predicate in a repository method is a coding error that any authenticated user can trigger (L=5); returns all customers' purchase records in a single API call, constituting mass privacy disclosure (I=6). |
| T-113 | Path traversal in report type name writes outside `/var/vendnet/` sandbox | 6 | 8 | 5.3 | Critical | URL-encoded traversal bypassing whitelist checks is a well-known technique with documented payloads (L=6); arbitrary OS path creation and file writes, enabling cron persistence, OS config overwrite, or log destruction (I=8). |
| T-114 | Concurrent backup writes saturate disk I/O and fill partition | 5 | 5 | 2.8 | Medium | Requires rapid repeated calls to the backup endpoint; concurrency limit is undocumented (L=5); combined disk I/O saturation and partition fill cascades to audit log and application write failures (I=5). |
| T-115 | Malicious CSV planted in report directory served to Administrator | 4 | 5 | 2.2 | Medium | Requires OS-level write access to `/var/vendnet/reports/`; file permissions limit this to the service account or root (L=4); CSV formula injection executes under the Administrator's desktop context when the file is opened in a spreadsheet (I=5). |
| T-116 | Audit log API serves full security event history to Operator role | 6 | 5 | 3.3 | High | A `hasAnyRole` annotation including OPERATOR is discoverable through normal API usage (L=6); full security event history including login failures, role changes, and Admin actions disclosed to Operator-role callers (I=5). |

---

## 6.3 Risk Heat Map

> Each cell shows the OWASP risk zone colour followed by the Threat IDs that fall within that Likelihood × Impact quadrant.

| | **Low Impact (0–2)** | **Medium Impact (3–5)** | **High Impact (6–9)** |
|---|---|---|---|
| **High Likelihood (6–9)** | *(none)* | **High** — T-01, T-04, T-07, T-10, T-34, T-38, T-48, T-49, T-88, T-116 | **Critical** — T-06, T-12, T-15, T-17, T-24, T-28, T-30, T-39, T-46, T-50, T-58, T-59, T-63, T-65, T-70, T-71, T-72, T-74, T-77, T-84, T-108, T-113 |
| **Medium Likelihood (3–5)** | **Low** — T-33, T-42, T-86 | **Medium** — T-02, T-03, T-05, T-08, T-09, T-14, T-16, T-20, T-21, T-26, T-31, T-32, T-37, T-40, T-41, T-44, T-51, T-53, T-54, T-60, T-62, T-66, T-68, T-75, T-81, T-83, T-85, T-87, T-89, T-90, T-92, T-93, T-94, T-95, T-97, T-98, T-99, T-105, T-107, T-111, T-114, T-115 | **High** — T-11, T-13, T-18, T-19, T-22, T-23, T-25, T-27, T-29, T-35, T-36, T-43, T-45, T-47, T-52, T-55, T-56, T-57, T-61, T-64, T-67, T-69, T-73, T-76, T-78, T-79, T-80, T-82, T-91, T-96, T-100, T-101, T-102, T-103, T-104, T-106, T-109, T-110, T-112 |
| **Low Likelihood (0–2)** | *(none)* | *(none)* | *(none)* |

**Summary:** 22 Critical · 49 High · 42 Medium · 3 Low

---

## 6.4 Prioritized Risk Register

> All 116 threats ordered by Risk Score descending. Ties broken by Threat ID ascending.

| Priority | Threat ID | Risk Score | Risk Level | Description | Recommended Action |
|----------|-----------|:----------:|:----------:|-------------|-------------------|
| 1 | T-28 | 7.1 | Critical | JWT `alg:none` signature-bypass attack | Mitigate immediately |
| 2 | T-39 | 7.0 | Critical | SQL injection via inventory search query parameters | Mitigate immediately |
| 3 | T-72 | 7.0 | Critical | SQL injection modifies user roles, prices, or sale records | Mitigate immediately |
| 4 | T-74 | 7.0 | Critical | Full table extraction via SQL injection UNION attack | Mitigate immediately |
| 5 | T-06 | 6.2 | Critical | JWT `role` claim tampered to escalate to Administrator | Mitigate immediately |
| 6 | T-12 | 6.2 | Critical | Administrator account takeover (no MFA documented) | Mitigate immediately |
| 7 | T-24 | 6.2 | Critical | Spoofed payment confirmation webhook bypasses HMAC | Mitigate immediately |
| 8 | T-46 | 6.2 | Critical | Client-supplied `unitPrice` bypasses server-side catalog validation | Mitigate immediately |
| 9 | T-50 | 6.2 | Critical | Unauthenticated `POST /sales` processed (missing security filter) | Mitigate immediately |
| 10 | T-108 | 6.2 | Critical | Forged payment confirmation webhook bypasses HMAC validation | Mitigate immediately |
| 11 | T-15 | 6.0 | Critical | Mass data exfiltration via compromised Administrator account | Mitigate immediately |
| 12 | T-17 | 6.0 | Critical | OS command execution via `ProcessBuilder` abuse from Admin account | Mitigate immediately |
| 13 | T-59 | 6.0 | Critical | OS command injection via unsanitised `ProcessBuilder` parameters | Mitigate immediately |
| 14 | T-63 | 6.0 | Critical | Application-to-OS privilege escalation via `ProcessBuilder` injection | Mitigate immediately |
| 15 | T-70 | 6.0 | Critical | Direct DB access using leaked application credentials | Mitigate immediately |
| 16 | T-71 | 6.0 | Critical | Direct SQL manipulation bypasses application audit and business rules | Mitigate immediately |
| 17 | T-77 | 6.0 | Critical | Excessive DB account privileges enable DDL operations | Mitigate immediately |
| 18 | T-84 | 6.0 | Critical | Path traversal writes to privileged OS directory enabling persistence | Mitigate immediately |
| 19 | T-30 | 5.3 | Critical | JWT payload `role` claim tampered after secret recovery | Mitigate immediately |
| 20 | T-58 | 5.3 | Critical | Path traversal via report type parameter escapes `/var/vendnet/` sandbox | Mitigate immediately |
| 21 | T-65 | 5.3 | Critical | Rogue Admin sets all product prices to zero across full catalog | Mitigate immediately |
| 22 | T-113 | 5.3 | Critical | Path traversal in report type name writes outside `/var/vendnet/` sandbox | Mitigate immediately |
| 23 | T-01 | 3.9 | High | Credential stuffing / account takeover | Mitigate in Phase 2 |
| 24 | T-04 | 3.9 | High | Horizontal IDOR — read other customer's purchase history | Mitigate in Phase 2 |
| 25 | T-13 | 3.9 | High | Rogue Administrator reassigns user roles / suspends accounts | Mitigate in Phase 2 |
| 26 | T-25 | 3.9 | High | Payment callback payload modified FAILED→COMPLETED | Mitigate in Phase 2 |
| 27 | T-27 | 3.9 | High | Payment Gateway outage blocks all card/mobile sales | Mitigate in Phase 2 |
| 28 | T-29 | 3.9 | High | JWT HMAC secret brute-force (weak signing key) | Mitigate in Phase 2 |
| 29 | T-34 | 3.9 | High | BCrypt-amplified login flood exhausts Tomcat thread pool | Mitigate in Phase 2 |
| 30 | T-55 | 3.9 | High | Coordinated VM fleet floods telemetry database | Mitigate in Phase 2 |
| 31 | T-56 | 3.9 | High | Malicious telemetry payload exploits server-side deserialisation | Mitigate in Phase 2 |
| 32 | T-64 | 3.9 | High | Suspended Admin's unexpired JWT authorises pricing changes | Mitigate in Phase 2 |
| 33 | T-79 | 3.9 | High | Audit log file tampered to erase security event evidence | Mitigate in Phase 2 |
| 34 | T-82 | 3.9 | High | Report files with customer PII served beyond Admin scope | Mitigate in Phase 2 |
| 35 | T-88 | 3.9 | High | Authenticated HTTP flood exhausts Tomcat thread pool | Mitigate in Phase 2 |
| 36 | T-96 | 3.9 | High | Admin JWT exfiltrated via XSS in companion web UI | Mitigate in Phase 2 |
| 37 | T-100 | 3.9 | High | `GET /admin/users` response serialises BCrypt password hashes | Mitigate in Phase 2 |
| 38 | T-102 | 3.9 | High | Compromised firmware sends false sales events (inflated quantity) | Mitigate in Phase 2 |
| 39 | T-106 | 3.9 | High | Payment amount tampered in outbound request via SSRF | Mitigate in Phase 2 |
| 40 | T-109 | 3.9 | High | Payment status modified FAILED→COMPLETED in callback payload | Mitigate in Phase 2 |
| 41 | T-110 | 3.9 | High | Second-order SQL injection via stored malicious input in native query | Mitigate in Phase 2 |
| 42 | T-07 | 3.3 | High | Operator credential theft via spear-phishing | Mitigate in Phase 2 |
| 43 | T-10 | 3.3 | High | Cross-machine IDOR — unauthorised telemetry access | Mitigate in Phase 2 |
| 44 | T-11 | 3.3 | High | Operator calls Administrator-only endpoint via missing `@PreAuthorize` | Mitigate in Phase 2 |
| 45 | T-22 | 3.3 | High | Compromised machine floods telemetry endpoint | Mitigate in Phase 2 |
| 46 | T-35 | 3.3 | High | Suspended account's unexpired JWT remains valid (no revocation list) | Mitigate in Phase 2 |
| 47 | T-36 | 3.3 | High | Missing method-level `@PreAuthorize` on application service layer | Mitigate in Phase 2 |
| 48 | T-38 | 3.3 | High | IDOR — stock update on machine outside Operator's assigned fleet | Mitigate in Phase 2 |
| 49 | T-43 | 3.3 | High | Mass-assignment on inventory endpoint silently modifies product pricing | Mitigate in Phase 2 |
| 50 | T-45 | 3.3 | High | TOCTOU race condition causes overselling (negative stock) | Mitigate in Phase 2 |
| 51 | T-47 | 3.3 | High | Missing payment `transactionRef` prevents chargeback dispute resolution | Mitigate in Phase 2 |
| 52 | T-48 | 3.3 | High | IDOR on `GET /sales/{saleId}` exposes other customers' purchases | Mitigate in Phase 2 |
| 53 | T-49 | 3.3 | High | Purchase flood exhausts Payment Gateway API quota | Mitigate in Phase 2 |
| 54 | T-52 | 3.3 | High | Compromised machine injects false stock-depletion telemetry | Mitigate in Phase 2 |
| 55 | T-57 | 3.3 | High | Non-Administrator triggers backup/rotation via RBAC misconfiguration | Mitigate in Phase 2 |
| 56 | T-61 | 3.3 | High | Backup files world-readable due to permission misconfiguration | Mitigate in Phase 2 |
| 57 | T-67 | 3.3 | High | Config endpoint response reveals internal infrastructure details | Mitigate in Phase 2 |
| 58 | T-69 | 3.3 | High | Operator accesses pricing endpoint via overly permissive annotation | Mitigate in Phase 2 |
| 59 | T-73 | 3.3 | High | No DB-level audit trail for direct-access operations | Mitigate in Phase 2 |
| 60 | T-76 | 3.3 | High | DB connection pool exhaustion via time-delayed query injection | Mitigate in Phase 2 |
| 61 | T-78 | 3.3 | High | OS attacker reads backup archive as application service account | Mitigate in Phase 2 |
| 62 | T-91 | 3.3 | High | JPA query missing `userId` filter returns all customers' records | Mitigate in Phase 2 |
| 63 | T-112 | 3.3 | High | Over-broad JPA query returns records beyond authorised scope | Mitigate in Phase 2 |
| 64 | T-116 | 3.3 | High | Audit log API serves full security event history to Operator role | Mitigate in Phase 2 |
| 65 | T-19 | 3.1 | High | Compromised firmware sends falsified sales events | Mitigate in Phase 2 |
| 66 | T-80 | 3.1 | High | Symlink attack on report directory writes data outside sandbox | Mitigate in Phase 2 |
| 67 | T-103 | 3.1 | High | Coordinated telemetry flood from multiple compromised VMs | Mitigate in Phase 2 |
| 68 | T-02 | 2.8 | Medium | Purchase request `unitPrice` parameter manipulation | Monitor |
| 69 | T-37 | 2.8 | Medium | Stolen Operator JWT used from unrecognised location | Monitor |
| 70 | T-44 | 2.8 | Medium | JWT replay attack repeats purchase on behalf of victim | Monitor |
| 71 | T-85 | 2.8 | Medium | Credential interception via TLS downgrade / SSL stripping | Monitor |
| 72 | T-87 | 2.8 | Medium | Credentials captured in application DEBUG-level logs | Monitor |
| 73 | T-89 | 2.8 | Medium | JWT leaked in verbose error response or access log | Monitor |
| 74 | T-92 | 2.8 | Medium | Unbounded response payload from unpaginated endpoint exhausts heap | Monitor |
| 75 | T-98 | 2.8 | Medium | Admin endpoint flooded via compromised Admin session | Monitor |
| 76 | T-111 | 2.8 | Medium | N+1 Hibernate query pattern exhausts DB connection pool | Monitor |
| 77 | T-114 | 2.8 | Medium | Concurrent backup writes saturate disk I/O and fill partition | Monitor |
| 78 | T-18 | 2.7 | High | Rogue machine presents stolen mTLS client certificate | Mitigate in Phase 2 |
| 79 | T-23 | 2.7 | High | VM mTLS cert used to access non-telemetry endpoints | Mitigate in Phase 2 |
| 80 | T-101 | 2.7 | High | Stolen mTLS cert used from attacker cloud infrastructure | Mitigate in Phase 2 |
| 81 | T-104 | 2.7 | High | Config push tampered on legacy machine without mTLS enforcement | Mitigate in Phase 2 |
| 82 | T-03 | 2.2 | Medium | Purchase repudiation / fraudulent chargeback | Monitor |
| 83 | T-05 | 2.2 | Medium | Brute-force login flood causes victim account lockout | Monitor |
| 84 | T-08 | 2.2 | Medium | Falsified stock restock submission (phantom inventory) | Monitor |
| 85 | T-16 | 2.2 | Medium | Disk exhaustion via repeated on-demand backup generation | Monitor |
| 86 | T-26 | 2.2 | Medium | Payment Gateway disputes having sent a confirmation | Monitor |
| 87 | T-31 | 2.2 | Medium | Auth events logged without IP / user-agent context | Monitor |
| 88 | T-32 | 2.2 | Medium | Username enumeration via differential login responses | Monitor |
| 89 | T-40 | 2.2 | Medium | Stock update audit log missing Operator identity and slot detail | Monitor |
| 90 | T-41 | 2.2 | Medium | Full fleet GPS/config enumeration via unbounded `GET /machines` | Monitor |
| 91 | T-51 | 2.2 | Medium | Telemetry packet replay injects stale data as current readings | Monitor |
| 92 | T-54 | 2.2 | Medium | Telemetry response exposes GPS coordinates and activity patterns | Monitor |
| 93 | T-62 | 2.2 | Medium | Disk exhaustion from silent backup rotation failure | Monitor |
| 94 | T-66 | 2.2 | Medium | Price change not attributed to specific Admin in audit log | Monitor |
| 95 | T-68 | 2.2 | Medium | Rapid config update flood causes excessive DB write contention | Monitor |
| 96 | T-75 | 2.2 | Medium | MySQL error messages in HTTP response expose schema structure | Monitor |
| 97 | T-83 | 2.2 | Medium | Disk exhaustion from silent report/log accumulation | Monitor |
| 98 | T-90 | 2.2 | Medium | API response modified via TLS cert mismatch on client side | Monitor |
| 99 | T-93 | 2.2 | Medium | Stock update payload tampered by proxy-level MITM | Monitor |
| 100 | T-94 | 2.2 | Medium | Operator endpoint flood causes DB write contention | Monitor |
| 101 | T-95 | 2.2 | Medium | Machine log response exposes security audit events to Operator role | Monitor |
| 102 | T-97 | 2.2 | Medium | Admin config request body modified via TLS misconfiguration | Monitor |
| 103 | T-99 | 2.2 | Medium | Audit log response modified in transit to conceal attacker activity | Monitor |
| 104 | T-105 | 2.2 | Medium | Price list and slot config stored in plaintext on VM device | Monitor |
| 105 | T-115 | 2.2 | Medium | Malicious CSV planted in report directory served to Administrator | Monitor |
| 106 | T-09 | 1.8 | Medium | Denial of stock update causing inventory discrepancy | Monitor |
| 107 | T-14 | 1.8 | Medium | Administrator denies pricing change or backup trigger | Monitor |
| 108 | T-20 | 1.8 | Medium | Machine operator denies erroneous telemetry / sales data origin | Monitor |
| 109 | T-21 | 1.8 | Medium | Compromised firmware harvests pushed configuration and price data | Monitor |
| 110 | T-53 | 1.8 | Medium | Telemetry source `MachineId` not recorded in audit log | Monitor |
| 111 | T-60 | 1.8 | Medium | OS file operations not correlated to requesting Admin in audit log | Monitor |
| 112 | T-81 | 1.8 | Medium | File system operations not linked to requesting user in audit log | Monitor |
| 113 | T-107 | 1.8 | Medium | Payment request body captured in DEBUG-level application log | Monitor |
| 114 | T-33 | 1.1 | Low | PII readable in JWT payload (base64, unencrypted) | Accept |
| 115 | T-42 | 0.9 | Low | Malformed slot data triggers unhandled domain exception | Accept |
| 116 | T-86 | 0.9 | Low | HTTP parameter pollution injects duplicate price field | Accept |

---

## 6.5 Justification of Methodology Choice

**Why OWASP Risk Rating over DREAD.** DREAD (Damage, Reproducibility, Exploitability, Affected Users, Discoverability) uses five coarse factors that were originally designed for Microsoft's internal triage process and are widely criticised for producing inconsistent scores across analysts due to overlapping factor definitions and a lack of separation between threat-agent characteristics and system-level impact. OWASP Risk Rating addresses these weaknesses by splitting likelihood into eight factors that separately characterise the threat agent (Skill Level, Motive, Opportunity, Size) and the vulnerability itself (Ease of Discovery, Ease of Exploit, Awareness, Intrusion Detection), and by splitting impact into seven factors that distinguish technical consequences (Confidentiality, Integrity, Availability) from business consequences (Financial Damage, Reputation Damage, Non-Compliance, Privacy Violation). This granularity is essential for a system like VendNet, which exposes heterogeneous attack surfaces — public REST APIs, mTLS machine channels, OS-level `ProcessBuilder` operations, and a Payment Gateway integration — each requiring different likelihood reasoning. OWASP Risk Rating is also the methodology explicitly recommended by the OWASP Application Security Verification Standard 5.0, which this project has already adopted as its security baseline, ensuring alignment between threat modelling, risk assessment, and the verification criteria used in Phase 2.

**How scoring decisions were applied consistently across 116 threats.** Likelihood scores were assigned by anchoring on the threat agent category established in §4.2: external attackers targeting public endpoints (authenticated or not) received L ≥ 6 because they combine high Motive, large Size, and automated tooling availability; malicious insiders (Operator or Administrator) received L = 5 because Opportunity and Size factors are constrained to authenticated roles; supply-chain or firmware-compromised actors received L = 4 because physical or logical device access is a non-trivial prerequisite; and infrastructure or configuration failures received L = 3–4 because they require a pre-existing error condition. Impact scores were anchored to the worst-case business outcome of each threat: threats involving arbitrary OS command execution or full database access received I = 9 (all services and all data at risk); threats enabling mass data exfiltration, zero-price fraud, or Admin privilege escalation received I = 8; threats causing limited financial loss, service disruption, or targeted PII disclosure received I = 5–7; and threats with localised or precursor-only impact received I ≤ 4. The resulting distribution — 22 Critical, 49 High, 42 Medium, 3 Low — closely mirrors the qualitative severity distribution in §4.3 (22 Critical, 49 High after recount, 42 Medium, 3 Low), validating internal consistency. Threats rated Low (T-33, T-42, T-86) all share the characteristic of either requiring a prior compromise as a precondition, providing minimal standalone exploitability, or having no meaningful business impact beyond a single user or request.
