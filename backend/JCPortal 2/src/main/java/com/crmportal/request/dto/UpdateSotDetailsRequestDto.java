package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class UpdateSotDetailsRequestDto {
    private Long sotId;
    private Long userId;
    private List<UpdateSotDetailItemRequestDto> details;
}