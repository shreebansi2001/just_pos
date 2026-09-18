package com.crmportal.response.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RoomMasterResponseDto {

    private Long id;

    private String nameEnglish;

    private String nameHindi;

    private String nameGujarati;

    private Integer price;

    private Boolean isActive;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
