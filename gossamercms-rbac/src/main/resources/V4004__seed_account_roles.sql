INSERT INTO account_roles (id, name, description, "createdAt")
VALUES
    (gen_random_uuid(), 'account-owner', 'Owner of the account', now()),
    (gen_random_uuid(), 'account-admin', 'Administrator of the account', now()),
    (gen_random_uuid(), 'account-member', 'Standard member', now()),
    (gen_random_uuid(), 'account-billing', 'Billing and subscription manager', now());