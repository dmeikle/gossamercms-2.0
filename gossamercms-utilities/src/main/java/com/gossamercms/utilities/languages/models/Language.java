package com.gossamercms.utilities.languages.models;

import com.gossamercms.utilities.languages.dtos.LanguageDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Language implements BaseModel{
    private UUID id;
    private String code;
    private String name;

    public LanguageDto toDto() {
        return LanguageDto.builder()
            .id(id)
            .code(code)
            .name(name)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("languages")
    .column("id", UUID.class)
    .column("code", String.class, 10)
    .column("name", String.class, 100)
    .defaultSort("id desc")
    .build();



    @Override
    public ModelMeta meta() {
        return META;
    }

    @Override
    public ModelMeta metaOf() {
        return BaseModel.super.metaOf();
    }

}
