package com.crmportal.response.dto;

import java.util.List;

import lombok.Data;

@Data
public class HallAvailabilityResponseDto {

    private String date;
    private String dayOfWeek;
    private List<HallDto> halls;

    @Data
    public static class HallDto {
        private Long hallId;
        private String hallName;
        private List<ShiftDto> shifts;
    }

    @Data
    public static class ShiftDto {
        private Long shiftId;
        private String shiftName;
        private String startTime;
        private String endTime;
        private Boolean isAvailable;

        private Long bookedByEventId;
        private String bookedByEventNo;
        private String bookedByPartyName;
        private Integer status;
    }
}
