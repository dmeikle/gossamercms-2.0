package com.gossamercms.utilities.languages.data;

import com.gossamercms.utilities.languages.dtos.LanguageDto;
import com.gossamercms.utilities.languages.models.Language;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@ModuleDbService
public class LanguagesDbService extends BaseDbService<Language, LanguageDto> {

    public LanguagesDbService(DataSourceManager ds) {
        super(Language.class, LanguageDto.class, ds);
    }

    @Override
    protected Language mapToEntity(LanguageDto dto) {
        return dto.toEntity();
    }

    @Override
    protected LanguageDto mapToDto(Language entity) {
        return entity.toDto();
    }

    @Override
    protected LanguageDto removeExcludedFields(LanguageDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {

    }


}
