-- Drop indexes that reference userId
DROP INDEX IF EXISTS ix_account_mappings_userid;
DROP INDEX IF EXISTS ux_account_mappings_user_account_role;



-- Add FK to user_contexts
-- ALTER TABLE account_mappings
--     ADD CONSTRAINT fk_account_mappings_context
--         FOREIGN KEY ("userContextId")
--             REFERENCES user_contexts ("id")
--             ON DELETE CASCADE;

-- Recreate indexes
-- CREATE INDEX ix_account_mappings_usercontextid
--     ON account_mappings ("userContextId");


-- CREATE UNIQUE INDEX ux_account_mappings_context_account_role
--     ON account_mappings (
--                          "userContextId",
--                          "accountId",
--                          "roleId"
--         );

-- CREATE UNIQUE INDEX ux_account_mappings_default_context
--     ON account_mappings ("userContextId")
--     WHERE "isDefault" = TRUE;