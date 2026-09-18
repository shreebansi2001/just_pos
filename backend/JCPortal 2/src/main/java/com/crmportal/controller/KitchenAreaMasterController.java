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

import com.crmportal.request.dto.KitchenAreaMasterRequestDto;
import com.crmportal.response.dto.KitchenAreaMasterResponseDto;
import com.crmportal.service.KitchenAreaMasterService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/kitchenarea" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class KitchenAreaMasterController {

    @Autowired
    private KitchenAreaMasterService kitchenAreaMasterService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addKitchenArea(
            @Valid @RequestBody KitchenAreaMasterRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        try {
            KitchenAreaMasterResponseDto responseDto =
                    kitchenAreaMasterService.addOrUpdateKitchenAreaMaster(request, -1L);

            if (responseDto == null) {
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_CREATE_FAIL);
                response.put("success", false);
            } else {
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_CREATE_SUCCESS);
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

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateKitchenArea(
            @RequestParam("id") Long id,
            @Valid @RequestBody KitchenAreaMasterRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        try {
            KitchenAreaMasterResponseDto responseDto =
                    kitchenAreaMasterService.addOrUpdateKitchenAreaMaster(request, id);

            if (responseDto == null) {
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_UPDATE_FAIL);
                response.put("success", false);
            } else {
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_UPDATE_SUCCESS);
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

    @GetMapping("/getallbyuserid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllKitchenAreasByUserId(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "kitchenAreaName", required = false) String kitchenAreaName) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<KitchenAreaMasterResponseDto> responseDtos =
                    kitchenAreaMasterService.getAllKitchenAreasByUserId(userId, kitchenAreaName);

            if (responseDtos.isEmpty()) {
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_NOT_FOUND);
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("KitchenAreas Details", responseDtos);
                response.put("data", data);
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_FOUND);
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getbyid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getKitchenAreaById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            KitchenAreaMasterResponseDto responseDto = kitchenAreaMasterService.getKitchenAreaById(id);

            if (responseDto == null) {
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_NOT_FOUND);
                response.put("success", false);
            } else {
                Map<String, Object> data = new HashMap<>();
                List<KitchenAreaMasterResponseDto> responseDtos = new ArrayList<>();
                responseDtos.add(responseDto);
                data.put("KitchenAreas Details", responseDtos);
                response.put("data", data);
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_FOUND);
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deletebyid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteKitchenAreaById(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean deleted = kitchenAreaMasterService.deleteKitchenAreaById(id);

            if (!deleted) {
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_DELETE_FAIL);
                response.put("success", false);
            } else {
                response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_DELETE_SUCCESS);
                response.put("success", true);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @PutMapping("/updatestatus")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateMenuItemStatus(
	        @RequestParam("id") Long id,
	        @RequestParam("isActive") Boolean isActive) {

	    Map<String, Object> response = new HashMap<>();
	    try {
	        boolean updated = kitchenAreaMasterService.updateKitchenAreaStatus(id, isActive);
	        if (updated) {
	            response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_UPDATE_SUCCESS);
	            response.put("success", true);
	        } else {
	            response.put("msg", ConstantsPoc.KITCHEN_CATEGORY_MASTER_UPDATE_FAIL);
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
