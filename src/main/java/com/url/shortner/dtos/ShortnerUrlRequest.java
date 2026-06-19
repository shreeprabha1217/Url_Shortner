package com.url.shortner.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShortnerUrlRequest {

    @NotBlank(message = "URL cannot be empty")
    private String longUrl;
}
