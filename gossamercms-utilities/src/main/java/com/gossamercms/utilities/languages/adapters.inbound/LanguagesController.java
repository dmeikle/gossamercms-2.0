package com.gossamercms.utilities.languages.adapters.inbound;


import com.gossamercms.utilities.languages.dtos.LanguageDto;
import com.gossamercms.utilities.languages.handlers.LanguagesHandler;
import com.gossamercms.utilities.languages.models.Language;
import com.gossamercms.mvc.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/languages")
public class LanguagesController extends BaseController<Language, LanguageDto> {

    public LanguagesController(LanguagesHandler handler) {
        super(handler);
    }
}
