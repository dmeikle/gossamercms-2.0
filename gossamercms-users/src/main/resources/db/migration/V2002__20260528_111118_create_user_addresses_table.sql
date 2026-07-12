CREATE TABLE user_addresses (
                                id              UUID PRIMARY KEY,
                                "userId"        UUID NOT NULL,

                                type            VARCHAR(50) NOT NULL,

                                address1        VARCHAR(255) NOT NULL,
                                address2        VARCHAR(255),

                                city            VARCHAR(100),
                                "stateProvince" VARCHAR(100),
                                "postalCode"    VARCHAR(20),
                                "countryCode"   VARCHAR(10),

                                "isDefault"     BOOLEAN NOT NULL DEFAULT FALSE,
                                "isBilling"     BOOLEAN NOT NULL DEFAULT FALSE

    -- If you want timestamps later:
    -- "createdAt" TIMESTAMP NOT NULL DEFAULT now(),
    -- "updatedAt" TIMESTAMP
);