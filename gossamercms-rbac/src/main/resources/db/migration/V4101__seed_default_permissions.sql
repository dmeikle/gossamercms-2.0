INSERT INTO permissions (
    id,
    name,
    description,
    "createdAt",
    "updatedAt"
)
VALUES
    (gen_random_uuid(), 'system.manage', 'Full system administration', now(), now()),
    (gen_random_uuid(), 'system.settings.view', 'View system settings', now(), now()),
    (gen_random_uuid(), 'system.settings.update', 'Modify system settings', now(), now()),

    (gen_random_uuid(), 'users.view', 'View users', now(), now()),
    (gen_random_uuid(), 'users.create', 'Create new users', now(), now()),
    (gen_random_uuid(), 'users.update', 'Update existing users', now(), now()),
    (gen_random_uuid(), 'users.delete', 'Delete users', now(), now()),
    (gen_random_uuid(), 'users.roles.assign', 'Assign roles to users', now(), now()),
    (gen_random_uuid(), 'users.roles.remove', 'Remove roles from users', now(), now()),

    (gen_random_uuid(), 'roles.view', 'View roles', now(), now()),
    (gen_random_uuid(), 'roles.create', 'Create roles', now(), now()),
    (gen_random_uuid(), 'roles.update', 'Update roles', now(), now()),
    (gen_random_uuid(), 'roles.delete', 'Delete roles', now(), now()),
    (gen_random_uuid(), 'roles.permissions.assign', 'Assign permissions to roles', now(), now()),
    (gen_random_uuid(), 'roles.permissions.remove', 'Remove permissions from roles', now(), now()),

    (gen_random_uuid(), 'content.view', 'View content', now(), now()),
    (gen_random_uuid(), 'content.create', 'Create content', now(), now()),
    (gen_random_uuid(), 'content.update', 'Update content', now(), now()),
    (gen_random_uuid(), 'content.delete', 'Delete content', now(), now()),
    (gen_random_uuid(), 'content.publish', 'Publish content', now(), now()),

    (gen_random_uuid(), 'media.view', 'View media library', now(), now()),
    (gen_random_uuid(), 'media.upload', 'Upload media files', now(), now()),
    (gen_random_uuid(), 'media.delete', 'Delete media files', now(), now()),

    (gen_random_uuid(), 'rbac.view', 'View RBAC configuration', now(), now()),
    (gen_random_uuid(), 'rbac.assign', 'Assign RBAC permissions', now(), now()),
    (gen_random_uuid(), 'rbac.revoke', 'Revoke RBAC permissions', now(), now()),

    (gen_random_uuid(), 'audit.view', 'View audit logs', now(), now()),

    (gen_random_uuid(), 'devtools.view', 'View developer tools', now(), now()),
    (gen_random_uuid(), 'devtools.execute', 'Execute developer tools', now(), now())

    ON CONFLICT (name)
DO UPDATE SET
    description = EXCLUDED.description,
           "updatedAt" = now();