package com.gossamercms.media.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MediaSummaryDto {

    private UUID id;
    private String url;
}
