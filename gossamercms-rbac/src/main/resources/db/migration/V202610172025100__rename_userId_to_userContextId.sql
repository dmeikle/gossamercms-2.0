TRUNCATE TABLE user_roles;
ALTER TABLE user_roles
    RENAME COLUMN "userId" TO "userContextId";


ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_contexts_user_role
        FOREIGN KEY ("userContextId")
            REFERENCES user_contexts(id)
            ON DELETE RESTRICT;
