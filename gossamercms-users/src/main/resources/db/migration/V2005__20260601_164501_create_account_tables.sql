CREATE TABLE IF NOT EXISTS accounts (
                          "id" UUID PRIMARY KEY,
                          "organizationId" UUID NOT NULL,
                          "name" TEXT NOT NULL,
                          "type" TEXT NOT NULL,
                          "createdAt" TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS account_mappings (
                                  "id" UUID PRIMARY KEY,
                                  "userId" UUID NOT NULL,
                                  "accountId" UUID NOT NULL,
                                  "roleId" UUID NOT NULL,
                                  "isDefault" BOOLEAN NOT NULL DEFAULT FALSE,
                                  "createdAt" TIMESTAMP NOT NULL DEFAULT NOW(),
                                  "expiresAt" TIMESTAMP NULL,

                                  UNIQUE ("userId", "accountId"),

                                  FOREIGN KEY ("userId") REFERENCES users("id"),
                                  FOREIGN KEY ("accountId") REFERENCES accounts("id"),
                                  FOREIGN KEY ("roleId") REFERENCES roles("id")
);

CREATE TABLE IF NOT EXISTS user_contexts (
                               "id" UUID PRIMARY KEY,
                               "userId" UUID NOT NULL,
                               "contextType" TEXT NOT NULL,
                               "metadata" JSONB NULL,
                               "createdAt" TIMESTAMP NOT NULL DEFAULT NOW(),

                               FOREIGN KEY ("userId") REFERENCES users("id")
);