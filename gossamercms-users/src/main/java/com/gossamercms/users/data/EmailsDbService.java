package com.gossamercms.users.data;

import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceAdapter;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.users.api.EmailDto;
import com.gossamercms.users.domain.Email;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService
public class EmailsDbService extends BaseDbService<Email, EmailDto> {

    public EmailsDbService(DataSourceManager dsManager) {
        super(Email.class, EmailDto.class, dsManager);
    }

    // -----------------------------
    //  Mapping
    // -----------------------------

    @Override
    protected Email mapToEntity(EmailDto dto) {
        if (dto == null) return null;

        return Email.builder()
                .id(dto.getId() != null ? dto.getId() : UUID.randomUUID())
                .userId(dto.getUserId())
                .email(dto.getEmail())
                .primary(dto.isPrimary())
                .status(dto.getStatus())
                .createdOn(dto.getCreatedOn())
                .build();
    }

    @Override
    protected EmailDto mapToDto(Email entity) {
        if (entity == null) return null;

        return EmailDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .primary(entity.isPrimary())
                .status(entity.getStatus())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    @Override
    protected EmailDto removeExcludedFields(EmailDto dto) {
        // No excluded fields for emails
        return dto;
    }

    // -----------------------------
    //  Bulk Replace (not used yet)
    // -----------------------------

    @Override
    public ListResultset<EmailDto> createOrReplaceBulk(
            UUID deletedBy,
            List<EmailDto> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        throw new UnsupportedOperationException("Bulk replace not implemented for emails");
    }

    // -----------------------------
    //  404 Handler
    // -----------------------------

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("Email not found: " + id);
    }

    // -----------------------------
    //  Custom Queries
    // -----------------------------

    public boolean emailExists(String email) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Map<String, Object> filter = Map.of("identifier", email);

        Map<String, Object> row = (Map<String, Object>) ds.findOne(meta.table(), filter);

        return row != null;
    }
}
//public boolean emailExists(String email) {
//    DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
//
//    Map<String, Object> filter = Map.of("identifier", email);
//
//    Map<String, Object> row = (Map<String, Object>) ds.findOne(meta.table(), filter);
//
//    return row != null;
//}