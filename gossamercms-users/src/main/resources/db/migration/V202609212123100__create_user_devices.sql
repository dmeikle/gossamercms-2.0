CREATE TABLE user_devices (
  "id" UUID PRIMARY KEY,
  "userId" UUID,
  "firebaseToken" VARCHAR(500),
  "platform" VARCHAR(20),
  "deviceId" VARCHAR(255),
  "appVersion" VARCHAR(50),
  "active" BOOLEAN,
  "lastSeenAt" TIMESTAMP WITH TIME ZONE,
  "createdAt" TIMESTAMP WITH TIME ZONE,
  "updatedAt" TIMESTAMP WITH TIME ZONE,    -- ADD COMMA HERE
  CONSTRAINT uq_patient_device_token
      UNIQUE ("firebaseToken")              -- MATCH COLUMN NAME (with quotes, camelCase)
)