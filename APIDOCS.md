# 🚀 Complete Postman Testing Guide - Basic Auth & OAuth2

## 🌍 Environment Variables Setup

Create a new environment called **"Auth Backend"** with these variables:

| Variable | Initial Value |
|----------|---------------|
| `baseUrl` | `http://localhost:8080/api/v1` |
| `accessToken` | |
| `refreshToken` | |
| `userId` | |
| `googleOAuth2Url` | `http://localhost:8080/api/v1/oauth2/authorize/google` |

## 📝 Basic Authentication Endpoints

### 1. Register User

**POST** `{{baseUrl}}/auth/register`

**Body (JSON):**
```json
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123"
}
```

**Tests Script:**
```javascript
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    pm.test("Registration successful", function () {
        pm.expect(jsonData.success).to.be.true;
        pm.expect(jsonData.message).to.include("registered successfully");
    });
}
```

---

### 2. Login User

**POST** `{{baseUrl}}/auth/login`

**Body (JSON):**
```json
{
    "email": "john.doe@example.com",
    "password": "password123"
}
```

**Tests Script:**
```javascript
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    
    // Save tokens
    pm.environment.set("accessToken", jsonData.accessToken);
    pm.environment.set("refreshToken", jsonData.refreshToken);
    pm.environment.set("userId", jsonData.user.id);
    
    pm.test("Login successful", function () {
        pm.expect(jsonData.accessToken).to.exist;
        pm.expect(jsonData.user.email).to.eql("john.doe@example.com");
        pm.expect(jsonData.user.provider).to.eql("LOCAL");
    });
}
```

---

### 3. Verify Email

**GET** `{{baseUrl}}/auth/verify-email?token=your-verification-token`

**Tests Script:**
```javascript
pm.test("Email verified", function () {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    pm.expect(jsonData.success).to.be.true;
    pm.expect(jsonData.message).to.include("verified successfully");
});
```

---

### 4. Resend Verification Email

**POST** `{{baseUrl}}/auth/resend-verification?email=john.doe@example.com`

**Tests Script:**
```javascript
pm.test("Verification email resent", function () {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    pm.expect(jsonData.success).to.be.true;
});
```

---

## 🌐 OAuth2 Endpoints

### 5. Google OAuth2 Login (Browser Required)

**GET** `{{googleOAuth2Url}}`

**Instructions:**
1. Copy the URL from Postman
2. Paste it in your browser
3. Complete Google OAuth2 flow
4. Extract token from redirect URL
5. Use token for subsequent requests

**Manual Test:**
- Open browser with the OAuth2 URL
- After successful login, you'll be redirected with a token parameter
- Copy the token for testing authenticated endpoints

---

### 6. OAuth2 Callback Handler (Automatic)

**GET** `{{baseUrl}}/oauth2/callback/google`

**Note:** This endpoint is called automatically by Google after OAuth2 authorization. It's not typically tested directly in Postman.

---

## 👤 User Management Endpoints

### 7. Get Current User

**GET** `{{baseUrl}}/users/me`

**Headers:**
- `Authorization: Bearer {{accessToken}}`

**Tests Script:**
```javascript
pm.test("User profile retrieved", function () {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    pm.expect(jsonData.email).to.exist;
    pm.expect(jsonData.id).to.exist;
    
    // Check if it's OAuth2 or basic auth user
    if (jsonData.provider === "GOOGLE") {
        pm.test("OAuth2 user has Google provider", function () {
            pm.expect(jsonData.emailVerified).to.be.true;
        });
    } else {
        pm.test("Basic auth user has LOCAL provider", function () {
            pm.expect(jsonData.provider).to.eql("LOCAL");
        });
    }
});
```

---

### 8. Update User Profile

**PUT** `{{baseUrl}}/users/{{userId}}`

**Headers:**
- `Authorization: Bearer {{accessToken}}`

**Body (JSON):**
```json
{
    "firstName": "Jane",
    "lastName": "Smith",
    "imageUrl": "https://example.com/avatar.jpg"
}
```

**Tests Script:**
```javascript
pm.test("Profile updated successfully", function () {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    pm.expect(jsonData.firstName).to.eql("Jane");
    pm.expect(jsonData.lastName).to.eql("Smith");
});
```

---

### 9. Change Password (Basic Auth Only)

**POST** `{{baseUrl}}/users/change-password`

**Headers:**
- `Authorization: Bearer {{accessToken}}`

**Body (JSON):**
```json
{
    "oldPassword": "password123",
    "newPassword": "newPassword456"
}
```

**Tests Script:**
```javascript
pm.test("Password changed successfully", function () {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    pm.expect(jsonData.success).to.be.true;
});

// Note: OAuth2 users (Google) cannot change password this way
```

---

### 10. Refresh Token

**POST** `{{baseUrl}}/auth/refresh-token`

**Body (JSON):**
```json
{
    "refreshToken": "{{refreshToken}}"
}
```

**Tests Script:**
```javascript
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    
    // Update tokens
    pm.environment.set("accessToken", jsonData.accessToken);
    pm.environment.set("refreshToken", jsonData.refreshToken);
    
    pm.test("Token refreshed", function () {
        pm.expect(jsonData.accessToken).to.exist;
        pm.expect(jsonData.refreshToken).to.exist;
    });
}
```

---

### 11. Get All Users (Admin Only)

**GET** `{{baseUrl}}/users?page=0&size=10`

**Headers:**
- `Authorization: Bearer {{accessToken}}`

**Tests Script:**
```javascript
pm.test("Users list retrieved", function () {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    pm.expect(jsonData.content).to.be.an('array');
    
    // Check if response contains both auth types
    if (jsonData.content.length > 0) {
        jsonData.content.forEach(user => {
            pm.expect(user.provider).to.be.oneOf(['LOCAL', 'GOOGLE']);
        });
    }
});
```

---

### 12. Logout

**POST** `{{baseUrl}}/auth/logout`

**Headers:**
- `Authorization: Bearer {{accessToken}}`

**Tests Script:**
```javascript
if (pm.response.code === 200) {
    // Clear tokens
    pm.environment.set("accessToken", "");
    pm.environment.set("refreshToken", "");
    pm.environment.set("userId", "");
    
    pm.test("Logout successful", function () {
        pm.response.to.have.status(200);
        const jsonData = pm.response.json();
        pm.expect(jsonData.success).to.be.true;
    });
}
```

---

## 🔄 Testing Workflows

### Basic Authentication Flow
**Recommended order:**
1. Register User
2. Login User (saves tokens)
3. Get Current User
4. Update User Profile
5. Change Password
6. Refresh Token
7. Logout (clears tokens)

### OAuth2 Testing Flow
**Manual steps:**
1. Copy Google OAuth2 URL from Postman
2. Open in browser and complete Google login
3. Extract token from redirect URL
4. Set `accessToken` environment variable manually
5. Test Get Current User (should show Google provider)
6. Update User Profile (works for OAuth2 users)
7. Logout

### Mixed Environment Testing
**For testing both auth types:**
1. Register basic auth user → Login → Test endpoints
2. Login with Google OAuth2 → Test same endpoints
3. Get All Users (should show both LOCAL and GOOGLE providers)
4. Compare functionality between auth types

---

## 🔍 Provider-Specific Testing

### Basic Auth User Properties
```json
{
    "provider": "LOCAL",
    "emailVerified": true/false,
    "password": "required for registration"
}
```

### OAuth2 User Properties
```json
{
    "provider": "GOOGLE",
    "emailVerified": true,
    "providerId": "google-user-id",
    "imageUrl": "google-profile-image"
}
```

---

## 💡 Testing Tips

- **Environment Variables**: Always use `{{baseUrl}}` and `{{accessToken}}`
- **OAuth2 Testing**: Requires browser interaction for initial login
- **Token Management**: Both auth types use same JWT token format
- **Provider Checking**: Test responses should include provider type
- **Mixed Users**: Admin endpoints will show both LOCAL and GOOGLE users
- **Password Changes**: Only available for LOCAL provider users
- **Email Verification**: AUTO for Google, MANUAL for basic auth