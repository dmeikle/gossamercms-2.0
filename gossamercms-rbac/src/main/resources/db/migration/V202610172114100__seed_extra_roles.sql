
-- ============================================================
-- Medical CMS: Seed RBAC roles
-- PostgreSQL
-- All UUIDs are hardcoded and stable.
-- ============================================================

-- Organization / clinic administration
INSERT INTO public.roles
(id, name, description, "isSystem", "createdAt", "updatedAt")
VALUES
    (
        'a10e1c20-7b34-4c85-9d62-1e8a3f6b4101',
        'admin',
        'Full system administrator with unrestricted access.',
        true, NOW(), NOW()
    ) ON CONFLICT DO NOTHING;

-- Clinical providers
INSERT INTO public.roles
(id, name, description, "isSystem", "createdAt", "updatedAt")
VALUES
    (
        'b21f2d31-8c45-4d96-ae73-2f9b4a7c5202',
        'provider',
        'Healthcare provider authorized to access assigned patients, clinical records, assessments.',
        true, NOW(), NOW()
    ) ON CONFLICT DO NOTHING;

-- Patient portal access
INSERT INTO public.roles
(id, name, description, "isSystem", "createdAt", "updatedAt")
VALUES
    (
        'c32a3e42-9d56-4ea7-bf84-3a0c5b8d6303',
        'patient',
        'Patient portal user authorized to access their own permitted health information, appointments.',
        true, NOW(), NOW()
    ) ON CONFLICT DO NOTHING;

-- Billing and financial operations
INSERT INTO public.roles
(id, name, description, "isSystem", "createdAt", "updatedAt")
VALUES
    (
        'd43b4f53-ae67-4fb8-8095-4b1d6c9e7404',
        'billing-staff',
        'Manages authorized billing workflows, invoices, payment records, and billing-related reports.',
        true, NOW(), NOW()
    ) ON CONFLICT DO NOTHING;

-- Reception and appointment scheduling
INSERT INTO public.roles
(id, name, description, "isSystem", "createdAt", "updatedAt")
VALUES
    (
        'e54c5064-bf78-40c9-91a6-5c2e7d0f8505',
        'receptionist',
        'Manages authorized appointment scheduling, patient registration, contact information, check-in.',
        true, NOW(), NOW()
    ) ON CONFLICT DO NOTHING;

