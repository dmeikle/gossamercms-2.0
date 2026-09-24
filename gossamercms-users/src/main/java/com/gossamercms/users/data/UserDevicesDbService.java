package com.gossamercms.users.data;

import com.gossamercms.users.api.UserDeviceDto;
import com.gossamercms.users.domain.UserDevice;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@ModuleDbService
public class UserDevicesDbService extends BaseDbService<UserDevice, UserDeviceDto> {

    public UserDevicesDbService(DataSourceManager ds) {
        super(UserDevice.class, UserDeviceDto.class, ds);
    }

    @Override
    protected UserDevice mapToEntity(UserDeviceDto dto) {
        return dto.toEntity();
    }

    @Override
    protected UserDeviceDto mapToDto(UserDevice entity) {
        return entity.toDto();
    }

    @Override
    protected UserDeviceDto removeExcludedFields(UserDeviceDto dto) {
        return dto;
    }


}
