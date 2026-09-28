package com.gossamercms.tests.api;

import java.util.List;

public record ControllerScenarioFile(
        List<EndpointScenario> scenarios
) {
}
