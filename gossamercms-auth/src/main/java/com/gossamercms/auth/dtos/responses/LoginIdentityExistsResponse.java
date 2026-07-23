package com.gossamercms.auth.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginIdentityExistsResponse {
    private String email;
    private String userId;
    private String firstname;
    private String lastname;
    private String middlename;
}
