// BanquetShiftRequestDto.java
package com.crmportal.request.dto;
import lombok.Data;

@Data
public class BanquetShiftRequestDto {
    private Long   id;
    private String shiftName;
    private String startTime;
    private String endTime;
    private Long   userId;
    private Boolean isActive;
}