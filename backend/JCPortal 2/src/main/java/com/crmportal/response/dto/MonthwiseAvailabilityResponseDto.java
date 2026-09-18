package com.crmportal.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class MonthwiseAvailabilityResponseDto {
    private String date;           // dd/MM/yyyy
    private String dayOfWeek;      // Monday, Tuesday etc.
    private List<ShiftAvailabilityDto> shifts;

    @Data
    public static class ShiftAvailabilityDto {
        private Long    shiftId;
        private String  shiftName;
        private String  startTime;
        private String  endTime;
        private Boolean isAvailable;
        private String  bookedByEventNo;
        private String  bookedByPartyName;
        private Long    bookedByEventId;
    }
}