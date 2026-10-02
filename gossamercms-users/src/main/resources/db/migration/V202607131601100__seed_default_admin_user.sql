-- Seed default admin user with complete profile
-- Password: SuperSecurePass123! (bcrypt hashed)
-- Note: Email is stored in login_identities table, not users table

-- Insert user
INSERT INTO users (
    id,
    firstname,
    lastname,
    status,
    "createdOn"
) VALUES (
             'b571d101-b9d3-42b7-ba48-118f9b5b5f3e'::uuid,
             'Admin',
             'ADMIN',
             'ACTIVE',
             NOW()
         ) ON CONFLICT DO NOTHING;

-- Link user to role
INSERT INTO user_roles (
    id,
    "userId",
    "roleId",
    "assignedAt"
) VALUES (
             gen_random_uuid(),
             'b571d101-b9d3-42b7-ba48-118f9b5b5f3e'::uuid,
             'ac4bfd3c-1f35-4d1d-8688-e4b062dda3f6'::uuid,
             NOW()
         ) ON CONFLICT DO NOTHING;

-- Insert user context (patient context)
INSERT INTO user_contexts (
    id,
    "userId",
    "contextType",
    metadata,
    "roleId",
    "createdAt",
    "isDefault"
) VALUES (
             'fe59cf0e-08cd-4e35-b152-a521fc81dcee'::uuid,
             'b571d101-b9d3-42b7-ba48-118f9b5b5f3e'::uuid,
             'admin',
             '{"theme":"dark","language":"en-US","timezone":"UTC","onboardingCompleted":false,"defaultContext":true,"homepage":"/dashboard-pages/front-desk-dashboard", "clinicId": "686d2af0-0e94-4863-a0cb-887be93c8778"}'::jsonb,
             'ac4bfd3c-1f35-4d1d-8688-e4b062dda3f6',
          NOW(),
             true
         ) ON CONFLICT DO NOTHING;

INSERT INTO user_contexts (id, "userId", "contextType", metadata, "createdAt", "roleId", "isDefault") VALUES
 ('5d9f516b-f218-4eea-831b-5164df6b263c', 'b571d101-b9d3-42b7-ba48-118f9b5b5f3e', 'default', '{"theme": "dark", "clinic": "e6b820a9-381e-471b-a884-be34cce9a033", "homepage": "/dashboard-pages/provider-dashboard", "language": "en-US", "timezone": "UTC", "defaultContext": true, "organizationId": "23bf84ee-1060-459c-82b2-a52d004af01f", "onboardingCompleted": false}', '2026-07-14 00:11:13.225612', 'ac4bfd3c-1f35-4d1d-8688-e4b062dda3f6', false);



-- Insert user address 1 (Shipping - Default)
INSERT INTO user_addresses (
    id,
    "userId",
    type,
    address1,
    address2,
    city,
    "stateProvince",
    "postalCode",
    "countryCode",
    "isDefault"
) VALUES (
             gen_random_uuid(),
             'b571d101-b9d3-42b7-ba48-118f9b5b5f3e'::uuid,
             'SHIPPING',
             '123 Main Street',
             'Unit 4B',
             'Vancouver',
             'BC',
             'V5K0A1',
             'CA',
             true
         ) ON CONFLICT DO NOTHING;

-- Insert user address 2 (Billing)
INSERT INTO user_addresses (
    id,
    "userId",
    type,
    address1,
    address2,
    city,
    "stateProvince",
    "postalCode",
    "countryCode",
    "isBilling"
) VALUES (
             gen_random_uuid(),
             'b571d101-b9d3-42b7-ba48-118f9b5b5f3e'::uuid,
             'BILLING',
             '500 Burrard Street',
             'Suite 1200',
             'Vancouver',
             'BC',
             'V6C3A6',
             'CA',
             true
         ) ON CONFLICT DO NOTHING;

-- Insert user telephone
INSERT INTO user_telephone (
    id,
    "userId",
    "countryCode",
    "numberRaw",
    "numberE164",
    type,
    verified,
    "smsOptIn",
    preferred,
    "createdOn"
) VALUES (
             gen_random_uuid(),
             'b571d101-b9d3-42b7-ba48-118f9b5b5f3e'::uuid,
             '+1',
             '604-123-1234',
             '+16041231234',
             'MOBILE',
             true,
             true,
             true,
             NOW()
         ) ON CONFLICT DO NOTHING;

-- Insert login identity (Auth0)
INSERT INTO login_identities (
    id,
    "userId",
    identifier,
    provider,
    "providerUserId",
    "isPrimary",
    type,
    "createdOn"
) VALUES (
             gen_random_uuid(),
             'b571d101-b9d3-42b7-ba48-118f9b5b5f3e'::uuid,
             'test.user1@example.com',
             'auth0',
             'auth0|6a49a876309ecf411451659b',
             true,
             'default',
             NOW()
         ) ON CONFLICT DO NOTHING;
