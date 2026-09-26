# Daily Khata App API

Spring Boot 3 and Java 17 REST API using AWS DynamoDB single-table design.

## Local run against AWS DynamoDB

Configure AWS credentials with the AWS CLI, then set the variables from `.env.example`
in the PowerShell session before starting the application:

```powershell
$env:AWS_REGION="eu-north-1"
$env:AWS_DYNAMODB_TABLE_NAME="Khataapp"
$env:AWS_DYNAMODB_ENDPOINT=""
$env:JWT_SECRET="use-a-random-secret-at-least-32-bytes"
$env:JWT_EXPIRATION_MS="3600000"
$env:CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173"
$env:SERVER_PORT="8080"
.\mvnw.cmd spring-boot:run
```

Never commit `.env` or cloud credentials. In App Runner, provide `JWT_SECRET`
through a secret and use the instance role for DynamoDB access.

## API endpoints

All khata endpoints require a bearer token from login.

```text
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/khata
GET    /api/v1/khata/{userId}
GET    /api/v1/khata/{userId}/summary
DELETE /api/v1/khata/{userId}/{transactionId}
GET    /actuator/health
```

Transaction creation derives the owner from the JWT; clients must not send a
`userId` in the request body. DELETE also verifies that the path user ID matches
the authenticated user.