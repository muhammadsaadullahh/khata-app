# Daily Khata App API

Spring Boot 3 and Java 17 REST API using AWS DynamoDB single-table design.

## Local run without Docker

### Option A: use an AWS DynamoDB table

Install the AWS CLI and configure credentials with `aws configure`. Then set
the following PowerShell variables:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:AWS_REGION="eu-north-1"
$env:AWS_DYNAMODB_TABLE_NAME="Khataapp"
$env:AWS_DYNAMODB_ENDPOINT=""
$env:JWT_SECRET="use-a-random-local-secret-with-at-least-32-bytes"
$env:JWT_EXPIRATION_MS="3600000"
$env:CORS_ALLOWED_ORIGINS="http://localhost:3000,http://localhost:5173"
$env:SERVER_PORT="8080"
.\mvnw.cmd spring-boot:run
```

The DynamoDB table must use `PK` as the partition key and `SK` as the sort key.
For a disposable AWS test table:

```powershell
aws dynamodb create-table --table-name Khataapp --attribute-definitions AttributeName=PK,AttributeType=S AttributeName=SK,AttributeType=S --key-schema AttributeName=PK,KeyType=HASH AttributeName=SK,KeyType=RANGE --billing-mode PAY_PER_REQUEST --region eu-north-1
```

### Option B: DynamoDB Local with Java

Docker is not required. Download DynamoDB Local from AWS, extract it, and
start it with Java in a separate PowerShell window:

```powershell
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -inMemory
```

Then use these backend variables and start Spring Boot:

```powershell
$env:AWS_REGION="eu-north-1"
$env:AWS_DYNAMODB_TABLE_NAME="Khataapp"
$env:AWS_DYNAMODB_ENDPOINT="http://localhost:8000"
$env:JWT_SECRET="use-a-random-local-secret-with-at-least-32-bytes"
$env:JWT_EXPIRATION_MS="3600000"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"
.\mvnw.cmd spring-boot:run
```

Create the local table once:

```powershell
aws dynamodb create-table --table-name Khataapp --attribute-definitions AttributeName=PK,AttributeType=S AttributeName=SK,AttributeType=S --key-schema AttributeName=PK,KeyType=HASH AttributeName=SK,KeyType=RANGE --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000 --region eu-north-1
```

### Start the frontend

In a second PowerShell window:

```powershell
cd ..\frontend
Copy-Item .env.example .env
npm.cmd install
npm.cmd run dev
```

Open `http://localhost:5173`, register a user, add transactions, change the
dashboard period, and test PDF/Excel exports from the transactions page.

Run automated checks with:

```powershell
cd ..\backend
.\mvnw.cmd test
cd ..\frontend
npm.cmd run build
```

Never commit `.env` files or cloud credentials. In App Runner, provide
`JWT_SECRET` through a secret and use the instance role for DynamoDB access.

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