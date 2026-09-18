package com.crmportal.request.dto;
import lombok.Data;

@Data
public class BanquetShiftBookingRequestDto {
    private Long   hallId;
    private Long   shiftId;
    private Long   eventId;
    private Long eventFunctionId; // null = event-level, set = function-level
    private String bookingDate; // dd/MM/yyyy
    private Long userId;
}