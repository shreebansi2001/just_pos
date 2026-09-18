package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class EventRawMaterialDisposableRequestDto {
    private Long eventId;
    private Long userId;
    private Long rawMaterialCatId;
    private List<EventRawMaterialDisposableItemDto> items;
}