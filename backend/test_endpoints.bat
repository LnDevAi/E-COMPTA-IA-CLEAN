@echo off
echo ========================================
echo TEST DES ENDPOINTS HR - Module 15
echo ========================================

echo.
echo 1. Test GET /api/hr/leaves/test
curl -X GET "http://localhost:8080/api/hr/leaves/test"

echo.
echo.
echo 2. Test POST /api/hr/leaves/test-post
curl -X POST "http://localhost:8080/api/hr/leaves/test-post" -H "Content-Type: application/json" -d "{\"test\": \"data\"}"

echo.
echo.
echo 3. Test POST /api/hr/leaves (creation)
curl -X POST "http://localhost:8080/api/hr/leaves" -H "Content-Type: application/json" -d "{\"leaveCode\": \"LEAVE-TEST-001\", \"employeeId\": 1, \"leaveType\": \"ANNUAL\", \"startDate\": \"2024-07-15\", \"endDate\": \"2024-07-30\", \"reason\": \"Test\", \"entrepriseId\": 1}"

echo.
echo.
echo ========================================
echo TESTS TERMINES
echo ========================================
pause


