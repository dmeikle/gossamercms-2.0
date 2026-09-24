package com.gossamercms.notifications.data;

import com.gossamercms.mvc.data.DataSourceAdapter;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.notifications.models.NotificationJob;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;

import java.util.List;
import java.util.Map;


@ModuleDbService
public class NotificationJobsDbService extends BaseDbService<NotificationJob, NotificationJobDto> {

    public NotificationJobsDbService(DataSourceManager ds) {
        super(NotificationJob.class, NotificationJobDto.class, ds);
    }

    @Override
    protected NotificationJob mapToEntity(NotificationJobDto dto) {
        return dto.toEntity();
    }

    @Override
    protected NotificationJobDto mapToDto(NotificationJob entity) {
        return entity.toDto();
    }

    @Override
    protected NotificationJobDto removeExcludedFields(NotificationJobDto dto) {
        return dto;
    }



}
