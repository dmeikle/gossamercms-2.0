package com.gossamercms.media.handlers;

import com.gossamercms.media.data.MediaLinksDbService;
import com.gossamercms.media.dtos.MediaLinkDto;
import com.gossamercms.media.models.MediaLink;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class MediaLinksHandler extends BaseHandler<MediaLink, MediaLinkDto> {
    public MediaLinksHandler(MediaLinksDbService dbService) {
        super(dbService);
    }
}
