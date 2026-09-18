// Request
package com.crmportal.request.dto;

import lombok.Data;

@Data
public class BanquetHallRequestDto {
    private Long    id;           // null or -1 = new
    private String  hallName;
    private Integer capacity;
    private Float   morningPrice;
    private Float   eveningPrice;
    private Float   fullDayPrice;
    private Float   exhibitionPrice;
    private Float   corporatePrice;
    private Float   extraChargesPerHr;
    private String  password;     // plain text — encrypted before saving
    private Boolean isActive = true;  // default true
    private Long    userId;
}