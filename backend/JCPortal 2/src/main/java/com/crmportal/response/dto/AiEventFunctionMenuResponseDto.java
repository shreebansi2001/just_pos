package com.crmportal.response.dto;

import java.util.List;

import lombok.Data;

@Data
public class AiEventFunctionMenuResponseDto {

    private Long eventFunctionId;
    private List<AiMenuCategoryResponseDto> menuCategories;
}