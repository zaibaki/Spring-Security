# Simple Database Migration Guide

## Setup (One-time)

1. **Add dependencies to pom.xml:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

2. **Create migration folder:**
```bash
mkdir -p src/main/resources/db/migration
```

3. **Update application.yml:**
```yaml
spring:
  flyway:
    enabled: true
  jpa:
    hibernate:
      ddl-auto: validate  # Change from 'update'
```

## Creating Migrations

**File naming:** `V{number}__{description}.sql`

Examples:
- `V1__Create_initial_schema.sql`
- `V2__Add_user_preferences.sql`
- `V3__Update_email_constraints.sql`

## Basic Commands

```bash
# Check migration status
./mvnw flyway:info

# Run migrations
./mvnw flyway:migrate

# Reset database (dev only)
./mvnw flyway:clean
```

## Migration Rules

- **Never modify** existing migration files after running them
- **Always test** migrations on copy of production data
- **Use transactions** for data changes
- **Add indexes** for performance
- **Document changes** with comments

## Common Migration Patterns

### Add Column
```sql
-- V5__Add_phone_to_users.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

### Remove Column (Safe way)
```sql
-- V6__Remove_old_field.sql
-- Step 1: Stop using in code first, then:
ALTER TABLE users DROP COLUMN old_field;
```

### Add Index
```sql
-- V7__Add_user_email_index.sql
CREATE INDEX idx_users_email ON users(email);
```

That's it! Flyway handles the rest automatically.