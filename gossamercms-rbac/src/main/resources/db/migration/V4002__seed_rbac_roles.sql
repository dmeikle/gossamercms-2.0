INSERT INTO roles (id, name, description, "createdAt")
VALUES
    ('ac4bfd3c-1f35-4d1d-8688-e4b062dda3f6', 'system-admin', 'Full system access', now()),
    ('4d37d9a9-012b-4fcb-ace1-f319f3c26601', 'content-manager', 'Manage content and media', now()),
    ('b720ee54-48aa-4909-bff1-9ddef786d27c', 'editor', 'Edit and publish content', now()),
    ('6e8c1f3c-dc9f-470d-b203-83a43154cbac', 'viewer', 'Read-only access', now());