-- Migration: Genericize notification_jobs table to support multiple recipient types and channels
-- Removes patient/task-specific columns and adds generic recipient and channel fields

ALTER TABLE notification_jobs
  DROP COLUMN IF EXISTS "taskOccurrenceId",
  DROP COLUMN IF EXISTS "patientId";

ALTER TABLE notification_jobs
  ADD COLUMN "recipientId" UUID NOT NULL,
  ADD COLUMN "recipientType" VARCHAR(50) NOT NULL,
  ADD COLUMN "recipientContactId" UUID,
  ADD COLUMN "notificationChannel" VARCHAR(50),
  ADD COLUMN priority VARCHAR(20),
  ADD COLUMN metadata TEXT;

-- Create indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_notification_jobs_recipient_id ON notification_jobs("recipientId");
CREATE INDEX IF NOT EXISTS idx_notification_jobs_recipient_type ON notification_jobs("recipientType");
CREATE INDEX IF NOT EXISTS idx_notification_jobs_status_scheduled_at ON notification_jobs(status, "scheduledAt");
