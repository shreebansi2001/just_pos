package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.InteractionRequestDTO;
import com.crmportal.response.dto.InteractionResponseDTO;
import com.crmportal.service.InteractionService;

@RestController
@RequestMapping("/v1/api/interaction")
@CrossOrigin(origins = "*", maxAge = 3600L)
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    // ------------------------- Add -----------------------------------
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addInteraction(
            @Valid @RequestBody InteractionRequestDTO request) {

        Map<String, Object> response = new HashMap<>();
        try {

            InteractionResponseDTO responseDto = interactionService.addOrUpdate(request);

            if (responseDto != null) {
                response.put("msg", "Interaction saved successfully !!");
                response.put("success", true);
            } else {
                response.put("msg", "Interaction save failed !!");
                response.put("success", false);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("msg", e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("msg", e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------------- Update -----------------------------------
    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateInteraction(
            @Valid @RequestBody InteractionRequestDTO request,
            @RequestParam("id") Long id) {

        Map<String, Object> response = new HashMap<>();
        try {

            request.setId(id);
            InteractionResponseDTO responseDto = interactionService.addOrUpdate(request);

            if (responseDto != null) {
                response.put("msg", "Interaction updated successfully !!");
                response.put("success", true);
            } else {
                response.put("msg", "Interaction update failed !!");
                response.put("success", false);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("msg", e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("msg", e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------------- Get All -----------------------------------
    @GetMapping("/getall")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllInteractions() {
        Map<String, Object> response = new HashMap<>();
        try {

            List<InteractionResponseDTO> list = interactionService.getAll();

            if (list.isEmpty()) {
                response.put("msg", "Interaction not found");
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("Interaction Details", list);

                response.put("data", data);
                response.put("msg", "Interaction data found");
                response.put("success", true);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("msg", e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("msg", e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------------- Get By Id -----------------------------------
    @GetMapping("/getbyid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getInteractionById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {

            InteractionResponseDTO dto = interactionService.getById(id);
            List<InteractionResponseDTO> list = new ArrayList<>();

            if (dto == null) {
                response.put("msg", "Interaction not found");
                response.put("success", false);
            } else {
                list.add(dto);
                Map<String, Object> data = new HashMap<>();
                data.put("Interaction Details", list);

                response.put("data", data);
                response.put("msg", "Interaction data found");
                response.put("success", true);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            response.put("msg", e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("msg", e.getMessage());
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}