create table if not exists user_contexts
(
    id            uuid                    not null
        primary key,
    "userId"      uuid                    not null
        constraint user_contexts_userid_fkey
            references users,
    "contextType" text                    not null,
    metadata      jsonb,
    "createdAt"   timestamp default now() not null,
    "roleId"      uuid not null
        constraint user_contexts_roleid_fkey
            references roles
);


