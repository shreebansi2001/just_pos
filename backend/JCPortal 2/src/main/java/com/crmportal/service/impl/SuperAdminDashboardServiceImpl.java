package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.PlansEntity;
import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.repository.InvoiceEntityRepository;
import com.crmportal.repository.PaymentInfoRepository;
import com.crmportal.repository.PlansRepository;
import com.crmportal.repository.RoleMasterRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.SimpleMenuPlanReportRequestDto;
import com.crmportal.response.dto.DateWiseUserCountResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.SuperAdminDashboardService;
import com.crmportal.utility.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class SuperAdminDashboardServiceImpl implements SuperAdminDashboardService {

	@Autowired
	CommonService commonService;
	
	@Autowired
	PaymentInfoRepository pymentInfoRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	PlansRepository plansRepository;
	
	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;
	
	@Autowired
	RoleMasterRepository roleMasterRepository;
	
	@Autowired
	InvoiceEntityRepository invoiceRepository;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private final static String DATE_FORMAT = "dd/MM/yyyy";
	  private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
	  private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
	

	public Map<String, Object> planWiseTotal() {
		try {
			List<Object[]> data = pymentInfoRepository.getPlanWiseTotalAmountReceived();
			ArrayNode arrayNode = objectMapper.createArrayNode();
			for (Object[] row : data) {

		        Long planId = row[0] != null ? ((Number) row[0]).longValue() : null;
		        String planName = row[1] != null ? row[1].toString() : "";
		        Double totalAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

		        ObjectNode obj = objectMapper.createObjectNode();
		        obj.put("planId", planId);
		        obj.put("planName", planName);
		        obj.put("totalAmountReceived", totalAmount);

		        arrayNode.add(obj);
		    }
			
			 return ResponseUtils.createSuccessRespones(arrayNode, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}
	
	public Map<String, Object> getTotalUserAndPlanData() {
		try {
			List<Object[]> data = pymentInfoRepository.getPlanWiseTotalAmountReceived();
			ArrayNode arrayNode = objectMapper.createArrayNode();
			for (Object[] row : data) {

		        Long planId = row[0] != null ? ((Number) row[0]).longValue() : null;
		        String planName = row[1] != null ? row[1].toString() : "";
		        Double totalAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

		        ObjectNode obj = objectMapper.createObjectNode();
		        obj.put("planId", planId);
		        obj.put("planName", planName);
		        obj.put("totalAmountReceived", totalAmount);

		        arrayNode.add(obj);
		    }
			ObjectNode finalResponse = objectMapper.createObjectNode();
			
			RoleMasterEntity role = roleMasterRepository.findById(2l).orElseThrow(() -> new RuntimeException("Role not found with id: " + 2));
			
			/* Member */
			Long totalMember = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Member", false, true);
			Long totalActiveMember = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Member", true, true);
			Long totalInActiveMember = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Member", true, false);

			/* Demo */
			Long totalDemo = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Demo", false, true);
			Long totalActiveDemo = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Demo", false, true);
			Long totalInActiveDemo = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Demo", false, false);
			
			/* Demo */
			Long totalLead = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Lead", false, true);
			Long totalActiveLead = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Lead", false, true);
			Long totalInActiveLead = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "Lead", false, false);

			/* ALL */
			Long totalAllUser = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "ALL", false, true);
			Long totalActiveUser = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "ALL", true, true);
			Long totalInActiveUser = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), "ALL", true, false);
			
			
			Double totalPlanBaseAmount = pymentInfoRepository.getTotalPlanBaseAmount();

			/* Member Active */
			finalResponse.put("totalMember", totalMember);
			
			List<Object[]> results = pymentInfoRepository.getPaymentSummary("MEMBER", true, true);
			
			Double totalAmount = 0.0;
			Double paidAmount = 0.0;
			Double unpaidAmount = 0.0;

			if (results != null && !results.isEmpty()) {
			    Object[] row = results.get(0);

			    totalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    paidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    unpaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
			finalResponse.put("totalActiveMember", totalActiveMember);
	        finalResponse.put("activeMemberUserTotalAmount", totalAmount);
	        finalResponse.put("activeMemberUserPaidAmount", paidAmount);
	        finalResponse.put("activeMemberUserUnpaidAmount", unpaidAmount);
	        
	        /* Member InActive */
			List<Object[]> inActiveMembers = pymentInfoRepository.getPaymentSummary("MEMBER", true, false);
			
			Double inActiveMemebertotalAmount = 0.0;
			Double inActiveMemberPaidAmount = 0.0;
			Double inActiveUnpaidAmount = 0.0;

			if (inActiveMembers != null && !inActiveMembers.isEmpty()) {
			    Object[] row = results.get(0);

			    inActiveMemebertotalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    inActiveMemberPaidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    inActiveUnpaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
			
			finalResponse.put("totalInActiveMember", totalInActiveMember);
	        finalResponse.put("inActiveMemebertotalAmount", inActiveMemebertotalAmount);
	        finalResponse.put("inActiveMemberPaidAmount", inActiveMemberPaidAmount);
	        finalResponse.put("inActiveUnpaidAmount", inActiveUnpaidAmount);
	        
	        
	        /* Demo Active */
	        finalResponse.put("totalDemoUser", totalDemo);
	        
			List<Object[]> demoUser = pymentInfoRepository.getPaymentSummary("DEMO", true, true);
			
			Double demoUserTotalAmount = 0.0;
			Double demoUserPaidAmount = 0.0;
			Double demoUserUnPaidAmount = 0.0;

			if (demoUser != null && !demoUser.isEmpty()) {
			    Object[] row = demoUser.get(0);

			    demoUserTotalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    demoUserPaidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    demoUserUnPaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
			
			finalResponse.put("totalActiveDemo", totalActiveDemo);
	        finalResponse.put("demoUserTotalAmount", demoUserTotalAmount);
	        finalResponse.put("demoUserPaidAmount", demoUserPaidAmount);
	        finalResponse.put("demoUserUnpaidAmount", demoUserUnPaidAmount);
	        
	        /* Demo InActive */
			List<Object[]> demoInActiveUser = pymentInfoRepository.getPaymentSummary("DEMO", true, false);
			
			Double demoInActiveUserTotalAmount = 0.0;
			Double demoInActiveUserPaidAmount = 0.0;
			Double demoInActiveUserUnPaidAmount = 0.0;

			if (demoInActiveUser != null && !demoInActiveUser.isEmpty()) {
			    Object[] row = demoInActiveUser.get(0);

			    demoInActiveUserTotalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    demoInActiveUserPaidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    demoInActiveUserUnPaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
			
			finalResponse.put("totalInActiveDemo", totalInActiveDemo);
	        finalResponse.put("demoInActiveUserTotalAmount", demoInActiveUserTotalAmount);
	        finalResponse.put("demoInActiveUserPaidAmount", demoInActiveUserPaidAmount);
	        finalResponse.put("demoInActiveUserUnPaidAmount", demoInActiveUserUnPaidAmount);
	        
	        /* Lead Active */
	        finalResponse.put("totalLead", totalLead);
	        
			List<Object[]> leadUser = pymentInfoRepository.getPaymentSummary("LEAD", true, true);
			
			Double leadUserTotalAmount = 0.0;
			Double leadUserPaidAmount = 0.0;
			Double leadUserUnPaidAmount = 0.0;

			if (leadUser != null && !leadUser.isEmpty()) {
			    Object[] row = leadUser.get(0);

			    leadUserTotalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    leadUserPaidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    leadUserUnPaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
			
			finalResponse.put("totalActiveLead", totalActiveLead);
	        finalResponse.put("leadUserTotalAmount", leadUserTotalAmount);
	        finalResponse.put("leadUserPaidAmount", leadUserPaidAmount);
	        finalResponse.put("leadUserUnPaidAmount", leadUserUnPaidAmount);
	        
	        /* Lead InActive */
			List<Object[]> leadInActiveUser = pymentInfoRepository.getPaymentSummary("DEMO", true, false);
			
			Double leadInActiveUserTotalAmount = 0.0;
			Double leadInActiveUserPaidAmount = 0.0;
			Double leadInActiveUserUnPaidAmount = 0.0;

			if (leadInActiveUser != null && !leadInActiveUser.isEmpty()) {
			    Object[] row = leadInActiveUser.get(0);

			    leadInActiveUserTotalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    leadInActiveUserPaidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    leadInActiveUserUnPaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
			
			finalResponse.put("totalInActiveLead", totalInActiveLead);
	        finalResponse.put("leadInActiveUserTotalAmount", leadInActiveUserTotalAmount);
	        finalResponse.put("leadInActiveUserPaidAmount", leadInActiveUserPaidAmount);
	        finalResponse.put("leadInActiveUserUnPaidAmount", leadInActiveUserUnPaidAmount);
			
	        /* All */
	        finalResponse.put("totalAllUser", totalAllUser);

	        List<Object[]> allUser = pymentInfoRepository.getPaymentSummary("ALL", false, false);
			
			Double allUserTotalAmount = 0.0;
			Double allUserPaidAmount = 0.0;
			Double allUserUnPaidAmount = 0.0;

			if (allUser != null && !allUser.isEmpty()) {
			    Object[] row = allUser.get(0);

			    allUserTotalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    allUserPaidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    allUserUnPaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
			finalResponse.put("allUserTotalAmount", allUserTotalAmount);
	        finalResponse.put("allUserPaidAmount", allUserPaidAmount);
	        finalResponse.put("allUserUnPaidAmount", allUserUnPaidAmount);
	        
	        /* All Active Users */
	        List<Object[]> allActiveUser = pymentInfoRepository.getPaymentSummary("ALL", true, true);
			
			Double allActiveUserTotalAmount = 0.0;
			Double allActiveUserPaidAmount = 0.0;
			Double allActiveUserUnPaidAmount = 0.0;

			if (allActiveUser != null && !allActiveUser.isEmpty()) {
			    Object[] row = allActiveUser.get(0);

			    allActiveUserTotalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    allActiveUserPaidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    allActiveUserUnPaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
	        finalResponse.put("totalActiveUser", totalActiveUser);
			finalResponse.put("allActiveUserTotalAmount", allActiveUserTotalAmount);
	        finalResponse.put("allActiveUserPaidAmount", allActiveUserPaidAmount);
	        finalResponse.put("allActiveUserUnPaidAmount", allActiveUserUnPaidAmount);
	        
	        /* All InActive Users */
			List<Object[]> allInActiveUser = pymentInfoRepository.getPaymentSummary("ALL", true, false);
			
			Double allInActiveUserTotalAmount = 0.0;
			Double allInActiveUserPaidAmount = 0.0;
			Double allInActiveUserUnPaidAmount = 0.0;

			if (allInActiveUser != null && !allInActiveUser.isEmpty()) {
			    Object[] row = allInActiveUser.get(0);

			    allInActiveUserTotalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			    allInActiveUserPaidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			    allInActiveUserUnPaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
			}
			finalResponse.put("totalInActiveUser", totalInActiveUser);
			finalResponse.put("allInActiveUserTotalAmount", allInActiveUserTotalAmount);
	        finalResponse.put("allInActiveUserPaidAmount", allInActiveUserPaidAmount);
	        finalResponse.put("allInActiveUserUnPaidAmount", allInActiveUserUnPaidAmount);
	        
	        finalResponse.set("planData", arrayNode);
	        
			return ResponseUtils.createSuccessRespones(finalResponse, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}
	
	public Map<String, Object> getMonthWisePlanTotal(Long planId, String startDate, String endDate) {
		try {
		    ArrayNode arrayNode = objectMapper.createArrayNode();
	
		    // Convert dd-MM-yyyy to LocalDateTime (Java 8 compatible)
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		    LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
		    LocalDateTime end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
	
		    List<Object[]> result;
	
		    if (planId == 0) {
		        result = pymentInfoRepository.getMonthWiseTotalNoPlan(start, end);
		    } else {
		        result = pymentInfoRepository.getMonthWiseTotalByPlan(planId, start, end);
		    }
	
		    // Java 8 month names
		    String[] monthNames = {
		        "January", "February", "March", "April", "May", "June",
		        "July", "August", "September", "October", "November", "December"
		    };
	
		    for (Object[] row : result) {
	
		        Integer monthNum = ((Number) row[0]).intValue(); // 1 to 12
		        Integer year = ((Number) row[1]).intValue();
		        Double total = ((Number) row[2]).doubleValue();
	
		        String monthName = monthNames[monthNum - 1];
	
		        ObjectNode obj = objectMapper.createObjectNode();
		        obj.put("month", monthName);
		        obj.put("year", year);
		        obj.put("total", total);
	
		        arrayNode.add(obj);
		    }

		    return ResponseUtils.createSuccessRespones(arrayNode, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}
	
	public Map<String, Object> getUsersDetailsBetweenDates(String startDate, String endDate) {
		try {
			ArrayNode arrayNode = objectMapper.createArrayNode();

	        LocalDateTime start = LocalDate.parse(startDate, INPUT_FORMATTER).atStartOfDay();
	        LocalDateTime endDT = LocalDate.parse(endDate, INPUT_FORMATTER).atTime(23, 59, 59);

	        List<Object[]> list = userMasterRepository.getUsersWithPlanNative(start, endDT);

	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a");

	        for (Object[] row : list) {

	            ObjectNode obj = objectMapper.createObjectNode();

	            // Extract fields
	            String firstName = row[1] != null ? row[1].toString() : "";
	            String lastName = row[2] != null ? row[2].toString() : "";

	            obj.put("fullName", (firstName + " " + lastName).trim());
	            obj.put("email", row[3] != null ? row[3].toString() : "");
	            obj.put("mobileNo", row[4] != null ? row[4].toString() : "");
	            obj.put("isActive", row[5] != null ? (Boolean) row[5] : false);
	            obj.put("isApproved", row[6] != null ? (Boolean) row[6] : false);
	            obj.put("userCode", row[7] != null ? row[7].toString() : "");
	            obj.put("preFix", row[8] != null ? row[8].toString() : "");

	            // Created at
	            LocalDateTime createdAt = (LocalDateTime) row[9];
	            obj.put("createdAt", createdAt != null ? createdAt.format(dtFormatter) : "");

	            // Basic details
	            obj.put("companyName", row[10] != null ? row[10].toString() : "");
	            obj.put("companyEmail", row[11] != null ? row[11].toString() : "");
	            obj.put("memberType", row[12] != null ? row[12].toString() : "");

	            // Plan details
	            Long planId = row[13] != null ? ((Number) row[13]).longValue() : 0L;
	            obj.put("planId", planId);
	            obj.put("planName", row[14] != null ? row[14].toString() : "");

	            LocalDateTime ps = (LocalDateTime) row[15];
	            LocalDateTime pe = (LocalDateTime) row[16];

	            obj.put("planStartDate", ps != null ? ps.format(dateFormatter) : "");
	            obj.put("planEndDate", pe != null ? pe.format(dateFormatter) : "");

	            arrayNode.add(obj);
	        }
			return ResponseUtils.createSuccessRespones(arrayNode, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
        
    }
	
	public List<DateWiseUserCountResponseDto> getDateWiseUserCount(String startDate, String endDate, Long planId) {
	    
		if(planId != null && planId != -1) {
			PlansEntity plan = plansRepository.findById(planId).orElseThrow(() -> new RuntimeException("Plan not found with id : " + planId));
		}
		List<Object[]> result = userMasterRepository.getUserCountBetweenDates(startDate, endDate, planId);

	    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    return result.stream()
	            .map(obj -> {
	                LocalDate date = LocalDate.parse(obj[0].toString(), inputFormatter);
	                String formattedDate = date.format(outputFormatter);

	                return new DateWiseUserCountResponseDto(
	                        formattedDate,
	                        ((Number) obj[1]).longValue()
	                );
	            })
	            .collect(Collectors.toList());
	}
	
	@Override
	public Map<String, Object> getChartData(String startDate, String endDate, Long planId) {
		try {
			List<DateWiseUserCountResponseDto> data = getDateWiseUserCount(startDate, endDate, planId);

			return ResponseUtils.createSuccessRespones(data, "Data Found Successfully.");
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}
	
	@Override
	public Map<String, Object> getInvoiceData() {
		try {
			Long totalInvoice = invoiceRepository.count();
			BigDecimal totalAmount = invoiceRepository.getTotalAmt();
			
			Map<String, Object> response = new HashMap<>();
			
			response.put("totalInvoice", totalInvoice);
			response.put("totalAmount", totalAmount);
			
			return response;
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}
}
