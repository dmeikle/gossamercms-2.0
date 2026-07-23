package com.gossamercms.users.api.responses;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VerifyUserExistsResponse {

    private boolean matchFound;
    private String[] matchType;
    private boolean verificationRequired;
    private Candidate candidate;


    @Data
    @Builder
    public static class Candidate {
        private UUID userId;
        private String displayName;
        private String email;
        private String phone;
        private String dateOfBirth;
        private String accountStatus;
        private String userType;
    }
}
