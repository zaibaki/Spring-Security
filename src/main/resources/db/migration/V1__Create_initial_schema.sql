-- V1__Create_initial_schema.sql
-- Initial schema creation for Spring Boot Auth Backend
-- Author: Richard Karanu
-- Created: 2025-09-09

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50),  -- Nullable for OAuth2 users
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(120),  -- Nullable for OAuth2 users
    email_verified BOOLEAN DEFAULT FALSE,
    provider VARCHAR(20) DEFAULT 'LOCAL',
    provider_id VARCHAR(255),  -- Google user ID
    image_url VARCHAR(255),    -- Profile image URL
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create user_roles junction table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create email_verification_tokens table
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_provider ON users(provider);
CREATE INDEX IF NOT EXISTS idx_users_provider_id ON users(provider_id);
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_email_tokens_token ON email_verification_tokens(token);
CREATE INDEX IF NOT EXISTS idx_email_tokens_user_id ON email_verification_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_email_tokens_expiry ON email_verification_tokens(expiry_date);

-- Insert default roles
INSERT INTO roles (name) 
VALUES ('USER'), ('ADMIN'), ('MODERATOR')
ON CONFLICT (name) DO NOTHING;

-- Add constraints
ALTER TABLE users ADD CONSTRAINT chk_provider 
    CHECK (provider IN ('LOCAL', 'GOOGLE'));

ALTER TABLE users ADD CONSTRAINT chk_email_format 
    CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Add trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE users IS 'User accounts supporting both local and OAuth2 authentication';
COMMENT ON TABLE roles IS 'User roles for role-based access control';
COMMENT ON TABLE user_roles IS 'Many-to-many relationship between users and roles';
COMMENT ON TABLE email_verification_tokens IS 'Tokens for email verification process';

COMMENT ON COLUMN users.provider IS 'Authentication provider: LOCAL for email/password, GOOGLE for OAuth2';
COMMENT ON COLUMN users.provider_id IS 'External provider user ID (e.g., Google sub claim)';
COMMENT ON COLUMN users.email_verified IS 'Whether email address has been verified';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password (null for OAuth2 users)';