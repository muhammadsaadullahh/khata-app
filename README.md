# Khata

Khata is a multilingual household ledger with a Spring Boot/DynamoDB API and a Vite React frontend.

## Local development

1. Copy `backend/.env.example` and `frontend/.env.example` to local environment files; provide a 256-bit JWT secret and DynamoDB table settings. Never commit passwords or secrets.
2. Start the API with `cd backend; .\mvnw.cmd spring-boot:run`.
3. Start the UI with `cd frontend; npm install; npm run dev`.

The API persists users, transactions, and per-household categories in DynamoDB. Category defaults are seeded at registration. Profile updates are authenticated and logout requires confirmation. Language and RTL preferences are stored in browser local storage.

For deployment, set `APP_CORS_ALLOWED_ORIGINS`, AWS region/table credentials, and `SECURITY_JWT_SECRET` in the hosting platform; use HTTPS and least-privilege IAM permissions.
