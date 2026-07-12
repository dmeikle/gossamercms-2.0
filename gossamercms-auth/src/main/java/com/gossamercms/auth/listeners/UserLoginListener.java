package com.gossamercms.auth.listeners;

import com.gossamercms.auth.data.LoginIdentityDbService;
import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.mvc.annotations.ModuleEventLister;
import gossamercms.globals.events.users.UserLoggedInEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;

@ModuleEventLister
public class UserLoginListener {

    private final LoginIdentityDbService loginIdentityDbService;

    public UserLoginListener(LoginIdentityDbService loginIdentityDbService) {
        this.loginIdentityDbService = loginIdentityDbService;
        System.out.println("UserLoginListener initialized");
    }

    @EventListener
    public void on(UserLoggedInEvent event) {
        System.out.println("UserLoggedInEvent received for user: " + event.userId());

        loginIdentityDbService.update(
                event.userId(),
                LoginIdentityDto.builder()
                        .userId(event.userId())
                        .lastLoginAt(event.loginTime())
                        .build(),
                Map.of("userId", event.userId())
        );
System.out.println("Updated last login time for user: " + event.userId());
    }
}
