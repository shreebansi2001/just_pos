package com.crmportal.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseUtils {

	public static Map<String, Object> createSuccessRespones(Object data, String message) {
		Map<String, Object> map = new HashMap<>();
		map.put("msg", message);
		map.put("success", true);
		map.put("data", data);

		return map;
	}

	public static Map<String, Object> createFailedRespones(String errorMessage) {
		Map<String, Object> map = new HashMap<>();
		map.put("msg", "FAILED");
		map.put("success", false);
		map.put("errorMessage", errorMessage);

		return map;
	}
	
	public static Map<String, Object> createFailedRespones(String error, String errorMessage) {
		Map<String, Object> map = new HashMap<>();
		map.put("msg", error);
		map.put("success", false);
		map.put("errorMessage", errorMessage);

		return map;
	}

	public static Map<String, Object> createFailedRespones(List<String> errorList) {
		Map<String, Object> map = new HashMap<>();
		map.put("msg", "FAILED");
		map.put("success", false);
		map.put("errorMessage", String.join(" ", errorList));

		return map;
	}

	public static Map<String, Object> createFailedRespones(String errorMessagge, Map<String, List<String>> errorMap) {
		Map<String, Object> map = new HashMap<>();
		map.put("msg", "FAILED");
		map.put("success", false);
		map.put("errorMessage", errorMap);

		return map;
	}
}
