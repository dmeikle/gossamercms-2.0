CREATE TABLE IF NOT EXISTS "emails" (
                                        "id" UUID PRIMARY KEY,
                                        "userId" UUID NOT NULL,
                                        "email" VARCHAR(255) NOT NULL,
    "primary" BOOLEAN NOT NULL DEFAULT FALSE,
    "status" VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    "createdOn" TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT "fk_emails_user"
    FOREIGN KEY ("userId")
    REFERENCES "users"("id")
    ON DELETE CASCADE
    );
