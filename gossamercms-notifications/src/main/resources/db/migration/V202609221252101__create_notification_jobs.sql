CREATE TABLE notification_jobs (
    "id" UUID PRIMARY KEY,
    "taskOccurrenceId" UUID,
    "patientId" UUID,
    "notificationType" VARCHAR(50),
    "title" VARCHAR(255),
    "body" VARCHAR(1000),
    "scheduledAt" TIMESTAMP WITH TIME ZONE,
    "status" VARCHAR(30),
    "attemptCount" INTEGER,
    "nextAttemptAt" TIMESTAMP WITH TIME ZONE,
    "claimedAt" TIMESTAMP WITH TIME ZONE,
    "sentAt" TIMESTAMP WITH TIME ZONE,
    "lastError" VARCHAR(2000),
    "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    "updatedAt" TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
