# Spring Boot Authentication Backend

A complete, production-ready authentication backend built with Spring Boot, featuring JWT authentication, OAuth2 integration, email verification, and comprehensive security features.

## 🚀 Features

- **JWT Authentication** - Secure token-based authentication with refresh tokens
- **Google OAuth2** - Social login integration with Google
- **Email Verification** - SMTP-based email verification system with beautiful HTML templates
- **Role-Based Access Control** - USER, ADMIN, and MODERATOR roles
- **Password Security** - BCrypt encryption with secure password policies
- **PostgreSQL Database** - Production-ready database with JPA/Hibernate
- **Environment Validation** - Smart .env file detection with fallback warnings
- **Comprehensive Testing** - Unit and integration tests included
- **Docker Support** - PostgreSQL containerization with Docker Compose
- **API Documentation** - Complete Postman collection included

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Docker & Docker Compose** (for PostgreSQL)
- **Git**
- **Gmail Account** (for SMTP email sending)
- **Google Cloud Console Account** (for OAuth2)

## 🛠 Quick Start

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd authbackend
```

### 2. Environment Setup
```bash
# Copy environment template
cp .env.example .env

# Edit .env with your actual values
nano .env
```

### 3. Configure Environment Variables

Edit your `.env` file with these required values:

```bash
# Database Configuration
DB_USERNAME=postgres
DB_PASSWORD=your_secure_db_password

# JWT Configuration (IMPORTANT: Use a secure random string)
JWT_SECRET=myVerySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm

# Email Configuration (Gmail SMTP)
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-gmail-app-password
EMAIL_FROM=noreply@yourapp.com

# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id-from-console
GOOGLE_CLIENT_SECRET=your-google-client-secret-from-console
```

### 4. Start PostgreSQL Database
```bash
# Start PostgreSQL with Docker Compose
docker-compose up -d postgres

# Optional: Start pgAdmin for database management
docker-compose up -d pgadmin
```

### 5. Run the Application
```bash
# Development mode
./mvnw spring-boot:run

# Or with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on `http://localhost:8080/api/v1`

## 🔧 Configuration Guide

### Gmail SMTP Setup

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password**:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. **Use Gmail and App Password** in your `.env` file

### Google OAuth2 Setup

1. **Create a Project** in [Google Cloud Console](https://console.cloud.google.com/)
2. **Enable Google+ API** or Google People API
3. **Create OAuth2 Credentials**:
   - APIs & Services → Credentials → Create Credentials → OAuth 2.0 Client IDs
   - Application type: Web application
   - Authorized redirect URIs:
     - `http://localhost:8080/api/v1/oauth2/callback/google`
     - `http://localhost:3000/oauth2/callback/google` (if using frontend)
4. **Copy Client ID and Secret** to your `.env` file

### Database Configuration

The application uses PostgreSQL by default. Connection details:
- **Host**: localhost:5432
- **Database**: auth_db
- **Username/Password**: From your `.env` file

**pgAdmin Access** (if started):
- URL: http://localhost:5050
- Email: admin@admin.com
- Password: admin

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/auth/register` | Register new user | No |
| `POST` | `/auth/login` | Login user | No |
| `GET` | `/auth/verify-email?token=` | Verify email address | No |
| `POST` | `/auth/resend-verification` | Resend verification email | No |
| `POST` | `/auth/refresh-token` | Refresh access token | No |
| `POST` | `/auth/logout` | Logout user | Yes |

### User Management Endpoints

| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| `GET` | `/users/me` | Get current user | Yes | USER |
| `GET` | `/users/{id}` | Get user by ID | Yes | USER/ADMIN |
| `GET` | `/users` | Get all users (paginated) | Yes | ADMIN |
| `PUT` | `/users/{id}` | Update user | Yes | USER/ADMIN |
| `DELETE` | `/users/{id}` | Delete user | Yes | ADMIN |
| `POST` | `/users/change-password` | Change password | Yes | USER |

### OAuth2 Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/oauth2/authorize/google` | Google OAuth2 login |
| `GET` | `/oauth2/callback/google` | Google OAuth2 callback |

## 📝 API Examples

### Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }'
```

### Login User
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }'
```

### Get Current User (with JWT token)
```bash
curl -X GET http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Categories
```bash
# Unit tests only
./mvnw test -Dtest="*Test"

# Integration tests only  
./mvnw test -Dtest="*IntegrationTest"
```

### Test Coverage
The project includes:
- **Unit Tests** for services and security components
- **Integration Tests** for complete API flows
- **Postman Collection** for manual API testing

## 🐳 Docker & Database

### PostgreSQL Commands
```bash
# Start PostgreSQL
docker-compose up -d postgres

# View PostgreSQL logs
docker-compose logs postgres

# Stop PostgreSQL
docker-compose down

# Reset database (WARNING: Deletes all data)
docker-compose down -v
docker-compose up -d postgres
```

### Database Schema
The application automatically creates these tables:
- `users` - User accounts and profiles
- `roles` - User roles (USER, ADMIN, MODERATOR)
- `user_roles` - Many-to-many user-role mapping
- `email_verification_tokens` - Email verification tokens

## 🔒 Security Features

### JWT Configuration
- **Access Token**: 24 hours expiration
- **Refresh Token**: 7 days expiration
- **Algorithm**: HS256 with secure signing key
- **Claims**: User email and authorities

### Password Security
- **Encryption**: BCrypt with strength 12
- **Validation**: Minimum 6 characters required
- **Change Password**: Requires old password verification

### OAuth2 Security
- **Google Integration**: Secure OAuth2 flow
- **User Mapping**: Automatic user creation from OAuth2 data
- **Email Verification**: OAuth2 users are auto-verified

## 🚨 Environment Validation

The application automatically validates your environment on startup:

```
🔍 Validating environment configuration...
✅ .env file found
⚠️  Using fallback values for: JWT_SECRET, EMAIL_FROM
🚨 SECURITY WARNING: Using default JWT secret!
🏁 Environment validation complete
```

**Security Warnings:**
- Missing `.env` file
- Using default JWT secret
- Missing required environment variables
- Using fallback values for production

## 📁 Project Structure

```
src/main/java/com/example/authbackend/
├── AuthBackendApplication.java          # Main application class
├── config/                              # Configuration classes
│   ├── SecurityConfig.java             # Spring Security configuration
│   ├── OAuth2AuthenticationSuccessHandler.java
│   ├── OAuth2AuthenticationFailureHandler.java
│   ├── DataLoader.java                 # Initial data loader
│   └── EnvironmentValidator.java       # Environment validation
├── controller/                          # REST controllers
│   ├── AuthController.java             # Authentication endpoints
│   └── UserController.java             # User management endpoints
├── dto/                                 # Data Transfer Objects
│   ├── request/                         # Request DTOs
│   └── response/                        # Response DTOs
├── entity/                              # JPA entities
│   ├── User.java                       # User entity
│   ├── Role.java                       # Role entity
│   ├── RoleName.java                   # Role enumeration
│   ├── AuthProvider.java               # Auth provider enumeration
│   └── EmailVerificationToken.java     # Email token entity
├── exception/                           # Custom exceptions
│   ├── GlobalExceptionHandler.java     # Global exception handling
│   ├── UserNotFoundException.java
│   ├── EmailAlreadyExistsException.java
│   └── InvalidTokenException.java
├── repository/                          # JPA repositories
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   └── EmailVerificationTokenRepository.java
├── security/                            # Security components
│   ├── JwtAuthenticationFilter.java    # JWT filter
│   ├── JwtTokenProvider.java           # JWT utilities
│   ├── CustomUserDetailsService.java   # User details service
│   ├── OAuth2UserService.java          # OAuth2 user service
│   └── UserPrincipal.java              # Security principal
├── service/                             # Business logic
│   ├── AuthService.java                # Authentication service
│   ├── UserService.java                # User management service
│   ├── EmailService.java               # Email service
│   └── impl/                            # Service implementations
└── util/                                # Utility classes
    └── EmailTemplates.java             # Email HTML templates
```

## 🎯 Development Profiles

### Development Profile (`dev`)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
- **Database**: `create-drop` (recreates schema on restart)
- **Logging**: Debug level enabled
- **SQL**: Show SQL queries

### Production Profile (`prod`)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```
- **Database**: `validate` (no automatic schema changes)
- **Logging**: Info level
- **SQL**: Hidden for performance

## 🔍 Troubleshooting

### Common Issues

**Database Connection Failed**
```bash
# Check if PostgreSQL is running
docker-compose ps

# Check PostgreSQL logs
docker-compose logs postgres

# Restart PostgreSQL
docker-compose restart postgres
```

**Email Sending Failed**
- Verify Gmail App Password (not regular password)
- Check 2FA is enabled on Gmail
- Verify SMTP settings in `.env`

**JWT Token Invalid**
- Check JWT_SECRET is properly set
- Verify token hasn't expired
- Ensure correct Authorization header format: `Bearer <token>`

**OAuth2 Redirect Error**
- Verify Google Console redirect URIs match exactly
- Check GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET
- Ensure Google+ API is enabled

### Environment Issues

**Missing .env File**
```bash
cp .env.example .env
# Edit with your values
```

**Using Fallback Values**
```bash
# Check which values are missing
./mvnw spring-boot:run | grep "FALLBACK_DETECTED"
```

### Application Logs

**Enable Debug Logging**
```bash
./mvnw spring-boot:run --debug
```

**Check Specific Package Logs**
```yaml
# In application.yml
logging:
  level:
    com.example.authbackend: DEBUG
    org.springframework.security: DEBUG
```

## 🚀 Deployment

### Production Deployment

1. **Environment Variables**
```bash
export DB_USERNAME=your_prod_db_user
export DB_PASSWORD=your_prod_db_password
export JWT_SECRET=your_very_secure_random_jwt_secret
# ... other variables
```

2. **Build Production JAR**
```bash
./mvnw clean package -DskipTests
```

3. **Run Production**
```bash
java -jar target/authbackend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker Deployment
```bash
# Build Docker image
docker build -t authbackend .

# Run with Docker Compose
docker-compose up -d
```

## 📊 Monitoring & Health

### Health Check Endpoint
```bash
curl http://localhost:8080/api/v1/actuator/health
```

### Application Metrics
```bash
curl http://localhost:8080/api/v1/actuator/metrics
```

## 🤝 Contributing

1. **Fork the repository**
2. **Create feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit changes**: `git commit -m 'Add amazing feature'`
4. **Push to branch**: `git push origin feature/amazing-feature`
5. **Open Pull Request**

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot** for the excellent framework
- **Spring Security** for robust security features
- **JWT.io** for JWT token standards
- **Google** for OAuth2 integration
- **PostgreSQL** for reliable database
- **Docker** for containerization

## 📞 Support

For support and questions:
- **Create an Issue** on GitHub
- **Check Documentation** in this README
- **Review Test Cases** for usage examples

---

**⭐ If this project helped you, please give it a star!**