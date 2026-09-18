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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.UserRightsPagesRequestDto;
import com.crmportal.response.dto.ModuleWiseUserRightsPagesResponseDto;
import com.crmportal.response.dto.UserRightsMasterResponseDto;
import com.crmportal.response.dto.UserRightsPageWithModuleResponseDto;
import com.crmportal.response.dto.UserRightsPagesResponseDto;
import com.crmportal.response.dto.UserRightsRequest;
import com.crmportal.service.UserRightsService;
import com.crmportal.service.impl.UserRightsResponse;

@RestController
@RequestMapping("/v1/api/user-rights")
@CrossOrigin(
		   origins = {"*"},
		   maxAge = 3600L
		)
public class UserRightsController {
	
	@Autowired
	private UserRightsService userRightsService;
	
	@PostMapping("/addPage")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addUserRightsPage(@Valid @RequestBody UserRightsPagesRequestDto request) {

        Map<String, Object> response = new HashMap<>();

        try {
            UserRightsPagesResponseDto responseDto =
            		userRightsService.addOrUpdateUserRightsPage(request, Long.valueOf("-1"));

            if (responseDto != null) {
                response.put("msg", "User Rights Page Created Successfully");
                response.put("success", true);
            } else {
                response.put("msg", "Failed to Create User Rights Page");
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
	
	@PostMapping("/updatePage/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateUserRightsPage(
	        @PathVariable Long id,
	        @Valid @RequestBody UserRightsPagesRequestDto request) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        UserRightsPagesResponseDto responseDto =
	                userRightsService.addOrUpdateUserRightsPage(request, id);
	        if (responseDto != null) {
	            response.put("msg", "User Rights Page Updated Successfully");
	            response.put("success", true);
	        } else {
	            response.put("msg", "Failed to Update User Rights Page");
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
	
	@GetMapping("/getPages")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllPages(@RequestParam("isAdminRights") Boolean isAdminRights,
			@RequestParam("isCombine") Boolean isCombine) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ModuleWiseUserRightsPagesResponseDto> res = userRightsService.getActivePages(isAdminRights, isCombine);
			if (res.isEmpty()) {
				response.put("msg", "User rights pages not found");
				response.put("success", false);
			} else {
				Map<String, Object> contactTypeRes = new HashMap<>();
				contactTypeRes.put("ModuleWiseUserRights", res);
				response.put("data", contactTypeRes);
				response.put("msg", "User rights pages found");
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
	
	@PostMapping("/addRights")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addUserRights(@RequestBody UserRightsRequest requestList) {

        Map<String, Object> response = new HashMap<>();

        try {
        	userRightsService.updateUserRights(requestList);
            response.put("success", true);
            response.put("msg", "User rights updated successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getByRole")
    public ResponseEntity<Map<String, Object>> getUserRights(@RequestParam("roleId") Long roleId) {
    	Map<String, Object> response = new HashMap<>();
        List<UserRightsPageWithModuleResponseDto> res = userRightsService.getRightsByRoleId(roleId);
		if (res.isEmpty()) {
			response.put("msg", "User rights not found");
			response.put("success", false);
		} else {
			Map<String, Object> userRightsRes = new HashMap<>();
			userRightsRes.put("UserRights", res);
			response.put("data", userRightsRes);
			response.put("msg", "User rights found");
			response.put("success", true);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/getByUser")
    public ResponseEntity<Map<String, Object>> getByUser(@RequestParam("userId") Long userId) {
    	Map<String, Object> response = new HashMap<>();
    	UserRightsResponse res = userRightsService.getRightsByUserId(userId);
		if (res == null) {
			response.put("msg", "User rights not found");
			response.put("success", false);
		} else {
			Map<String, Object> userRightsRes = new HashMap<>();
			userRightsRes.put("UserRights", res);
			response.put("data", userRightsRes);
			response.put("msg", "User rights found");
			response.put("success", true);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @DeleteMapping("/page/delete/{pageId}")
    public ResponseEntity<Map<String, Object>> deleteUserRightsPage(@PathVariable Long pageId) {

        Map<String, Object> response = new HashMap<>();

        try {
            userRightsService.deleteUserRightsPage(pageId);

            response.put("success", true);
            response.put("msg", "User Rights Page deleted successfully");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            response.put("success", false);
            response.put("msg", e.getMessage());

            return ResponseEntity.ok(response);
        }
    }

}
