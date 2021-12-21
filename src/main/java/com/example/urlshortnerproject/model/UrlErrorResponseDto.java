package com.example.urlshortnerproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlErrorResponseDto {
    private String status;
    private String error;
}
