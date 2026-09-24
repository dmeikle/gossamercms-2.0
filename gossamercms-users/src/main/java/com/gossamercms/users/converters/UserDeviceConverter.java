package com.gossamercms.users.converters;

import com.gossamercms.users.data.UserDevicesDbService;
import com.gossamercms.users.api.UserDeviceDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;

import java.util.UUID;


@ModuleConverter
public class UserDeviceConverter
        extends BaseConverter<UserDeviceDto>
         implements Converter<String, UserDeviceDto> {

    public UserDeviceConverter(UserDevicesDbService dbService) {
        super(dbService, UserDeviceDto.class);
    }
}

