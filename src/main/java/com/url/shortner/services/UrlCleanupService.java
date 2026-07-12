package com.url.shortner.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UrlCleanupService {

    private static final Logger log = LoggerFactory.getLogger(UrlCleanupService.class);

    @Autowired
    private ShortUrlService shortUrlService;

    // Runs every day at midnight by default; override via scheduling config
    @Scheduled(cron = "${url.shortner.cleanup-cron:0 0 0 * * *}")
    public void cleanupExpiredUrls() {
        log.info("Running scheduled cleanup of expired short URLs...");
        shortUrlService.deleteExpiredUrls();
        log.info("Expired short URL cleanup completed.");
    }
}
