package com.crmportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crmportal.service.DashboardService;
import com.crmportal.service.SuperAdminDashboardService;

@RestController
@RequestMapping({ "/v1/api/dashboard" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class DashboardController {

	@Autowired
	DashboardService dashboardService;

	@Autowired
	SuperAdminDashboardService superAdminDashboardService;

	@GetMapping("/admin/userWiseDashboardData")
	public ResponseEntity<Map<String, Object>> userWiseDashboardData(@RequestParam(name = "userId") Long userId) {
		return ResponseEntity.ok(dashboardService.userWiseDashboardData(userId));
	}

	@GetMapping("/admin/userWiseDashboardPieChart1")
	public ResponseEntity<Map<String, Object>> userWiseDashboardPieChart1(@RequestParam(name = "userId") Long userId,
			@RequestParam(name = "dateString") String dateString) {
		return ResponseEntity.ok(dashboardService.userWiseDashboardCostingDataPieChart1(userId, dateString));
	}

	@GetMapping("/admin/userWiseSalesInvoicePieChart2")
	public ResponseEntity<Map<String, Object>> userWiseSalesInvoicePieChart2(@RequestParam(name = "userId") Long userId,
			@RequestParam(name = "dateString") String dateString) {
		return ResponseEntity.ok(dashboardService.userWiseSalesInvoiceDataPieChart2(userId, dateString));
	}

	@GetMapping("/admin/userWiseEventQuotationPieChart3")
	public ResponseEntity<Map<String, Object>> userWiseEventQuotationPieChart3(
			@RequestParam(name = "userId") Long userId, @RequestParam(name = "dateString") String dateString) {
		return ResponseEntity.ok(dashboardService.userWiseEventQuotationDataPieChart3(userId, dateString));
	}

	@GetMapping("/admin/getEventsByUserAndDate")
	public ResponseEntity<Map<String, Object>> getEventsByUserAndDate(@RequestParam(name = "userId") Long userId,
			@RequestParam(name = "startDate") String startDate, @RequestParam(name = "endDate") String endDate) {
		return ResponseEntity.ok(dashboardService.getEventsByUserAndDateJson(userId, startDate, endDate));
	}

	@GetMapping("/superadmin/planWiseTotal")
	public ResponseEntity<Map<String, Object>> planWiseTotal() {
		return ResponseEntity.ok(superAdminDashboardService.planWiseTotal());
	}

	@GetMapping("/superadmin/getTotalUserAndPlanData")
	public ResponseEntity<Map<String, Object>> getTotalUserAndPlanData() {
		return ResponseEntity.ok(superAdminDashboardService.getTotalUserAndPlanData());
	}

	@GetMapping("/superadmin/getMonthWisePlanTotal")
	public ResponseEntity<Map<String, Object>> getMonthWisePlanTotal(@RequestParam(name = "planId") Long planId,
			@RequestParam(name = "startDate") String startDate, @RequestParam(name = "endDate") String endDate) {
		return ResponseEntity.ok(superAdminDashboardService.getMonthWisePlanTotal(planId, startDate, endDate));
	}

	@GetMapping("/superadmin/getUsersDetailsBetweenDates")
	public ResponseEntity<Map<String, Object>> getUsersDetailsBetweenDates(
			@RequestParam(name = "startDate") String startDate, @RequestParam(name = "endDate") String endDate) {
		return ResponseEntity.ok(superAdminDashboardService.getUsersDetailsBetweenDates(startDate, endDate));

	}

	@GetMapping("/admin/getMostSellingItems")
	public ResponseEntity<Map<String, Object>> getMostSellingItems(@RequestParam(name = "startDate") String startDate,
			@RequestParam(name = "endDate") String endDate, @RequestParam("userId") Long userId) {
		return ResponseEntity.ok(dashboardService.getMostSellingItems(startDate, endDate, userId));

	}

	@GetMapping("/superadmin/getChartData")
	public ResponseEntity<?> getChartData(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("planId") Long planId) {
		return ResponseEntity.ok(superAdminDashboardService.getChartData(startDate, endDate, planId));
	}

	@GetMapping("/superadmin/getInvoiceData")
	public ResponseEntity<?> getInvoiceData() {
		return ResponseEntity.ok(superAdminDashboardService.getInvoiceData());
	}

	@GetMapping("/admin/summery")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getSummery(@RequestParam("userId") Long userId) {

		Map<String, Object> response = new HashMap<>();

		try {
			Map<String, Object> summery = dashboardService.getSummery(userId);
			response.put("data", summery);
			response.put("msg", "Data Found");
			response.put("success", true);
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
