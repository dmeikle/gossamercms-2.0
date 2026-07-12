package com.gossamercms.users.handlers;


import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.users.api.UserTelephoneDto;
import com.gossamercms.users.data.UserTelephonesDbService;
import com.gossamercms.users.domain.UserTelephone;
import org.springframework.stereotype.Service;

@ModuleHandler
public class UserTelephonesHandler
        extends BaseHandler<UserTelephone, UserTelephoneDto> {

    public UserTelephonesHandler(UserTelephonesDbService db) {
        super(db);
    }
}