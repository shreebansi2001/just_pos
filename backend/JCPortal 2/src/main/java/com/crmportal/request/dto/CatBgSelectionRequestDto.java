package com.crmportal.request.dto;

import lombok.Data;

@Data
public class CatBgSelectionRequestDto {
    private Long id;           // -1 or null = new
    private String categoryName;
    private Boolean isCatImg;  // true = category image, false = background image
    // imagePath set by controller after file upload
}