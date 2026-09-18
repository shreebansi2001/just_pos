package com.crmportal.service;

import java.util.List;
import com.crmportal.request.dto.BanquetShiftBookingRequestDto;
import com.crmportal.request.dto.BanquetShiftRequestDto;
import com.crmportal.response.dto.BanquetShiftAvailabilityResponseDto;
import com.crmportal.response.dto.BanquetShiftResponseDto;
import com.crmportal.response.dto.HallAvailabilityResponseDto;
import com.crmportal.response.dto.MonthwiseAvailabilityResponseDto;

public interface BanquetShiftService {

    // Shift master CRUD
    BanquetShiftResponseDto addOrUpdate(BanquetShiftRequestDto request);
    List<BanquetShiftResponseDto> getAll(Long userId);
    BanquetShiftResponseDto getById(Long id);
    Boolean delete(Long id);
    Boolean toggleStatus(Long id);

    // Availability — get all shifts for a hall+date with booked/available status
    List<BanquetShiftAvailabilityResponseDto> getAvailability(
            Long hallId, String bookingDate, Long userId, Long eventId);

    // Book a shift for an event
    void bookShift(BanquetShiftBookingRequestDto request);

    // Release all shift bookings for an event (called on event delete)
    void releaseBookingsByEvent(Long eventId);
    
    
    
    // Availability for a specific event function
    List<BanquetShiftAvailabilityResponseDto> getAvailabilityByFunction(
            Long hallId, String bookingDate, Long eventFunctionId, Long userId);

    // Book shift for a specific event function
    void bookShiftForFunction(BanquetShiftBookingRequestDto request);

    // Release bookings for an event function
    void releaseBookingsByEventFunction(Long eventFunctionId);
    
    List<HallAvailabilityResponseDto> getDatewiseAvailability(
            String startDate,
            String endDate,
            Long userId);
    
    
}