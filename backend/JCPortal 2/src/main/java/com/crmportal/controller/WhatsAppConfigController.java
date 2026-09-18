package com.crmportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.request.dto.PdfSendWhatsappRequestDto;
import com.crmportal.service.WhatsAppConfigService;

@RestController
@RequestMapping({ "/v1/api/whatsappconfig" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class WhatsAppConfigController {

	@Autowired
	WhatsAppConfigService whatsAppConfigService;

	@PostMapping("/sendpdf")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> sendPdf(@RequestBody PdfSendWhatsappRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = whatsAppConfigService.sendPdf(request);
			if (isSuccess) {
				response.put("success", isSuccess);
				response.put("msg", "Pdf Send Successfully");
			} else {
				response.put("success", isSuccess);
				response.put("msg", "Pdf Send Failed");
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
