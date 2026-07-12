-- ============================================================
-- SYSTEM ADMIN → ALL PERMISSIONS
-- ============================================================
INSERT INTO role_permissions (id, "roleId", "permissionId", "createdAt")
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    now()
FROM roles r
         JOIN permissions p ON TRUE
WHERE r.name = 'system-admin'
    ON CONFLICT ("roleId", "permissionId") DO NOTHING;


-- ============================================================
-- CONTENT MANAGER
-- ============================================================
INSERT INTO role_permissions (id, "roleId", "permissionId", "createdAt")
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    now()
FROM roles r
         JOIN permissions p ON p.name IN (
                                          'content.view',
                                          'content.create',
                                          'content.update',
                                          'content.delete',
                                          'content.publish',
                                          'media.view',
                                          'media.upload',
                                          'media.delete',
                                          'audit.view',
                                          'users.view',
                                          'roles.view',
                                          'rbac.view'
    )
WHERE r.name = 'content-manager'
    ON CONFLICT ("roleId", "permissionId") DO NOTHING;


-- ============================================================
-- EDITOR
-- ============================================================
INSERT INTO role_permissions (id, "roleId", "permissionId", "createdAt")
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    now()
FROM roles r
         JOIN permissions p ON p.name IN (
                                          'content.view',
                                          'content.create',
                                          'content.update',
                                          'content.publish',
                                          'media.view',
                                          'media.upload'
    )
WHERE r.name = 'editor'
    ON CONFLICT ("roleId", "permissionId") DO NOTHING;


-- ============================================================
-- VIEWER
-- ============================================================
INSERT INTO role_permissions (id, "roleId", "permissionId", "createdAt")
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    now()
FROM roles r
         JOIN permissions p ON p.name IN (
                                          'content.view',
                                          'media.view'
    )
WHERE r.name = 'viewer'
    ON CONFLICT ("roleId", "permissionId") DO NOTHING;