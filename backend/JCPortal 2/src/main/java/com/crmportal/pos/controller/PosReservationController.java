package com.crmportal.pos.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crmportal.pos.entity.PosReservationEntity;
import com.crmportal.pos.service.PosService;

@RestController
@RequestMapping("/v1/api/pos")
@CrossOrigin(origins = {"*"}, maxAge = 3600L)
public class PosReservationController extends PosBaseController {

    @Autowired
    private PosService posService;

    @GetMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReservations(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "status", required = false) String status) {
        return ok("Reservations fetched", posService.getReservations(date, status, userId));
    }

    @PostMapping("/reservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveReservation(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestBody PosReservationEntity reservation) {
        return ok("Reservation saved", posService.saveReservation(reservation, userId));
    }

    @PostMapping("/reservations/{id}/seat")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> seatReservation(
            @PathVariable("id") Long id,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam("tableId") Long tableId) {
        return ok("Reservation seated", posService.seatReservation(id, tableId, userId));
    }
}
