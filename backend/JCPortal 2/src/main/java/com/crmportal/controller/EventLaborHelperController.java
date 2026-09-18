package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.EventLaborHelperRequestDto;
import com.crmportal.response.dto.EventLaborHelperResponseDto;
import com.crmportal.service.EventLaborHelperService;

@RestController
@RequestMapping({ "/v1/api/eventlaborhelper" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventLaborHelperController {

    @Autowired
    private EventLaborHelperService eventLaborHelperService;

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveEventLaborHelpers(
            @Valid @RequestBody EventLaborHelperRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EventLaborHelperResponseDto> responseDtos = eventLaborHelperService.saveEventLaborHelpers(request);
            response.put("msg", "Event labor helpers mapped and saved successfully");
            response.put("data", responseDtos);
            response.put("success", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getbyeventid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getByEventId(@RequestParam("eventId") Long eventId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EventLaborHelperResponseDto> responseDtos = eventLaborHelperService.getByEventId(eventId);
            if (responseDtos.isEmpty()) {
                response.put("msg", "No records found for the given event ID");
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Event Labor Helper Details", responseDtos);
                response.put("data", data);
                response.put("msg", "Event labor helper details fetched successfully");
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getbyeventandfunctionandparty")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getByEventAndFunctionAndParty(
            @RequestParam("eventId") Long eventId,
            @RequestParam("eventFunctionId") Long eventFunctionId,
            @RequestParam("partyId") Long partyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EventLaborHelperResponseDto> responseDtos = eventLaborHelperService
                    .getByEventIdAndFunctionIdAndPartyId(eventId, eventFunctionId, partyId);
            if (responseDtos.isEmpty()) {
                response.put("msg", "No records found for the given criteria");
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Event Labor Helper Details", responseDtos);
                response.put("data", data);
                response.put("msg", "Event labor helper details fetched successfully");
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deletebyeventandfunctionandparty")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteByEventAndFunctionAndParty(
            @RequestParam("eventId") Long eventId,
            @RequestParam("eventFunctionId") Long eventFunctionId,
            @RequestParam("partyId") Long partyId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean result = eventLaborHelperService.deleteByEventIdAndFunctionIdAndPartyId(eventId, eventFunctionId, partyId);
            response.put("msg", "Event labor helper records deleted successfully");
            response.put("success", result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deletebyeventid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteByEventId(@RequestParam("eventId") Long eventId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean result = eventLaborHelperService.deleteByEventId(eventId);
            response.put("msg", "All labor helper records for the event deleted successfully");
            response.put("success", result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
