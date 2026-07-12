package com.gossamercms.users.handlers;

import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.users.api.EmailDto;
import com.gossamercms.users.data.EmailsDbService;
import com.gossamercms.users.domain.Email;
import org.springframework.stereotype.Service;

@ModuleHandler
public class EmailsHandler extends BaseHandler<Email, EmailDto> {

    private final EmailsDbService emailsDb;

    public EmailsHandler(EmailsDbService emailsDb) {
        super(emailsDb);
        this.emailsDb = emailsDb;
    }

    public boolean emailExists(String email) {
        return emailsDb.emailExists(email);
    }

}