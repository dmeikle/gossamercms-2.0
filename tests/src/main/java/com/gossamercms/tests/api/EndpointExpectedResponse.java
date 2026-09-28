package com.gossamercms.tests.api;

import com.fasterxml.jackson.databind.JsonNode;

public record EndpointExpectedResponse(
        int status,
        JsonNode body,
        Boolean strictJson
) {
}
