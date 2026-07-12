package com.url.shortner.services;

import com.url.shortner.dtos.ShortnerUrlRequest;
import com.url.shortner.dtos.ShortenUrlResponse;
import com.url.shortner.dtos.UrlStatsResponse;

public interface ShortUrlService {

    ShortenUrlResponse createShortUrl(ShortnerUrlRequest request);

    String getOriginalUrl(String shortCode);

    UrlStatsResponse getStats(String shortCode);

    void deleteExpiredUrls();
}
