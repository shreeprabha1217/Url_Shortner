package com.url.shortner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.url.shortner.dtos.ShortenUrlResponse;
import com.url.shortner.dtos.ShortnerUrlRequest;
import com.url.shortner.dtos.UrlStatsResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/url")
public interface ShortUrlController {


    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> createShortUrl(
    @Valid @RequestBody ShortnerUrlRequest request);

    @GetMapping("/{shortCode}")
public ResponseEntity<Void> redirect(
        @PathVariable String shortCode);

        @GetMapping("/stats/{shortCode}")
public ResponseEntity<UrlStatsResponse> getStats(
        @PathVariable String shortCode);
} 


    


