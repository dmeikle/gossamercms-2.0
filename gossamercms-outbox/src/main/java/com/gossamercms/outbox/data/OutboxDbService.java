package com.gossamercms.outbox.data;

import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.outbox.dtos.OutboxEventDto;
import com.gossamercms.outbox.models.OutboxEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService
public class OutboxDbService extends BaseDbService<OutboxEvent, OutboxEventDto> {

    public OutboxDbService(DataSourceManager ds) {
        super(OutboxEvent.class, OutboxEventDto.class, ds);
    }

    public ListResultset<OutboxEventDto> getPendingEvents() {
        return this.getAll(QueryOptions.builder()
                .filters(Map.of("status","PENDING"))
                .orderBy(Map.of("createdAt", "ASC"))
                .build());
    }

    @Override
    protected OutboxEvent mapToEntity(OutboxEventDto dto) {
        return dto.toEntity();
    }

    @Override
    protected OutboxEventDto mapToDto(OutboxEvent entity) {
        return entity.toDto();
    }

    @Override
    protected OutboxEventDto removeExcludedFields(OutboxEventDto dto) {
        return dto;
    }

    @Override
    public ListResultset<OutboxEventDto> createOrReplaceBulk(UUID deletedBy, List<OutboxEventDto> dtos, Map<String, Object> deleteExistingKey) {
        return null;
    }

    @Override
    protected void throw404(String id) {

    }
}
