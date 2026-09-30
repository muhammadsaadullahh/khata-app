# Deployment guide

The production layout uses an EC2 instance for the Spring Boot API and an S3
static website bucket for the React frontend. The EC2 instance should have an
IAM role granting access to the Khata DynamoDB table; the AWS SDK uses that
role through `DefaultCredentialsProvider`. Environment credentials are only a
local fallback.

## Backend on EC2

1. Create the DynamoDB table and an IAM role with least-privilege access to
   that table. Attach the role to the EC2 instance through an instance profile.
2. Install Docker and the Compose plugin, then clone the repository:

   ```bash
   git clone <repository-url> khata-app
   cd khata-app/backend
   ```

3. Create the production environment file:

   ```bash
   cp .env.example .env
   nano .env
   ```

   Set `AWS_REGION`, `AWS_DYNAMODB_TABLE_NAME`, a strong `JWT_SECRET`,
   `SPRING_PROFILES_ACTIVE=prod`, and `CORS_ALLOWED_ORIGINS` to the exact
   CloudFront origin, for example `https://d123example.cloudfront.net`. Set
   `AWS_DYNAMODB_ENDPOINT=` so the application uses AWS DynamoDB rather than
   local DynamoDB. Do not add access keys when the EC2 IAM role is attached. The optional
   `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, and `AWS_SESSION_TOKEN`
   variables are only for local fallback/testing.

4. Start the backend:

   ```bash
   docker compose up -d --build
   docker compose ps
   curl http://localhost:8080/actuator/health
   ```

   Restrict port `8080` with the EC2 security group or place it behind a TLS
   reverse proxy. View logs with `docker compose logs -f backend`.

## Frontend on S3

1. Configure the API URL before building:

   ```bash
   cd ../frontend
   cp .env.example .env
   # For the same CloudFront distribution serving /api/*, use VITE_API_BASE_URL=/api/v1
   npm ci
   npm run build
   ```

2. Configure an S3 bucket for static website hosting and set `index.html` as
   both the index and error document for client-side routing.
3. Upload the generated assets:

   ```bash
   aws s3 sync dist s3://<bucket-name> --delete
   ```

4. Add the final CloudFront origin to `CORS_ALLOWED_ORIGINS`, then restart the
   backend:

   ```bash
   cd ../backend
   docker compose up -d
   ```

Never commit `.env` files, access keys, private keys, or JWT secrets.

## HTTPS with CloudFront

Create one CloudFront distribution with two origins and two behaviors:

- Default behavior `/` -> the S3 website endpoint, using HTTP-only origin protocol.
- `/api/*` -> the EC2 public DNS/IP on port `8080`, using HTTP-only origin protocol.

For `/api/*`, allow `GET, HEAD, OPTIONS, PUT, POST, PATCH, DELETE`, disable
caching, and forward query strings and the `Authorization` header. Set the
frontend `VITE_API_BASE_URL=/api/v1`, rebuild, upload `dist/` to S3, and set
the backend `CORS_ALLOWED_ORIGINS` to the CloudFront HTTPS domain. The browser
then uses HTTPS for both the static frontend and API while the CloudFront
origins can remain HTTP.
