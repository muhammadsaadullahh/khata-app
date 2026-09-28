# Khata

Khata is a production-oriented multilingual household expense tracker with a Spring Boot/DynamoDB API and a Vite React frontend.

This is a private, proprietary repository. No open-source license or redistribution permission is granted.

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

For deployment, set the environment variables below in App Runner and S3/GitHub Actions. Use HTTPS and least-privilege IAM permissions.

### Backend environment

`AWS_REGION`, `AWS_DYNAMODB_TABLE_NAME`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `CORS_ALLOWED_ORIGINS`, `SPRING_PROFILES_ACTIVE`, `EXCHANGE_RATE_PROVIDER_URL`, `EXCHANGE_RATE_API_KEY`, and `EXCHANGE_RATE_CACHE_HOURS`.

### Frontend environment

`VITE_API_URL` must point to the deployed API base URL, including `/api/v1`.

### CI/CD

The workflow in `.github/workflows/deploy.yml` runs backend tests, builds and pushes the backend image to ECR, triggers the App Runner service deployment, builds the React application, and syncs `frontend/dist` to S3. Configure the App Runner service to use the `latest` image tag from the ECR repository. Configure AWS credentials, `AWS_REGION`, `APP_RUNNER_SERVICE_ARN`, `FRONTEND_S3_BUCKET`, and `VITE_API_URL` as repository secrets.
