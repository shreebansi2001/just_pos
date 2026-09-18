package com.crmportal.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.crmportal.entity.AITemplateEntity;
import com.crmportal.entity.UpgradedModuleEntity;
import com.crmportal.repository.AITemplateRepository;
import com.crmportal.request.dto.AITemplateRequestDto;
import com.crmportal.request.dto.GenerateAIRequestDto;
import com.crmportal.request.dto.UserUpgradedListOfModulePaymentRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.AITemplateResponseDto;
import com.crmportal.response.dto.MenuPreparationSelectedItemDetailsResponseDto;
import com.crmportal.service.AITemplateService;

@RestController
@RequestMapping({ "/v1/api/ai-template" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class AITemplateController {

	@Autowired
	private AITemplateService aiTemplateService;

	@Value("${rasor_key}")
	private String rasor_key;

	@Value("${rasor_secret_key}")
	private String rasor_secret_key;

	@Autowired
	private AITemplateRepository aiTemplateRepository;

	@PostMapping("/add-update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdate(@RequestBody AITemplateRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = aiTemplateService.addOrUpdate(request);
			if (isSuccess) {
				response.put("msg", "AI Template Added/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "AI Template Added/Updated Fail");
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

	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAll(
			@RequestParam(value = "isActive", required = false) Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<AITemplateResponseDto> dtos = aiTemplateService.getAll(isActive);
			if (dtos.isEmpty()) {
				response.put("msg", "Data Fetch Failed");
				response.put("success", false);
			} else {
				Map<String, Object> aiResp = new HashMap<>();
				aiResp.put("AITemplates", dtos);
				response.put("data", aiResp);
				response.put("msg", "Data Fetch Successfully");
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
	public ResponseEntity<Map<String, Object>> getById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			AITemplateResponseDto dtos = aiTemplateService.getById(id);
			if (dtos == null) {
				response.put("msg", "Data Fetch Failed");
				response.put("success", false);
			} else {
				Map<String, Object> aiResp = new HashMap<>();
				aiResp.put("AITemplates", dtos);
				response.put("data", aiResp);
				response.put("msg", "Data Fetch Successfully");
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
	public ResponseEntity<Map<String, Object>> deleteById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = aiTemplateService.deleteById(id);
			if (isSuccess) {
				response.put("msg", "Data Deleted Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Data Deleted Failed");
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

	@PutMapping("/isactive")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> isActive(@RequestParam("id") Long id,
			@RequestParam("isActive") Boolean isActive) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = aiTemplateService.isActive(id, isActive);
			if (isSuccess) {
				response.put("msg", "Data Actived/DeActived Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Data Actived/DeActived Failed");
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

	@SuppressWarnings("unchecked")
	@PostMapping("/createPayAiOrder")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> createOrder(@RequestBody UserUpgradedModulePaymentRequestDto request) {

		Map<String, Object> responseMap = new HashMap<>();
		List<Map<String, Object>> orderList = new ArrayList<>();

		try {
			String apiKey = rasor_key.trim();
			String secretKey = rasor_secret_key.trim();

			for (UserUpgradedListOfModulePaymentRequestDto moduleReq : request.getUserUpgradedModulePayments()) {

				AITemplateEntity module = aiTemplateRepository
						.findByIdAndIsDeleteFalseAndIsActiveTrue(moduleReq.getUpgradeModuleId())
						.orElseThrow(() -> new RuntimeException(
								"Ai Template not found with id: " + moduleReq.getUpgradeModuleId()));

				String url = "https://api.razorpay.com/v1/orders";
				URL obj = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/json");

				String userpass = apiKey + ":" + secretKey;
				String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userpass.getBytes("UTF-8"));

				conn.setRequestProperty("Authorization", basicAuth);

				double amount = moduleReq.getPayAmnt().doubleValue();

				String data = "{" + "\"amount\":" + Math.round(amount * 100) + "," + "\"currency\":\"INR\","
						+ "\"receipt\":\"INV-" + request.getUserId() + "-" + module.getId() + "\","
						+ "\"payment_capture\":1" + "}";

				OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
				out.write(data);
				out.close();

				int responseCode = conn.getResponseCode();
				InputStream is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder response = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();

				JSONObject objRes = new JSONObject(response.toString());

				if (responseCode >= 400) {
					throw new RuntimeException(objRes.toString());
				}

				Map<String, Object> orderData = new HashMap<>();
				orderData.put("moduleId", module.getId());
				orderData.put("orderId", objRes.optString("id", ""));

				orderList.add(orderData);
			}

			responseMap.put("msg", "Orders created successfully");
			responseMap.put("data", orderList);
			responseMap.put("success", true);

			return ResponseEntity.ok(responseMap);

		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("msg", "Order creation failed");
			responseMap.put("success", false);
			return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/adduseraitemplate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUserAiTemplate(
			@RequestBody UserUpgradedModulePaymentRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = aiTemplateService.addUserAiTemplate(request);
			if (isSuccess) {
				response.put("msg", "User Ai Template Add/Updated successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Ai Template Add/Updated failed");
				response.put("success", false);
			}
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/isactiveuserai")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> isActiveUserAi(@RequestParam("userId") Long userId,
			@RequestParam("isActive") Boolean isActive, @RequestParam("otp") String otp,
			@RequestParam("moduleId") List<Long> moduleId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = aiTemplateService.isActiveUserAI(userId, isActive, otp, moduleId);
			if (isSuccess) {
				response.put("msg", "User Ai Template Actived/DeActived Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Ai Template Actived Failed");
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

	@PutMapping("/extenddate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> extendDate(@RequestParam("userId") Long userId,
			@RequestParam("endDate") String endDate, @RequestParam("otp") String otp,
			@RequestParam("moduleId") List<Long> moduleId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = aiTemplateService.extendDate(userId, moduleId, endDate, otp);
			if (isSuccess) {
				response.put("msg", "User AI Template Date Extended Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User AI Template Date Extended Failed");
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

	@PostMapping("/generateaidata")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> generateAIData(@RequestBody GenerateAIRequestDto request) {
		Map<String, Object> dtoMap = new HashMap<>();
		try {
			dtoMap = aiTemplateService.generateAIData(request);
			return new ResponseEntity<>(dtoMap, HttpStatus.OK);

		} catch (RuntimeException e) {
			dtoMap.put("success", false);
			dtoMap.put("msg", e.getMessage());
			return new ResponseEntity<>(dtoMap, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			dtoMap.put("success", false);
			dtoMap.put("msg", e.getMessage());
			return new ResponseEntity<>(dtoMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
