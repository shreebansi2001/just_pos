package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.TicketCommentRequestDTO;
import com.crmportal.response.dto.TicketCommentResponseDTO;
import com.crmportal.service.TicketCommentService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/ticketcomment" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class TicketCommentController {

    @Autowired
    private TicketCommentService ticketCommentService;

    // ----------------------------------------------------
    // Add Comment
    // ----------------------------------------------------
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addComment(
            @Valid @RequestBody TicketCommentRequestDTO request) {

        Map<String, Object> response = new HashMap<>();
        try {
            TicketCommentResponseDTO responseDto = ticketCommentService.addOrUpdateComment(request, 0l);

            if (responseDto != null) {
                response.put("msg", ConstantsPoc.TICKET_COMMENT_CREATE_SUCCESS);
                response.put("success", true);
                response.put("data", responseDto);
            } else {
                response.put("msg", ConstantsPoc.TICKET_COMMENT_CREATE_FAIL);
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

    // ----------------------------------------------------
    // Update Comment
    // ----------------------------------------------------
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateComment(
            @Valid @RequestBody TicketCommentRequestDTO request,
            @RequestParam("id") Long id) {

        Map<String, Object> response = new HashMap<>();
        try {
            TicketCommentResponseDTO responseDto = ticketCommentService.addOrUpdateComment(request, id);

            if (responseDto != null) {
                response.put("msg", ConstantsPoc.TICKET_COMMENT_UPDATE_SUCCESS);
                response.put("success", true);
                response.put("data", responseDto);
            } else {
                response.put("msg", ConstantsPoc.TICKET_COMMENT_UPDATE_FAIL);
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

    // ----------------------------------------------------
    // Get All Comments by Ticket ID
    // ----------------------------------------------------
    @GetMapping("/getallbyticketid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllCommentsByTicketId(
            @RequestParam("ticketId") Long ticketId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<TicketCommentResponseDTO> list = ticketCommentService.getCommentsByTicketId(ticketId);

            if (list.isEmpty()) {
                response.put("msg", ConstantsPoc.TICKET_COMMENT_FOUND_FAIL);
                response.put("success", false);
            } else {
                Map<String, Object> commentRes = new HashMap<>();
                commentRes.put("Ticket Comment Details", list);

                response.put("data", commentRes);
                response.put("msg", ConstantsPoc.TICKET_COMMENT_FOUND_SUCCESS);
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

    // ----------------------------------------------------
    // Delete Comment
    // ----------------------------------------------------
    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteComment(@RequestParam("id") Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = ticketCommentService.deleteComment(id);

            if (deleted) {
                response.put("msg", ConstantsPoc.TICKET_COMMENT_DELETE_SUCCESS);
                response.put("success", true);
            } else {
                response.put("msg", ConstantsPoc.TICKET_COMMENT_DELETE_FAIL);
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