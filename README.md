# Khata

Khata is a production-oriented multilingual household expense tracker with a Spring Boot/DynamoDB API and a Vite React frontend.

## Features

- JWT authentication with protected, user-scoped transaction APIs
- Monthly, 3-month, 6-month, yearly analytics and transaction filtering
- Category totals, income/outcome trend charts, and deterministic spending insights
- INR, USD, EUR, GBP, AED and other ISO currency display support
- PDF and Excel transaction exports
- English, Urdu, Hindi, Arabic, Spanish, French, German and Turkish UI catalogs
- Responsive Bootstrap-inspired UI with dark mode, loading states and RTL support
- OpenAPI documentation at `/swagger-ui.html`

## Local development

1. Copy `backend/.env.example` and `frontend/.env.example` to local environment files; provide a 256-bit JWT secret and DynamoDB table settings. Never commit passwords or secrets.
2. Start the API with `cd backend; .\mvnw.cmd spring-boot:run`.
3. Start the UI with `cd frontend; npm install; npm run dev`.

The API persists users, transactions, and per-household categories in DynamoDB. Category defaults are seeded at registration. Profile updates are authenticated and logout requires confirmation. Language and RTL preferences are stored in browser local storage.

For deployment, set the environment variables below on EC2 and build the frontend with its S3 API URL. Use HTTPS and least-privilege IAM permissions.

### Backend environment

`AWS_REGION`, `AWS_DYNAMODB_TABLE_NAME`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `CORS_ALLOWED_ORIGINS`, and `SPRING_PROFILES_ACTIVE`.

### Frontend environment

`VITE_API_BASE_URL` must point to the deployed API base URL, including `/api/v1`.

### CI/CD

Run `docker compose up -d --build` from `backend` on EC2. The backend container uses the EC2 instance role through the AWS SDK; no AWS access keys are required on the instance.

### EC2 deployment setup

1. Create an Ubuntu EC2 instance, install Docker Compose, and attach an IAM role with access only to the required DynamoDB table.
2. Allow port `22` only from your IP and the API port only from the frontend/proxy. Keep DynamoDB private.
3. Clone the repository and create `backend/.env` from `backend/.env.example`. Set `SPRING_PROFILES_ACTIVE=prod`, `AWS_DYNAMODB_ENDPOINT=`, a strong `JWT_SECRET`, and the S3/CloudFront origin in `CORS_ALLOWED_ORIGINS`. Never commit this file.

```bash
git clone <repository-url> khata-app
cd khata-app/backend
cp .env.example .env
docker compose up -d --build
```

4. Create an S3 bucket with static website hosting, build the frontend with `VITE_API_BASE_URL`, and run `aws s3 sync frontend/dist s3://BUCKET --delete`.
5. Set `CORS_ALLOWED_ORIGINS` to the exact S3 website or CloudFront origin and restart the backend container.

For HTTPS, put the API behind a domain and TLS reverse proxy. Do not expose Docker port `8080` broadly.
