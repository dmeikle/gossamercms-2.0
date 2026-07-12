package com.gossamercms.media.handlers;

import com.gossamercms.media.dtos.MediaDto;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.media.data.MediaDbService;
import com.gossamercms.media.models.Media;

@ModuleHandler
public class MediaHandler extends BaseHandler<Media, MediaDto> {
    public MediaHandler(MediaDbService dbService) {
        super(dbService);
    }
}
