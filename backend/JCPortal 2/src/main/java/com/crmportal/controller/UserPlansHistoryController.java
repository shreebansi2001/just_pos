package com.crmportal.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.net.ssl.SSLContext;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.CoupenEntity;
import com.crmportal.entity.ExtraPaymentEntity;
import com.crmportal.entity.PlansEntity;
import com.crmportal.repository.CoupenMasterRepository;
import com.crmportal.repository.ExtraPaymentMasterRepository;
import com.crmportal.repository.PlansRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.CreatePaymentSessionRequestDto;
import com.crmportal.request.dto.UserPlansHistoryRequestDto;
import com.crmportal.response.dto.UserPlansHistoryResponseDto;
import com.crmportal.response.dto.ZohoPaymentSessionResponse;
import com.crmportal.service.UserPlansHistoryService;
import com.crmportal.service.impl.ZohoPaymentSessionService;
import com.crmportal.utility.ResponseUtils;

@RestController
@RequestMapping({ "v1/api/userplanshistory" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class UserPlansHistoryController {

	@Autowired
	UserPlansHistoryService userPlansHistoryService;

	@Autowired
	ZohoPaymentSessionService zohoPaymentService;

	@Value("${rasor_key}")
	private String rasor_key;

	@Value("${rasor_secret_key}")
	private String rasor_secret_key;

	@Autowired
	PlansRepository plansRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CoupenMasterRepository coupenMasterRepository;

	@Autowired
	ExtraPaymentMasterRepository extraPaymentMasterRepository;

	@PostMapping("/adduserplan")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUserPlan(@Valid @RequestBody UserPlansHistoryRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		Boolean isSuccess = false;
		try {
			isSuccess = userPlansHistoryService.addUserPlan(request);
			if (isSuccess) {
				response.put("msg", "Plan Updated successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Plan Updated failed");
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

	@SuppressWarnings("unchecked")
	@ResponseBody
	@PostMapping("/createPaymentOrder")
	public ResponseEntity<Map<String, Object>> createPaymentOrder(@RequestBody UserPlansHistoryRequestDto request)
			throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		try {
			// ❌ REMOVE TLS override (can cause issues)
			// SSLContext ctx = SSLContext.getInstance("TLSv1.2");
			// ctx.init(null, null, null);
			// SSLContext.setDefault(ctx);

			String apiKey = rasor_key.trim();
			String secretKey = rasor_secret_key.trim();

			PlansEntity plan = plansRepository.findByIdAndIsDeleteFalse(request.getPlanId())
					.orElseThrow(() -> new RuntimeException("Plan not found with id: " + request.getPlanId()));

			String url = "https://api.razorpay.com/v1/orders";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");

			String userpass = apiKey + ":" + secretKey;
			String basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);

			double tamount = request.getFinalTotal().doubleValue();

			String data = "{" + "\"amount\":" + Math.round(tamount * 100) + "," + "\"currency\":\"INR\","
					+ "\"receipt\":\"INV-" + request.getUserId() + "-" + plan.getId() + "\"," + "\"payment_capture\":1"
					+ "}";

			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();

			int responseCode = conn.getResponseCode();

			InputStream is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();

			BufferedReader iny = new BufferedReader(new InputStreamReader(is));

			String output;
			StringBuffer response = new StringBuffer();

			while ((output = iny.readLine()) != null) {
				response.append(output);
			}
			iny.close();

			JSONObject objRes = new JSONObject(response.toString());

			if (responseCode >= 400) {
				responseMap.put("msg", objRes.toString());
				responseMap.put("success", false);
				return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
			}

			responseMap.put("msg", "Plan Updated successfully");
			responseMap.put("data", objRes.optString("id", ""));
			responseMap.put("success", true);
			return ResponseEntity.ok(responseMap);

		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("msg", "Plan Updated Failed");
			responseMap.put("success", false);
			return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/create-session")
	public ResponseEntity<ZohoPaymentSessionResponse> createPaymentSession(
			@RequestBody CreatePaymentSessionRequestDto requestDto) {

		return ResponseEntity.ok(zohoPaymentService.createSession(requestDto));
	}

	@GetMapping("/getplanhistorybyuser")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getPlanHistoryByUser(@RequestParam("userId") Long userId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<UserPlansHistoryResponseDto> responseDto = userPlansHistoryService.getPlanHistoryByUser(userId);
			if (responseDto.isEmpty()) {
				response.put("msg", "User Plan History Not Found");
				response.put("success", false);
			} else {
				Map<String, Object> userPlansResp = new HashMap<>();
				userPlansResp.put("User Plans Histories", responseDto);
				response.put("data", userPlansResp);
				response.put("msg", "User Plan History Found");
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

	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("Server is running");
	}

	@GetMapping("/renewal-customer-info")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getRenewalInfo(@RequestParam String startDate,
			@RequestParam String endDate, @RequestParam boolean isActive) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
			LocalDateTime end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59, 999_000_000);

			return ResponseEntity.ok(userPlansHistoryService.getRenewalCustomerInfo(start, end, isActive));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(ResponseUtils.createFailedRespones(e.getLocalizedMessage()));
		}
	}

	@PutMapping("/updateuserplandate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateUserPlanDate(@RequestParam("date") String date,
			@RequestParam("userId") Long userId, @RequestParam(value = "otp", required = false) String otp) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = userPlansHistoryService.updateUserPlanDate(date, userId, otp);
			if (isSuccess) {
				response.put("msg", "User Plan Date Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "User Plan Date Updated Failed");
				response.put("success", isSuccess);
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
}
