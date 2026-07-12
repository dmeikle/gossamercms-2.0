package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@ModuleModel
@Getter
@Builder(toBuilder = true)
public class User implements BaseModel {


    private UUID id;
    private String firstname;
    private String lastname;
    private String status;
    private String ipAddress;
    private Instant createdOn;

    public static final ModelMeta META = ModelMeta.builder()
            .table("users")
            .datasource("postgres")
            .column("id", UUID.class)              // ⭐ REQUIRED
            .column("firstname", String.class, 50)
            .column("lastname", String.class, 50)
            .column("status", String.class, 20)
            .column("ipAddress", String.class, 15)
            .column("createdOn", Instant.class)
            .defaultSort("createdOn DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public static User createNew(String email, String password, String firstname, String lastname) {
        return User.builder()
                .id(UUID.randomUUID())             //  REQUIRED
                .firstname(firstname)
                .lastname(lastname)
                .status("ACTIVE")
                .createdOn(Instant.now())
                .build();
    }


    public UserDto toDto() {
        return UserDto.builder()
                .id(id)
                .firstname(firstname)
                .lastname(lastname)
                .status(status)
                .ipAddress(ipAddress)
                .createdOn(createdOn)
                .build();
    }

    public Map<String, Object> toClaims() {
        return Map.of(
                "firstname", firstname,
                "lastname", lastname
        );
    }
}