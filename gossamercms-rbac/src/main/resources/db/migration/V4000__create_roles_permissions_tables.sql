-- V1__create_rbac_tables.sql

CREATE TABLE IF NOT EXISTS permissions  (
                             "id" UUID PRIMARY KEY,
                             "name" VARCHAR(50) NOT NULL,
                             "description" VARCHAR(100),
                             "createdAt" TIMESTAMPTZ,
                             "updatedAt" TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS  ux_permissions_name
    ON permissions ("name");


CREATE TABLE IF NOT EXISTS roles (
                       "id" UUID PRIMARY KEY,
                       "name" VARCHAR(50) NOT NULL,
                       "description" VARCHAR(100),
                       "isSystem" BOOLEAN NOT NULL DEFAULT FALSE,
                       "createdAt" TIMESTAMPTZ,
                       "updatedAt" TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS  ux_roles_name
    ON roles ("name");


CREATE TABLE IF NOT EXISTS role_permissions (
                                  "id" UUID PRIMARY KEY,
                                  "roleId" UUID NOT NULL,
                                  "permissionId" UUID NOT NULL,
                                  "createdAt" TIMESTAMPTZ,

                                  CONSTRAINT fk_role_permissions_role
                                      FOREIGN KEY ("roleId")
                                          REFERENCES roles ("id")
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_role_permissions_permission
                                      FOREIGN KEY ("permissionId")
                                          REFERENCES permissions ("id")
                                          ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS  ux_role_permissions_role_permission
    ON role_permissions ("roleId", "permissionId");

CREATE INDEX IF NOT EXISTS  ix_role_permissions_roleid
    ON role_permissions ("roleId");

CREATE INDEX IF NOT EXISTS  ix_role_permissions_permissionid
    ON role_permissions ("permissionId");


CREATE TABLE IF NOT EXISTS user_roles (
                            "id" UUID PRIMARY KEY,
                            "userId" UUID NOT NULL,
                            "roleId" UUID NOT NULL,
                            "assignedBy" UUID,
                            "assignedAt" TIMESTAMPTZ,
                            "expiresAt" TIMESTAMPTZ,

                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY ("roleId")
                                    REFERENCES roles ("id")
                                    ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS  ux_user_roles_user_role
    ON user_roles ("userId", "roleId");

CREATE INDEX IF NOT EXISTS  ix_user_roles_userid
    ON user_roles ("userId");

CREATE INDEX IF NOT EXISTS  ix_user_roles_roleid
    ON user_roles ("roleId");

CREATE INDEX IF NOT EXISTS  ix_user_roles_assignedat
    ON user_roles ("assignedAt");