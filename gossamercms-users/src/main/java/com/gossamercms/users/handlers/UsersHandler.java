package com.gossamercms.users.handlers;

import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.users.api.UserDetailDto;
import com.gossamercms.users.api.UserDirectoryDto;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.data.UsersDbService;
import com.gossamercms.users.domain.User;

import java.util.UUID;


@ModuleHandler
public class UsersHandler extends BaseHandler<User, UserDto> {

    public UsersHandler(UsersDbService db) {
        super(db);
    }

    public ListResultset<UserDirectoryDto> directory(QueryOptions options) {
        return ((UsersDbService) db).directory(options);
    }

    public UserDetailDto getUserDetail(UUID userId) {
        return ((UsersDbService) db).getUserDetail(userId);
    }
}