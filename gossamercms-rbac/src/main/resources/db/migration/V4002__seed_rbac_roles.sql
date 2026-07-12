INSERT INTO roles (id, name, description, "createdAt")
VALUES
    (gen_random_uuid(), 'system-admin', 'Full system access', now()),
    (gen_random_uuid(), 'content-manager', 'Manage content and media', now()),
    (gen_random_uuid(), 'editor', 'Edit and publish content', now()),
    (gen_random_uuid(), 'viewer', 'Read-only access', now());