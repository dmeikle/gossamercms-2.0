package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.EmailDto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@ModuleModel
@Data
@Builder
public class Email implements BaseModel {

    private UUID id;
    private UUID userId;
    private String email;
    private boolean primary;
    private String status;
    private String createdOn;

    public static final ModelMeta META = ModelMeta.builder()
            .table("emails")
            .datasource("postgres")
            .column("id", UUID.class)                // primary key
            .column("user_id", UUID.class)
            .column("email", String.class)
            .column("primary", Boolean.class)
            .column("status", String.class)
            .column("created_on", String.class)
            .defaultSort("created_on DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public EmailDto toDto() {
        return EmailDto.builder()
                .id(id)
                .userId(userId)
                .email(email)
                .primary(primary)
                .status(status)
                .createdOn(createdOn)
                .build();
    }

    public static Email fromDto(EmailDto dto) {
        return Email.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .email(dto.getEmail())
                .primary(dto.isPrimary())
                .status(dto.getStatus())
                .createdOn(dto.getCreatedOn())
                .build();
    }
}