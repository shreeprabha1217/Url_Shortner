package com.url.shortner.services.impl;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.url.shortner.dtos.ShortnerUrlRequest;
import com.url.shortner.dtos.ShortenUrlResponse;
import com.url.shortner.dtos.UrlStatsResponse;
import com.url.shortner.entities.UrlMapping;
import com.url.shortner.exception.UrlExpiredException;
import com.url.shortner.exception.UrlNotFoundException;
import com.url.shortner.repository.UrlMappingRepository;
import com.url.shortner.services.ShortUrlService;
import com.url.shortner.util.Base62Encoder;
import com.url.shortner.util.SnowflakeIdGenerator;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShortUrlServiceImpl implements ShortUrlService {

    @Value("${url.shortner.default-ttl-days:30}")
    private int defaultTtlDays;

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public ShortenUrlResponse createShortUrl(ShortnerUrlRequest request) {
        System.out.println("Original URL: " + request.getLongUrl());

        int ttl = (request.getTtlDays() != null) ? request.getTtlDays() : defaultTtlDays;
        LocalDateTime expiryAt = LocalDateTime.now().plusDays(ttl);

        long id = snowflakeIdGenerator.nextId();
        String shortCode = Base62Encoder.encode(id);

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setId(id);
        urlMapping.setShortCode(shortCode);
        urlMapping.setOriginalUrl(request.getLongUrl());
        urlMapping.setCreatedAt(LocalDateTime.now());
        urlMapping.setClickCount(0L);
        urlMapping.setExpiryAt(expiryAt);

        urlMappingRepository.save(urlMapping);

        String shortUrl = "http://localhost:8080/" + shortCode;
        return new ShortenUrlResponse(shortUrl, expiryAt);
    }

   @Override
public String getOriginalUrl(String shortCode) {

    // 1. Check Redis
    String originalUrl = redisTemplate.opsForValue().get(shortCode);

    if (originalUrl != null) {

        System.out.println("Cache Hit");

        // Still update analytics
        urlMappingRepository.incrementClickCount(shortCode);

        return originalUrl;
    }

    System.out.println("Cache Miss");

    // 2. Fetch from DB
    UrlMapping mapping = urlMappingRepository
            .findByShortCode(shortCode)
            .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));

    if (mapping.getExpiryAt() != null &&
        LocalDateTime.now().isAfter(mapping.getExpiryAt())) {

        throw new UrlExpiredException("Short URL has expired");
    }

    // 3. Update click count
    mapping.setClickCount(mapping.getClickCount() + 1);

    // 4. Store in Redis
    redisTemplate.opsForValue().set(
            shortCode,
            mapping.getOriginalUrl(),
            Duration.ofHours(1)
    );

    // 5. Return URL
    return mapping.getOriginalUrl();
}
    @Override
    public UrlStatsResponse getStats(String shortCode) {
        UrlMapping mapping = urlMappingRepository
                .findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));

        if (mapping.getExpiryAt() != null && LocalDateTime.now().isAfter(mapping.getExpiryAt())) {
            throw new UrlExpiredException("Short URL has expired");
        }

        UrlStatsResponse response = new UrlStatsResponse();
        response.setShortCode(mapping.getShortCode());
        response.setOriginalUrl(mapping.getOriginalUrl());
        response.setClickCount(mapping.getClickCount());
        response.setCreatedAt(mapping.getCreatedAt());
        response.setExpiryAt(mapping.getExpiryAt());

        return response;
    }

    @Override
    public void deleteExpiredUrls() {
        urlMappingRepository.deleteByExpiryAtBefore(LocalDateTime.now());
    }
}
