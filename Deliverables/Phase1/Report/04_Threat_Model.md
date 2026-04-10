# 4. Threat Model — STRIDE Analysis

## 4.1 Methodology
<!-- TODO: Describe the STRIDE-per-element approach. Reference Microsoft Threat Modeling Tool or equivalent. -->

## 4.2 STRIDE Analysis per DFD Element

### 4.2.1 External Entities

#### Customer

| STRIDE Category | Applicable? | Threat ID | Threat Description | Attack Vector | Threat Agent |
|-----------------|-------------|-----------|-------------------|---------------|-------------|
| **S**poofing | Yes/No | T-XX | <!-- TODO --> | <!-- TODO --> | <!-- TODO --> |
| **T**ampering | Yes/No | T-XX | <!-- TODO --> | <!-- TODO --> | <!-- TODO --> |
| **R**epudiation | Yes/No | T-XX | <!-- TODO --> | <!-- TODO --> | <!-- TODO --> |
| **I**nformation Disclosure | Yes/No | T-XX | <!-- TODO --> | <!-- TODO --> | <!-- TODO --> |
| **D**enial of Service | Yes/No | T-XX | <!-- TODO --> | <!-- TODO --> | <!-- TODO --> |
| **E**levation of Privilege | Yes/No | T-XX | <!-- TODO --> | <!-- TODO --> | <!-- TODO --> |

#### Operator
<!-- TODO: Repeat table -->

#### Administrator
<!-- TODO: Repeat table -->

#### Vending Machine (Edge)
<!-- TODO: Repeat table -->

#### Payment Gateway
<!-- TODO: Repeat table -->

### 4.2.2 Processes

#### 1.0 Authentication & Authorization
<!-- TODO: Repeat STRIDE table -->

#### 2.0 Inventory Management
<!-- TODO: Repeat STRIDE table -->

#### 3.0 Sales Processing
<!-- TODO: Repeat STRIDE table -->

#### 4.0 Telemetry & Monitoring
<!-- TODO: Repeat STRIDE table -->

#### 5.0 OS Operations
<!-- TODO: Repeat STRIDE table -->

#### 6.0 Pricing & Configuration
<!-- TODO: Repeat STRIDE table -->

### 4.2.3 Data Stores

#### Users DB
<!-- TODO: Focus on Tampering, Information Disclosure, Denial of Service -->

#### Inventory DB
<!-- TODO: Repeat -->

#### Sales DB
<!-- TODO: Repeat -->

#### Telemetry DB
<!-- TODO: Repeat -->

#### File System (OS)
<!-- TODO: Repeat — critical for path traversal, unauthorized access -->

### 4.2.4 Data Flows

<!-- TODO: For each data flow in Level 1 DFD, analyze STRIDE (focus on Tampering, Information Disclosure) -->

| Data Flow | From → To | S | T | R | I | D | E | Key Threats |
|-----------|-----------|---|---|---|---|---|---|-------------|
| Credentials | Customer → P1 | <!-- TODO --> | | | | | | |
| JWT Token | P1 → Customer | | | | | | | |
| Purchase Request | Customer → P3 | | | | | | | |
| Payment Request | P3 → PayGW | | | | | | | |
| Telemetry Data | VM → P4 | | | | | | | |
| Backup Files | P5 → FS | | | | | | | |
| ... | | | | | | | | |

## 4.3 Threat Summary

| Threat ID | Element | STRIDE | Description | Severity (pre-mitigation) |
|-----------|---------|--------|-------------|---------------------------|
| T-01 | <!-- TODO --> | S | <!-- TODO --> | <!-- TODO --> |
| T-02 | <!-- TODO --> | T | <!-- TODO --> | <!-- TODO --> |
| ... | | | | |
