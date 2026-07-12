WITH ar AS (SELECT id, name FROM account_roles),
     rr AS (SELECT id, name FROM roles)

-- ACCOUNT OWNER → system-admin
INSERT INTO accountrole_rbacrole (id, "accountRoleId", "rbacRoleId", "createdAt")
SELECT gen_random_uuid(), ar.id, rr.id, now()
FROM ar, rr
WHERE ar.name = 'account-owner'
  AND rr.name = 'system-admin';

-- ACCOUNT ADMIN → content-manager
INSERT INTO accountrole_rbacrole (id, "accountRoleId", "rbacRoleId", "createdAt")
SELECT gen_random_uuid(), ar.id, rr.id, now()
FROM ar, rr
WHERE ar.name = 'account-admin'
  AND rr.name = 'content-manager';

-- ACCOUNT MEMBER → editor
INSERT INTO accountrole_rbacrole (id, "accountRoleId", "rbacRoleId", "createdAt")
SELECT gen_random_uuid(), ar.id, rr.id, now()
FROM ar, rr
WHERE ar.name = 'account-member'
  AND rr.name = 'editor';

-- ACCOUNT BILLING → viewer (or billing-specific role if you add one later)
INSERT INTO accountrole_rbacrole (id, "accountRoleId", "rbacRoleId", "createdAt")
SELECT gen_random_uuid(), ar.id, rr.id, now()
FROM ar, rr
WHERE ar.name = 'account-billing'
  AND rr.name = 'viewer';