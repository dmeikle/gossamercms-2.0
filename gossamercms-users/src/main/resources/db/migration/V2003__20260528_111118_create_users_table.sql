CREATE TABLE IF NOT EXISTS users (
    "id" UUID PRIMARY KEY,
    "firstname" VARCHAR(50),
    "lastname" VARCHAR(50),
    "status" VARCHAR(20),
    "ipAddress" VARCHAR(15),
    "createdOn" TIMESTAMP
);
CREATE INDEX if not exists idx_users_lastname ON users(lastname);
