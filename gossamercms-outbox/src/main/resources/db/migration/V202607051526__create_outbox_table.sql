
CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               "aggregateType" VARCHAR(50) NOT NULL,
                               "aggregateId" UUID NOT NULL,
                               "eventType" VARCHAR(50) NOT NULL,
                               payload JSONB NOT NULL,

                               status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                               "retryCount" INT NOT NULL DEFAULT 0,
                               "errorMessage" TEXT,

                               "createdAt" TIMESTAMP NOT NULL DEFAULT now(),
                               "processedAt" TIMESTAMP
);

CREATE INDEX idx_outbox_status_created
    ON outbox_events(status, "createdAt");