package com.gossamercms.notifications.handlers;

import com.gossamercms.notifications.data.NotificationJobsDbService;
import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.notifications.models.NotificationJob;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class NotificationJobsHandler extends BaseHandler<NotificationJob, NotificationJobDto> {

    public NotificationJobsHandler(NotificationJobsDbService db) {
        super(db);
    }
}
