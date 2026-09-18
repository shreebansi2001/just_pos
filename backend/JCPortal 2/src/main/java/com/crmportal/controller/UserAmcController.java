package com.crmportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crmportal.repository.EventExtraExpenseRepository;
import com.crmportal.response.dto.UserAmcResponseDto;
import com.crmportal.service.UserAmcService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping({"v1/api/useramcmaster"})
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UserAmcController {

    private final EventExtraExpenseRepository eventExtraExpenseRepository;

	@Autowired
	UserAmcService userAmcService;


    UserAmcController(EventExtraExpenseRepository eventExtraExpenseRepository) {
        this.eventExtraExpenseRepository = eventExtraExpenseRepository;
    }
	
	
	@GetMapping("/getallusersmc")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllUserAmc() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<UserAmcResponseDto> allUserAmc = userAmcService.getAllUserAmc();
			if(allUserAmc == null || allUserAmc.isEmpty()) {
				response.put("success", true);
	            response.put("msg", "All User Amcs Fetched Successfully.");
			} else {
				response.put("success", false);
	            response.put("msg", "User Amcs Fetched Failed.");
			}
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(RuntimeException e) {
			response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
		} catch(Exception e) {
			response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
    @DeleteMapping("/deleteuseramc")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUserAmc(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        Boolean isSuccess = false;
        
        try {
        	
        	isSuccess = userAmcService.deleteUserAmcById(id);
        	if(isSuccess) {
        		 response.put("success", true);
                 response.put("msg", "UserAmc Deleted Successfully.");
        	} else {
       		 	response.put("success", false);
                response.put("msg", "UserAmc Deletation failed.");
        	}
        	
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(RuntimeException e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("msg", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
