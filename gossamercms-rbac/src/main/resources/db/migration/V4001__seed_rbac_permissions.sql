INSERT INTO permissions (id, name, description, "createdAt")
VALUES
    (gen_random_uuid(), 'content.view', 'View content', now()),
    (gen_random_uuid(), 'content.create', 'Create content', now()),
    (gen_random_uuid(), 'content.update', 'Update content', now()),
    (gen_random_uuid(), 'content.delete', 'Delete content', now()),
    (gen_random_uuid(), 'content.publish', 'Publish content', now()),

    (gen_random_uuid(), 'media.view', 'View media', now()),
    (gen_random_uuid(), 'media.upload', 'Upload media', now()),
    (gen_random_uuid(), 'media.delete', 'Delete media', now()),

    (gen_random_uuid(), 'users.view', 'View users', now()),
    (gen_random_uuid(), 'users.manage', 'Manage users', now()),

    (gen_random_uuid(), 'roles.view', 'View roles', now()),
    (gen_random_uuid(), 'roles.manage', 'Manage roles', now()),

    (gen_random_uuid(), 'rbac.view', 'View RBAC configuration', now()),
    (gen_random_uuid(), 'rbac.manage', 'Manage RBAC configuration', now()),

    (gen_random_uuid(), 'audit.view', 'View audit logs', now());