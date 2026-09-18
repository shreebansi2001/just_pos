package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class SotResponseDto {
    private Long id;
    private String sotNo;
    private Long eventId;
    private String eventName;
    private String status;
    private String createdAt;
    private Long userId;
    private List<SotDetailResponseDto> details;
}