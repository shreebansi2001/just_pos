package com.crmportal.request.dto;

import java.util.List;
import lombok.Data;

@Data
public class ReturnSotRequestDto {
    private Long sotId;
    private Long userId;
    private List<ReturnSotDetailRequestDto> details;
}