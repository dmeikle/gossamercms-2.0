CREATE TABLE IF NOT EXISTS login_identities (
    "id" UUID PRIMARY KEY,
    "userId" UUID,
    "type" VARCHAR(50),
    "identifier" VARCHAR(150),
    "passwordHash" VARCHAR(200),
    "provider" VARCHAR(100),
    "providerUserId" VARCHAR(150),
    "isPrimary" BOOLEAN,
    "createdOn" TIMESTAMP,
    "lastLoginAt" TIMESTAMP
);
