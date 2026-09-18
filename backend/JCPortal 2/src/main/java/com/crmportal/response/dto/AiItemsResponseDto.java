package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiItemsResponseDto {

    private Long menuItemId;
    private String itemName;
    private String itemSlogan;
}