package com.crmportal.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.entity.AdminTemplateModuleEntity;
import com.crmportal.entity.PlansEntity;
import com.crmportal.repository.AdminTemplateModuleRepository;
import com.crmportal.request.dto.ExclusivePaymentRequestDto;
import com.crmportal.request.dto.ExclusiveThemePaymentRequestDto;
import com.crmportal.request.dto.UserPlansHistoryRequestDto;
import com.crmportal.response.dto.ExclusiveThemePaymentResponseDto;
import com.crmportal.service.ExclusiveThemePaymentService;

@RestController
@RequestMapping({ "/v1/api/exclusivethemeoayment" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class ExclusiveThemePaymentController {

	@Autowired
	ExclusiveThemePaymentService exclusiveThemePaymentService;

	@Value("${rasor_key}")
	private String rasor_key;

	@Value("${rasor_secret_key}")
	private String rasor_secret_key;

	@Autowired
	AdminTemplateModuleRepository adminTemplateModuleRepository;

	@PostMapping
	public ResponseEntity<Map<String, Object>> addExclusiveThemePayment(
			@RequestBody ExclusivePaymentRequestDto request) {
		Map<String, Object> response = new HashMap<>();

		try {
			ExclusiveThemePaymentResponseDto responseDto = exclusiveThemePaymentService
					.addExclusiveThemePayment(request);
			if (responseDto != null) {
				response.put("data", responseDto);
				response.put("msg", "Payment Done.");
				response.put("success", true);
			} else {
				response.put("msg", "Payment failed.");
				response.put("success", false);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@PostMapping("/createThemePayment")
	public ResponseEntity<Map<String, Object>> createPaymentOrder(@RequestBody ExclusiveThemePaymentRequestDto request)
			throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		try {
			// ❌ REMOVE TLS override (can cause issues)
			// SSLContext ctx = SSLContext.getInstance("TLSv1.2");
			// ctx.init(null, null, null);
			// SSLContext.setDefault(ctx);

			String apiKey = rasor_key.trim();
			String secretKey = rasor_secret_key.trim();

			AdminTemplateModuleEntity adminTemplateModuleEntity = adminTemplateModuleRepository
					.findByIdAndIsDeleteFalse(request.getAdminTemplateId()).orElseThrow(() -> new RuntimeException(
							"Admin Template not found with id: " + request.getAdminTemplateId()));

			String url = "https://api.razorpay.com/v1/orders";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");

			String userpass = apiKey + ":" + secretKey;
			String basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);

			double tamount = request.getPrice().doubleValue();

			String data = "{" + "\"amount\":" + Math.round(tamount * 100) + "," + "\"currency\":\"INR\","
					+ "\"receipt\":\"ET-" + adminTemplateModuleEntity.getUserId() + "\"," + "\"payment_capture\":1"
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

			responseMap.put("msg", "Payment successfully");
			responseMap.put("data", objRes.optString("id", ""));
			responseMap.put("success", true);
			return ResponseEntity.ok(responseMap);

		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("msg", "Payment Failed");
			responseMap.put("success", false);
			return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
