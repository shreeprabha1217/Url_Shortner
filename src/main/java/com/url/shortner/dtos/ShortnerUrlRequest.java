package com.url.shortner.dtos;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShortnerUrlRequest {

    @NotBlank(message = "URL cannot be empty")
    private String longUrl;

    @Min(value = 1, message = "TTL must be at least 1 day")
    @Max(value = 365, message = "TTL cannot exceed 365 days")
    private Integer ttlDays;
}
