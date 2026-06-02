# Visitor Management System - Comprehensive API Test Plan

**Version**: 1.0  
**Date**: May 15, 2026  
**Author**: API Tester  
**Backend**: Java Spring Boot (Port 8081)  
**Database**: SQL Server (192.168.2.74 / visit_db)  
**Base URL**: `http://localhost:8081`

---

## Table of Contents

1. [Authentication Flow](#1-authentication-flow)
2. [Visitor Invite Flow](#2-visitor-invite-flow)
3. [Direct Check-In Flow](#3-direct-check-in-flow)
4. [Self Check-In/Check-Out Flow](#4-self-check-incheck-out-flow)
5. [Dashboard APIs](#5-dashboard-apis)
6. [Filter & Export APIs](#6-filter--export-apis)
7. [Admin APIs (Cancel, Expire, Upload)](#7-admin-apis)
8. [Master Data APIs](#8-master-data-apis)
9. [Email Setup APIs](#9-email-setup-apis)
10. [Public APIs](#10-public-apis)
11. [Negative & Edge Case Tests](#11-negative--edge-case-tests)
12. [API Gap Analysis: .NET vs Java](#12-api-gap-analysis-net-vs-java)
13. [Security Audit Findings](#13-security-audit-findings)
14. [Performance Benchmarks](#14-performance-benchmarks)
15. [Test Execution Instructions](#15-test-execution-instructions)

---

## 1. Authentication Flow

### 1.1 POST `/api/auth/login` - User Login

**Auth**: None (public)  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "Username": "admin",
  "Password": "admin123"
}
```

**Response (200 - Success)**:
```json
{
  "Username": null,
  "Password": null,
  "EmpId": 1,
  "EmpCode": "EMP001",
  "FirstName": "Admin",
  "MiddleName": null,
  "LastName": "User",
  "FullName": "Admin User",
  "EmailId": "admin@example.com",
  "MobileNo": "9876543210",
  "DesignationId": 1,
  "DesignationName": "Manager",
  "DeptName": "IT",
  "Photo": null,
  "Token": "eyJhbGciOiJIUzI1NiJ9...",
  "Msg": "Login successful",
  "Success": true
}
```

**Response (200 - Failed)**:
```json
{
  "Success": false,
  "Msg": "Invalid username or password"
}
```

**Error Scenarios**:
| Scenario | Expected HTTP | Expected Msg |
|----------|--------------|--------------|
| Empty username | 200 (success=false) | "Username and password are required" |
| Empty password | 200 (success=false) | "Username and password are required" |
| Invalid username | 200 (success=false) | "Invalid username or password" |
| Wrong password | 200 (success=false) | "Invalid username or password" |

**Test Script**:
```bash
curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"Username":"admin","Password":"admin123"}' | jq .
```

### 1.2 POST `/api/auth/validate` - Validate Token

**Auth**: Bearer Token (in header)  
**Content-Type**: None

**Request Headers**:
```
Authorization: Bearer <token>
```

**Response (200 - Valid)**:
```json
{
  "Success": true,
  "EmpId": 1,
  "FullName": "Admin User",
  "Token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response (200 - Invalid)**:
```json
{
  "Success": false,
  "Msg": "Invalid token"
}
```

**Error Scenarios**:
| Scenario | Expected HTTP | Expected Msg |
|----------|--------------|--------------|
| Missing token | 200 (success=false) | "Invalid token" |
| Expired token | 200 (success=false) | "Session expired" |
| Malformed token | 200 (success=false) | "Invalid token" |

### 1.3 POST `/api/auth/change-password` - Change Password

**Auth**: Bearer Token (in header)  
**Content-Type**: `application/json`

**Request Headers**:
```
Authorization: Bearer <token>
```

**Request Body**:
```json
{
  "oldPassword": "currentPass123",
  "newPassword": "newPass456"
}
```

**Response (200 - Success)**:
```json
{
  "Success": true,
  "Msg": "Password changed successfully"
}
```

**Error Scenarios**:
| Scenario | Expected HTTP | Expected Msg |
|----------|--------------|--------------|
| Wrong old password | 200 | "Current password is incorrect" |
| Invalid token | 200 | "Invalid token" |

---

## 2. Visitor Invite Flow

### 2.1 POST `/api/visitor/invite` - Invite a Visitor

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "Name": "John Doe",
  "Designation": "Software Engineer",
  "Company": "Tech Corp",
  "Purpose": "Business Meeting",
  "PMail": "john.doe@example.com",
  "OMail": "john@techcorp.com",
  "Mobile": "9876543210",
  "AMobile": "9123456780",
  "Photo": "",
  "CompId": "1",
  "WhomtoMeet": 1,
  "Date": "2026-05-15T00:00:00",
  "Time": "10:00",
  "EmpId": 1
}
```

**Response (200 - Success)**:
```json
{
  "VisitId": 45,
  "Name": "John Doe",
  "Designation": "Software Engineer",
  "Company": "Tech Corp",
  "Purpose": "Business Meeting",
  "PMail": "john.doe@example.com",
  "OMail": "john@techcorp.com",
  "Mobile": "9876543210",
  "Invited": true,
  "Accept": false,
  "Approved": false,
  "Status": "Invited",
  "InviteCode": "784512",
  "QR": "REG45",
  "Msg": "Visitor invited successfully",
  "EmpId": 1
}
```

**Response (200 - Missing EmpId)**:
```json
{
  "Msg": "EmpId is required"
}
```

**Error Scenarios**:
| Scenario | Expected HTTP | Expected Result |
|----------|--------------|----------------|
| Missing EmpId | 200 | `Msg: "EmpId is required"` |
| Valid request | 200 | `VisitId` returned, `InviteCode` returned |
| Database error | 500 | Server error |

**Flow Integration**: The `InviteCode` returned is the **Invite OTP** (6 digits) used for verification.

### 2.2 POST `/api/public/verify-otp` - Verify Invite OTP (Public)

**Auth**: None  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "OTP": "784512"
}
```

**Response (200 - Verified)**:
```json
{
  "VisitId": 45,
  "Name": "John Doe",
  "Status": "Invited",
  "InviteCode": "784512",
  "Msg": "OTP verified successfully"
}
```

**Response (200 - Invalid OTP)**:
```json
{
  "Msg": "Invalid OTP"
}
```

**Response (200 - Already Accepted/Expired)**:
```json
{
  "Msg": "Invalid or expired invitation"
}
```

### 2.3 POST `/api/visitor/accept-invite` - Accept Invite (Authenticated)

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "VisitId": 45,
  "Name": "John Doe",
  "Designation": "Software Engineer",
  "Company": "Tech Corp",
  "Purpose": "Business Meeting",
  "PMail": "john.doe@example.com",
  "Mobile": "9876543210"
}
```

**Response (200 - Accepted)**:
```json
{
  "VisitId": 45,
  "Name": "John Doe",
  "Status": "Invite Accepted",
  "Accept": true,
  "OTP": "235689",
  "Msg": "Invite accepted successfully"
}
```

**Note**: This also triggers:
- Check-in OTP generated and linked to the visit
- Email sent to employee with check-in OTP
- Email sent to visitor with confirmation

### 2.4 POST `/api/public/accept-invite` - Accept Invite (Public)

**Auth**: None  
**Content-Type**: `application/json`

Same request/response as 2.3 but without auth requirement.

### 2.5 POST `/api/public/verify-checkin-otp` - Verify Check-In OTP

**Auth**: None  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "OTP": "235689"
}
```

**Response (200 - Verified)**:
```json
{
  "VisitId": 45,
  "Name": "John Doe",
  "OTP": "235689",
  "Msg": "OTP verified successfully"
}
```

**Response (200 - Invalid)**:
```json
{
  "Msg": "Invalid OTP"
}
```

### 2.6 POST `/api/visitor/checkin` - Check-In

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "VisitId": 45,
  "IdCard": "DL-123456",
  "Accessories": "Laptop, Bag"
}
```

**Response (200 - Success)**:
```json
{
  "VisitId": 45,
  "IdCard": "DL-123456",
  "Accessories": "Laptop, Bag",
  "CheckIn": "2026-05-15T10:30:00",
  "Status": "Checked In",
  "Msg": "Check-in successful"
}
```

**Error Scenarios**:
| Scenario | Expected HTTP | Expected Msg |
|----------|--------------|--------------|
| Missing VisitId | 200 | "VisitId is required" |
| Non-existent VisitId | 200 | "Visitor not found" |
| Valid check-in | 200 | "Check-in successful" |

### 2.7 POST `/api/visitor/checkout` - Check-Out

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "VisitId": 45
}
```

**Response (200 - Success)**:
```json
{
  "VisitId": 45,
  "CheckOut": "2026-05-15T17:30:00",
  "Status": "Checked Out",
  "Msg": "Check-out successful"
}
```

---

## 3. Direct Check-In Flow

### 3.1 POST `/api/visitor/direct-checkin` - Direct Check-In

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Description**: Checks in a visitor directly without needing invite/accept flow. Sets `Invited=true`, `Accept=true`, `Approved=true`, `DirectCheckIn=true`, and `CheckIn` immediately.

**Request Body**:
```json
{
  "Name": "Walk-in Visitor",
  "Designation": "Consultant",
  "Company": "Biz Corp",
  "Purpose": "Consultation",
  "PMail": "walkin@example.com",
  "OMail": "walkin@bizcorp.com",
  "Mobile": "9988776655",
  "AMobile": "",
  "Photo": "",
  "CompId": "1",
  "WhomtoMeet": 1,
  "Date": "2026-05-15T00:00:00",
  "Time": "14:00",
  "IdCard": "WALK001",
  "Accessories": "None",
  "EmpId": 1
}
```

**Response (200 - Success)**:
```json
{
  "VisitId": 46,
  "Name": "Walk-in Visitor",
  "CheckIn": "2026-05-15T14:00:00",
  "Status": "Checked In",
  "DirectCheckIn": true,
  "Msg": "Direct check-in successful"
}
```

**Critical Fields**:
- `EmpId` is required (validated in service)
- Creates `VisitorInviteHistory` with `InviteCode="0"` and a generated `CheckInCode`

### 3.2 POST `/api/visitor/visitor-direct-checkin` - Self Direct Check-In

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Description**: Similar to direct check-in but by the visitor themselves (no `EmpId` or `WhomtoMeet` required). The visitor provides their own details.

**Request Body**:
```json
{
  "Name": "Self Visitor",
  "Designation": "Self",
  "Company": "Individual",
  "Purpose": "Personal Visit",
  "PMail": "self@example.com",
  "Mobile": "8877665544",
  "Time": "15:00",
  "Accessories": "Bag"
}
```

**Response (200 - Success)**:
```json
{
  "VisitId": 47,
  "Name": "Self Visitor",
  "CheckIn": "2026-05-15T15:00:00",
  "Status": "Checked In",
  "OTP": "456123",
  "Msg": "Direct check-in successful"
}
```

**Note**: Returns `OTP` (check-in code) in the response, useful for later checkout.

---

## 4. Self Check-In/Check-Out Flow

### 4.1 POST `/api/visitor/visitor-checkin` - Self Check-In

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Description**: Allows a visitor to check themselves in without reception staff intervention (requires prior VisitId).

**Request Body**:
```json
{
  "VisitId": 45
}
```

**Response (200 - Success)**:
```json
{
  "VisitId": 45,
  "CheckIn": "2026-05-15T11:00:00",
  "Status": "Checked In",
  "Msg": "Check-in successful"
}
```

### 4.2 POST `/api/visitor/visitor-checkout` - Self Check-Out

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Description**: Allows check-out via VisitId **OR** OTP (check-in code).

**Request Body (by VisitId)**:
```json
{
  "VisitId": 45
}
```

**Request Body (by OTP)**:
```json
{
  "OTP": "235689"
}
```

**Response (200 - Success)**:
```json
{
  "VisitId": 45,
  "CheckOut": "2026-05-15T17:00:00",
  "Status": "Checked Out",
  "Msg": "Check-out successful"
}
```

**Error Scenarios**:
| Scenario | Expected HTTP | Expected Msg |
|----------|--------------|--------------|
| Neither VisitId nor OTP | 200 | "VisitId or OTP is required" |
| Invalid VisitId | 200 | "Visitor not found" |
| Invalid OTP | 200 | "Visitor not found" |

---

## 5. Dashboard APIs

### 5.1 GET `/api/visitor/dashboard` - Main Dashboard

**Auth**: Bearer Token  
**Content-Type**: None

**Response (200)**:
```json
{
  "TotalVisitorsToday": 25,
  "TotalCheckIns": 18,
  "TotalCheckOuts": 12,
  "TotalInvited": 5,
  "TotalPending": 2
}
```

**Field Description**:
| Field | Source | Description |
|-------|--------|-------------|
| `TotalVisitorsToday` | `countTodayVisitors()` | All visitors with today's date |
| `TotalCheckIns` | `countTodayCheckIns()` | Visitors with non-null CheckIn today |
| `TotalCheckOuts` | `countTodayCheckOuts()` | Visitors with non-null CheckOut today |
| `TotalInvited` | Stream filter `Invited=true` | Visitors invited today |
| `TotalPending` | Stream filter `Invited=true && Accept!=true` | Invites not yet accepted |

### 5.2 GET `/api/visitor/user-dashboard/{empId}` - User Dashboard

**Auth**: Bearer Token  
**Content-Type**: None

**Response (200)**:
```json
{
  "TotalVisitors": 42,
  "TotalInvited": 15,
  "TotalAccepted": 10,
  "TotalCheckedIn": 8,
  "TotalCheckedOut": 5,
  "TodayVisitors": 3,
  "TodayCheckIns": 2,
  "TodayCheckOuts": 1,
  "TotalContacted": 20
}
```

**Field Description**:
| Field | Source |
|-------|--------|
| `TotalVisitors` | `findByCreatedBy` (all visitors created by user) |
| `TotalContacted` | `findByWhomToMeet` (visitors meeting this employee) |
| `TotalInvited` | Created by user, `Invited=true` |
| `TotalAccepted` | Created by user, `Accept=true` |
| `TotalCheckedIn` | Created by user, `CheckIn != null` |
| `TotalCheckedOut` | Created by user, `CheckOut != null` |
| `TodayVisitors` | Created by user, visit date == today |
| `TodayCheckIns` | Created by user, CheckIn != null, date == today |
| `TodayCheckOuts` | Created by user, CheckOut != null, date == today |

### 5.3 GET `/api/visitor/hr-dashboard` - HR Dashboard

**Auth**: Bearer Token  
**Content-Type**: None

**Response (200)**:
```json
{
  "TotalVisitorsToday": 25,
  "TotalCheckIns": 18,
  "TotalCheckOuts": 12
}
```

**Note**: Simpler than main dashboard - only returns today's counts without invite/pending breakdown.

---

## 6. Filter & Export APIs

### 6.1 GET `/api/visitor/today` - Today's Visitors

**Auth**: Bearer Token  
**Content-Type**: None

**Response (200)**:
```json
[
  {
    "VisitId": 45,
    "Name": "John Doe",
    "Company": "Tech Corp",
    "Status": "Checked In",
    ...
  },
  ...
]
```

### 6.2 POST `/api/visitor/list` - All Visitors

**Auth**: Bearer Token  
**Content-Type**: None (no body needed)

**Response (200)**: Array of `VisitorManagementViewModel` objects ordered by `VisitId DESC`.

### 6.3 POST `/api/visitor/all-invites` - All Invites

**Auth**: Bearer Token  
**Content-Type**: None (no body needed)

**Response (200)**:
```json
{
  "VisitorList": [
    {
      "VisitId": 45,
      "Name": "John Doe",
      ...
    }
  ],
  "Msg": "Success"
}
```

### 6.4 POST `/api/visitor/by-employee` - Visitors by Employee

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "EmpId": 1
}
```

**Response (200)**: Array of visitors created by the specified employee.

### 6.5 POST `/api/visitor/visit-filter` - Filter Visits

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Request Body (all params optional)**:
```json
{
  "FromDate": "2026-05-01T00:00:00",
  "ToDate": "2026-05-15T23:59:59",
  "Status": "CHECKED IN",
  "EmpId": 1
}
```

**Valid Status Values**:
| Status | Filter Logic |
|--------|-------------|
| `INVITED` | `Invited=true && Accept!=true` |
| `INVITE ACCEPTED` | `Accept=true && CheckIn==null` |
| `CHECKED IN` | `CheckIn!=null && CheckOut==null` |
| `CHECKED OUT` | `CheckOut!=null` |
| `EXPIRED` | `Expired=true` |

**Note**: Results are sorted with today's entries first, then by date DESC, then by VisitId DESC.

### 6.6 POST `/api/visitor/export-csv` - Export CSV

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Request Body** (same filter as 6.5):
```json
{
  "FromDate": "2026-01-01T00:00:00",
  "ToDate": "2026-12-31T23:59:59",
  "Status": ""
}
```

**Response (200)**: Binary CSV file with headers:
```
VisitId,Name,Company,Designation,PMail,OMail,Mobile,Date,Time,Purpose,Status,CheckIn,CheckOut,Photo
```

### 6.7 GET `/api/visitor/employees` - Employee Dropdown

**Auth**: Bearer Token  
**Content-Type**: None

**Response (200)**:
```json
[
  {
    "Id": 1,
    "Name": "Admin User",
    "Code": "EMP001"
  },
  ...
]
```

---

## 7. Admin APIs

### 7.1 POST `/api/visitor/cancel` - Cancel Invite

**Auth**: Bearer Token  
**Content-Type**: `application/json`

**Request Body**:
```json
{
  "VisitId": 45
}
```

**Response (200 - Success)**:
```json
{
  "VisitId": 45,
  "Msg": "Invite cancelled successfully"
}
```

**Response (200 - Not Found)**:
```json
{
  "Msg": "Visitor not found"
}
```

**Effect**: Sets `IsDeleted=true`, `IsActive=false` on the visitor record.

### 7.2 POST `/api/visitor/expire-invites` - Expire Old Invites

**Auth**: Bearer Token  
**Content-Type**: None

**Response (200)**:
```json
{
  "Msg": "Expired invites processed"
}
```

**Logic**: Marks invites from before yesterday 23:59:59 as `Expired=true` if they have no CheckIn and were not already checked out/expired.

### 7.3 POST `/api/visitor/upload-photo` - Upload Photo

**Auth**: Bearer Token  
**Content-Type**: `multipart/form-data`

**Request Form Data**:
```
file: <binary image file>
```

**Response (200 - Success)**:
```json
{
  "Photo": "/9j/4AAQSkZJRg...",
  "Msg": "Photo uploaded successfully"
}
```

**Response (400 - Empty)**:
```json
{
  "Msg": "File is empty"
}
```

**Response (400 - Not Image)**:
```json
{
  "Msg": "Only image files are allowed"
}
```

**Validation**:
- File must not be empty
- Content-Type must start with `image/`
- Max file size: 10MB (configured)
- Returns Base64 encoded image string

---

## 8. Master Data APIs

All under `/api/master`. Auth: Bearer Token (though no actual auth guard exists in the backend).

### 8.1 Departments
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/master/departments` | List all departments |
| POST | `/api/master/departments` | Create/update department |
| DELETE | `/api/master/departments/{id}` | Soft-delete department |
| GET | `/api/master/dept-list` | Active departments dropdown |
| GET | `/api/master/dept-names` | Distinct department names |
| GET | `/api/master/dept-names-by-company?compId=X` | Dept names by company |

### 8.2 Designations
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/master/designations` | List all designations |
| GET | `/api/master/designations/by-dept/{deptId}` | Designations by department |
| POST | `/api/master/designations` | Create/update designation |
| DELETE | `/api/master/designations/{id}` | Soft-delete designation |
| GET | `/api/master/designation-list?deptId=X` | Active designations dropdown |

### 8.3 Companies
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/master/companies` | List all companies |
| POST | `/api/master/companies` | Create/update company |
| DELETE | `/api/master/companies/{id}` | Soft-delete company |

### 8.4 Employees
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/master/employees-all` | List all employees |
| POST | `/api/master/employees` | Create/update employee |
| DELETE | `/api/master/employees/{id}` | Soft-delete employee |
| GET | `/api/master/employees-by-designation/{designationId}` | Employees by designation |
| GET | `/api/master/employees-by-dept?deptName=X` | Employees by department |
| GET | `/api/master/employees-filter?compId=X&deptName=X&designationId=X` | Cascading filter |

### 8.5 Grades
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/master/grades` | List grades |

---

## 9. Email Setup APIs

All under `/api/email-setup`. Auth: Bearer Token.

### 9.1 Configuration
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/email-setup` | Get first SMTP config |
| GET | `/api/email-setup/all` | Get all SMTP configs |
| POST | `/api/email-setup` | Create/update SMTP config |
| DELETE | `/api/email-setup/{id}` | Delete SMTP config |
| POST | `/api/email-setup/test` | Send test email |

### 9.2 POST `/api/email-setup/test` - Test Email

**Request Body**:
```json
{
  "email": "test@example.com"
}
```

**Response (200)**:
```json
{
  "Msg": "Test email sent successfully to test@example.com"
}
```

---

## 10. Public APIs

All under `/api/public`. No authentication required.

| Method | Path | Auth | Description | Documented In |
|--------|------|------|-------------|---------------|
| POST | `/api/public/verify-otp` | None | Verify invite OTP | Section 2.2 |
| POST | `/api/public/verify-checkin-otp` | None | Verify check-in OTP | Section 2.5 |
| POST | `/api/public/accept-invite` | None | Accept invite | Section 2.4 |

---

## 11. Negative & Edge Case Tests

### 11.1 No Authentication
Attempt to access any `/api/visitor/*` or `/api/master/*` endpoint without Bearer token.

**Note**: The Java backend has **NO authentication filter/interceptor**. All endpoints are technically accessible without valid tokens. The AuthService can issue/validate tokens, but no controller enforces them.

### 11.2 Invalid IDs
| Test | Endpoint | Input | Expected |
|------|----------|-------|----------|
| Cancel non-existent | `POST /api/visitor/cancel` | `{"VisitId":99999}` | `Msg: "Visitor not found"` |
| Check-in non-existent | `POST /api/visitor/checkin` | `{"VisitId":99999}` | `Msg: "Visitor not found"` |
| Check-out non-existent | `POST /api/visitor/checkout` | `{"VisitId":99999}` | `Msg: "Visitor not found"` |

### 11.3 Missing Required Fields
| Test | Endpoint | Input | Expected |
|------|----------|-------|----------|
| Invite without EmpId | `POST /api/visitor/invite` | `{"Name":"Test"}` | `Msg: "EmpId is required"` |
| Check-in without VisitId | `POST /api/visitor/checkin` | `{}` | `Msg: "VisitId is required"` |
| Visitor checkout without ids | `POST /api/visitor/visitor-checkout` | `{}` | `Msg: "VisitId or OTP is required"` |

### 11.4 Invalid OTP
| Test | Endpoint | Input | Expected |
|------|----------|-------|----------|
| Wrong invite OTP | `POST /api/public/verify-otp` | `{"OTP":"000000"}` | `Msg: "Invalid OTP"` |
| Wrong check-in OTP | `POST /api/public/verify-checkin-otp` | `{"OTP":"000000"}` | `Msg: "Invalid OTP"` |

### 11.5 Photo Upload Edge Cases
| Test | Expected HTTP | Expected Response |
|------|--------------|-------------------|
| Empty file | 400 | `"Msg": "File is empty"` |
| Text file (not image) | 400 | `"Msg": "Only image files are allowed"` |
| Valid PNG | 200 | Base64 photo data |
| File > 10MB | 400/413 | Size limit error |

### 11.6 Boundary Dates for Filter
| Test | Input | Expected |
|------|-------|----------|
| FromDate > ToDate | `{"FromDate":"2026-06-01","ToDate":"2026-01-01"}` | Empty results |
| Far future dates | `{"FromDate":"2099-01-01"}` | Empty results |
| Null dates | `{}` | All records |

---

## 12. API Gap Analysis: .NET vs Java

### Overview
The Java backend has significantly evolved beyond the original .NET implementation. The .NET controller (`VisitorController.cs`) represents an older generation with fewer features.

### Endpoint Mapping

| .NET Endpoint | Java Endpoint | Status | Notes |
|---------------|---------------|--------|-------|
| `Visitor/InviteVisit` | `POST /api/visitor/invite` | ✅ Matched | Same logic, JSON property names differ |
| `Visitor/VerifyOTP` | `POST /api/public/verify-otp` | ✅ Matched | Moved to public in Java |
| `Visitor/AcceptInvite` | `POST /api/visitor/accept-invite` + `/api/public/accept-invite` | ✅ Matched | Java has both auth and public versions |
| `Visitor/GetAllInvite` | `POST /api/visitor/all-invites` | ✅ Matched | |
| `Visitor/GetAllEmployeeInvite` | `POST /api/visitor/by-employee` | ✅ Matched | |
| `Visitor/CancelInvite` | `POST /api/visitor/cancel` | ✅ Matched | |
| `Visitor/DirectCheckIn` | `POST /api/visitor/direct-checkin` | ✅ Matched | |
| `Visitor/CheckIn` | `POST /api/visitor/checkin` | ✅ Matched | |
| `Visitor/CheckOut` | `POST /api/visitor/checkout` | ✅ Matched | |
| `Visitor/VisitorCheckIn` | `POST /api/visitor/visitor-checkin` | ✅ Matched | |
| `Visitor/VisitorCheckOut` | `POST /api/visitor/visitor-checkout` | ✅ Matched | |
| `Visitor/VerifyOTPCheckIn` | `POST /api/public/verify-checkin-otp` | ✅ Matched | Moved to public |
| `Visitor/VisitFilter` | `POST /api/visitor/visit-filter` | ✅ Matched | |
| `Visitor/DDCompany` | `GET /api/master/companies` + cascading dropdowns | ✅ Replaced | Enhanced with full master module |
| `Visitor/DDEmployee` | `GET /api/visitor/employees` + cascading filters | ✅ Replaced | Enhanced |
| `Visitor/VisitorDirectCheckIn` | `POST /api/visitor/visitor-direct-checkin` | ✅ Matched | |
| `Visitor/VisitExportCSV` | `POST /api/visitor/export-csv` | ✅ Matched | |
| `Visitor/VisitExportExcel` | **MISSING** | ❌ **Gap** | Java only has CSV export |
| `Visitor/UploadFileVisitor` | `POST /api/visitor/upload-photo` | ✅ Similar | Java is photo-only multipart upload |

### Features in Java NOT in .NET
| Feature | Java Endpoint | Description |
|---------|---------------|-------------|
| Authentication | `/api/auth/*` | Login, validate, change password with JWT |
| Dashboards | `/api/visitor/dashboard`, `/user-dashboard/{empId}`, `/hr-dashboard` | Stats and metrics |
| Today's visitors | `GET /api/visitor/today` | Quick lookup |
| Expire invites | `POST /api/visitor/expire-invites` | Batch expiry |
| Public API module | `/api/public/*` | Unauthenticated access for visitors |
| Master data CRUD | `/api/master/*` | Full departments, designations, companies, employees management |
| Cascading dropdowns | `/api/master/employees-filter` | Filtered employee lookup |
| Email setup | `/api/email-setup/*` | SMTP configuration management |
| Grade master | `/api/master/grades` | Grade listing |
| Photo upload | `POST /api/visitor/upload-photo` | Base64 photo upload |

### Key Gap: Excel Export
The .NET backend has `VisitExportExcel` which generates Excel files using EPPlus library. The Java backend only has CSV export. If Excel export is required, a new endpoint would need to be added using Apache POI or similar.

### Key Gap: Authentication Enforcement
The .NET controller uses `[AuthAttribute]` to enforce authentication. The Java backend has no equivalent - all controllers are publicly accessible despite having a token system. **This is a critical security vulnerability.**

---

## 13. Security Audit Findings

### 13.1 CRITICAL: No Authentication Enforcement
**Severity**: 🔴 **CRITICAL**

The Java backend has no security filter or interceptor. All endpoints under `/api/visitor/*`, `/api/master/*`, and `/api/email-setup/*` are publicly accessible without token validation. While the `AuthController` can generate and validate JWT tokens, **no other controller checks for them**.

**Evidence**:
- `WebConfig.java` only configures CORS (no security)
- No `OncePerRequestFilter`, `HandlerInterceptor`, or Spring Security configuration
- Controllers have no method-level security annotations
- The `@RestController` classes do not extract or validate the `Authorization` header

**Recommendation**: Implement a Spring Security filter or HandlerInterceptor to validate JWT tokens on all endpoints except `/api/auth/login` and `/api/public/*`.

### 13.2 MEDIUM: Hardcoded JWT Secret
**Severity**: 🟡 **MEDIUM**

```java
private static final String SECRET = "RimIndiaRimIndiaRimIndiaRimIndia";
```

The JWT signing key is hardcoded in `AuthService.java`. This should be externalized to application properties or environment variables.

### 13.3 MEDIUM: Password Encoding Exposed
**Severity**: 🟡 **MEDIUM**

```java
String encodedPassword = Base64.getEncoder().encodeToString(
    (password + "RimIndia@123").getBytes(StandardCharsets.UTF_16LE)
);
```

Password encoding uses Base64 (reversible, not a hash) with a hardcoded salt suffix. Should use bcrypt or Argon2.

### 13.4 LOW: CORS Wide Open
**Severity**: 🟢 **LOW**

```java
.allowedOrigins("*")
```

Allows any origin. Should be restricted to known frontend URLs in production.

### 13.5 LOW: SQL Server Credentials in Properties
**Severity**: 🟢 **LOW**

```properties
spring.datasource.username=sa
spring.datasource.password=Sqloct@2021
```

Database credentials should use environment variables or a vault.

---

## 14. Performance Benchmarks

### Target SLAs
| Metric | Target |
|--------|--------|
| Response Time (95th percentile) | < 500ms |
| Throughput | > 100 req/s |
| Error Rate | < 0.1% |
| Concurrent Users | 50+ |

### Performance Test Scenarios

#### 14.1 Dashboard Endpoints
| Endpoint | Expected Performance | Notes |
|----------|---------------------|-------|
| `GET /dashboard` | Fast (< 100ms) | 3 count queries, small dataset |
| `GET /user-dashboard/{empId}` | Fast (< 100ms) | Filtered by employee |
| `GET /hr-dashboard` | Fast (< 100ms) | 3 count queries |

#### 14.2 Data Retrieval
| Endpoint | Expected Performance | Notes |
|----------|---------------------|-------|
| `POST /list` | Medium (< 300ms) | Returns all non-deleted records |
| `POST /visit-filter` | Medium/High | Filters in memory (no SQL WHERE) - **optimization needed** |
| `GET /today` | Fast (< 100ms) | Date-filtered query |

**⚠️ Optimization Alert**: The `visitFilter()` method currently loads **ALL** records from the database (`visitorRepo.findAll()`) and filters in memory using Java streams. This will not scale beyond a few thousand records. Consider implementing proper JPA Specifications or native queries with WHERE clauses.

#### 14.3 Transactional Endpoints
| Endpoint | Expected Performance | Notes |
|----------|---------------------|-------|
| `POST /invite` | Medium (< 500ms) | Creates 2 entities + QR + email |
| `POST /direct-checkin` | Fast (< 200ms) | Creates 2 entities |
| `POST /checkin` | Fast (< 200ms) | Updates 2 entities |
| `POST /checkout` | Fast (< 200ms) | Updates 2 entities |

#### 14.4 Export
| Endpoint | Expected Performance | Notes |
|----------|---------------------|-------|
| `POST /export-csv` | Depends on data size | Streams data, large payloads may be slow |

### Load Testing Recommendations
- Use k6 or JMeter for load testing
- Key transaction: Invite → Verify OTP → Accept → Check-in → Check-out
- Monitor SQL Server connection pool usage
- Test with 10x normal expected concurrent users

---

## 15. Test Execution Instructions

### Prerequisites
```bash
# Install required tools
choco install curl jq     # Windows (Chocolatey)
# OR
sudo apt install curl jq  # Linux
# OR
brew install curl jq      # macOS
```

### Running the Automated Test Script

**Option 1: Bash script** (requires bash on Windows via Git Bash/WSL, or Linux/macOS):
```bash
# Make executable
chmod +x api-test.sh

# Run with default credentials
./api-test.sh

# Run with custom credentials
./api-test.sh admin mypassword

# Run with custom base URL
BASE_URL=http://localhost:8081 ./api-test.sh admin pass
```

**Option 2: PowerShell** (native Windows):
```powershell
# Run the bash script via Git Bash
& "C:\Program Files\Git\bin\bash.exe" api-test.sh
```

### Manual Test Workflow (Complete Happy Path)

```bash
# Step 1: Login
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"Username":"admin","Password":"admin123"}' | jq -r '.Token')
EMP_ID=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"Username":"admin","Password":"admin123"}' | jq -r '.EmpId')

echo "Token: ${TOKEN:0:50}..."
echo "EmpId: $EMP_ID"

# Step 2: Invite a visitor
INVITE=$(curl -s -X POST http://localhost:8081/api/visitor/invite \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"Name\":\"John Doe\",\"Designation\":\"Engineer\",\"Company\":\"TestCorp\",\"Purpose\":\"Visit\",\"PMail\":\"john@test.com\",\"Mobile\":\"9876543210\",\"EmpId\":$EMP_ID,\"WhomtoMeet\":$EMP_ID}")
VISIT_ID=$(echo $INVITE | jq -r '.VisitId')
INVITE_OTP=$(echo $INVITE | jq -r '.InviteCode')
echo "VisitId: $VISIT_ID, OTP: $INVITE_OTP"

# Step 3: Verify OTP (public)
curl -s -X POST http://localhost:8081/api/public/verify-otp \
  -H "Content-Type: application/json" \
  -d "{\"OTP\":\"$INVITE_OTP\"}" | jq .

# Step 4: Accept invite
ACCEPT=$(curl -s -X POST http://localhost:8081/api/visitor/accept-invite \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"VisitId\":$VISIT_ID,\"Name\":\"John Doe\",\"Designation\":\"Engineer\",\"Company\":\"TestCorp\",\"Purpose\":\"Visit\",\"PMail\":\"john@test.com\",\"Mobile\":\"9876543210\"}")
CHECKIN_OTP=$(echo $ACCEPT | jq -r '.OTP')
echo "Check-in OTP: $CHECKIN_OTP"

# Step 5: Check-in
curl -s -X POST http://localhost:8081/api/visitor/checkin \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"VisitId\":$VISIT_ID,\"IdCard\":\"DL123\",\"Accessories\":\"Bag\"}" | jq .

# Step 6: Check-out
curl -s -X POST http://localhost:8081/api/visitor/checkout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"VisitId\":$VISIT_ID}" | jq .
```

### Test Data Management
- Tests create real records in the database
- To clean up, note the VisitIds created and use the cancel endpoint
- The `expire-invites` endpoint can clean up old test data

### Environment Configuration
The script expects:
- **BASE_URL** env var (default: `http://localhost:8081`)
- Backend running and accessible
- SQL Server database accessible from test machine
- Valid employee credentials in the database

---

## 16. Complete API Inventory

### Summary Table

| # | Method | Path | Auth Required | Request Body | Response Type |
|---|--------|------|:------------:|-------------|---------------|
| **Auth** | | | | | |
| A1 | POST | `/api/auth/login` | No | `LoginViewModel` | `LoginViewModel` |
| A2 | POST | `/api/auth/validate` | Yes (Header) | None | `LoginViewModel` |
| A3 | POST | `/api/auth/change-password` | Yes (Header) | `{oldPassword, newPassword}` | `LoginViewModel` |
| **Visitor** | | | | | |
| V1 | GET | `/api/visitor/dashboard` | No* | None | `DashboardViewModel` |
| V2 | GET | `/api/visitor/user-dashboard/{empId}` | No* | None | `UserDashboardViewModel` |
| V3 | GET | `/api/visitor/hr-dashboard` | No* | None | `DashboardViewModel` |
| V4 | GET | `/api/visitor/today` | No* | None | `[VisitorManagementViewModel]` |
| V5 | POST | `/api/visitor/list` | No* | None | `[VisitorManagementViewModel]` |
| V6 | POST | `/api/visitor/all-invites` | No* | None | `VisitorManagementViewModel` |
| V7 | POST | `/api/visitor/by-employee` | No* | `{EmpId}` | `[VisitorManagementViewModel]` |
| V8 | POST | `/api/visitor/invite` | No* | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| V9 | POST | `/api/visitor/accept-invite` | No* | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| V10 | POST | `/api/visitor/direct-checkin` | No* | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| V11 | POST | `/api/visitor/checkin` | No* | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| V12 | POST | `/api/visitor/checkout` | No* | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| V13 | POST | `/api/visitor/cancel` | No* | `{VisitId}` | `VisitorManagementViewModel` |
| V14 | POST | `/api/visitor/visit-filter` | No* | `FilterViewModel` | `[VisitorManagementViewModel]` |
| V15 | POST | `/api/visitor/visitor-checkin` | No* | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| V16 | POST | `/api/visitor/visitor-checkout` | No* | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| V17 | POST | `/api/visitor/visitor-direct-checkin` | No* | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| V18 | POST | `/api/visitor/expire-invites` | No* | None | `{Msg}` |
| V19 | POST | `/api/visitor/upload-photo` | No* | Multipart | `{Photo, Msg}` |
| V20 | POST | `/api/visitor/export-csv` | No* | `FilterViewModel` | CSV file (byte[]) |
| V21 | GET | `/api/visitor/employees` | No* | None | `[DropdownViewModel]` |
| **Public** | | | | | |
| P1 | POST | `/api/public/verify-otp` | No | `{OTP}` | `VisitorManagementViewModel` |
| P2 | POST | `/api/public/verify-checkin-otp` | No | `{OTP}` | `VisitorManagementViewModel` |
| P3 | POST | `/api/public/accept-invite` | No | `VisitorManagementViewModel` | `VisitorManagementViewModel` |
| **Master** | | | | | |
| M1-M21 | Various | `/api/master/*` | No* | Various | Various |
| **Email** | | | | | |
| E1-E5 | Various | `/api/email-setup/*` | No* | Various | Various |

**No\*** = No auth is enforced despite the endpoint being under "authenticated" routes. This is a security gap.

---

## Test Script Location

The automated test script has been saved to:
```
G:\Business Projects\visit_app\backend\api-test.sh
```

Run with:
```bash
# Option 1: Bash
bash api-test.sh

# Option 2: PowerShell
& "C:\Program Files\Git\bin\bash.exe" api-test.sh
```

---

*End of Test Plan*
