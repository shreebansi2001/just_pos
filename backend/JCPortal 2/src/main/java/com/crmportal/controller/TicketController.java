package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.TicketRequestDTO;
import com.crmportal.response.dto.TicketResponseDto;
import com.crmportal.service.TicketService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping("/v1/api/ticket")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TicketController {

    @Autowired
    TicketService ticketService;

    // ---------------------- ADD ----------------------
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addTicket(@Valid @ModelAttribute TicketRequestDTO request,@RequestParam(value = "file",required = false) MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
        	TicketResponseDto dto = ticketService.addOrUpdateTicket(request, -1L,file);

            if (dto != null) {
                response.put("msg", ConstantsPoc.TICKET_CREATE_SUCCESS);
                Map<String, Object> data = new HashMap<>();
                data.put("ticket", dto);

                response.put("data", data);
                response.put("success", true);
            } else {
                response.put("msg", ConstantsPoc.TICKET_CREATE_FAIL);
                response.put("success", false);
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

    // ---------------------- UPDATE ----------------------
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTicket(
            @Valid @ModelAttribute TicketRequestDTO request,
            @RequestParam("id") Long id,@RequestParam(value = "file",required = false) MultipartFile file) {

        Map<String, Object> response = new HashMap<>();
        try {
            TicketResponseDto dto = ticketService.addOrUpdateTicket(request, id,file);

            if (dto != null) {
            	Map<String, Object> data = new HashMap<>();
                data.put("ticket", dto);

                response.put("data", data);
                response.put("msg", ConstantsPoc.TICKET_UPDATE_SUCCESS);
                response.put("success", true);
            } else {
                response.put("msg", ConstantsPoc.TICKET_UPDATE_FAIL);
                response.put("success", false);
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

    // ---------------------- GET BY ID ----------------------
    @GetMapping("/getbyid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTicketById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {

            List<TicketResponseDto> list = new ArrayList<>();
            TicketResponseDto dto = ticketService.getTicketById(id);

            if (dto == null) {
                response.put("msg", ConstantsPoc.TICKET_FOUND_FAIL);
                response.put("success", false);
            } else {
                list.add(dto);
                Map<String, Object> data = new HashMap<>();
                data.put("Ticket Details", list);

                response.put("data", data);
                response.put("msg", ConstantsPoc.TICKET_FOUND_SUCCESS);
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

    // ---------------------- GET ALL ----------------------
    @GetMapping("/getall")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllTickets() {

        Map<String, Object> response = new HashMap<>();
        try {
            List<TicketResponseDto> list = ticketService.getAllTickets();

            if (list.isEmpty()) {
                response.put("msg", ConstantsPoc.TICKET_FOUND_FAIL);
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Ticket Details", list);

                response.put("data", data);
                response.put("msg", ConstantsPoc.TICKET_FOUND_SUCCESS);
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

    // ---------------------- GET BY USER ID ----------------------
    @GetMapping("/getallbyuserid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllTicketsByUserId(@RequestParam("userId") Long userId) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<TicketResponseDto> list = ticketService.getAllTicketsByUserId(userId);

            if (list.isEmpty()) {
                response.put("msg", ConstantsPoc.TICKET_FOUND_FAIL);
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Ticket Details", list);

                response.put("data", data);
                response.put("msg", ConstantsPoc.TICKET_FOUND_SUCCESS);
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

    // ---------------------- DELETE SOFT ----------------------
    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteTicket(@RequestParam("id") Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            boolean deleted = ticketService.deleteTicket(id);

            if (deleted) {
                response.put("msg", ConstantsPoc.TICKET_DELETE_SUCCESS);
                response.put("success", true);
            } else {
                response.put("msg", ConstantsPoc.TICKET_DELETE_FAIL);
                response.put("success", false);
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
}
