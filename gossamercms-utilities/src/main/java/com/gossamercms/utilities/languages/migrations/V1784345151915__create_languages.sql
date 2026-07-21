CREATE TABLE languages (
    "id" UUID PRIMARY KEY,
    "id" UUID,
    "code" VARCHAR(10),
    "name" VARCHAR(100),
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
