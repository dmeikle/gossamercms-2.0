CREATE TABLE if not exists refresh_tokens (
                               id UUID PRIMARY KEY,
                               token VARCHAR(512) NOT NULL,
                               username VARCHAR(255) NOT NULL,
                               "expiresAt" TIMESTAMP WITH TIME ZONE NOT NULL,
                               revoked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX if not exists idx_refresh_token_username
    ON refresh_tokens (username);

CREATE UNIQUE INDEX if not exists idx_refresh_token_token
    ON refresh_tokens (token);

CREATE INDEX if not exists idx_refresh_token_expires_at
    ON refresh_tokens ("expiresAt");