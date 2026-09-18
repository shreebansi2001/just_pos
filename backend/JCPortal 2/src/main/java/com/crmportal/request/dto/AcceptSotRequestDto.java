package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class AcceptSotRequestDto {
    private Long sotId;
    private Long userId;
    private List<AcceptSotDetailRequestDto> details;
}