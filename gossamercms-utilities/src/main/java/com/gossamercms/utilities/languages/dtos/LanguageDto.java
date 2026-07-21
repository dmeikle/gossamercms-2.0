package com.gossamercms.utilities.languages.dtos;

import com.gossamercms.utilities.languages.models.Language;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDto implements DtoWithId{
    private UUID id;
    private String code;
    private String name;

    public Language toEntity() {
        return Language.builder()
            .id(id)
            .code(code)
            .name(name)
            .build();
    }
}
