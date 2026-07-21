package com.gossamercms.utilities.languages.converters;

import com.gossamercms.utilities.languages.data.LanguagesDbService;
import com.gossamercms.utilities.languages.dtos.LanguageDto;
import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.convert.converter.Converter;
import com.gossamercms.mvc.converters.BaseConverter;



@ModuleConverter
public class LanguageConverter
        extends BaseConverter<LanguageDto>
         implements Converter<String, LanguageDto> {

    public LanguageConverter(LanguagesDbService dbService) {
        super(dbService, LanguageDto.class);
    }
}

