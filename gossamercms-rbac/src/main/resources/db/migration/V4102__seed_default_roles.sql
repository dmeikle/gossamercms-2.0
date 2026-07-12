CREATE UNIQUE INDEX IF NOT EXISTS ux_roles_name
    ON roles(name);
INSERT INTO roles (id, name, description, "isSystem", "createdAt", "updatedAt")
SELECT
    gen_random_uuid(),
    v.name,
    v.description,
    v.isSystem,
    now(),
    now()
FROM (
         VALUES
             ('system-admin', 'Full system administrator with unrestricted access', true),
             ('content-manager', 'Manages content, media, and publishing workflows', false),
             ('editor', 'Creates and edits content but cannot publish or manage users', false),
             ('viewer', 'Read-only access to content and media', false)
     ) AS v(name, description, isSystem)
    ON CONFLICT (name)
DO UPDATE SET
    description = EXCLUDED.description,
           "isSystem" = EXCLUDED."isSystem",
           "updatedAt" = now();