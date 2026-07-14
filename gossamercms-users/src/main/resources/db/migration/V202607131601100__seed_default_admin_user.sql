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
             'Test',
             'User',
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
             '{"theme":"dark","language":"en-US","timezone":"UTC","onboardingCompleted":false,"defaultContext":true,"homepage":"/admin/dashboard"}'::jsonb,
             'ac4bfd3c-1f35-4d1d-8688-e4b062dda3f6',
          NOW(),
             true
         ) ON CONFLICT DO NOTHING;

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
             'test',
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
             'test',
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
             '604-555-0199',
             '+16045550199',
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
             'test.user64@example.com',
             'auth0',
             'auth0|6a49a876309ecf411451659b',
             true,
             'default',
             NOW()
         ) ON CONFLICT DO NOTHING;
