CREATE TABLE IF NOT EXISTS user_telephone (
    "id" UUID PRIMARY KEY,
    "userId" UUID,
    "countryCode" VARCHAR(3),
    "numberRaw" VARCHAR(15),
    "numberE164" VARCHAR(15),
    "type" VARCHAR(20),
    "verified" BOOLEAN,
    "smsOptIn" BOOLEAN,
    "preferred" BOOLEAN,
    "extension" VARCHAR(10),
    "createdOn" TIMESTAMP
);
