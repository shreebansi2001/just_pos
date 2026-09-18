package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRawMaterialFunctionDeleteDto {

    private Long eventRawMaterialFunctionId;
    private Long eventRawMaterialId;
}