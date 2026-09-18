package com.crmportal.response.dto;

import lombok.Data;

@Data
public class BanquetHallImageResponseDto {
    private Long   id;
    private String imagePath;
    private String imageUrl;   // full URL for frontend
}