package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.service.RawMaterialSupplierService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({"/v1/api/rawmaterialsupplier"})
@CrossOrigin(
		   origins = {"*"},
		   maxAge = 3600L
		)
public class RawMaterialSupplierController {

	@Autowired
	RawMaterialSupplierService rawMaterialSupplierService;
	
	@DeleteMapping("/deletebyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteRawMaterialSupplierById(@RequestParam("id") Long id){
		Boolean isSuccess = false;
		Map<String, Object> response = new HashMap<>();
		try {
			isSuccess = rawMaterialSupplierService.deleteRawMaterialSupplierById(id);
			if(isSuccess) {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_SUPPLIER_DELETE_SUCCESS);
				response.put("success", true);
			}else {
				response.put("msg", ConstantsPoc.RAW_MATERIAL_SUPPLIER_DELETE_FAIL);
				response.put("success", false);				
			}
			return new ResponseEntity<>(response , HttpStatus.OK);
		}catch (RuntimeException e) {
		    response.put("success", false);
		    response.put("msg", e.getMessage());
		    return new ResponseEntity<>(response, HttpStatus.OK);
		}  catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
