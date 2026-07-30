package com.gossamercms.mvc.converters;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.exceptions.NotFoundException;
import lombok.NonNull;

import java.util.UUID;

public abstract class BaseConverter<D extends DtoWithId> implements Converter<String, D> {

    protected final BaseDbService<?, D> dbService;

    private final Class<D> dtoClass;

    protected BaseConverter(BaseDbService<?, D> dbService, Class<D> dtoClass) {
        this.dbService = dbService;
        this.dtoClass = dtoClass;
        System.out.println("************ Converter created for " + dtoClass.getSimpleName() + " ************************");
    }

    @Override
    public D convert(@NonNull String source) {
        System.out.println("************ BaseConverter loading " + dtoClass.getSimpleName() + " with id " + source + " ************************");
        UUID id = UUID.fromString(source);
        D dto = dbService.getById(id);
        if (dto == null) {
            throw new NotFoundException(id.toString()); // or whatever your 404 exception is
        }
        return dto;
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(dtoClass);
    }

}

