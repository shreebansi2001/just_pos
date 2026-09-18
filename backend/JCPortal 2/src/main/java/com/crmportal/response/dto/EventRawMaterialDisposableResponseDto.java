package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class EventRawMaterialDisposableResponseDto {
    private Long eventId;
    private List<EventRawMaterialDisposableCategoryDto> categories;
}