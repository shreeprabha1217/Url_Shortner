package com.url.shortner.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortenUrlResponse {
    private String shortUrl;
    private LocalDateTime expiryAt;
}
