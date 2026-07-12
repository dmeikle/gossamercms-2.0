package com.gossamercms.auth.handlers;

import com.gossamercms.auth.data.LoginIdentityDbService;
import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.models.LoginIdentity;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class LoginIdentitiesHandler extends BaseHandler<LoginIdentity, LoginIdentityDto> {

    public LoginIdentitiesHandler(LoginIdentityDbService dbService) {
        super(dbService);
    }
}
