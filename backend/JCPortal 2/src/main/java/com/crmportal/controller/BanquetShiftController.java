package com.crmportal.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.entity.BanquetHallMasterEntity;
import com.crmportal.entity.BanquetHallShiftBookingEntity;
import com.crmportal.repository.BanquetHallMasterRepository;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.request.dto.BanquetShiftBookingRequestDto;
import com.crmportal.request.dto.BanquetShiftRequestDto;
import com.crmportal.response.dto.BanquetShiftAvailabilityResponseDto;
import com.crmportal.response.dto.HallAvailabilityResponseDto;
import com.crmportal.service.BanquetShiftService;

@RestController
@RequestMapping("/v1/api/banquetshift")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class BanquetShiftController {

    @Autowired
    private BanquetShiftService service;
    
    @Autowired
    private BanquetHallMasterRepository banquetHallMasterRepository;
    
    @Autowired
    private BanquetHallShiftBookingRepository bookingRepository;

    // ── Add / Update shift master ─────────────────────────────────────────
    @PostMapping("/add-update")
    public ResponseEntity<Map<String, Object>> addOrUpdate(
            @RequestBody BanquetShiftRequestDto request) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",  "Shift saved successfully");
            response.put("data", service.addOrUpdate(request));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get all shifts ────────────────────────────────────────────────────
    @GetMapping("/getall")
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",  "Shifts fetched successfully");
            response.put("data", service.getAll(userId));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Get by ID ─────────────────────────────────────────────────────────
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<Map<String, Object>> getById(
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("msg",  "Shift fetched successfully");
            response.put("data", service.getById(id));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.delete(id);
            response.put("success", true);
            response.put("msg", "Shift deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Toggle status ─────────────────────────────────────────────────────
    @PostMapping("/toggle-status/{id}")
    public ResponseEntity<Map<String, Object>> toggleStatus(
            @PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.toggleStatus(id);
            response.put("success", true);
            response.put("msg", "Status updated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Check availability for a hall on a date ───────────────────────────
    @GetMapping("/availability")
    public ResponseEntity<Map<String, Object>> getAvailability(
            @RequestParam List<Long> hallIds,           // ?hallIds=1&hallIds=2  OR  ?hallIds=1  (single still works)
            @RequestParam String     bookingDate,
            @RequestParam Long       userId,
            @RequestParam(required = false) Long eventId) {

        Map<String, Object> response = new HashMap<>();
        try {
        	HallAvailabilityResponseDto dto =
        	        new HallAvailabilityResponseDto();

        	dto.setDate(bookingDate);

        	try {
        	    LocalDate localDate =
        	            LocalDate.parse(
        	                    bookingDate,
        	                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        	   // dto.setDayOfWeek(localDate.getDayOfWeek().toString());
        	} catch (Exception e) {
        	    dto.setDayOfWeek(null);
        	}

        	List<HallAvailabilityResponseDto.HallDto> halls =
        	        new ArrayList<>();
        	
        	List<BanquetHallMasterEntity> hallEntities =
        	        banquetHallMasterRepository.findAllById(hallIds);

        	Map<Long, BanquetHallMasterEntity> hallMap =
        	        hallEntities.stream()
        	                .collect(Collectors.toMap(
        	                        BanquetHallMasterEntity::getId,
        	                        Function.identity()));

        	for (Long hallId : hallIds) {

        	    HallAvailabilityResponseDto.HallDto hallDto =
        	            new HallAvailabilityResponseDto.HallDto();

        	    BanquetHallMasterEntity hall = hallMap.get(hallId);

        	    hallDto.setHallId(hallId);
        	    hallDto.setHallName(
        	            hall != null
        	                    ? hall.getHallName()
        	                    : null
        	    );

        	    List<BanquetShiftAvailabilityResponseDto> shifts =
        	            service.getAvailability(
        	                    hallId,
        	                    bookingDate,
        	                    userId,
        	                    eventId);

        	    List<HallAvailabilityResponseDto.ShiftDto> shiftDtos =
        	            new ArrayList<>();

        	    for (BanquetShiftAvailabilityResponseDto shift : shifts) {

        	        HallAvailabilityResponseDto.ShiftDto s =
        	                new HallAvailabilityResponseDto.ShiftDto();

        	        s.setShiftId(shift.getShiftId());
        	        s.setShiftName(shift.getShiftName());
        	        s.setStartTime(shift.getStartTime());
        	        s.setEndTime(shift.getEndTime());
        	        s.setIsAvailable(shift.getIsAvailable());

        	        s.setBookedByEventNo(shift.getBookedByEventId());
        	        s.setBookedByPartyName(shift.getBookedByEventName());

        	        shiftDtos.add(s);
        	    }

        	    hallDto.setShifts(shiftDtos);

        	    halls.add(hallDto);
        	}

        	dto.setHalls(halls);

        	response.put("data", dto);
            response.put("success", true);
            response.put("msg", "Availability fetched successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // ── Book a shift for an event ─────────────────────────────────────────
//    @PostMapping("/book")
//    public ResponseEntity<Map<String, Object>> book(
//            @RequestBody BanquetShiftBookingRequestDto request) {
//
//        Map<String, Object> response = new HashMap<>();
//        try {
//            service.bookShift(request);
//            response.put("success", true);
//            response.put("msg", "Shift booked successfully");
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("msg", e.getMessage()); // ← "shift not available" msg here
//            return ResponseEntity.ok(response);
//        }
//    }
    
 // ── Check availability for a hall + eventFunction + date ─────────────────
//    @GetMapping("/availability-by-function")
//    public ResponseEntity<Map<String, Object>> getAvailabilityByFunction(
//            @RequestParam("hallId")            Long   hallId,
//            @RequestParam("bookingDate")       String bookingDate,
//            @RequestParam("eventFunctionId")   Long   eventFunctionId,
//            @RequestParam("userId")            Long   userId) {
//
//        Map<String, Object> response = new HashMap<>();
//        try {
//            response.put("success", true);
//            response.put("msg",  "Availability fetched successfully");
//            response.put("data", service.getAvailabilityByFunction(
//                    hallId, bookingDate, eventFunctionId, userId));
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("msg", e.getMessage());
//            return ResponseEntity.ok(response);
//        }
//    }

    // ── Book a shift for a specific event function ────────────────────────────
//    @PostMapping("/book-for-function")
//    public ResponseEntity<Map<String, Object>> bookForFunction(
//            @RequestBody BanquetShiftBookingRequestDto request) {
//
//        Map<String, Object> response = new HashMap<>();
//        try {
//            service.bookShiftForFunction(request);
//            response.put("success", true);
//            response.put("msg", "Shift booked for function successfully");
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("msg", e.getMessage());
//            return ResponseEntity.ok(response);
//        }
//    }

    // ── Get bookings for an event function ────────────────────────────────────
    @GetMapping("/getbyeventfunction")
    public ResponseEntity<Map<String, Object>> getByEventFunction(
            @RequestParam("eventFunctionId") Long eventFunctionId) {

        Map<String, Object> response = new HashMap<>();
        try {
            Optional<BanquetHallShiftBookingEntity> bookingOpt =
                    bookingRepository.findFirstBookingByEventFunctionId(eventFunctionId);

            if (bookingOpt.isPresent()) {
                BanquetHallShiftBookingEntity b = bookingOpt.get();
                Map<String, Object> data = new HashMap<>();
                data.put("shiftId",       b.getShift() != null ? b.getShift().getId() : null);
                data.put("shiftName",     b.getShift() != null ? b.getShift().getShiftName() : null);
                data.put("startTime",     b.getShift() != null ? b.getShift().getStartTime() : null);
                data.put("endTime",       b.getShift() != null ? b.getShift().getEndTime() : null);
                data.put("hallId",        b.getHall()  != null ? b.getHall().getId() : null);
                data.put("hallName",      b.getHall()  != null ? b.getHall().getHallName() : null);
                data.put("bookingDate",   b.getBookingDate() != null
                        ? b.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null);
                data.put("eventFunctionId", eventFunctionId);
                response.put("success", true);
                response.put("msg",  "Booking found");
                response.put("data", data);
            } else {
                response.put("success", false);
                response.put("msg", "No booking found for this function");
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
 // ── Monthwise availability for a hall ─────────────────────────────────
    @GetMapping("/datewise-availability")
    public ResponseEntity<Map<String,Object>> getDatewiseAvailability(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam Long userId) {

        Map<String,Object> response = new HashMap<>();

        response.put("success", true);
        response.put("msg", "Availability fetched successfully");
        response.put("data",
                service.getDatewiseAvailability(
                        startDate,
                        endDate,
                        userId));

        return ResponseEntity.ok(response);
    }
}