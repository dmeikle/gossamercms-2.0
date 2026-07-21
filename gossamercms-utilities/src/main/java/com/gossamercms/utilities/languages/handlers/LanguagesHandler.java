package com.gossamercms.utilities.languages.handlers;

import com.gossamercms.utilities.languages.data.LanguagesDbService;
import com.gossamercms.utilities.languages.dtos.LanguageDto;
import com.gossamercms.utilities.languages.models.Language;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;

@ModuleHandler
public class LanguagesHandler extends BaseHandler<Language, LanguageDto> {

    public LanguagesHandler(LanguagesDbService db) {
        super(db);
    }
}
