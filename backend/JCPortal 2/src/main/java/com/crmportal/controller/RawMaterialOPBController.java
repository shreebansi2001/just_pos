package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.RawMaterialOPBRequestDto;
import com.crmportal.response.dto.RawMaterialOPBResponseDto;
import com.crmportal.service.RawMaterialOPBService;

@RestController
@RequestMapping(value = "/v1/api/rawmaterialopb", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class RawMaterialOPBController {

    @Autowired
    private RawMaterialOPBService service;

    // Get items by category (on category dropdown change)
    @GetMapping("/getbycategory")
    public ResponseEntity<Map<String, Object>> getByCategory(

            @RequestParam(value = "categoryId", defaultValue = "0")
            Long categoryId,

            @RequestParam("userId")
            Long userId,

            @RequestParam(value = "itemName", defaultValue = "")
            String search,

            @RequestParam(value = "pageNo", defaultValue = "1")
            Integer pageNo,

            @RequestParam(value = "pageSize", defaultValue = "10")
            Integer pageSize) {

        Map<String, Object> response = new HashMap<>();

        try {

            Map<String, Object> serviceResponse =
                    service.getByCategory(
                            categoryId,
                            userId,
                            search,
                            pageNo,
                            pageSize);

            return ResponseEntity.ok(serviceResponse);

        } catch (RuntimeException e) {

            e.printStackTrace();

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    //  Save OPB for all items on Save button click
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveOPB(
            @RequestBody RawMaterialOPBRequestDto request) {

        Map<String, Object> response = new HashMap<>();
        try {
            service.saveOPB(request);
            response.put("success", true);
            response.put("msg", "Opening balance saved successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}