package com.crmportal.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.CashOpbRequestDto;
import com.crmportal.response.dto.CashOpbResponseDto;
import com.crmportal.service.CashAccountService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/cash-opb" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class CashAccountController {

	@Autowired
	CashAccountService cashAccountService;
	
	@PostMapping("/addUpdate")
	public ResponseEntity<?> addOrUpdateCashAccount(@Valid @RequestBody CashOpbRequestDto request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
        try {
        	CashOpbResponseDto responseDto = cashAccountService.addOrUpdateCashAccount(request, id);
            
        	if (responseDto != null) {
                response.put("msg", ConstantsPoc.CASH_OPB_CREATE_SUCCESS);
                response.put("success", true);
                response.put("data", responseDto);
            } else {
                response.put("msg", ConstantsPoc.CASH_OPB_CREATE_FAIL);
                response.put("success", false);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
        	e.printStackTrace();
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
	public ResponseEntity<?> getAllByUserId(@RequestParam("userId") Long userId, @RequestParam(value = "isPrimary", required = false) Boolean isPrimary) {
		Map<String, Object> response = new HashMap<>();
        try {
        	List<CashOpbResponseDto> responseDto = cashAccountService.getAllByUserId(userId, isPrimary);
            
        	if (responseDto != null) {
                response.put("msg", ConstantsPoc.CASH_OPB_FOUND_SUCCESS);
                response.put("success", true);
                response.put("data", responseDto);
            } else {
                response.put("msg", ConstantsPoc.CASH_OPB_FOUND_FAIL);
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
	
	@GetMapping("/getbyid")
	public ResponseEntity<?> getById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
        try {
        	CashOpbResponseDto responseDto = cashAccountService.getById(id);
            
        	if (responseDto != null) {
                response.put("msg", ConstantsPoc.CASH_OPB_FOUND_SUCCESS);
                response.put("success", true);
                response.put("data", responseDto);
            } else {
                response.put("msg", ConstantsPoc.CASH_OPB_FOUND_FAIL);
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
	
	@DeleteMapping("/deletebyid")
	public ResponseEntity<?> deleteById(@RequestParam("id") Long id) {
		Map<String, Object> response = new LinkedHashMap<>();
		try {
        	response = cashAccountService.deleteById(id);
            
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
