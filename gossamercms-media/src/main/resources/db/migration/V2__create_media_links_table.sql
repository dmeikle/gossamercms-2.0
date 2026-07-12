CREATE TABLE media_links (
                             "id" UUID PRIMARY KEY,

                             "mediaId" UUID NOT NULL,

                             "entityType" VARCHAR(100) NOT NULL,

                             "entityId" UUID NOT NULL,

                             "sortOrder" INTEGER DEFAULT 0,

                             "isPrimary" BOOLEAN DEFAULT FALSE,

                             "createdAt" TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_media_links_entity
    ON media_links ("entityType", "entityId");

CREATE INDEX idx_media_links_media
    ON media_links ("mediaId");