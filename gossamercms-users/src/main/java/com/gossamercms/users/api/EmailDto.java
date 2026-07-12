package com.gossamercms.users.api;


import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class EmailDto  implements DtoWithId {

    private UUID id;
    private UUID userId;
    private String email;
    private boolean primary;
    private String status;     // ACTIVE, VERIFIED, UNVERIFIED
    private String createdOn;
}
