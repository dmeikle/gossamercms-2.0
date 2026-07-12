package com.gossamercms.auth.models;


import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role implements BaseModel {

    private UUID id;
    private String name;
    private String description;
    private boolean isSystem;
    private Instant createdAt;
    private Instant updatedAt;

    public static final ModelMeta META = ModelMeta.builder()
            .table("roles")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("name", String.class, 50)
            .column("description", String.class, 100)
            .column("isSystem", Boolean.class)
            .column("createdAt", Instant.class)
            .column("updatedAt", Instant.class)
            .defaultSort("name asc")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }
}