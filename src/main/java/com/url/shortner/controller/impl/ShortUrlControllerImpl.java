package com.url.shortner.controller.impl;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.url.shortner.controller.ShortUrlController;
import com.url.shortner.dtos.ShortenUrlResponse;
import com.url.shortner.dtos.ShortnerUrlRequest;
import com.url.shortner.dtos.UrlStatsResponse;
import com.url.shortner.services.ShortUrlService;

import jakarta.validation.Valid;

@RestController
public class ShortUrlControllerImpl implements ShortUrlController {

    @Autowired
    private ShortUrlService shortUrlService;

    @Override
    public ResponseEntity<ShortenUrlResponse> createShortUrl(@Valid ShortnerUrlRequest request) {
        ShortenUrlResponse response = shortUrlService.createShortUrl(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> redirect(String shortCode) {
    String originalUrl =
            shortUrlService.getOriginalUrl(shortCode);

    return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(originalUrl))
            .build();
    }

    @Override
    public ResponseEntity<UrlStatsResponse> getStats(String shortCode) {
        return ResponseEntity.ok(
            shortUrlService.getStats(shortCode)
    );
    }
    
}
