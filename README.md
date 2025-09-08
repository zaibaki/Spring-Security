# Auth Backend Setup Instructions

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

## Quick Start

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd auth-backend
   ```

2. **Setup environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your actual values
   ```

3. **Start PostgreSQL**
   ```bash
   docker-compose up -d postgres
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

## Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Google+ API
4. Create OAuth2 credentials
5. Add authorized redirect URIs:
   - `http://localhost:8080/api/v1/oauth2/callback/google`
6. Copy Client ID and Client Secret to your `.env` file

## Gmail SMTP Setup

1. Enable 2-factor authentication on your Gmail account
2. Generate an app password:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. Use your Gmail and the app password in `.env`

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login user
- `GET /api/v1/auth/verify-email?token=` - Verify email
- `POST /api/v1/auth/resend-verification` - Resend verification email
- `POST /api/v1/auth/refresh-token` - Refresh access token
- `POST /api/v1/auth/logout` - Logout user

### Users
- `GET /api/v1/users/me` - Get current user
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users` - Get all users (Admin only)
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user (Admin only)
- `POST /api/v1/users/change-password` - Change password

### OAuth2
- `GET /api/v1/oauth2/authorize/google` - Google OAuth2 login

## Testing

Run tests with:
```bash
./mvnw test
```