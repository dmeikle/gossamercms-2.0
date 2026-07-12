package com.gossamercms.users.handlers;

import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.users.api.AddressDto;
import com.gossamercms.users.data.UserAddressDbService;
import com.gossamercms.users.domain.Address;

@ModuleHandler
public class UserAddressesHandler extends BaseHandler<Address, AddressDto> {
    public UserAddressesHandler(UserAddressDbService dbService) {
        super(dbService);
    }
}
