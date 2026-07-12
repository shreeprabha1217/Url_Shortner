package com.url.shortner.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.url.shortner.entities.UrlMapping;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface UrlMappingRepository
        extends JpaRepository<UrlMapping, Long> {
                Optional<UrlMapping> findByShortCode(String shortCode);
                void deleteByExpiryAtBefore(LocalDateTime dateTime);
                
                @Modifying
                @Query("""
    UPDATE UrlMapping u
    SET u.clickCount = u.clickCount + 1
    WHERE u.shortCode = :shortCode
    """)
void incrementClickCount(@Param("shortCode") String shortCode);
}
