package com.gossamercms.users.converters;

import com.gossamercms.mvc.annotations.ModuleConverter;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.data.UsersDbService;
import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

@ModuleConverter
public class UserConverter implements Converter<String, UserDto> {

    private final UsersDbService usersDbService;

    public UserConverter(UsersDbService usersDbService) {
        this.usersDbService = usersDbService;
        System.out.println("***************************** UserConverter constructor *****************************");
    }

    @Override
    public UserDto convert(String source) {
System.out.println("***************************** UserConverter convert method called with source: " + source + " *****************************");
        UUID id = UUID.fromString(source);

        return usersDbService.getById(id);
    }
}
