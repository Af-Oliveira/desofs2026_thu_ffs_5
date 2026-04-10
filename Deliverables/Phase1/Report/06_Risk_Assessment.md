# 6. Risk Assessment

## 6.1 Methodology

<!-- TODO: Choose and justify a risk assessment methodology. Recommended: OWASP Risk Rating Methodology.
     Alternatively: DREAD (Damage, Reproducibility, Exploitability, Affected Users, Discoverability) -->

### Selected Methodology: OWASP Risk Rating *(or DREAD — choose one)*

<!-- For OWASP Risk Rating, risk = Likelihood × Impact -->

### Likelihood Factors

| Factor | Description | Scale |
|--------|-------------|-------|
| Skill Level | <!-- TODO --> | 0-9 |
| Motive | <!-- TODO --> | 0-9 |
| Opportunity | <!-- TODO --> | 0-9 |
| Size (threat agent) | <!-- TODO --> | 0-9 |
| Ease of Discovery | <!-- TODO --> | 0-9 |
| Ease of Exploit | <!-- TODO --> | 0-9 |
| Awareness | <!-- TODO --> | 0-9 |
| Intrusion Detection | <!-- TODO --> | 0-9 |

### Impact Factors

| Factor | Description | Scale |
|--------|-------------|-------|
| Financial Damage | <!-- TODO --> | 0-9 |
| Reputation Damage | <!-- TODO --> | 0-9 |
| Non-Compliance | <!-- TODO --> | 0-9 |
| Privacy Violation | <!-- TODO --> | 0-9 |
| Loss of Confidentiality | <!-- TODO --> | 0-9 |
| Loss of Integrity | <!-- TODO --> | 0-9 |
| Loss of Availability | <!-- TODO --> | 0-9 |

### Risk Levels

| Risk Score | Level | Color |
|------------|-------|-------|
| 0–3 | Low | 🟢 |
| 4–6 | Medium | 🟡 |
| 7–8 | High | 🟠 |
| 9+ | Critical | 🔴 |

## 6.2 Risk Assessment Results

| Threat ID | Threat Description | Likelihood (L) | Impact (I) | Risk Score (L×I) | Risk Level | Justification |
|-----------|--------------------|-----------------|------------|-------------------|------------|---------------|
| T-01 | <!-- TODO --> | <!-- TODO: 0-9 --> | <!-- TODO: 0-9 --> | <!-- TODO --> | <!-- TODO --> | <!-- TODO: explain scoring --> |
| T-02 | <!-- TODO --> | | | | | |
| ... | | | | | | |

## 6.3 Risk Heat Map

<!-- TODO: Create a visual heat map. Example structure: -->

|  | **Low Impact (1-3)** | **Medium Impact (4-6)** | **High Impact (7-9)** |
|---|---|---|---|
| **High Likelihood (7-9)** | 🟡 Medium | 🟠 High | 🔴 Critical |
| **Medium Likelihood (4-6)** | 🟢 Low | 🟡 Medium | 🟠 High |
| **Low Likelihood (1-3)** | 🟢 Low | 🟢 Low | 🟡 Medium |

<!-- TODO: Place each Threat ID in the appropriate cell -->

## 6.4 Prioritized Risk Register

| Priority | Threat ID | Risk Level | Description | Recommended Action |
|----------|-----------|------------|-------------|-------------------|
| 1 | T-XX | Critical | <!-- TODO --> | Mitigate immediately |
| 2 | T-XX | High | <!-- TODO --> | Mitigate in Sprint 1 |
| ... | | | | |

## 6.5 Justification of Methodology Choice
<!-- TODO: Explain why this methodology was chosen over alternatives -->
