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

import com.crmportal.entity.PlansEntity;
import com.crmportal.entity.UpgradedModuleEntity;
import com.crmportal.repository.UpgradedModuleRepository;
import com.crmportal.request.dto.UpgradedModuleRequestDto;
import com.crmportal.request.dto.UserUpgradedListOfModulePaymentRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.UpgradedModuleResponseDto;
import com.crmportal.service.UpgradedModuleService;
import com.crmportal.utility.ConstantsPoc;

@RestController
@RequestMapping({ "/v1/api/upgradedmodule" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UpgradedModuleController {

	@Autowired
	UpgradedModuleService upgradedModuleService;

	@Value("${rasor_key}")
	private String rasor_key;

	@Value("${rasor_secret_key}")
	private String rasor_secret_key;

	@Autowired
	UpgradedModuleRepository upgradedModuleRepository;

	@PostMapping("/addorupdateupgradedmodule")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrUpdateUpgradedModule(
			@RequestBody UpgradedModuleRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = upgradedModuleService.addOrUpgradedModule(request);
			response.put("success", isSuccess ? true : false);
			response.put("msg",
					isSuccess ? "Upgraded Module Added/Updated Successfully" : "Upgraded Module Added/Updated Failed");
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

	@GetMapping("/getall")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAll(
			@RequestParam(value = "isActive", required = false) Boolean isActive,@RequestParam("userId") Long userId,@RequestParam(value = "isConfig", required = false) Boolean isConfig) {

		Map<String, Object> response = new HashMap<>();
		try {

			List<UpgradedModuleResponseDto> dtos = upgradedModuleService.getAll(isActive,userId,isConfig);
			if (dtos.isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> resp = new HashMap<>();
				resp.put("UpgradedModule", dtos);
				response.put("data", resp);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
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

	@SuppressWarnings("unchecked")
	@PostMapping("/createPayOrder")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> createOrder(@RequestBody UserUpgradedModulePaymentRequestDto request) {

		Map<String, Object> responseMap = new HashMap<>();
		List<Map<String, Object>> orderList = new ArrayList<>();

		try {
			String apiKey = rasor_key.trim();
			String secretKey = rasor_secret_key.trim();

			for (UserUpgradedListOfModulePaymentRequestDto moduleReq : request.getUserUpgradedModulePayments()) {

				UpgradedModuleEntity module = upgradedModuleRepository
						.findByIdAndIsDeleteFalseAndIsActiveTrue(moduleReq.getUpgradeModuleId())
						.orElseThrow(() -> new RuntimeException(
								"Upgraded Module not found with id: " + moduleReq.getUpgradeModuleId()));

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

	@PostMapping("/adduserupgrademodule")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUserUpgradedModule(
			@RequestBody UserUpgradedModulePaymentRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = upgradedModuleService.addUserUpgradeModule(request);
			if (isSuccess) {
				response.put("msg", "User Upgrad Module Add/Updated successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Upgrad Module Add/Updated failed");
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

	@PutMapping("/isActive")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> isActive(@RequestParam("userId") Long userId,
			@RequestParam("isActive") Boolean isActive, @RequestParam("otp") String otp,
			@RequestParam("moduleId") List<Long> moduleId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = upgradedModuleService.isActive(userId, isActive, otp, moduleId);
			if (isSuccess) {
				response.put("msg", "User Upgrad Module Actived/DeActived Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Upgrad Module Actived Failed");
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
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteById(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = upgradedModuleService.deleteById(id);
			if (isSuccess) {
				response.put("msg", "User Upgrad Module Deleted Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Upgrad Module Delete Failed");
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
			@RequestParam("endDate") String endDate, @RequestParam("otp") String otp,@RequestParam("moduleId") List<Long> moduleId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = upgradedModuleService.extendDate(userId,moduleId, endDate, otp);
			if (isSuccess) {
				response.put("msg", "User Upgrad Module Date Extended Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "User Upgrad Module Date Extended Failed");
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
