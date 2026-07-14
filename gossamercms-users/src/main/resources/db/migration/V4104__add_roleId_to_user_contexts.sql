ALTER TABLE user_contexts
    ADD COLUMN "roleId" uuid;

ALTER TABLE user_contexts
    ADD CONSTRAINT fk_user_contexts_role
        FOREIGN KEY ("roleId")
            REFERENCES roles(id)
            ON DELETE RESTRICT;
