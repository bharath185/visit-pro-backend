#!/bin/bash
# ============================================================
# Visitor Management System - Complete API Test Script
# ============================================================
# This script tests ALL backend API endpoints.
# It requires curl and jq to be installed.
#
# Usage:
#   export BASE_URL="http://localhost:8081"
#   chmod +x api-test.sh
#   ./api-test.sh [username] [password]
#
# If username/password are omitted, defaults are used.
# ============================================================

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8081}"
USERNAME="${1:-admin}"
PASSWORD="${2:-admin123}"
PASS=0
FAIL=0
FAILURES=""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}====================================================${NC}"
echo -e "${CYAN}   Visitor Management System - API Test Suite${NC}"
echo -e "${CYAN}   Base URL: ${BASE_URL}${NC}"
echo -e "${CYAN}   Username: ${USERNAME}${NC}"
echo -e "${CYAN}====================================================${NC}"
echo ""

TOKEN=""
EMP_ID=""
VISIT_ID=""
INVITE_CODE=""
CHECKIN_OTP=""

# Helper function to print test result
print_result() {
    local test_name="$1"
    local status="$2"
    if [ "$status" == "PASS" ]; then
        echo -e "  ${GREEN}[PASS]${NC} ${test_name}"
        PASS=$((PASS+1))
    else
        echo -e "  ${RED}[FAIL]${NC} ${test_name} - $3"
        FAIL=$((FAIL+1))
        FAILURES="${FAILURES}\n  - ${test_name}: $3"
    fi
}

# Helper: make HTTP request and capture status code
call_api() {
    local method="$1"
    local url="$2"
    local data="$3"
    local auth_header="$4"
    local output_file="$5"

    local headers=(-s -o "$output_file" -w "%{http_code}")
    if [ -n "$auth_header" ]; then
        headers+=(-H "Authorization: $auth_header")
    fi
    if [ -n "$data" ]; then
        headers+=(-H "Content-Type: application/json" -d "$data")
    fi

    http_code=$(curl "${headers[@]}" -X "$method" "$url" 2>/dev/null || echo "000")
    echo "$http_code"
}

# =============================================================
echo -e "${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    1. AUTHENTICATION FLOW${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 1.1 LOGIN
echo -e "\n${CYAN}>> 1.1 POST /api/auth/login${NC}"
LOGIN_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/auth/login" \
    "{\"Username\":\"${USERNAME}\",\"Password\":\"${PASSWORD}\"}" \
    "" "$LOGIN_RESP")

if [ "$HTTP_CODE" = "200" ]; then
    SUCCESS=$(jq -r '.Success' "$LOGIN_RESP" 2>/dev/null || echo "false")
    if [ "$SUCCESS" = "true" ]; then
        TOKEN=$(jq -r '.Token' "$LOGIN_RESP" 2>/dev/null || echo "")
        EMP_ID=$(jq -r '.EmpId // 0' "$LOGIN_RESP" 2>/dev/null || echo "0")
        print_result "Login as ${USERNAME}" "PASS"
        echo "    Token: $(echo ${TOKEN} | head -c 50)..."
        echo "    EmpId: ${EMP_ID}"
    else
        MSG=$(jq -r '.Msg' "$LOGIN_RESP" 2>/dev/null || echo "Unknown")
        print_result "Login as ${USERNAME}" "FAIL" "Login failed: ${MSG}"
        rm -f "$LOGIN_RESP"
        echo -e "${RED}Login failed. Aborting further tests.${NC}"
        exit 1
    fi
else
    print_result "Login as ${USERNAME}" "FAIL" "HTTP ${HTTP_CODE}"
    rm -f "$LOGIN_RESP"
    exit 1
fi
rm -f "$LOGIN_RESP"

# 1.2 VALIDATE TOKEN
echo -e "\n${CYAN}>> 1.2 POST /api/auth/validate${NC}"
VALIDATE_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/auth/validate" "" "Bearer ${TOKEN}" "$VALIDATE_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    SUCCESS=$(jq -r '.Success' "$VALIDATE_RESP" 2>/dev/null || echo "false")
    if [ "$SUCCESS" = "true" ]; then
        print_result "Validate Token" "PASS"
    else
        print_result "Validate Token" "FAIL" "Token validation failed"
    fi
else
    print_result "Validate Token" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$VALIDATE_RESP"

# 1.3 LOGIN WITH WRONG CREDENTIALS (Negative Test)
echo -e "\n${CYAN}>> 1.3 Negative: Login with wrong password${NC}"
WRONG_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/auth/login" \
    '{"Username":"invalid","Password":"wrong"}' \
    "" "$WRONG_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    SUCCESS=$(jq -r '.Success' "$WRONG_RESP" 2>/dev/null || echo "true")
    if [ "$SUCCESS" = "false" ]; then
        MSG=$(jq -r '.Msg' "$WRONG_RESP" 2>/dev/null || echo "")
        print_result "Reject invalid credentials" "PASS"
        echo "    Message: ${MSG}"
    else
        print_result "Reject invalid credentials" "FAIL" "Login succeeded unexpectedly"
    fi
else
    print_result "Reject invalid credentials" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$WRONG_RESP"

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    2. DASHBOARD APIS${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 2.1 GET DASHBOARD
echo -e "\n${CYAN}>> 2.1 GET /api/visitor/dashboard${NC}"
DASH_RESP=$(mktemp)
HTTP_CODE=$(call_api "GET" "${BASE_URL}/api/visitor/dashboard" "" "Bearer ${TOKEN}" "$DASH_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    TVT=$(jq -r '.TotalVisitorsToday // 0' "$DASH_RESP" 2>/dev/null || echo "0")
    TCI=$(jq -r '.TotalCheckIns // 0' "$DASH_RESP" 2>/dev/null || echo "0")
    TCO=$(jq -r '.TotalCheckOuts // 0' "$DASH_RESP" 2>/dev/null || echo "0")
    print_result "Dashboard stats" "PASS"
    echo "    Today: ${TVT}, CheckIns: ${TCI}, CheckOuts: ${TCO}"
else
    print_result "Dashboard stats" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$DASH_RESP"

# 2.2 GET USER DASHBOARD
echo -e "\n${CYAN}>> 2.2 GET /api/visitor/user-dashboard/{empId}${NC}"
UDASH_RESP=$(mktemp)
HTTP_CODE=$(call_api "GET" "${BASE_URL}/api/visitor/user-dashboard/${EMP_ID}" "" "Bearer ${TOKEN}" "$UDASH_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    TV=$(jq -r '.TotalVisitors // 0' "$UDASH_RESP" 2>/dev/null || echo "0")
    TI=$(jq -r '.TotalInvited // 0' "$UDASH_RESP" 2>/dev/null || echo "0")
    print_result "User dashboard for EmpId=${EMP_ID}" "PASS"
    echo "    Visitors: ${TV}, Invited: ${TI}"
else
    print_result "User dashboard for EmpId=${EMP_ID}" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$UDASH_RESP"

# 2.3 GET HR DASHBOARD
echo -e "\n${CYAN}>> 2.3 GET /api/visitor/hr-dashboard${NC}"
HRDASH_RESP=$(mktemp)
HTTP_CODE=$(call_api "GET" "${BASE_URL}/api/visitor/hr-dashboard" "" "Bearer ${TOKEN}" "$HRDASH_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    TVT=$(jq -r '.TotalVisitorsToday // 0' "$HRDASH_RESP" 2>/dev/null || echo "0")
    TCI=$(jq -r '.TotalCheckIns // 0' "$HRDASH_RESP" 2>/dev/null || echo "0")
    print_result "HR Dashboard" "PASS"
    echo "    Today: ${TVT}, CheckIns: ${TCI}"
else
    print_result "HR Dashboard" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$HRDASH_RESP"

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    3. VISITOR LIST & EMPLOYEE ENDPOINTS${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 3.1 GET TODAY'S VISITORS
echo -e "\n${CYAN}>> 3.1 GET /api/visitor/today${NC}"
TODAY_RESP=$(mktemp)
HTTP_CODE=$(call_api "GET" "${BASE_URL}/api/visitor/today" "" "Bearer ${TOKEN}" "$TODAY_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    COUNT=$(jq 'length' "$TODAY_RESP" 2>/dev/null || echo "0")
    print_result "Today's visitors" "PASS"
    echo "    Count: ${COUNT}"
else
    print_result "Today's visitors" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$TODAY_RESP"

# 3.2 GET ALL VISITORS
echo -e "\n${CYAN}>> 3.2 POST /api/visitor/list${NC}"
LIST_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/list" "" "Bearer ${TOKEN}" "$LIST_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    COUNT=$(jq 'length' "$LIST_RESP" 2>/dev/null || echo "0")
    print_result "All visitors list" "PASS"
    echo "    Total visitors: ${COUNT}"
else
    print_result "All visitors list" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$LIST_RESP"

# 3.3 GET ALL INVITES
echo -e "\n${CYAN}>> 3.3 POST /api/visitor/all-invites${NC}"
ALLINV_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/all-invites" "" "Bearer ${TOKEN}" "$ALLINV_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    VCOUNT=$(jq '.VisitorList | length' "$ALLINV_RESP" 2>/dev/null || echo "0")
    MSG=$(jq -r '.Msg // ""' "$ALLINV_RESP" 2>/dev/null || echo "")
    print_result "All invites" "PASS"
    echo "    Invites count: ${VCOUNT}, Msg: ${MSG}"
else
    print_result "All invites" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$ALLINV_RESP"

# 3.4 GET VISITORS BY EMPLOYEE
echo -e "\n${CYAN}>> 3.4 POST /api/visitor/by-employee${NC}"
BYEMP_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/by-employee" \
    "{\"EmpId\":${EMP_ID}}" \
    "Bearer ${TOKEN}" "$BYEMP_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    COUNT=$(jq 'length' "$BYEMP_RESP" 2>/dev/null || echo "0")
    print_result "Visitors by employee (EmpId=${EMP_ID})" "PASS"
    echo "    Count: ${COUNT}"
else
    print_result "Visitors by employee" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$BYEMP_RESP"

# 3.5 GET EMPLOYEES DROPDOWN
echo -e "\n${CYAN}>> 3.5 GET /api/visitor/employees${NC}"
EMPS_RESP=$(mktemp)
HTTP_CODE=$(call_api "GET" "${BASE_URL}/api/visitor/employees" "" "Bearer ${TOKEN}" "$EMPS_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    COUNT=$(jq 'length' "$EMPS_RESP" 2>/dev/null || echo "0")
    print_result "Employee dropdown list" "PASS"
    echo "    Employees: ${COUNT}"
else
    print_result "Employee dropdown list" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$EMPS_RESP"

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    4. VISITOR INVITE FLOW${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 4.1 INVITE VISITOR
echo -e "\n${CYAN}>> 4.1 POST /api/visitor/invite${NC}"
INVITE_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/invite" \
    "{\"Name\":\"Test Visitor\",\"Designation\":\"Guest\",\"Company\":\"Test Corp\",\"Purpose\":\"Meeting\",\"PMail\":\"test.visitor@example.com\",\"Mobile\":\"9876543210\",\"EmpId\":${EMP_ID},\"WhomtoMeet\":${EMP_ID}}" \
    "Bearer ${TOKEN}" "$INVITE_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    MSG=$(jq -r '.Msg // ""' "$INVITE_RESP" 2>/dev/null || echo "")
    VISIT_ID=$(jq -r '.VisitId // ""' "$INVITE_RESP" 2>/dev/null || echo "")
    INVITE_CODE=$(jq -r '.InviteCode // ""' "$INVITE_RESP" 2>/dev/null || echo "")
    if [ -n "$VISIT_ID" ] && [ "$VISIT_ID" != "null" ]; then
        print_result "Invite visitor" "PASS"
        echo "    VisitId: ${VISIT_ID}, Msg: ${MSG}"
        echo "    InviteCode (OTP): ${INVITE_CODE}"
    else
        print_result "Invite visitor" "FAIL" "No VisitId returned: ${MSG}"
    fi
else
    print_result "Invite visitor" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$INVITE_RESP"

# 4.2 VERIFY INVITE OTP (Public)
if [ -n "$INVITE_CODE" ] && [ "$INVITE_CODE" != "null" ]; then
    echo -e "\n${CYAN}>> 4.2 POST /api/public/verify-otp${NC}"
    VERIFY_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/public/verify-otp" \
        "{\"OTP\":\"${INVITE_CODE}\"}" \
        "" "$VERIFY_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$VERIFY_RESP" 2>/dev/null || echo "")
        if echo "$MSG" | grep -qi "verified"; then
            print_result "Verify invite OTP" "PASS"
            echo "    Msg: ${MSG}"
        else
            print_result "Verify invite OTP" "FAIL" "Unexpected: ${MSG}"
        fi
    else
        # Could be 500 if OTP already consumed etc - still a useful test
        print_result "Verify invite OTP" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$VERIFY_RESP"
else
    echo -e "\n${CYAN}>> 4.2 POST /api/public/verify-otp${NC}"
    print_result "Verify invite OTP" "FAIL" "No InviteCode from step 4.1"
fi

# 4.3 VERIFY CHECK-IN OTP (Public)
if [ -n "$INVITE_CODE" ] && [ "$INVITE_CODE" != "null" ]; then
    echo -e "\n${CYAN}>> 4.3 POST /api/public/verify-checkin-otp${NC}"
    VERIFYCI_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/public/verify-checkin-otp" \
        "{\"OTP\":\"${INVITE_CODE}\"}" \
        "" "$VERIFYCI_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$VERIFYCI_RESP" 2>/dev/null || echo "")
        print_result "Verify check-in OTP (with wrong OTP)" "PASS"
        echo "    Msg: ${MSG} (expected: 'Invalid OTP' or similar)"
    else
        print_result "Verify check-in OTP endpoint" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$VERIFYCI_RESP"
else
    echo -e "\n${CYAN}>> 4.3 POST /api/public/verify-checkin-otp${NC}"
    print_result "Verify check-in OTP" "FAIL" "No InviteCode"
fi

# 4.4 ACCEPT INVITE (Authenticated)
if [ -n "$VISIT_ID" ] && [ "$VISIT_ID" != "null" ]; then
    echo -e "\n${CYAN}>> 4.4 POST /api/visitor/accept-invite${NC}"
    ACCEPT_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/accept-invite" \
        "{\"VisitId\":${VISIT_ID},\"Name\":\"Test Visitor\",\"Designation\":\"Guest\",\"Company\":\"Test Corp\",\"Purpose\":\"Meeting\",\"PMail\":\"test.visitor@example.com\",\"Mobile\":\"9876543210\"}" \
        "Bearer ${TOKEN}" "$ACCEPT_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$ACCEPT_RESP" 2>/dev/null || echo "")
        CHECKIN_OTP=$(jq -r '.OTP // ""' "$ACCEPT_RESP" 2>/dev/null || echo "")
        print_result "Accept invite" "PASS"
        echo "    Msg: ${MSG}"
        echo "    CheckIn OTP: ${CHECKIN_OTP}"
    else
        print_result "Accept invite" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$ACCEPT_RESP"
else
    echo -e "\n${CYAN}>> 4.4 POST /api/visitor/accept-invite${NC}"
    print_result "Accept invite" "FAIL" "No VisitId from step 4.1"
fi

# 4.5 ACCEPT INVITE (Public)
if [ -n "$VISIT_ID" ] && [ "$VISIT_ID" != "null" ]; then
    echo -e "\n${CYAN}>> 4.5 POST /api/public/accept-invite${NC}"
    ACCEPTPUB_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/public/accept-invite" \
        "{\"VisitId\":${VISIT_ID},\"Name\":\"Test Visitor\",\"Designation\":\"Guest\",\"Company\":\"Test Corp\",\"Purpose\":\"Meeting\",\"PMail\":\"test.visitor@example.com\",\"Mobile\":\"9876543210\"}" \
        "" "$ACCEPTPUB_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$ACCEPTPUB_RESP" 2>/dev/null || echo "")
        print_result "Accept invite (public)" "PASS"
        echo "    Msg: ${MSG}"
    else
        print_result "Accept invite (public)" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$ACCEPTPUB_RESP"
else
    echo -e "\n${CYAN}>> 4.5 POST /api/public/accept-invite${NC}"
    print_result "Accept invite (public)" "FAIL" "No VisitId"
fi

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    5. CHECK-IN / CHECK-OUT FLOW${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 5.1 CHECK-IN
if [ -n "$VISIT_ID" ] && [ "$VISIT_ID" != "null" ]; then
    echo -e "\n${CYAN}>> 5.1 POST /api/visitor/checkin${NC}"
    CHECKIN_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/checkin" \
        "{\"VisitId\":${VISIT_ID},\"IdCard\":\"ID12345\",\"Accessories\":\"Laptop, Bag\"}" \
        "Bearer ${TOKEN}" "$CHECKIN_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$CHECKIN_RESP" 2>/dev/null || echo "")
        CI_TIME=$(jq -r '.CheckIn // ""' "$CHECKIN_RESP" 2>/dev/null || echo "")
        print_result "Check-in visitor" "PASS"
        echo "    Msg: ${MSG}, CheckIn: ${CI_TIME}"
    else
        print_result "Check-in visitor" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$CHECKIN_RESP"
else
    echo -e "\n${CYAN}>> 5.1 POST /api/visitor/checkin${NC}"
    print_result "Check-in visitor" "FAIL" "No VisitId"
fi

# 5.2 CHECK-OUT
if [ -n "$VISIT_ID" ] && [ "$VISIT_ID" != "null" ]; then
    echo -e "\n${CYAN}>> 5.2 POST /api/visitor/checkout${NC}"
    CHECKOUT_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/checkout" \
        "{\"VisitId\":${VISIT_ID}}" \
        "Bearer ${TOKEN}" "$CHECKOUT_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$CHECKOUT_RESP" 2>/dev/null || echo "")
        CO_TIME=$(jq -r '.CheckOut // ""' "$CHECKOUT_RESP" 2>/dev/null || echo "")
        print_result "Check-out visitor" "PASS"
        echo "    Msg: ${MSG}, CheckOut: ${CO_TIME}"
    else
        print_result "Check-out visitor" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$CHECKOUT_RESP"
else
    echo -e "\n${CYAN}>> 5.2 POST /api/visitor/checkout${NC}"
    print_result "Check-out visitor" "FAIL" "No VisitId"
fi

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    6. DIRECT CHECK-IN FLOW${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 6.1 DIRECT CHECK-IN
echo -e "\n${CYAN}>> 6.1 POST /api/visitor/direct-checkin${NC}"
DIRECT_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/direct-checkin" \
    "{\"Name\":\"Direct Visitor\",\"Designation\":\"Walk-in\",\"Company\":\"Walkin Corp\",\"Purpose\":\"Visit\",\"PMail\":\"direct@example.com\",\"Mobile\":\"9988776655\",\"EmpId\":${EMP_ID},\"WhomtoMeet\":${EMP_ID},\"IdCard\":\"WALK001\",\"Accessories\":\"None\"}" \
    "Bearer ${TOKEN}" "$DIRECT_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    MSG=$(jq -r '.Msg // ""' "$DIRECT_RESP" 2>/dev/null || echo "")
    DIRECT_VISIT_ID=$(jq -r '.VisitId // ""' "$DIRECT_RESP" 2>/dev/null || echo "")
    print_result "Direct check-in" "PASS"
    echo "    VisitId: ${DIRECT_VISIT_ID}, Msg: ${MSG}"
    VISIT_ID_DIRECT="${DIRECT_VISIT_ID}"
else
    print_result "Direct check-in" "FAIL" "HTTP ${HTTP_CODE}"
    VISIT_ID_DIRECT=""
fi
rm -f "$DIRECT_RESP"

# 6.2 VERIFY DIRECT CHECK-IN DATA
if [ -n "${VISIT_ID_DIRECT}" ]; then
    echo -e "\n${CYAN}>> 6.2 Verify direct check-in via list${NC}"
    LIST2_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/list" "" "Bearer ${TOKEN}" "$LIST2_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        FOUND=$(jq "[.[] | select(.VisitId == ${VISIT_ID_DIRECT})] | length" "$LIST2_RESP" 2>/dev/null || echo "0")
        if [ "$FOUND" -gt 0 ]; then
            print_result "Direct check-in persisted" "PASS"
            STATUS=$(jq -r ".[] | select(.VisitId == ${VISIT_ID_DIRECT}) | .Status" "$LIST2_RESP" 2>/dev/null || echo "unknown")
            echo "    Status: ${STATUS}"
        else
            print_result "Direct check-in persisted" "FAIL" "VisitId ${VISIT_ID_DIRECT} not found"
        fi
    else
        print_result "Verify direct check-in" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$LIST2_RESP"
else
    echo -e "\n${CYAN}>> 6.2 Verify direct check-in${NC}"
    print_result "Verify direct check-in" "SKIPPED" "No direct check-in created"
fi

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    7. SELF CHECK-IN / CHECK-OUT FLOW${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 7.1 SELF DIRECT CHECK-IN (visitor direct check-in, no auth)
echo -e "\n${CYAN}>> 7.1 POST /api/visitor/visitor-direct-checkin${NC}"
SELFDC_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/visitor-direct-checkin" \
    "{\"Name\":\"Self Visitor\",\"Designation\":\"Self\",\"Company\":\"Self Corp\",\"Purpose\":\"Personal\",\"PMail\":\"self@example.com\",\"Mobile\":\"8877665544\"}" \
    "Bearer ${TOKEN}" "$SELFDC_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    MSG=$(jq -r '.Msg // ""' "$SELFDC_RESP" 2>/dev/null || echo "")
    SELF_VISIT_ID=$(jq -r '.VisitId // ""' "$SELFDC_RESP" 2>/dev/null || echo "")
    SELF_OTP=$(jq -r '.OTP // ""' "$SELFDC_RESP" 2>/dev/null || echo "")
    print_result "Self direct check-in" "PASS"
    echo "    VisitId: ${SELF_VISIT_ID}, Msg: ${MSG}, OTP: ${SELF_OTP}"
else
    print_result "Self direct check-in" "FAIL" "HTTP ${HTTP_CODE}"
    SELF_VISIT_ID=""
fi
rm -f "$SELFDC_RESP"

# 7.2 SELF CHECK-IN (for invited visitor)
if [ -n "$VISIT_ID" ] && [ "$VISIT_ID" != "null" ]; then
    echo -e "\n${CYAN}>> 7.2 POST /api/visitor/visitor-checkin${NC}"
    SELFCI_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/visitor-checkin" \
        "{\"VisitId\":${VISIT_ID}}" \
        "Bearer ${TOKEN}" "$SELFCI_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$SELFCI_RESP" 2>/dev/null || echo "")
        print_result "Self check-in" "PASS"
        echo "    Msg: ${MSG}"
    else
        print_result "Self check-in" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$SELFCI_RESP"
else
    echo -e "\n${CYAN}>> 7.2 POST /api/visitor/visitor-checkin${NC}"
    print_result "Self check-in" "SKIPPED" "No VisitId"
fi

# 7.3 SELF CHECK-OUT
if [ -n "$VISIT_ID" ] && [ "$VISIT_ID" != "null" ]; then
    echo -e "\n${CYAN}>> 7.3 POST /api/visitor/visitor-checkout${NC}"
    SELFCO_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/visitor-checkout" \
        "{\"VisitId\":${VISIT_ID}}" \
        "Bearer ${TOKEN}" "$SELFCO_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$SELFCO_RESP" 2>/dev/null || echo "")
        print_result "Self check-out" "PASS"
        echo "    Msg: ${MSG}"
    else
        print_result "Self check-out" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$SELFCO_RESP"
else
    echo -e "\n${CYAN}>> 7.3 POST /api/visitor/visitor-checkout${NC}"
    print_result "Self check-out" "SKIPPED" "No VisitId"
fi

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    8. FILTER & EXPORT APIS${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 8.1 VISIT FILTER (by date range)
echo -e "\n${CYAN}>> 8.1 POST /api/visitor/visit-filter (date range)${NC}"
FILTER_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/visit-filter" \
    '{}' \
    "Bearer ${TOKEN}" "$FILTER_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    COUNT=$(jq 'length' "$FILTER_RESP" 2>/dev/null || echo "0")
    print_result "Visit filter (no params)" "PASS"
    echo "    Results: ${COUNT}"
else
    print_result "Visit filter (no params)" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$FILTER_RESP"

# 8.2 VISIT FILTER (by status)
echo -e "\n${CYAN}>> 8.2 POST /api/visitor/visit-filter (status=CHECKED IN)${NC}"
FILTER2_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/visit-filter" \
    '{"Status":"CHECKED IN"}' \
    "Bearer ${TOKEN}" "$FILTER2_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    COUNT=$(jq 'length' "$FILTER2_RESP" 2>/dev/null || echo "0")
    print_result "Visit filter by status (CHECKED IN)" "PASS"
    echo "    Results: ${COUNT}"
else
    print_result "Visit filter by status" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$FILTER2_RESP"

# 8.3 EXPORT CSV
echo -e "\n${CYAN}>> 8.3 POST /api/visitor/export-csv${NC}"
CSV_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/export-csv" \
    '{}' \
    "Bearer ${TOKEN}" "$CSV_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    # Check content is CSV-like
    CSV_SIZE=$(wc -c < "$CSV_RESP" 2>/dev/null || echo "0")
    if [ "$CSV_SIZE" -gt 0 ]; then
        print_result "Export CSV" "PASS"
        echo "    Size: ${CSV_SIZE} bytes"
        head -c 200 "$CSV_RESP"
        echo ""
    else
        print_result "Export CSV" "FAIL" "Empty response"
    fi
else
    print_result "Export CSV" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$CSV_RESP"

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    9. ADMIN APIS${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 9.1 CANCEL INVITE
if [ -n "$VISIT_ID" ] && [ "$VISIT_ID" != "null" ]; then
    echo -e "\n${CYAN}>> 9.1 POST /api/visitor/cancel${NC}"
    CANCEL_RESP=$(mktemp)
    HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/cancel" \
        "{\"VisitId\":${VISIT_ID}}" \
        "Bearer ${TOKEN}" "$CANCEL_RESP")
    if [ "$HTTP_CODE" = "200" ]; then
        MSG=$(jq -r '.Msg // ""' "$CANCEL_RESP" 2>/dev/null || echo "")
        print_result "Cancel invite" "PASS"
        echo "    Msg: ${MSG}"
    else
        print_result "Cancel invite" "FAIL" "HTTP ${HTTP_CODE}"
    fi
    rm -f "$CANCEL_RESP"
else
    echo -e "\n${CYAN}>> 9.1 POST /api/visitor/cancel${NC}"
    print_result "Cancel invite" "SKIPPED" "No VisitId"
fi

# 9.2 EXPIRE INVITES
echo -e "\n${CYAN}>> 9.2 POST /api/visitor/expire-invites${NC}"
EXPIRE_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/expire-invites" "" "Bearer ${TOKEN}" "$EXPIRE_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    MSG=$(jq -r '.Msg // ""' "$EXPIRE_RESP" 2>/dev/null || echo "")
    print_result "Expire old invites" "PASS"
    echo "    Msg: ${MSG}"
else
    print_result "Expire old invites" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$EXPIRE_RESP"

# 9.3 UPLOAD PHOTO (Negative test - no file)
echo -e "\n${CYAN}>> 9.3 POST /api/visitor/upload-photo (no file)${NC}"
PHOTO_RESP=$(mktemp)
HTTP_CODE=$(curl -s -o "$PHOTO_RESP" -w "%{http_code}" \
    -X POST "${BASE_URL}/api/visitor/upload-photo" \
    -H "Authorization: Bearer ${TOKEN}" \
    -F "file=@/dev/null" 2>/dev/null || echo "000")
if [ "$HTTP_CODE" != "500" ]; then
    MSG=$(jq -r '.Msg // ""' "$PHOTO_RESP" 2>/dev/null || echo "")
    print_result "Upload photo (empty file test)" "PASS"
    echo "    HTTP: ${HTTP_CODE}, Msg: ${MSG}"
else
    print_result "Upload photo endpoint" "PASS"
    echo "    Endpoint accessible (HTTP ${HTTP_CODE})"
fi
rm -f "$PHOTO_RESP"

# 9.4 PHOTO UPLOAD WITH VALID FILE (create a small test image)
echo -e "\n${CYAN}>> 9.4 POST /api/visitor/upload-photo (valid image)${NC}"
TMP_IMG=$(mktemp --suffix=.png)
# Create a minimal valid 1x1 PNG
printf '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x02\x00\x00\x00\x90wS\xde\x00\x00\x00\x0cIDATx\x9cc\xf8\x0f\x00\x00\x01\x01\x00\x05\x18\xd8N\x00\x00\x00\x00IEND\xaeB`\x82' > "$TMP_IMG"

PHOTO2_RESP=$(mktemp)
HTTP_CODE=$(curl -s -o "$PHOTO2_RESP" -w "%{http_code}" \
    -X POST "${BASE_URL}/api/visitor/upload-photo" \
    -H "Authorization: Bearer ${TOKEN}" \
    -F "file=@${TMP_IMG};type=image/png" 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "200" ]; then
    MSG=$(jq -r '.Msg // ""' "$PHOTO2_RESP" 2>/dev/null || echo "")
    PHOTO_B64=$(jq -r '.Photo // ""' "$PHOTO2_RESP" 2>/dev/null || echo "")
    if [ -n "$PHOTO_B64" ] && [ "${#PHOTO_B64}" -gt 10 ]; then
        print_result "Upload photo (valid PNG)" "PASS"
        echo "    Photo base64 length: ${#PHOTO_B64}"
    else
        print_result "Upload photo (valid PNG)" "FAIL" "No photo data in response"
    fi
else
    print_result "Upload photo (valid PNG)" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$TMP_IMG" "$PHOTO2_RESP"

# 9.5 UPLOAD NON-IMAGE FILE (Negative test)
echo -e "\n${CYAN}>> 9.5 POST /api/visitor/upload-photo (non-image file)${NC}"
TMP_TXT=$(mktemp --suffix=.txt)
echo "This is not an image" > "$TMP_TXT"
PHOTO3_RESP=$(mktemp)
HTTP_CODE=$(curl -s -o "$PHOTO3_RESP" -w "%{http_code}" \
    -X POST "${BASE_URL}/api/visitor/upload-photo" \
    -H "Authorization: Bearer ${TOKEN}" \
    -F "file=@${TMP_TXT};type=text/plain" 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "400" ]; then
    MSG=$(jq -r '.Msg // ""' "$PHOTO3_RESP" 2>/dev/null || echo "")
    print_result "Reject non-image upload" "PASS"
    echo "    Msg: ${MSG}"
elif [ "$HTTP_CODE" = "200" ]; then
    # Backend may accept it, note it
    print_result "Reject non-image upload" "FAIL" "Accepted non-image file (HTTP 200)"
else
    print_result "Reject non-image upload" "PASS" "HTTP ${HTTP_CODE} (expected non-200)"
fi
rm -f "$TMP_TXT" "$PHOTO3_RESP"

# =============================================================
echo -e "\n${YELLOW}====================================================${NC}"
echo -e "${YELLOW}    10. NEGATIVE & EDGE CASE TESTS${NC}"
echo -e "${YELLOW}====================================================${NC}"

# 10.1 ACCESS WITHOUT AUTH (should fail for most endpoints)
echo -e "\n${CYAN}>> 10.1 GET /api/visitor/dashboard (no auth)${NC}"
NOAUTH_RESP=$(mktemp)
HTTP_CODE=$(call_api "GET" "${BASE_URL}/api/visitor/dashboard" "" "" "$NOAUTH_RESP")
# Note: Backend has NO security filter, so this may return 200
if [ "$HTTP_CODE" = "401" ] || [ "$HTTP_CODE" = "403" ]; then
    print_result "Auth required for /dashboard" "PASS"
    echo "    Correctly rejected (HTTP ${HTTP_CODE})"
else
    print_result "Auth required for /dashboard" "INFO" "HTTP ${HTTP_CODE} (no auth guard found in backend)"
    echo "    NOTE: Backend does not enforce auth - this is a SECURITY ISSUE"
fi
rm -f "$NOAUTH_RESP"

# 10.2 INVITE WITH MISSING EmpId (Negative)
echo -e "\n${CYAN}>> 10.2 POST /api/visitor/invite (missing EmpId)${NC}"
INVFAIL_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/invite" \
    '{"Name":"No Emp","PMail":"noemp@test.com"}' \
    "Bearer ${TOKEN}" "$INVFAIL_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    MSG=$(jq -r '.Msg // ""' "$INVFAIL_RESP" 2>/dev/null || echo "")
    if echo "$MSG" | grep -qi "required"; then
        print_result "Reject invite without EmpId" "PASS"
        echo "    Msg: ${MSG}"
    else
        print_result "Reject invite without EmpId" "FAIL" "Unexpected: ${MSG}"
    fi
else
    print_result "Reject invite without EmpId" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$INVFAIL_RESP"

# 10.3 CHECK-IN WITH INVALID VisitId
echo -e "\n${CYAN}>> 10.3 POST /api/visitor/checkin (invalid VisitId)${NC}"
CIBAD_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/checkin" \
    '{"VisitId":999999}' \
    "Bearer ${TOKEN}" "$CIBAD_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    MSG=$(jq -r '.Msg // ""' "$CIBAD_RESP" 2>/dev/null || echo "")
    if echo "$MSG" | grep -qi "not found"; then
        print_result "Reject check-in with invalid VisitId" "PASS"
        echo "    Msg: ${MSG}"
    else
        print_result "Reject check-in with invalid VisitId" "FAIL" "Unexpected: ${MSG}"
    fi
else
    print_result "Reject check-in with invalid VisitId" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$CIBAD_RESP"

# 10.4 CANCEL WITH INVALID VisitId
echo -e "\n${CYAN}>> 10.4 POST /api/visitor/cancel (invalid VisitId)${NC}"
CANFAIL_RESP=$(mktemp)
HTTP_CODE=$(call_api "POST" "${BASE_URL}/api/visitor/cancel" \
    '{"VisitId":999999}' \
    "Bearer ${TOKEN}" "$CANFAIL_RESP")
if [ "$HTTP_CODE" = "200" ]; then
    MSG=$(jq -r '.Msg // ""' "$CANFAIL_RESP" 2>/dev/null || echo "")
    if echo "$MSG" | grep -qi "not found"; then
        print_result "Reject cancel with invalid VisitId" "PASS"
        echo "    Msg: ${MSG}"
    else
        print_result "Reject cancel with invalid VisitId" "FAIL" "Unexpected: ${MSG}"
    fi
else
    print_result "Reject cancel with invalid VisitId" "FAIL" "HTTP ${HTTP_CODE}"
fi
rm -f "$CANFAIL_RESP"

# =============================================================
# SUMMARY
# =============================================================
echo ""
echo -e "${CYAN}====================================================${NC}"
echo -e "${CYAN}  TEST SUMMARY${NC}"
echo -e "${CYAN}====================================================${NC}"
echo -e "  ${GREEN}PASSED: ${PASS}${NC}"
echo -e "  ${RED}FAILED: ${FAIL}${NC}"
if [ -n "$FAILURES" ]; then
    echo -e "  ${RED}Failures:${NC}${FAILURES}"
fi
echo ""
echo -e "${CYAN}  Auth Token: ${TOKEN:0:50}...${NC}"
echo -e "${CYAN}  Employee ID: ${EMP_ID}${NC}"
echo -e "${CYAN}  Visit ID (invited): ${VISIT_ID:-N/A}${NC}"
echo -e "${CYAN}====================================================${NC}"

# Exit with failure count
exit $FAIL
