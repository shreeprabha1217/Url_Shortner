package com.url.shortner.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.url.shortner.entities.UrlMapping;
import com.url.shortner.repository.UrlMappingRepository;
import com.url.shortner.util.SnowflakeIdGenerator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    // --- Existing tests ---

    @Test
    public void whenShortCodeNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/url/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Short URL not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void whenUrlStatsNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/url/stats/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Short URL not found"));
    }

    @Test
    public void whenCreateUrlWithEmptyPayload_shouldReturn400() throws Exception {
        String invalidPayload = "{\"longUrl\":\"\"}";

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details[0]").value("URL cannot be empty"));
    }

    // --- Expiration tests ---

    @Test
    public void whenCreateUrlWithTtlDays_shouldReturnExpiryAt() throws Exception {
        String payload = "{\"longUrl\":\"https://example.com\", \"ttlDays\": 7}";

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").exists())
                .andExpect(jsonPath("$.expiryAt").exists());
    }

    @Test
    public void whenTtlDaysExceedsMax_shouldReturn400() throws Exception {
        String payload = "{\"longUrl\":\"https://example.com\", \"ttlDays\": 400}";

        mockMvc.perform(post("/api/url/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    public void whenUrlIsExpired_redirectShouldReturn410() throws Exception {
        // Create an already-expired mapping directly in the DB
        long id = snowflakeIdGenerator.nextId();
        String shortCode = com.url.shortner.util.Base62Encoder.encode(id);

        UrlMapping expired = new UrlMapping();
        expired.setId(id);
        expired.setShortCode(shortCode);
        expired.setOriginalUrl("https://expired.example.com");
        expired.setCreatedAt(LocalDateTime.now().minusDays(5));
        expired.setExpiryAt(LocalDateTime.now().minusDays(1)); // expired yesterday
        expired.setClickCount(0L);
        urlMappingRepository.save(expired);

        mockMvc.perform(get("/api/url/" + shortCode))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410))
                .andExpect(jsonPath("$.error").value("Gone"))
                .andExpect(jsonPath("$.message").value("Short URL has expired"));
    }

    @Test
    public void whenUrlIsExpired_statsShouldReturn410() throws Exception {
        // Create an already-expired mapping directly in the DB
        long id = snowflakeIdGenerator.nextId();
        String shortCode = com.url.shortner.util.Base62Encoder.encode(id);

        UrlMapping expired = new UrlMapping();
        expired.setId(id);
        expired.setShortCode(shortCode);
        expired.setOriginalUrl("https://expired-stats.example.com");
        expired.setCreatedAt(LocalDateTime.now().minusDays(10));
        expired.setExpiryAt(LocalDateTime.now().minusDays(2)); // expired 2 days ago
        expired.setClickCount(0L);
        urlMappingRepository.save(expired);

        mockMvc.perform(get("/api/url/stats/" + shortCode))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410))
                .andExpect(jsonPath("$.error").value("Gone"));
    }
}
