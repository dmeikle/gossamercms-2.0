package com.gossamercms.users.adapters.inbound;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.handlers.UsersHandler;
import com.gossamercms.users.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController extends BaseController<User, UserDto> {

    public UsersController(UsersHandler handler) {
        super(handler);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from public endpoint";
    }

}