package com.gossamercms.users.handlers;

import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.users.api.AccountMappingDto;
import com.gossamercms.users.data.AccountMappingsDbService;
import com.gossamercms.users.domain.AccountMapping;

@ModuleHandler
public class AccountMappingsHandler extends BaseHandler<AccountMapping, AccountMappingDto> {

    public AccountMappingsHandler(AccountMappingsDbService db) {
        super(db);
    }
}

