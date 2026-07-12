package com.gossamercms.users.converters;

import com.gossamercms.mvc.annotations.ModuleConverter;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.data.UserContextsDbService;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

@ModuleConverter
public class UserContextConverter implements Converter<String, UserContextDto> {

    private final UserContextsDbService userContextsDbService;

    public UserContextConverter(UserContextsDbService userContextsDbService) {
        this.userContextsDbService = userContextsDbService;
        System.out.println("***************************** UserContextConverter constructor *****************************");
    }

    @Override
    public UserContextDto convert(String source) {
        System.out.println("***************************** UserContextConverter convert method called with source: " + source + " *****************************");
        UUID id = UUID.fromString(source);

        return userContextsDbService.getById(id);
    }
}
