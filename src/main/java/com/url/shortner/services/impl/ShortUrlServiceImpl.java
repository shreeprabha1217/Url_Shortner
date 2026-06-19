package com.url.shortner.services.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.url.shortner.dtos.UrlStatsResponse;
import com.url.shortner.entities.UrlMapping;
import com.url.shortner.repository.UrlMappingRepository;
import com.url.shortner.services.ShortUrlService;
import com.url.shortner.util.Base62Encoder;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShortUrlServiceImpl implements ShortUrlService{

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Override
    public String createShortUrl(String originalUrl) {
    System.out.println("Original URL: " + originalUrl);
    UrlMapping urlMapping = new UrlMapping();
    urlMapping.setOriginalUrl(originalUrl);
    urlMapping.setCreatedAt(LocalDateTime.now());
    urlMapping.setClickCount(0L);

    urlMappingRepository.save(urlMapping);
     String shortCode =
            Base62Encoder.encode(urlMapping.getId());

    urlMapping.setShortCode(shortCode);

    urlMappingRepository.save(urlMapping);

    return "http://localhost:8080/" + shortCode;
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        UrlMapping mapping =
        urlMappingRepository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new RuntimeException("Short URL not found"));
                        mapping.setClickCount(mapping.getClickCount() + 1);

return mapping.getOriginalUrl();
    }

    @Override
    public UrlStatsResponse getStats(String shortCode) {
        UrlMapping mapping = urlMappingRepository
            .findByShortCode(shortCode)
            .orElseThrow(() ->
                    new RuntimeException("Short URL not found"));

    UrlStatsResponse response = new UrlStatsResponse();

    response.setShortCode(mapping.getShortCode());
    response.setOriginalUrl(mapping.getOriginalUrl());
    response.setClickCount(mapping.getClickCount());
    response.setCreatedAt(mapping.getCreatedAt());
    response.setExpiryAt(mapping.getExpiryAt());

    return response;
    }
    
}
