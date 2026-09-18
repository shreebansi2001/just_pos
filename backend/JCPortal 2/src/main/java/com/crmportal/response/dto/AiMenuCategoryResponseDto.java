package com.crmportal.response.dto;

import java.util.List;

import lombok.Data;

@Data
public class AiMenuCategoryResponseDto {

    private Long menuCategoryId;
    private String menuName;
    private String menuSlogan;
    private List<AiItemsResponseDto> items;
}