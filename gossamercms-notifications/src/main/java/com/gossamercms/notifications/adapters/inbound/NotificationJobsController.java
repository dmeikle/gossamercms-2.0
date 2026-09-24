package com.gossamercms.notifications.adapters.inbound;


import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.notifications.handlers.NotificationJobsHandler;
import com.gossamercms.notifications.models.NotificationJob;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/notification_jobs")
public class NotificationJobsController extends BaseController<NotificationJob, NotificationJobDto> {

    public NotificationJobsController(NotificationJobsHandler handler) {
        super(handler);
    }
}
