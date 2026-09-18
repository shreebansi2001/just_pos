package com.crmportal.request.dto;

import lombok.Data;

@Data
public class InteractionRequestDTO {

    private Long id; // used for update
    private String interactionname;
    private String interactiontype;
    private Boolean isActive;
}
