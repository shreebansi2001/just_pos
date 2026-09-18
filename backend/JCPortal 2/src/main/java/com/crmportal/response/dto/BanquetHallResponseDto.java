// Response
package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class BanquetHallResponseDto {
    private Long    id;
    private String  hallName;
    private Integer capacity;
    private Float   morningPrice;
    private Float   eveningPrice;
    private Float   fullDayPrice;
    private Float   exhibitionPrice;
    private Float   corporatePrice;
    private Float   extraChargesPerHr;
    private Long    userId;
    private Boolean isActive;
    private String  createdAt;
    private List<BanquetHallImageResponseDto> images;
}