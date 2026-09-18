package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.service.MenuPreparationServiceMultiLang;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/menupreparationmultilang" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class MenuPreparationControllerMultiLang {

	@Autowired
	MenuPreparationServiceMultiLang menuPreparationServiceMultiLang;
	
	@GetMapping("/generateexclusivereport3")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> generateExclusiveReport(@RequestParam("eventId") Long eventId ,@RequestParam("eventFunctionId") Long eventFunctionId , @RequestParam("isCategorySlogan") Integer isCategorySlogan,@RequestParam("isCategoryInstruction") Integer isCategoryInstruction,@RequestParam("isCategoryImage") Long isCategoryImage,@RequestParam("isItemSlogan") Integer isItemSlogan,@RequestParam("isItemInstruction") Integer isItemInstruction, @RequestParam("lang") Integer lang,HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();
		try {
			String filePath = menuPreparationServiceMultiLang.generateExclusiveReport(eventId,eventFunctionId,isCategorySlogan,isCategoryInstruction,isCategoryImage,isItemSlogan,isItemInstruction,re, 1, lang);
			if(filePath.equalsIgnoreCase("")) {
				response.put("msg", ConstantsPoc.MENU_PREPARATION_REPORT_FAIL);
				response.put("success", false);
			}else {
				response.put("filePath", filePath);
				response.put("msg", ConstantsPoc.MENU_PREPARATION_REPORT_SUCCESS);
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
	
}
