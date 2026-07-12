package com.gossamercms.media.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MediaAggregateDto {
    private MediaDto media;
    private MediaLinkDto mediaLink;

}
