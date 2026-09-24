package com.gossamercms.notifications.converters;

import com.gossamercms.notifications.data.NotificationJobsDbService;
import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;


@ModuleConverter
public class NotificationJobConverter
        extends BaseConverter<NotificationJobDto>
         implements Converter<String, NotificationJobDto> {

    public NotificationJobConverter(NotificationJobsDbService dbService) {
        super(dbService, NotificationJobDto.class);
    }
}

