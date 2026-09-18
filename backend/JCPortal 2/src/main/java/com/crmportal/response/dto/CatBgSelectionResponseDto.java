package com.crmportal.response.dto;

import lombok.Data;

@Data
public class CatBgSelectionResponseDto {
    private Long    id;
    private String  categoryName;
    private String  imagePath;     // relative path
    private String  imageUrl;      // full URL for frontend
    private Boolean isCatImg;
    private String  createdAt;
}