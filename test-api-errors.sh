#!/bin/bash

# Test script for API error handling
BASE_URL="http://localhost:8082/api/v1/users"

echo "=== Testing API Error Handling ==="
echo

# Test 1: Validation errors
echo "1. Testing validation errors (empty fields, invalid email):"
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "firstName": "",
    "lastName": "",
    "email": "invalid-email",
    "gdprConsentGiven": true,
    "marketingConsentGiven": false
  }' | jq .
echo
echo "---"
echo

# Test 2: Create a valid user first
echo "2. Creating a valid user:"
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test.user",
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user@example.com",
    "gdprConsentGiven": true,
    "marketingConsentGiven": false
  }' | jq .
echo
echo "---"
echo

# Test 3: Try to create duplicate user
echo "3. Testing duplicate user error:"
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test.user",
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user@example.com",
    "gdprConsentGiven": true,
    "marketingConsentGiven": false
  }' | jq .
echo
echo "---"
echo

# Test 4: Invalid JSON format
echo "4. Testing invalid JSON format:"
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test.user2",
    "firstName": "Test",
    "lastName": "User",
    "email": "test.user2@example.com",
    "invalidField": invalid_value_without_quotes,
    "gdprConsentGiven": true
  }' | jq .
echo
echo "---"
echo

# Test 5: User not found
echo "5. Testing user not found:"
curl -X GET "$BASE_URL/123e4567-e89b-12d3-a456-426614174999" \
  -H "Content-Type: application/json" | jq .
echo
echo "---"
echo

# Test 6: Invalid UUID format
echo "6. Testing invalid UUID format:"
curl -X GET "$BASE_URL/invalid-uuid" \
  -H "Content-Type: application/json" | jq .
echo
echo "---"
echo

# Test 7: Unsupported HTTP method
echo "7. Testing unsupported HTTP method:"
curl -X PATCH "$BASE_URL" \
  -H "Content-Type: application/json" | jq .
echo
echo "---"
echo

# Test 8: Missing Content-Type header
echo "8. Testing unsupported media type:"
curl -X POST "$BASE_URL" \
  -H "Content-Type: text/plain" \
  -d 'plain text data' | jq .
echo

echo "=== Test Complete ==="