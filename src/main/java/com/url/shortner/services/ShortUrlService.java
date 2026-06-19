package com.url.shortner.services;

import com.url.shortner.dtos.UrlStatsResponse;

public interface ShortUrlService {

    String createShortUrl(String originalUrl);

    public String getOriginalUrl(String shortCode);

    UrlStatsResponse getStats(String shortCode);
}
