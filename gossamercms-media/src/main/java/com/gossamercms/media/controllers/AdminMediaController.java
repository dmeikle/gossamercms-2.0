package com.gossamercms.media.controllers;

import com.gossamercms.media.dtos.MediaDto;
import com.gossamercms.media.handlers.MediaHandler;
import com.gossamercms.media.services.MediaService;
import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.media.models.Media;
import com.gossamercms.security.annotations.CurrentUser;
import com.gossamercms.security.jwt.JwtUser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


@RestController
@RequestMapping("/admin/media")
public class AdminMediaController extends BaseController<Media, MediaDto> {
    protected final MediaService mediaService;

    public AdminMediaController(MediaHandler handler,
                                MediaService mediaService) {
        super(handler);
        this.mediaService = mediaService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public MediaDto upload(
            @RequestParam("file") MultipartFile file,
            @CurrentUser JwtUser jwtUser
    ) throws IOException {
//        BufferedImage image =
//                ImageIO.read(file.getInputStream());
//
//        Integer width = image != null ? image.getWidth() : null;
//        Integer height = image != null ? image.getHeight() : null;
        return mediaService.upload(
                jwtUser.getUserId(),
                file
        );
    }
}
