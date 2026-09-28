package com.gossamercms.tests.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public record EndpointScenario(
        String name,
        String method,
        String uri,
        JsonNode requestBody,
        Map<String, String> headers,
        AuthenticatedUserSpec currentUser,
        EndpointExpectedResponse expected
) {
}
