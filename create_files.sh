#!/bin/bash

# Spring Boot Auth Backend Project Structure Generator
# Run from: ~/github_projects/spring/authbackend/

echo "🚀 Creating Spring Boot Auth Backend Project Structure..."

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to create directory if it doesn't exist
create_dir() {
    if [ ! -d "$1" ]; then
        mkdir -p "$1"
        echo -e "${GREEN}✓${NC} Created directory: $1"
    else
        echo -e "${YELLOW}⚠${NC} Directory already exists: $1"
    fi
}

# Function to create file if it doesn't exist
create_file() {
    if [ ! -f "$1" ]; then
        touch "$1"
        echo -e "${GREEN}✓${NC} Created file: $1"
    else
        echo -e "${YELLOW}⚠${NC} File already exists: $1"
    fi
}

echo -e "${BLUE}📁 Creating directory structure...${NC}"

# Main Java source directories
create_dir "src/main/java/com/example/authbackend"
create_dir "src/main/java/com/example/authbackend/config"
create_dir "src/main/java/com/example/authbackend/controller"
create_dir "src/main/java/com/example/authbackend/dto/request"
create_dir "src/main/java/com/example/authbackend/dto/response"
create_dir "src/main/java/com/example/authbackend/entity"
create_dir "src/main/java/com/example/authbackend/exception"
create_dir "src/main/java/com/example/authbackend/repository"
create_dir "src/main/java/com/example/authbackend/security"
create_dir "src/main/java/com/example/authbackend/service/impl"
create_dir "src/main/java/com/example/authbackend/util"

# Resources directory
create_dir "src/main/resources"

# Test directories
create_dir "src/test/java/com/example/authbackend/controller"
create_dir "src/test/java/com/example/authbackend/service/impl"
create_dir "src/test/java/com/example/authbackend/security"
create_dir "src/test/java/com/example/authbackend/integration"

echo -e "${BLUE}📄 Creating Java files...${NC}"

# Main Application
create_file "src/main/java/com/example/authbackend/AuthBackendApplication.java"

# Config files
create_file "src/main/java/com/example/authbackend/config/SecurityConfig.java"
create_file "src/main/java/com/example/authbackend/config/OAuth2AuthenticationSuccessHandler.java"
create_file "src/main/java/com/example/authbackend/config/OAuth2AuthenticationFailureHandler.java"
create_file "src/main/java/com/example/authbackend/config/DataLoader.java"

# Controllers
create_file "src/main/java/com/example/authbackend/controller/AuthController.java"
create_file "src/main/java/com/example/authbackend/controller/UserController.java"

# DTOs - Request
create_file "src/main/java/com/example/authbackend/dto/request/LoginRequest.java"
create_file "src/main/java/com/example/authbackend/dto/request/RegisterRequest.java"
create_file "src/main/java/com/example/authbackend/dto/request/PasswordResetRequest.java"

# DTOs - Response
create_file "src/main/java/com/example/authbackend/dto/response/AuthResponse.java"
create_file "src/main/java/com/example/authbackend/dto/response/UserResponse.java"
create_file "src/main/java/com/example/authbackend/dto/response/ApiResponse.java"

# Entities
create_file "src/main/java/com/example/authbackend/entity/User.java"
create_file "src/main/java/com/example/authbackend/entity/Role.java"
create_file "src/main/java/com/example/authbackend/entity/RoleName.java"
create_file "src/main/java/com/example/authbackend/entity/AuthProvider.java"
create_file "src/main/java/com/example/authbackend/entity/EmailVerificationToken.java"

# Exceptions
create_file "src/main/java/com/example/authbackend/exception/GlobalExceptionHandler.java"
create_file "src/main/java/com/example/authbackend/exception/UserNotFoundException.java"
create_file "src/main/java/com/example/authbackend/exception/EmailAlreadyExistsException.java"
create_file "src/main/java/com/example/authbackend/exception/InvalidTokenException.java"

# Repositories
create_file "src/main/java/com/example/authbackend/repository/UserRepository.java"
create_file "src/main/java/com/example/authbackend/repository/RoleRepository.java"
create_file "src/main/java/com/example/authbackend/repository/EmailVerificationTokenRepository.java"

# Security
create_file "src/main/java/com/example/authbackend/security/JwtAuthenticationFilter.java"
create_file "src/main/java/com/example/authbackend/security/JwtTokenProvider.java"
create_file "src/main/java/com/example/authbackend/security/CustomUserDetailsService.java"
create_file "src/main/java/com/example/authbackend/security/OAuth2UserService.java"
create_file "src/main/java/com/example/authbackend/security/UserPrincipal.java"

# Services
create_file "src/main/java/com/example/authbackend/service/AuthService.java"
create_file "src/main/java/com/example/authbackend/service/UserService.java"
create_file "src/main/java/com/example/authbackend/service/EmailService.java"

# Service Implementations
create_file "src/main/java/com/example/authbackend/service/impl/AuthServiceImpl.java"
create_file "src/main/java/com/example/authbackend/service/impl/UserServiceImpl.java"
create_file "src/main/java/com/example/authbackend/service/impl/EmailServiceImpl.java"

# Utils
create_file "src/main/java/com/example/authbackend/util/EmailTemplates.java"

echo -e "${BLUE}⚙️ Creating resource files...${NC}"

# Resource files
create_file "src/main/resources/application.yml"
create_file "src/main/resources/application-dev.yml"
create_file "src/main/resources/application-prod.yml"
create_file "src/main/resources/application-test.properties"

echo -e "${BLUE}🧪 Creating test files...${NC}"

# Test files
create_file "src/test/java/com/example/authbackend/controller/AuthControllerTest.java"
create_file "src/test/java/com/example/authbackend/service/impl/AuthServiceImplTest.java"
create_file "src/test/java/com/example/authbackend/service/impl/UserServiceImplTest.java"
create_file "src/test/java/com/example/authbackend/security/JwtTokenProviderTest.java"
create_file "src/test/java/com/example/authbackend/integration/AuthIntegrationTest.java"

echo -e "${BLUE}📋 Creating root directory files...${NC}"

# Root directory files
create_file "pom.xml"
create_file "docker-compose.yml"
create_file ".env.example"
create_file "README.md"
create_file "Postman_Collection.json"

# Create .gitignore if it doesn't exist
if [ ! -f ".gitignore" ]; then
    cat > .gitignore << 'EOF'
# Compiled class file
*.class

# Log file
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# Virtual machine crash logs
hs_err_pid*

# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IntelliJ IDEA
.idea/
*.iws
*.iml
*.ipr

# Eclipse
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

# VS Code
.vscode/

# NetBeans
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

# Spring Boot
.gradle
build/
!gradle/wrapper/gradle-wrapper.jar
!**/src/main/**/build/
!**/src/test/**/build/

# Environment variables
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Database
*.db
*.sqlite
*.sqlite3

# OS
.DS_Store
Thumbs.db

# Logs
logs/
*.log

# Runtime data
pids
*.pid
*.seed
*.pid.lock

# Coverage directory used by tools like istanbul
coverage/

# Application specific
uploads/
temp/
EOF
    echo -e "${GREEN}✓${NC} Created file: .gitignore"
else
    echo -e "${YELLOW}⚠${NC} File already exists: .gitignore"
fi

echo ""
echo -e "${GREEN}🎉 Project structure created successfully!${NC}"
echo ""
echo -e "${BLUE}📊 Summary:${NC}"
echo "   • Main Java files: $(find src/main/java -name "*.java" | wc -l) files"
echo "   • Test files: $(find src/test/java -name "*.java" | wc -l) files"
echo "   • Resource files: $(find src/main/resources -type f | wc -l) files"
echo "   • Configuration files: $(ls -1 *.xml *.yml *.json *.md 2>/dev/null | wc -l) files"
echo ""
echo -e "${BLUE}🔧 Next Steps:${NC}"
echo "   1. Edit pom.xml to add your dependencies"
echo "   2. Configure application.yml files"
echo "   3. Implement your Java classes"
echo "   4. Set up your database in docker-compose.yml"
echo "   5. Create your .env file from .env.example"
echo ""
echo -e "${GREEN}Happy coding! 🚀${NC}"

# Make the script executable if run directly
chmod +x "$0" 2>/dev/null || true