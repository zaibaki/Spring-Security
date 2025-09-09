# Google OAuth2 Setup Guide for Spring Boot Authentication

## Step 1: Access Google Cloud Console

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Sign in with your Google account

## Step 2: Create a New Project

1. Click the **project selector** dropdown at the top
2. Click **"New Project"**
3. Enter project details:
   - **Project name**: `Auth Backend App` (or your preferred name)
   - **Organization**: Leave as default or select your organization
4. Click **"Create"**
5. Wait for project creation and select the new project

## Step 3: Enable Required APIs

1. In the left sidebar, go to **"APIs & Services"** → **"Library"**
2. Search for and enable these APIs:
   - **Google+ API** (or **People API** - newer version)
   - **Google Identity API** (if available)

For each API:
- Click on the API name
- Click **"Enable"** button
- Wait for activation

## Step 4: Configure OAuth Consent Screen

1. Go to **"APIs & Services"** → **"OAuth consent screen"**
2. Choose **"External"** (unless you have Google Workspace)
3. Click **"Create"**

### Fill OAuth Consent Screen Form:

**App Information:**
- **App name**: `Your App Name`
- **User support email**: Your email address
- **App logo**: Upload your app logo (optional)

**App domain:**
- **Application home page**: `http://localhost:3000` (your frontend URL)
- **Application privacy policy link**: `http://localhost:3000/privacy` (create this page)
- **Application terms of service link**: `http://localhost:3000/terms` (create this page)

**Authorized domains:**
- Add `localhost` (for development)
- Add your production domain when ready

**Developer contact information:**
- Add your email address

4. Click **"Save and Continue"**

### Scopes Configuration:
1. Click **"Add or Remove Scopes"**
2. Select these scopes:
   - `../auth/userinfo.email`
   - `../auth/userinfo.profile`
   - `openid`
3. Click **"Update"** then **"Save and Continue"**

### Test Users (for development):
1. Click **"Add Users"**
2. Add email addresses that can test your app during development
3. Click **"Save and Continue"**

## Step 5: Create OAuth2 Credentials

1. Go to **"APIs & Services"** → **"Credentials"**
2. Click **"+ Create Credentials"** → **"OAuth 2.0 Client IDs"**

### Configure OAuth Client:

**Application type**: `Web application`

**Name**: `Auth Backend OAuth Client`

**Authorized JavaScript origins:**
```
http://localhost:3000
http://localhost:8080
```

**Authorized redirect URIs:**
```
http://localhost:8080/api/v1/oauth2/callback/google
http://localhost:3000/oauth2/redirect
```

3. Click **"Create"**

## Step 6: Save Your Credentials

After creation, you'll see a modal with:
- **Client ID**: `1234567890-abcdefghijklmnop.apps.googleusercontent.com`
- **Client Secret**: `GOCSPX-abcd_efgh_ijkl_mnop`

**Important**: Copy these values immediately and save them securely.

## Step 7: Update Your Environment Variables

Add these to your `.env` file:

```bash
# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=1234567890-abcdefghijklmnop.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-abcd_efgh_ijkl_mnop
```

## Step 8: Test Your Configuration

### 8.1 Start Your Application
```bash
./mvnw spring-boot:run
```

### 8.2 Test OAuth2 Flow
1. Open browser and go to:
   ```
   http://localhost:8080/api/v1/oauth2/authorize/google
   ```

2. You should be redirected to Google's login page
3. Sign in with a test user account
4. Grant permissions to your app
5. You should be redirected back with a token

## Step 9: Production Setup

When deploying to production:

### Update OAuth Client Settings:
1. Go back to **"Credentials"** in Google Cloud Console
2. Click your OAuth client
3. Add production URLs:

**Authorized JavaScript origins:**
```
https://yourdomain.com
https://api.yourdomain.com
```

**Authorized redirect URIs:**
```
https://api.yourdomain.com/api/v1/oauth2/callback/google
https://yourdomain.com/oauth2/redirect
```

### Update Environment Variables:
```bash
GOOGLE_CLIENT_ID=your-production-client-id
GOOGLE_CLIENT_SECRET=your-production-client-secret
```

## Troubleshooting Common Issues

### Error: "redirect_uri_mismatch"
**Problem**: The redirect URI in your request doesn't match the configured ones.

**Solution**: 
- Check your `application.yml` redirect URI configuration
- Ensure it matches exactly what's in Google Cloud Console
- Remove trailing slashes if present

### Error: "access_denied"
**Problem**: User denied permission or app not approved.

**Solution**:
- Add user to test users list in OAuth consent screen
- Check if app is in testing mode and user has access

### Error: "invalid_client"
**Problem**: Client ID or Secret is incorrect.

**Solution**:
- Double-check your `.env` file values
- Ensure no extra spaces or characters
- Regenerate credentials if necessary

### Error: "unauthorized_client"
**Problem**: Client not authorized for this grant type.

**Solution**:
- Ensure OAuth2 is properly configured in `application.yml`
- Check that the application type is set to "Web application"

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** for all sensitive data
3. **Regularly rotate** client secrets
4. **Monitor usage** in Google Cloud Console
5. **Set up proper domains** for production
6. **Review OAuth scopes** - only request what you need

## Testing Your OAuth2 Integration

### Manual Test:
```bash
# 1. Visit OAuth2 URL
curl -L "http://localhost:8080/api/v1/oauth2/authorize/google"

# 2. Complete browser flow
# 3. Extract token from redirect URL
# 4. Test authenticated endpoint
curl -H "Authorization: Bearer YOUR_TOKEN" \
     "http://localhost:8080/api/v1/users/me"
```

### Expected Response:
```json
{
  "id": 123,
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john.doe@gmail.com",
  "emailVerified": true,
  "provider": "GOOGLE",
  "imageUrl": "https://lh3.googleusercontent.com/...",
  "roles": ["USER"]
}
```

Your Google OAuth2 integration should now be working correctly!