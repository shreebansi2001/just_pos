package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignEventToChildResponseDto {

    private Boolean success;

    private String message;

    private Integer totalAssigned;
}
