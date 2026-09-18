// BanquetShiftResponseDto.java
package com.crmportal.response.dto;
import lombok.Data;

@Data
public class BanquetShiftResponseDto {
    private Long    id;
    private String  shiftName;
    private String  startTime;
    private String  endTime;
    private Long    userId;
    private Boolean isActive;
    private String  createdAt;
}