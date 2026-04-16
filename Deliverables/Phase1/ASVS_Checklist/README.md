# ASVS 4.0 Assessment Checklist — Phase 1 (Architecture Focus)

> **ASVS Level Target:** L2 — The application targets Level 2 as it handles authenticated users, business logic, and sensitive operations, requiring protection against common and advanced web application risks.The project also demonstrates security practises, such as detailed threat modeling using STRIDE and mitigations, indicating a strong security posture.
> **Date:** 2026-04-15
> **Assessor:** Group 5 

## V1 - Encoding and Sanitization

| Section_ID | Section Name  | Req ID                   | Description | Level         | Status | Observations | Reference___Link | 
|------------|---------------|--------------------------|-------------|---------------|--------|--------------|---|
| V1.1.1     | <!-- TODO --> | <!-- Met/Planned/N/A --> | SR-XX       | <!-- TODO --> |        |              |   |
| V1.1     | Encoding_and_Sanitization_Architecture |                          |       |  |        |              |   |
| V1.1       |       Encoding_and_Sanitization_Architecture       | V1.1.1                   |       Verify that input is decoded or unescaped into a canonical form only once, it is only decoded when encoded data in that form is expected, and that this is done before processing the input further, for example it is not performed after input validation or sanitization.      | 2             |        |              |   |
| V1.1       |        Encoding and Sanitization Architecture      | V1.1.2                   |       Verify that the application performs output encoding and escaping either as a final step before being used by the interpreter for which it is intended or by the interpreter itself.      | 2             |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.1                   |             |               |        |              |   | 
| V1.2        |        Injection Prevention      | V1.2.2                   |             |               |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.3                   |             |               |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.4                   |             |               |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.5                   |             |               |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.6                   |             |               |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.7                   |             |               |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.8                   |             |               |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.9                   |             |               |        |              |   |
| V1.2        |        Injection Prevention      | V1.2.10                  |             |               |        |              |   |


## V1: Architecture, Design and Threat Modeling

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes                                                                                                                                                                                         |
|---------|------------------------|--------|-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| V1.1.1 | <!-- TODO --> | <!-- Met/Planned/N/A --> | SR-XX | <!-- TODO --> |
| ... | | | | |

## V2: Authentication

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| V2.1.1 | <!-- TODO --> | | SR-XX | |
| ... | | | | |

## V3: Session Management

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| | <!-- TODO --> | | | |

## V4: Access Control

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| | <!-- TODO --> | | | |

## V5: Validation, Sanitization and Encoding

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| | <!-- TODO --> | | | |

## V6: Stored Cryptography

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| | <!-- TODO --> | | | |

## V7: Error Handling and Logging

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| | <!-- TODO --> | | | |

## V8: Data Protection

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| | <!-- TODO --> | | | |

## V9: Communication

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| | <!-- TODO --> | | | |

## V14: Configuration

| ASVS ID | Requirement Description | Status | Mapped SR | Evidence / Notes |
|---------|------------------------|--------|-----------|------------------|
| | <!-- TODO --> | | | |
