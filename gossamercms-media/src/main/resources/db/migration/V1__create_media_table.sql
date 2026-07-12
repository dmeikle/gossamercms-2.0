CREATE TABLE media (
                       "id" UUID PRIMARY KEY,

                       "filename" VARCHAR(255) NOT NULL,
                       "originalFilename" VARCHAR(255) NOT NULL,

                       "mimeType" VARCHAR(100) NOT NULL,

                       "storageProvider" VARCHAR(50) NOT NULL,
                       "storageKey" VARCHAR(500) NOT NULL,

                       "sizeBytes" BIGINT NOT NULL,

                       "width" INTEGER,
                       "height" INTEGER,

                       "createdAt" TIMESTAMPTZ NOT NULL,
                       "createdBy" UUID
);

CREATE INDEX idx_media_created_by
    ON media ("createdBy");