package com.crmportal.response.dto;
import lombok.Data;

@Data
public class BanquetShiftAvailabilityResponseDto {
    private Long    shiftId;
    private String  shiftName;
    private String  startTime;
    private String  endTime;
    private Boolean isAvailable;
    private String  bookedByEventId;   // event no if booked
    private Long eventFunctionId;      //functionId if booked
    private String  bookedByEventName; // party name if booked
}