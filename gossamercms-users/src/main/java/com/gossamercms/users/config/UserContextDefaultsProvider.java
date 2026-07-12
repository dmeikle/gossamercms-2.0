package com.gossamercms.users.config;


import java.util.Map;

public interface UserContextDefaultsProvider {
    Map<String, Object> forType(String contextType);
}


