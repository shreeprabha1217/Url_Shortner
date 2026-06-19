package com.url.shortner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.url.shortner.entities.UrlMapping;

@Repository
public interface UrlMappingRepository
        extends JpaRepository<UrlMapping, Long> {
                Optional<UrlMapping> findByShortCode(String shortCode);
}
