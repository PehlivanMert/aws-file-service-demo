#!/bin/bash

echo "=== AWS S3 App Test Script ==="
echo "Starting tests at $(date)"
echo "============================="

# Test dosyaları oluştur
echo "Creating test files..."
echo "Test content" > testfile.txt
echo "Test image content" > test.jpg
echo "Test user photo" > user_photo.jpg

echo -e "\n=== 1. Testing AWS Controller ==="
echo "1.1 Testing ping endpoint..."
curl -s http://localhost:8080/api/aws/ping
echo -e "\n"

echo "1.2 Testing file upload..."
curl -s -F "file=@testfile.txt" http://localhost:8080/api/aws/upload
echo -e "\n"

echo "1.3 Testing file list..."
curl -s http://localhost:8080/api/aws/files
echo -e "\n"

# Dosya ID'sini al
FILE_ID=$(curl -s http://localhost:8080/api/aws/files | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)
echo "1.4 Testing file delete with ID: $FILE_ID..."
curl -s -X DELETE "http://localhost:8080/api/aws/files/$FILE_ID?fileName=testfile.txt"
echo -e "\n"

echo -e "\n=== 2. Testing S3 Controller ==="
echo "2.1 Testing file upload..."
curl -s -F "file=@test.jpg" http://localhost:8080/api/v1/files/upload
echo -e "\n"

echo "2.2 Testing file list..."
curl -s http://localhost:8080/api/v1/files
echo -e "\n"

# Dosya key'ini al
FILE_KEY=$(curl -s http://localhost:8080/api/v1/files | grep -o '"key":"[^"]*' | head -1 | cut -d'"' -f4)
echo "2.3 Testing file download with key: $FILE_KEY..."
curl -s -o downloaded_file.jpg http://localhost:8080/api/v1/files/$FILE_KEY
echo -e "\n"

echo "2.4 Testing file delete..."
curl -s -X DELETE http://localhost:8080/api/v1/files/$FILE_KEY
echo -e "\n"

echo -e "\n=== 3. Testing User Controller ==="
echo "3.1 Testing user creation..."
curl -s -X POST \
  -F "email=test@example.com" \
  -F "firstName=Test" \
  -F "lastName=User" \
  -F "password=test123" \
  -F "profilePhoto=@user_photo.jpg" \
  http://localhost:8080/api/v1/users/create
echo -e "\n"

echo "3.2 Testing user get by email..."
curl -s "http://localhost:8080/api/v1/users?email=test@example.com"
echo -e "\n"

echo "3.3 Testing profile photo update..."
curl -s -X POST \
  -F "profilePhoto=@user_photo.jpg" \
  http://localhost:8080/api/v1/users/profilePhoto/1
echo -e "\n"

echo "============================="
echo "Cleaning up test files..."
rm testfile.txt test.jpg user_photo.jpg downloaded_file.jpg
echo "Tests completed at $(date)"
echo "============================="