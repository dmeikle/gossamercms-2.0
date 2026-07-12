WITH system_user AS (
    SELECT id
    FROM users
    WHERE firstname = 'System'
      AND lastname = 'Administrator'
    LIMIT 1
    ),
    system_role AS (
SELECT "id"
FROM roles
WHERE "name" = 'system-admin'
    LIMIT 1
    ),
    system_account AS (
INSERT INTO accounts (
    "id",
    "organizationId",
    "name",
    "type",
    "createdAt"
)
VALUES (
    gen_random_uuid(),
    NULL,
    'System Account',
    'SYSTEM',
    now()
    )
    RETURNING "id"
    ),
    admin_context AS (
INSERT INTO user_contexts (
    "id",
    "userId",
    "contextType",
    "metadata",
    "createdAt"
)
SELECT
    gen_random_uuid(),
    u.id,
    'admin',
    '{}'::jsonb,
    now()
FROM system_user u
    RETURNING "id"
    )
INSERT INTO account_mappings (
    "id",
    "userContextId",
    "accountId",
    "roleId",
    "isDefault",
    "createdAt",
    "expiresAt"
)
SELECT
    gen_random_uuid(),
    c.id,
    a.id,
    r.id,
    TRUE,
    now(),
    NULL
FROM admin_context c
         CROSS JOIN system_account a
         CROSS JOIN system_role r;