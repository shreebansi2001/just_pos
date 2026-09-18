package com.crmportal.response.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HallPackagePriceResponseDto {
    private Long hallId;
    private Long packageId;
    private Integer functionPax;
    private String tierLabel;
    private Integer minGuests;
    private BigDecimal price;
    private Boolean found;
    private String message;
}