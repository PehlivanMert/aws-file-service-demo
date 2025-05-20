# AWS File Service Demo

## İçindekiler
1. [Genel Bakış](#genel-bakış)
2. [Sistem Gereksinimleri](#sistem-gereksinimleri)
3. [Kurulum](#kurulum)
4. [Servislerin Başlatılması](#servislerin-başlatılması)
5. [Test Etme](#test-etme)
6. [API Endpointleri](#api-endpointleri)
7. [Hata Kodları](#hata-kodları)
8. [Sorun Giderme](#sorun-giderme)
9. [Log Yönetimi](#log-yönetimi)
10. [English](#Table-of-Contents)

## Genel Bakış
AWS File Service Demo, Amazon Web Services (AWS) servislerini kullanarak dosya yönetimi ve kullanıcı profil fotoğrafı yönetimi sağlayan bir REST API uygulamasıdır. Uygulama aşağıdaki servisleri kullanmaktadır:

- Amazon S3: Dosya depolama
- Amazon DynamoDB: Metadata depolama
- Amazon SNS: Bildirim gönderimi
- Amazon IAM: Kimlik doğrulama
- PostgreSQL: Veritabanı
- Redis: Önbellek

## Sistem Gereksinimleri

### Yazılım Gereksinimleri
- Java 17 veya üzeri
- Maven 3.6 veya üzeri
- Docker 20.10 veya üzeri
- Docker Compose 2.0 veya üzeri
- Git

## Kurulum

### 1. Projeyi İndirme
```bash
# Projeyi klonlayın
git clone [https://github.com/PehlivanMert/aws-file-service-demo.git]
cd aws-file-service-demo
```

### 2. Docker Servislerinin Kurulumu
```bash
# Docker servislerini başlatın
docker-compose up -d
```

Bu komut aşağıdaki servisleri başlatacaktır:
- PostgreSQL (Port: 5432)
- LocalStack (Port: 4566)
- Redis (Port: 6379)

LocalStack container'ı başlatıldığında, `aws-init.sh` scripti otomatik olarak çalıştırılır ve aşağıdaki AWS servislerini yapılandırır:
- S3 bucket oluşturma
- DynamoDB tablo oluşturma
- SNS topic oluşturma
- S3 event notifications yapılandırması
- CORS yapılandırması

### 3. AWS Servislerinin Kontrolü
```bash
# S3 bucket'ı kontrol edin
docker-compose exec aws-file-service-localstack awslocal s3api list-buckets

# DynamoDB tablosunu kontrol edin
docker-compose exec aws-file-service-localstack awslocal dynamodb list-tables

# SNS topic'lerini kontrol edin
docker-compose exec aws-file-service-localstack awslocal sns list-topics
```

### 4. AWS Servislerini Yeniden Başlatma
Eğer AWS servislerinde bir sorun yaşarsanız veya servisleri yeniden yapılandırmak isterseniz:
```bash
# AWS servislerini yeniden başlatın
chmod +x aws-init.sh
docker-compose exec aws-file-service-localstack /docker-entrypoint-initaws.d/aws-init.sh
```

Bu komut:
- S3 bucket'ı yeniden oluşturur
- DynamoDB tablosunu yeniden oluşturur
- SNS topic'ini yeniden oluşturur
- S3 event notifications'ı yeniden yapılandırır
- CORS ayarlarını yeniden yapılandırır

### 5. Uygulamanın Derlenmesi ve Başlatılması
```bash
# Uygulamayı derleyin
mvn clean install -DskipTests

# Uygulamayı başlatın
mvn spring-boot:run
```

Uygulama varsayılan olarak 8088 portunda çalışacaktır.

## Test Etme

### 1. Test Scriptini Çalıştırma
```bash
# Test scriptini çalıştırılabilir yapın
chmod +x test_aws_app.sh

# Test scriptini çalıştırın
./test_aws_app.sh
```

Bu script aşağıdaki testleri gerçekleştirecektir:

#### AWS Controller Testleri
1. Ping endpoint testi
2. Dosya yükleme testi
3. Dosya listeleme testi
4. Dosya silme testi

#### S3 Controller Testleri
1. Dosya yükleme testi
2. Dosya listeleme testi
3. Dosya indirme testi
4. Dosya silme testi

#### User Controller Testleri
1. Kullanıcı oluşturma testi
2. Kullanıcı bilgilerini getirme testi
3. Profil fotoğrafı güncelleme testi

Test scripti otomatik olarak:
- Test dosyaları oluşturur
- Her endpoint'i test eder
- Test sonuçlarını gösterir
- Test dosyalarını temizler

### 2. Test Çıktıları

Test scripti çalıştırıldığında aşağıdaki çıktıları göreceksiniz:

```bash
=== AWS File-Service-App Test Script ===
Starting tests at Sal 20 May 2025 19:03:22 +03
=============================
Creating test files...

=== 1. Testing AWS Controller ===
1.1 Testing ping endpoint...
AWS Controller is working!

1.2 Testing file upload...
File uploaded successfully: ad85879c-3729-4a91-8b4b-cce06f659538_testfile.txt

1.3 Testing file list...
[{"id":"c3fcc365-ea8b-4f57-83da-4ae25927b36c","fileName":"testfile.txt","fileType":"text/plain","fileSize":13,"s3Url":"ad85879c-3729-4a91-8b4b-cce06f659538_testfile.txt","uploadDate":null,"status":"UPLOADED"}]

1.4 Testing file delete with ID: c3fcc365-ea8b-4f57-83da-4ae25927b36c...
File deleted successfully

=== 2. Testing S3 Controller ===
2.1 Testing file upload...
{"fileKey":"3cfacb08-8322-44d1-b21f-67ff10f77d14_test.jpg","fileUrl":"http://localhost:4566/test-bucket/3cfacb08-8322-44d1-b21f-67ff10f77d14_test.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250520T160323Z&X-Amz-SignedHeaders=host&X-Amz-Expires=259200&X-Amz-Credential=test%2F20250520%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=353a777f7a6c191e132d821b3694ed1324bf347c577cfbee6e997dcfff804de8"}

2.2 Testing file list...
[{"key":"3cfacb08-8322-44d1-b21f-67ff10f77d14_test.jpg"},{"key":"ad85879c-3729-4a91-8b4b-cce06f659538_testfile.txt"}]

2.3 Testing file download with key: 3cfacb08-8322-44d1-b21f-67ff10f77d14_test.jpg...

2.4 Testing file delete...

=== 3. Testing User Controller ===
3.1 Testing user creation...
{"email":"test@example.com","profilePhotoKey":"bff1d81e-001e-4d55-95a3-95a577cb93fb_user_photo.jpg"}

3.2 Testing user get by email...
{"email":"test@example.com","profilePhotoKey":"bff1d81e-001e-4d55-95a3-95a577cb93fb_user_photo.jpg"}

3.3 Testing profile photo update...
{"email":"test@example.com","profilePhotoKey":"959b8741-825f-4f9d-aed9-8889526ee1ab_user_photo.jpg"}

=============================
Cleaning up test files...
Tests completed at Sal 20 May 2025 19:03:23 +03
=============================
```

### 3. SNS Bildirimleri

SNS (Simple Notification Service) yapılandırması ve bildirimleri:

```bash
# SNS Topic Oluşturma
{
    "TopicArn": "arn:aws:sns:us-east-1:000000000000:aws-file-service-notifications"
}

# SNS Email Aboneliği
{
    "SubscriptionArn": "arn:aws:sns:us-east-1:000000000000:aws-file-service-notifications:0dfbc0bb-b366-4ed1-95ba-c4743e55ce3a",
    "Protocol": "email",
    "Endpoint": "your-email@example.com"
}

# SNS Abonelik Listesi
{
    "Subscriptions": [
        {
            "SubscriptionArn": "arn:aws:sns:us-east-1:000000000000:aws-file-service-notifications:0dfbc0bb-b366-4ed1-95ba-c4743e55ce3a",
            "Owner": "000000000000",
            "Protocol": "email",
            "Endpoint": "your-email@example.com",
            "TopicArn": "arn:aws:sns:us-east-1:000000000000:aws-file-service-notifications"
        }
    ]
}
```

SNS bildirimleri aşağıdaki olaylar için tetiklenir:
1. Dosya Yükleme: `s3:ObjectCreated:*`
2. Dosya Silme: `s3:ObjectRemoved:*`

Test sırasında gerçekleşen olaylar:
1. Dosya Yükleme: `ad85879c-3729-4a91-8b4b-cce06f659538_testfile.txt`
2. Dosya Silme: `testfile.txt`
3. Dosya Yükleme: `3cfacb08-8322-44d1-b21f-67ff10f77d14_test.jpg`
4. Dosya Silme: `3cfacb08-8322-44d1-b21f-67ff10f77d14_test.jpg`
5. Kullanıcı Fotoğrafı Yükleme: `bff1d81e-001e-4d55-95a3-95a577cb93fb_user_photo.jpg`
6. Kullanıcı Fotoğrafı Güncelleme: `959b8741-825f-4f9d-aed9-8889526ee1ab_user_photo.jpg`

Her bu olay için SNS topic'ine bildirim gönderilir. Test ortamında gerçek email gönderimi yapılmaz, ancak gerçek ortamda belirtilen email adresine bildirimler gönderilir.

## API Endpointleri

### AWS Controller (`/api/aws`)

#### 1. Ping Testi
```bash
GET /api/aws/ping
```
**Yanıt:** `AWS Controller is working!`

#### 2. Dosya Yükleme
```bash
POST /api/aws/upload
Content-Type: multipart/form-data
```
**Parametreler:**
- `file`: Yüklenecek dosya

**Yanıt:**
```json
{
    "message": "File uploaded successfully: [s3-url]"
}
```

#### 3. Dosya Listeleme
```bash
GET /api/aws/files
```
**Yanıt:**
```json
[
    {
        "id": "uuid",
        "fileName": "example.txt",
        "fileType": "text/plain",
        "fileSize": 1024,
        "s3Url": "http://...",
        "status": "UPLOADED"
    }
]
```

#### 4. Dosya Silme
```bash
DELETE /api/aws/files/{id}?fileName={fileName}
```
**Parametreler:**
- `id`: Dosya ID'si
- `fileName`: Dosya adı

**Yanıt:**
```json
{
    "message": "File deleted successfully"
}
```

## Hata Kodları

| Code | Description |
|------|-------------|
| 400  | Invalid request parameters |
| 404  | Resource not found |
| 500  | Server error |

## Sorun Giderme

### 1. Docker Servisleri
```bash
# Servis durumunu kontrol edin
docker-compose ps

# Servis günlüklerini görüntüleyin
docker-compose logs

# Servisleri yeniden başlatın
docker-compose restart
```

### 2. AWS Servisleri
```bash
# S3 bucket'ı kontrol edin
docker-compose exec aws-file-service-localstack awslocal s3api list-buckets

# DynamoDB tablosunu kontrol edin
docker-compose exec aws-file-service-localstack awslocal dynamodb list-tables

# SNS topic'lerini kontrol edin
docker-compose exec aws-file-service-localstack awslocal sns list-topics

# AWS servislerini yeniden başlatın
docker-compose exec aws-file-service-localstack /docker-entrypoint-initaws.d/aws-init.sh
```

### 3. Veritabanı Kontrolü
```bash
# PostgreSQL container'ına bağlanın
docker-compose exec aws-file-service-postgres psql -U postgres -d aws-demo

# Veritabanı tablolarını listele
\dt

# Bağlantıyı kapat
\q
```

### 4. Ortak Sorunlar

#### "Port zaten kullanılıyor"
```bash
# Belirtilen portu kullanan süreci bulun
lsof -i :8088

# Süreci sonlandırın
kill -9 [PID]
```

#### "Docker kapsayıcısı başlatılamıyor"
```bash
# Kapsayıcıyı yeniden başlatın
docker-compose down
docker-compose up -d
```

#### "Veritabanı bağlantı hatası"
```bash
# PostgreSQL container'ının durumunu kontrol edin
docker-compose ps aws-file-service-postgres

# PostgreSQL loglarını kontrol edin
docker-compose logs aws-file-service-postgres
```

## Log Yönetimi

### 1. Uygulama Günlükleri
Uygulama aşağıdaki log seviyelerini kullanır:
- INFO: Normal işlem günlükleri
- ERROR: Hata koşulları
- DEBUG: Ayrıntılı hata ayıklama bilgileri

### 2. Önemli Günlük Mesajları
```
# Dosya Yükleme
Dosya başarıyla yüklendi. Anahtar [uuid]_[filename]

# Dosya Silme
Dosya başarıyla silindi. Anahtar [filename]

# Kullanıcı İşlemleri
E-posta [email] ile kullanıcı bulunamadı
```

### 3. Günlük İzleme
```bash
# Canlı günlük izleme
tail -f logs/application.log

# Hata günlüklerini filtrele
grep ERROR logs/application.log

# Belirli işlem için günlükleri filtrele
grep "File uploaded" logs/application.log
```

## Destek

Sorunları ve önerileriniz için:
- GitHub Issues
- E-posta: [pehlivanmert@outlook.com.tr]


---

# AWS S3 App User Manual

## Table of Contents
1. [Overview](#overview)
2. [System Requirements](#system-requirements)
3. [Installation](#installation)
4. [Starting Services](#starting-services)
5. [Testing](#testing)
6. [API Endpoints](#api-endpoints)
7. [Error Codes](#error-codes)
8. [Troubleshooting](#troubleshooting)
9. [Log Management](#log-management)

## Overview
AWS File Service Demo is a REST API application that provides file management and user profile photo management using Amazon Web Services (AWS) services. The application uses the following AWS services:

- Amazon S3: File storage
- Amazon DynamoDB: Metadata storage
- Amazon SNS: Notification sending
- Amazon IAM: Authentication
- PostgreSQL: Database
- Redis: Cache

## System Requirements

### Software Requirements
- Java 17 or higher
- Maven 3.6 or higher
- Docker 20.10 or higher
- Docker Compose 2.0 or higher
- Git


## Installation

### 1. Download Project
```bash
# Clone the project
git clone [https://github.com/PehlivanMert/aws-file-service-demo.git]
cd aws-file-service-demo
```

### 2. Docker Services Setup
```bash
# Start Docker services
docker-compose up -d
```

This command will start the following services:
- PostgreSQL (Port: 5433)
- LocalStack (Port: 4566)
- Redis (Port: 6379)

When the LocalStack container starts, the `aws-init.sh` script runs automatically and configures the following AWS services:
- S3 bucket creation
- DynamoDB table creation
- SNS topic creation
- S3 event notifications configuration
- CORS configuration

### 3. AWS Services Check
```bash
# Check S3 bucket
docker-compose exec aws-file-service-localstack awslocal s3api list-buckets

# Check DynamoDB table
docker-compose exec aws-file-service-localstack awslocal dynamodb list-tables

# Check SNS topics
docker-compose exec aws-file-service-localstack awslocal sns list-topics
```

### 4. AWS Services Restart
If you encounter issues with AWS services or want to re-configure them:
```bash
# Restart AWS services
chmod +x aws-init.sh
docker-compose exec aws-file-service-localstack /docker-entrypoint-initaws.d/aws-init.sh
```

This command:
- Recreates S3 bucket
- Recreates DynamoDB table
- Recreates SNS topic
- Recreates S3 event notifications
- Recreates CORS configuration

### 5. Building and Starting the Application
```bash
# Build the application
mvn clean install -DskipTests

# Start the application
mvn spring-boot:run
```

The application will run on port 8088 by default.

## Testing

### 1. Running Test Script
```bash
# Make test script executable
chmod +x test_aws_app.sh

# Run test script
./test_aws_app.sh
```

This script performs the following tests:

#### AWS Controller Tests
1. Ping endpoint test
2. File upload test
3. File listing test
4. File deletion test

#### S3 Controller Tests
1. File upload test
2. File listing test
3. File download test
4. File deletion test

#### User Controller Tests
1. User creation test
2. User information retrieval test
3. Profile photo update test

The test script automatically:
- Creates test files
- Tests each endpoint
- Shows test results
- Cleans up test files

### 2. Manual Testing
```bash
# Ping test
curl http://localhost:8088/api/aws/ping

# File upload
echo "Test content" > testfile.txt
curl -F "file=@testfile.txt" http://localhost:8088/api/aws/upload

# File listing
curl http://localhost:8088/api/aws/files
```

## API Endpoints

### AWS Controller (`/api/aws`)

#### 1. Ping Test
```bash
GET /api/aws/ping
```
**Response:** `AWS Controller is working!`

#### 2. File Upload
```bash
POST /api/aws/upload
Content-Type: multipart/form-data
```
**Parameters:**
- `file`: File to upload

**Response:**
```json
{
    "message": "File uploaded successfully: [s3-url]"
}
```

#### 3. File Listing
```bash
GET /api/aws/files
```
**Response:**
```json
[
    {
        "id": "uuid",
        "fileName": "example.txt",
        "fileType": "text/plain",
        "fileSize": 1024,
        "s3Url": "http://...",
        "status": "UPLOADED"
    }
]
```

#### 4. File Deletion
```bash
DELETE /api/aws/files/{id}?fileName={fileName}
```
**Parameters:**
- `id`: File ID
- `fileName`: File name

**Response:**
```json
{
    "message": "File deleted successfully"
}
```

## Error Codes

| Code | Description |
|------|-------------|
| 400  | Invalid request parameters |
| 404  | Resource not found |
| 500  | Server error |

## Troubleshooting

### 1. Docker Services
```bash
# Check service status
docker-compose ps

# View service logs
docker-compose logs

# Restart services
docker-compose restart
```

### 2. AWS Services
```bash
# Check S3 bucket
docker-compose exec aws-file-service-localstack awslocal s3api list-buckets

# Check DynamoDB table
docker-compose exec aws-file-service-localstack awslocal dynamodb list-tables

# Check SNS topics
docker-compose exec aws-file-service-localstack awslocal sns list-topics

# Restart AWS services
docker-compose exec aws-file-service-localstack /docker-entrypoint-initaws.d/aws-init.sh
```

### 3. Database Check
```bash
# Connect to PostgreSQL container
docker-compose exec aws-file-service-postgres psql -U postgres -d aws-demo

# List database tables
\dt

# Disconnect
\q
```

### 4. Common Issues

#### "Port already in use"
```bash
# Find process using the port
lsof -i :8088

# Kill the process
kill -9 [PID]
```

#### "Docker container failed to start"
```bash
# Restart container
docker-compose down
docker-compose up -d
```

#### "Database connection error"
```bash
# Check PostgreSQL container status
docker-compose ps aws-file-service-postgres

# Check PostgreSQL logs
docker-compose logs aws-file-service-postgres
```

## Log Management

### 1. Application Logs
The application uses the following log levels:
- INFO: Normal operation logs
- ERROR: Error conditions
- DEBUG: Detailed debugging information

### 2. Important Log Messages
```
# File Upload
File uploaded successfully. Key [uuid]_[filename]

# File Deletion
File deleted successfully. Key [filename]

# User Operations
User not found with email [email]
```

### 3. Log Monitoring
```bash
# Live log monitoring
tail -f logs/application.log

# Filter error logs
grep ERROR logs/application.log

# Filter logs for specific operation
grep "File uploaded" logs/application.log
```

## Support

For issues or suggestions:
- GitHub Issues
- E-Mail: [pehlivanmert@outlook.com.tr]