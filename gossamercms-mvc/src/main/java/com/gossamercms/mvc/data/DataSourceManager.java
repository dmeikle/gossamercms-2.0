package com.gossamercms.mvc.data;

import java.util.HashMap;
import java.util.Map;

public class DataSourceManager {

    private final Map<String, DataSourceAdapter> sources = new HashMap<>();

    public void register(String key, DataSourceAdapter adapter) {
        sources.put(key, adapter);
    }

    public DataSourceAdapter get(String key) {
        return sources.get(key);
    }
}