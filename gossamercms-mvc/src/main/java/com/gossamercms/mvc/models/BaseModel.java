package com.gossamercms.mvc.models;

public interface BaseModel {

    ModelMeta meta();

    static ModelMeta metaOf(Class<? extends BaseModel> modelClass) {
        try {
            return (ModelMeta) modelClass.getField("META").get(null);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Model class " + modelClass.getName() + " must define public static final ModelMeta META"
            );
        }
    }

    default ModelMeta metaOf() {
        return BaseModel.metaOf(this.getClass());
    }
}
