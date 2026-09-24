package com.gossamercms.users.handlers;

import com.gossamercms.users.data.UserDevicesDbService;
import com.gossamercms.users.api.UserDeviceDto;
import com.gossamercms.users.domain.UserDevice;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class UserDevicesHandler extends BaseHandler<UserDevice, UserDeviceDto> {

    public UserDevicesHandler(UserDevicesDbService db) {
        super(db);
    }
}
