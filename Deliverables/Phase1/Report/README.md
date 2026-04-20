# Phase 1 Deliverable — Vending Machine Network (VendNet)

> **Course:** DESOFS 2025/2026 · ISEP
> **Team:** desofs2026_thu_ffs_5
> **Phase:** 1 — Analysis & Design
> **Date:** 19 April 2026

## Team Members

1250536 - Mário
1221170 - Ricardo
1221160 - Afonso
1211378 - Max

## Abstract

VendNet is a centralized REST API back-end for managing a geographically distributed vending machine network, built with Java 17, Spring Boot 3.x, MySQL 8.4, JWT/BCrypt authentication, mTLS for edge devices, and Role-Based Access Control across three roles (Customer, Operator, Administrator). Phase 1 covers the full SSDLC analysis and design phase: system context, a Domain-Driven Design model with five bounded contexts, multi-level Data Flow Diagrams, and a complete security engineering artefact set. The threat model applies STRIDE-per-element analysis across all DFD elements, identifying 116 threats (T-01–T-116; 22 Critical, 49 High, 42 Medium, 3 Low) quantified via the OWASP Risk Rating methodology. From those threats, 8 abuse cases (AC-01–AC-08) were derived alongside 102 requirements (31 functional, 19 non-functional, 6 constraints, 46 security), each mapped to OWASP ASVS 5.0 as the compliance baseline, and a security testing plan comprising 46 test cases with full traceability from requirements to threats.

## Table of Contents

1. [System Overview](01_System_Overview.md)
2. [Domain Model (DDD)](02_Domain_Model.md)
3. [Data Flow Diagrams](03_Dataflow_Diagrams.md)
4. [Threat Model (STRIDE)](04_Threat_Model.md)
5. [Abuse Cases](05_Abuse_Cases.md)
6. [Risk Assessment](06_Risk_Assessment.md)
7. [Mitigations](07_Mitigations.md)
8. [Requirements (Functional, Non-Functional, Security)](08_Requirements.md)
9. [Security Testing Plan & Traceability](09_Security_Testing.md)
10. [Secure Architecture & Design](10_Secure_Architecture.md)
11. [ASVS Checklist](../ASVS_Checklist/ASVS_5.0_Tracker.md), [ASVS Checklist (Excel)](../ASVS_Checklist/ASVS_5.0_Tracker1.xlsx)

## Document Summaries

**1. System Overview** — Describes VendNet's purpose, high-level C4 context, technology stack (Java 17, Spring Boot 3.x, MySQL 8.4), and three-role RBAC model (Customer, Operator, Administrator). Documents the three OS-level operations (database backup generation, audit log rotation, report directory creation) that are sandboxed under `/var/vendnet/` and subject to strict path validation.

**2. Domain Model (DDD)** — Defines the five bounded contexts (Identity & Access, Machine Management, Slot Management, Product Catalog, Sales) and their five aggregate roots (User, VendingMachine, Slot, Product, Sale). All cross-aggregate communication is enforced via ID references, and each aggregate specifies its domain invariants.

**3. Data Flow Diagrams** — Presents three levels of DFDs: Level 0 context diagram, Level 1 decomposition into six sub-processes (Auth, Inventory, Sales, Telemetry, OS Operations, Pricing), and Level 2 granular breakdowns of each sub-process. Five trust boundaries (TB1–TB5) are defined and carried through all levels, identifying three entry points and four exit points.

**4. Threat Model (STRIDE)** — Applies STRIDE-per-element analysis to every DFD element (5 external entities, 6 processes, 2 data stores, 14 data flows), producing 116 identified threats (T-01–T-116). Severity distribution: 22 Critical, 49 High, 42 Medium, 3 Low; all threats feed directly into the risk assessment, abuse cases, mitigations, and security requirements.

**5. Abuse Cases** — Translates the 8 highest-severity threats into concrete attacker-centric narratives (AC-01–AC-08), each specifying preconditions, step-by-step attack execution, business impact, and forward links to mitigations (M-01–M-08) and security requirements (SR-01–SR-08). Cases cover OS command injection, forged payment webhooks, price manipulation, JWT algorithm bypass, SQL injection, path traversal, TOCTOU race conditions, and coordinated VM fleet flooding.

**6. Risk Assessment** — Scores all 116 threats using the OWASP Risk Rating methodology (8 likelihood factors × 7 impact factors), producing a prioritised risk register ordered by Risk Score descending. The heat map yields 22 Critical, 49 High, 42 Medium, and 3 Low risks, with recommended actions (Mitigate Immediately / Mitigate in Phase 2 / Monitor / Accept) assigned per band.

**7. Mitigations** — Specifies 71 security mitigations (SR-01–SR-71) ordered by Risk Score, each with a technology-specific Implementation Notes column naming concrete Spring Security annotations, JJWT API calls, MySQL grant statements, and systemd directives for direct use by Phase 2 developers. The section concludes with a three-layer defense-in-depth strategy covering the Perimeter, Application, and Data layers.

**8. Requirements** — Defines 102 requirements using FURPS+: 31 functional (FR-01–FR-31), 3 usability, 6 reliability, 5 performance, 5 supportability NFRs, 6 constraints, and 46 security requirements (SR-01–SR-46) each mapped to an ASVS 5.0 verification item. A bidirectional traceability matrix links every functional requirement to the NFRs and SRs that constrain it, and every SR back to its originating threats and abuse cases.

**9. Security Testing Plan & Traceability** — Defines a shift-left testing strategy across six testing types (SAST, DAST, SCA, IAST, manual pen testing, unit/integration tests) with tool selections and CI/CD triggers. A traceability matrix provides 46 test cases (TST-01–TST-46) each linked to an SR, ASVS reference, threat ID, and concrete pass criteria; detailed test outlines for all 8 abuse cases are also included, achieving 100% SR coverage.

**10. Secure Architecture & Design** — Documents the seven secure-design principles applied to VendNet and the definitive architectural decisions for authentication (JWT + BCrypt(12) + TOTP MFA for Admins), RBAC enforcement (five-layer chain, ArchUnit CI verification), cryptography (AES-256-GCM backups, HMAC-SHA256 audit log integrity, mTLS with CRL/OCSP), OS operations sandboxing (four-layer path validation pipeline, symlink detection, systemd confinement), and the structured audit logging architecture. Addresses all 22 Critical threats and the 14 highest-impact High threats with direct mitigation references.

**11. ASVS 5.0 Checklist** — OWASP ASVS 5.0 compliance tracker mapping each applicable verification item to its implementation status, responsible security requirement (SR-XX), and supporting evidence. Targets ASVS Level 2, consistent with the system handling authenticated users, business logic, and sensitive financial and PII data.

## References

### Methodology & Standards

- Microsoft. (2009). *The STRIDE Threat Model*. Microsoft Developer Network. https://learn.microsoft.com/en-us/previous-versions/commerce-server/ee823878(v=cs.20)
- Shostack, A. (2014). *Threat Modeling: Designing for Security*. Wiley.
- OWASP Foundation. (2025). *OWASP Application Security Verification Standard (ASVS) Version 5.0.0* (May 2025). https://owasp.org/www-project-application-security-verification-standard/
- OWASP Foundation. (2023). *OWASP Risk Rating Methodology*. https://owasp.org/www-community/OWASP_Risk_Rating_Methodology
- OWASP Foundation. (2021). *OWASP Top Ten 2021*. https://owasp.org/www-project-top-ten/
- OWASP Foundation. (2023). *OWASP API Security Top 10 2023*. https://owasp.org/www-project-api-security/

### Architecture & Design
- Evans, E. (2003). *Domain-Driven Design: Tackling Complexity in the Heart of Software*. Addison-Wesley.
- Brown, S. (2023). *The C4 Model for Software Architecture*. https://c4model.com/

### Technology References
- Spring Security Team. (2024). *Spring Security Reference Documentation*. https://docs.spring.io/spring-security/reference/
- Auth0. (2024). *JSON Web Token Introduction*. https://jwt.io/introduction
- Oracle. (2024). *MySQL 8.4 Reference Manual*. https://dev.mysql.com/doc/refman/8.4/en/
- NIST. (2017). *NIST SP 800-63B: Digital Identity Guidelines — Authentication and Lifecycle Management*. https://pages.nist.gov/800-63-3/sp800-63b.html

### Course Materials
- ISEP. (2025). *DESOFS — Desenvolvimento de Software Seguro: Phase 1 Assignment Specification*. Instituto Superior de Engenharia do Porto.

### Project Artefacts
- desofs2026_wed_ffs_6. (2025). *OWASP ASVS 5.0 Compliance Tracker* [Spreadsheet, V1–V17]. Internal project artefact.