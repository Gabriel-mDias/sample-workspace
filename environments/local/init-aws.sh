#!/bin/bash
echo "Configuring LocalStack S3..."

# Create bucket if it doesn't exist
awslocal s3api create-bucket --bucket sample-bucket || true

# Apply CORS configuration
awslocal s3api put-bucket-cors --bucket sample-bucket --cors-configuration '{
  "CORSRules": [
    {
      "AllowedHeaders": ["*"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE", "HEAD"],
      "AllowedOrigins": ["*"],
      "ExposeHeaders": ["ETag"]
    }
  ]
}'

echo "LocalStack S3 configured successfully!"
