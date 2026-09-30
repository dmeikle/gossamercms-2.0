package com.gossamercms.mvc.filters;

import com.gossamercms.mvc.handlers.DefaultResponseStatusAdvice;
import com.gossamercms.mvc.interceptors.DefaultResponseStatusMetadataInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DefaultResponseStatusFilterTest {

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new DefaultResponseStatusAdvice())
            .addInterceptors(new DefaultResponseStatusMetadataInterceptor())
            .addFilters(new DefaultResponseStatusFilter())
            .build();

    @Test
    void postDefaultsToCreated() throws Exception {
        mockMvc.perform(post("/status-test/resources"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"result\":\"created\"}"));
    }

    @Test
    void deleteDefaultsToNoContent() throws Exception {
        mockMvc.perform(delete("/status-test/resources/123"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void getRemainsOk() throws Exception {
        mockMvc.perform(get("/status-test/resources/123"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"result\":\"fetched\"}"));
    }

    @Test
    void putRemainsOk() throws Exception {
        mockMvc.perform(put("/status-test/resources/123"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"result\":\"updated\"}"));
    }

    @Test
    void explicitResponseStatusIsPreserved() throws Exception {
        mockMvc.perform(post("/status-test/login"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"result\":\"authenticated\"}"));
    }

    @Test
    void responseEntityStatusIsPreserved() throws Exception {
        mockMvc.perform(post("/status-test/accepted"))
                .andExpect(status().isAccepted())
                .andExpect(content().json("{\"result\":\"queued\"}"));
    }

    @RestController
    @RequestMapping("/status-test")
    static class TestController {

        @GetMapping("/resources/123")
        Map<String, String> getResource() {
            return Map.of("result", "fetched");
        }

        @PostMapping("/resources")
        Map<String, String> createResource() {
            return Map.of("result", "created");
        }

        @PutMapping("/resources/123")
        Map<String, String> updateResource() {
            return Map.of("result", "updated");
        }

        @DeleteMapping("/resources/123")
        void deleteResource() {
        }

        @PostMapping("/login")
        @ResponseStatus(HttpStatus.OK)
        Map<String, String> login() {
            return Map.of("result", "authenticated");
        }

        @PostMapping("/accepted")
        ResponseEntity<Map<String, String>> accepted() {
            return ResponseEntity.accepted().body(Map.of("result", "queued"));
        }
    }
}
