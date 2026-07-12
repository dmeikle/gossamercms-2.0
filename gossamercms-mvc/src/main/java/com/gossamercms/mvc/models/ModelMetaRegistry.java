package com.gossamercms.mvc.models;


import java.util.*;

public class ModelMetaRegistry {

    private final Map<String, ModelMeta> registry = new HashMap<>();

    public void register(ModelMeta meta) {
        registry.put(meta.table(), meta);
    }

    public ModelMeta get(String table) {
        return registry.get(table);
    }

    public Collection<ModelMeta> all() {
        return registry.values();
    }
}
