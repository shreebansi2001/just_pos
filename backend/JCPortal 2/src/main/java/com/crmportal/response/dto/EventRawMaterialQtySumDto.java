package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRawMaterialQtySumDto {

    private Long eventRawMaterialId;
    private Double totalQty;
}
