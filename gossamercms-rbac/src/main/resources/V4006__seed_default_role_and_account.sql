-- Create default organization
INSERT INTO organizations (id, name, "createdAt")
VALUES (gen_random_uuid(), 'Default Organization', now())
    ON CONFLICT DO NOTHING;

-- Create default account
INSERT INTO accounts (id, "organizationId", label, type, "createdOn")
SELECT gen_random_uuid(), o.id, 'Default Account', 'primary', now()
FROM organizations o
WHERE o.name = 'Default Organization'
    ON CONFLICT DO NOTHING;