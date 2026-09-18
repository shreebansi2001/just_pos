package com.crmportal.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.RawMaterialCategoryTypeMasterRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryTypeMasterResponseDto;
import com.crmportal.service.RawMaterialCategoryTypeMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/rawmaterialcattype" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class RawMaterialCategoryTypeMasterController {

    @Autowired
    RawMaterialCategoryTypeMasterService rawMaterialCategoryTypeMasterService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addRawMaterialCategoryType(
            @Valid @RequestBody RawMaterialCategoryTypeMasterRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        try {
            RawMaterialCategoryTypeMasterResponseDto responseDto =
                    rawMaterialCategoryTypeMasterService.addOrUpdateRawMaterialCategoryType(request, Long.valueOf("-1"));
            if (responseDto != null) {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_CREATE_SUCCESS);
                response.put("success", true);
            } else {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_CREATE_FAIL);
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

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRawMaterialCategoryType(
            @Valid @RequestBody RawMaterialCategoryTypeMasterRequestDto request, @RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            RawMaterialCategoryTypeMasterResponseDto responseDto =
                    rawMaterialCategoryTypeMasterService.addOrUpdateRawMaterialCategoryType(request, id);
            if (responseDto != null) {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_UPDATE_SUCCESS);
                response.put("success", true);
            } else {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_UPDATE_FAIL);
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

    @GetMapping("/getallbyuserid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllRawMaterialCategoryTypeByUserId(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "categoryTypeName", required = false) String categoryTypeName,
            @RequestParam(value = "isActive", required = false) Boolean isActive) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<RawMaterialCategoryTypeMasterResponseDto> responseDtos =
                    rawMaterialCategoryTypeMasterService.getAllRawMaterialCategoryTypeByUserId(userId, categoryTypeName, isActive);
            if (responseDtos.isEmpty()) {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_FOUND_FAIL);
                response.put("success", false);
            } else {
                Map<String, Object> resData = new HashMap<>();
                resData.put("Raw Material Category Type Details", responseDtos);
                response.put("data", resData);
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_FOUND_SUCCESS);
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

    @GetMapping("/getbyid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRawMaterialCategoryTypeById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<RawMaterialCategoryTypeMasterResponseDto> responseDtos = new ArrayList<>();
            RawMaterialCategoryTypeMasterResponseDto responseDto =
                    rawMaterialCategoryTypeMasterService.getRawMaterialCategoryTypeById(id);
            if (responseDto == null) {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_FOUND_FAIL);
                response.put("success", false);
            } else {
                Map<String, Object> resData = new HashMap<>();
                responseDtos.add(responseDto);
                resData.put("Raw Material Category Type Details", responseDtos);
                response.put("data", resData);
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_FOUND_SUCCESS);
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

    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteRawMaterialCategoryTypeById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        Boolean isSuccess = false;
        try {
            isSuccess = rawMaterialCategoryTypeMasterService.deleteRawMaterialCategoryTypeById(id);
            if (isSuccess) {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_DELETE_SUCCESS);
                response.put("success", isSuccess);
            } else {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_DELETE_FAIL);
                response.put("success", isSuccess);
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

    @PutMapping("/updatestatus")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRawMaterialCategoryTypeStatus(
            @RequestParam("id") Long id,
            @RequestParam("isActive") Boolean isActive) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean updated = rawMaterialCategoryTypeMasterService.updateRawMaterialCategoryTypeStatus(id, isActive);
            if (updated) {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_UPDATE_SUCCESS);
                response.put("success", true);
            } else {
                response.put("msg", ConstantsPoc.RAW_MATERIAL_CATEGORY_TYPE_MASTER_UPDATE_FAIL);
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
