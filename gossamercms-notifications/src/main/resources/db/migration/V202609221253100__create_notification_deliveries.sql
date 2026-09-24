

CREATE INDEX idx_notification_jobs_due
    ON notification_jobs(status, "scheduledAt");


CREATE TABLE notification_deliveries (
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

     "notificationJobId" UUID NOT NULL
         REFERENCES notification_jobs(id),

     "userDeviceId" UUID NOT NULL
         REFERENCES user_devices(id),

     "attemptNumber" INTEGER NOT NULL DEFAULT 1,

     status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

     "firebaseMessageId" TEXT,

     "attemptedAt" TIMESTAMPTZ,

     "acceptedAt" TIMESTAMPTZ,

     "deliveredAt" TIMESTAMPTZ,

     "openedAt" TIMESTAMPTZ,

     "errorCode" VARCHAR(100),

     "errorMessage" TEXT,

     "createdAt" TIMESTAMPTZ NOT NULL DEFAULT NOW(),

     CONSTRAINT uq_delivery_attempt
         UNIQUE (
                 "notificationJobId",
                 "userDeviceId",
                 "attemptNumber"
             )
);

CREATE INDEX idx_notification_deliveries_job
    ON notification_deliveries("notificationJobId");