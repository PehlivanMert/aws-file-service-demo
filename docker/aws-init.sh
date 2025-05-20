#!/bin/bash
set -x

echo "Starting AWS Services Initialization..."

# S3 Bucket Creation
echo "Creating S3 Bucket..."
awslocal s3api create-bucket --bucket test-bucket
awslocal s3api list-buckets

# DynamoDB Table Creation
echo "Creating DynamoDB Table..."
awslocal dynamodb create-table \
    --table-name file-metadata \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=fileName,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
        AttributeName=fileName,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

# SNS Topic Creation
echo "Creating SNS Topic..."
awslocal sns create-topic --name file-notifications-new
awslocal sns subscribe \
    --topic-arn arn:aws:sns:us-east-1:000000000000:file-notifications-new \
    --protocol email \
    --notification-endpoint your-email@example.com

# S3 Event Notifications
echo "Setting up S3 Event Notifications..."
awslocal s3api put-bucket-notification-configuration \
    --bucket test-bucket \
    --notification-configuration '{
        "TopicConfigurations": [
            {
                "TopicArn": "arn:aws:sns:us-east-1:000000000000:file-notifications-new",
                "Events": ["s3:ObjectCreated:*", "s3:ObjectRemoved:*"]
            }
        ]
    }'

# CORS Configuration
echo "Setting up CORS configuration..."
awslocal s3api put-bucket-cors \
    --bucket test-bucket \
    --cors-configuration '{
        "CORSRules": [
            {
                "AllowedHeaders": ["*"],
                "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
                "AllowedOrigins": ["*"],
                "ExposeHeaders": ["ETag"]
            }
        ]
    }'

# SNS Logging Configuration
echo "Setting up SNS logging..."
awslocal sns set-topic-attributes \
    --topic-arn arn:aws:sns:us-east-1:000000000000:file-notifications-new \
    --attribute-name DisplayName \
    --attribute-value "File Notifications"

# Enable SNS logging
awslocal sns set-topic-attributes \
    --topic-arn arn:aws:sns:us-east-1:000000000000:file-notifications-new \
    --attribute-name Policy \
    --attribute-value '{
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Principal": "*",
                "Action": "SNS:Publish",
                "Resource": "arn:aws:sns:us-east-1:000000000000:file-notifications-new",
                "Condition": {
                    "ArnLike": {
                        "aws:SourceArn": "arn:aws:s3:::test-bucket"
                    }
                }
            }
        ]
    }'

echo "AWS Services Initialization Completed."

# Start SNS message monitoring
echo "Starting SNS message monitoring..."
awslocal sns list-subscriptions-by-topic --topic-arn arn:aws:sns:us-east-1:000000000000:file-notifications-new

set +x