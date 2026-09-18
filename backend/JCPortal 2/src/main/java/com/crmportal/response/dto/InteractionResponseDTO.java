package com.crmportal.response.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class InteractionResponseDTO {

    private Long id;
    private String interactionname;
    private String interactiontype;
    private Boolean isDelete;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
