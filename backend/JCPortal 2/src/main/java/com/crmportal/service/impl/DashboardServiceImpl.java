package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.repository.EventExtraExpenseRepository;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventFunctionQuotationItemRepository;
import com.crmportal.repository.EventFunctionQuotationRepository;
import com.crmportal.repository.EventInvoiceRepository;
import com.crmportal.repository.EventLaborRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.repository.EvetFunctionMenuAllocationOrderRepository;
import com.crmportal.repository.MenuAllocationItemRawMaterialRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.Top50UserResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DashboardService;
import com.crmportal.service.EventFunctionMasterService;
import com.crmportal.utility.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventInvoiceRepository eventInvoiceRepository;

	@Autowired
	EventFunctionQuotationItemRepository eventFunctionQuotationItemRepository;

	@Autowired
	EvetFunctionMenuAllocationOrderRepository evetFunctionMenuAllocationOrderRepository;

	@Autowired
	MenuAllocationItemRawMaterialRepository menuAllocationItemRawMaterialRepository;

	@Autowired
	EventExtraExpenseRepository eventExtraExpenseRepository;

	@Autowired
	EventFunctionMasterService eventFunctionMasterService;

	@Autowired
	EventLaborRepository eventLaborRepository;

	@Autowired
	EventFunctionQuotationRepository eventFunctionQuotationRepository;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Autowired
	MenuPreparationRepository menuPreparationRepository;

	@Autowired
	MenuItemMasterRepository itemMasterRepository;

	@Autowired
	EventFunctionMenuAllocationRepository allocationRepository;

	@Autowired
	EventRawMaterialRepository eventRawMaterialRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final String DATE_FORMAT = "dd/MM/yyyy";

	public Map<String, Object> userWiseDashboardData(Long userId) {
		try {
			Long totalEvent = eventMasterRepository.countByUser_IdAndIsDeleteFalse(userId);

			Long totalInvoice = eventInvoiceRepository.countByEvent_User_IdAndIsDeleteFalse(userId);

			BigDecimal totalInvoiceAmount = eventInvoiceRepository.getTotalAmountByUserId(userId);

			Long totalQuotationAmount = eventFunctionQuotationRepository.getTotalGrandTotalByUserId(userId).longValue();

			ObjectNode node = objectMapper.createObjectNode();
			node.put("totalEvent", totalEvent);
			node.put("totalInvoice", totalInvoice);
			node.put("totalInvoiceAmount", totalInvoiceAmount);
			node.put("totalQuotationAmount", totalQuotationAmount);

			return ResponseUtils.createSuccessRespones(node, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}

	public Map<String, Object> userWiseDashboardCostingDataPieChart1(Long userId, String dateString) {
		LocalDateTime givenDate = null;
		try {
			Double cheflaborcharge = 0.0;
			Double outsideagencycharge = 0.0;
			Double laborcharge = 0.0;
			Double rawmaterialcharge = 0.0;
			Double extraexpensecharge = 0.0;

			if (dateString != null && !dateString.trim().isEmpty()) {
				givenDate = convertStringDateToLocalDate(dateString, DATE_FORMAT);
				cheflaborcharge = evetFunctionMenuAllocationOrderRepository
						.getChefLabourTotalByUserAndDate(userId, givenDate).doubleValue();
				outsideagencycharge = evetFunctionMenuAllocationOrderRepository
						.getOutsideTotalByUserAndDate(userId, givenDate).doubleValue();
				laborcharge = eventLaborRepository.getTotalLaborPriceByUserAndDate(userId, givenDate);
				rawmaterialcharge = menuAllocationItemRawMaterialRepository.getTotalRateByUserAndDate(userId, givenDate)
						.doubleValue();
				extraexpensecharge = eventExtraExpenseRepository.getTotalExtraExpenseByUserAndDate(userId, givenDate);
			} else {
				cheflaborcharge = evetFunctionMenuAllocationOrderRepository.getChefLabourTotalByUser(userId)
						.doubleValue();
				outsideagencycharge = evetFunctionMenuAllocationOrderRepository.getOutsideTotalByUser(userId)
						.doubleValue();
				laborcharge = eventLaborRepository.getTotalLaborPriceByUser(userId);
				rawmaterialcharge = menuAllocationItemRawMaterialRepository.getTotalRateByUser(userId).doubleValue();
				extraexpensecharge = eventExtraExpenseRepository.getTotalExtraExpenseByUser(userId);
			}

			ObjectNode node = objectMapper.createObjectNode();
			node.put("cheflaborcharge", cheflaborcharge);
			node.put("outsideagencycharge", outsideagencycharge);
			node.put("laborcharge", laborcharge);
			node.put("rawmaterialcharge", rawmaterialcharge);
			node.put("extraexpensecharge", extraexpensecharge);

			return ResponseUtils.createSuccessRespones(node, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}

	private LocalDateTime convertStringDateToLocalDate(String strDate, String dateFormat) {
		LocalDate givenDate = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		givenDate = LocalDate.parse(strDate, formatter);
		LocalDateTime givenDateTime = givenDate.atStartOfDay();
		return givenDateTime;
	}

	public Map<String, Object> userWiseSalesInvoiceDataPieChart2(Long userId, String dateString) {
		LocalDateTime givenDate = null;
		try {
			givenDate = convertStringDateToLocalDate(dateString, DATE_FORMAT);
			Long dateWiseTotalInvoice = eventInvoiceRepository.getTotalGrandTotal(userId, givenDate).longValue();
			Long dateWiseRemainingInvoice = eventInvoiceRepository.getTotalRemainingTotal(userId, givenDate)
					.longValue();
			Long paidTotalIncoice = dateWiseTotalInvoice - dateWiseRemainingInvoice;

			ObjectNode node = objectMapper.createObjectNode();
			node.put("totalSales", dateWiseTotalInvoice);
			node.put("totalSalesRemaining", dateWiseRemainingInvoice);
			node.put("totalSalesPaid", paidTotalIncoice);

			return ResponseUtils.createSuccessRespones(node, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}

	public Map<String, Object> userWiseEventQuotationDataPieChart3(Long userId, String dateString) {
		LocalDateTime givenDate = null;
		try {
			givenDate = convertStringDateToLocalDate(dateString, DATE_FORMAT);
			Long dateWiseTotalQuotation = eventFunctionQuotationRepository
					.getTotalGrandTotalQuotation(userId, givenDate).longValue();
			Long dateWiseRemainingQuotation = eventFunctionQuotationRepository
					.getTotalRemainingTotalQuotation(userId, givenDate).longValue();
			Long paidTotalQuotation = dateWiseTotalQuotation - dateWiseRemainingQuotation;

			ObjectNode node = objectMapper.createObjectNode();
			node.put("totalQuotaion", dateWiseTotalQuotation);
			node.put("totalQuotaionRemaining", dateWiseRemainingQuotation);
			node.put("totalQuotaionPaid", paidTotalQuotation);
			return ResponseUtils.createSuccessRespones(node, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}

	public Map<String, Object> getEventsByUserAndDateJson(Long userId, String startDate, String endDate) {
		try {
			// Parse incoming dd-MM-yyyy
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

			LocalDate start = LocalDate.parse(startDate, inputFormatter);
			LocalDate end = LocalDate.parse(endDate, inputFormatter);

			// Inclusive start and end datetime
			LocalDateTime startDateTime = start.atStartOfDay(); // 00:00:00
			LocalDateTime endDateTime = end.atTime(23, 59, 59); // 23:59:59

			List<EventMasterEntity> list = eventMasterRepository.getEventsByUserAndDateRange(userId, startDateTime,
					endDateTime);

			ArrayNode arrayNode = objectMapper.createArrayNode();

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

			for (EventMasterEntity e : list) {
				ObjectNode node = objectMapper.createObjectNode();

				node.put("id", e.getId());
				node.put("eventNo", e.getEventNo());
				node.put("eventName", e.getEventType().getNameEnglish());
				node.put("venueName", e.getVenue() != null ? e.getVenue().getNameEnglish() : e.getBanquetHall() != null ? e.getBanquetHall().getHallName() : "");
				node.put("inquiryDate", e.getInquiryDate() != null ? e.getInquiryDate().format(dateFormatter) : null);

				node.put("eventStartDateTime",
						e.getEventStartDateTime() != null ? e.getEventStartDateTime().format(dateTimeFormatter) : null);

				node.put("eventEndDateTime",
						e.getEventEndDateTime() != null ? e.getEventEndDateTime().format(dateTimeFormatter) : null);

				node.put("status", e.getStatus() == 0 ? "Inquiry" : e.getStatus() == 1 ? "Confirm" : e.getStatus() == 2 ? "Cancle" : "Tentative");
				node.put("mobileno", e.getMobileno());

				String userFullName = e.getParty().getNameEnglish();

				node.put("userFullName", userFullName);

				arrayNode.add(node);
			}

			return ResponseUtils.createSuccessRespones(arrayNode, "Data fetched successfully");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}

	public Map<String, Object> getTop10SellingMenuItems() {
		try {
			List<Object[]> result = menuPreparationDetailsRepository.getTopSellingMenuItems();

			ArrayNode arrayNode = objectMapper.createArrayNode();

			for (Object[] row : result) {

				Long itemId = row[0] != null ? ((Number) row[0]).longValue() : null;
				String nameEnglish = row[1] != null ? row[1].toString() : "";
				String nameHindi = row[2] != null ? row[2].toString() : "";
				String nameGujarati = row[3] != null ? row[3].toString() : "";
				String slogan = row[4] != null ? row[4].toString() : "";
				Long totalCount = row[5] != null ? ((Number) row[5]).longValue() : 0L;

				ObjectNode node = objectMapper.createObjectNode();
				node.put("itemId", itemId);
				node.put("nameEnglish", nameEnglish);
				node.put("nameHindi", nameHindi);
				node.put("nameGujarati", nameGujarati);
				node.put("slogan", slogan);
				node.put("count", totalCount);

				arrayNode.add(node);
			}
			return ResponseUtils.createSuccessRespones(arrayNode, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}

	}

	@Override
	public Map<String, Object> getMostSellingItems(String startDate, String endDate, Long userId) {
		Map<String, Object> response = new HashMap<>();

		try {
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate sDate = LocalDate.parse(startDate, inputFormatter);
			LocalDate eDate = LocalDate.parse(endDate, inputFormatter);

			if (sDate.isAfter(eDate)) {
				response.put("success", false);
				response.put("message", "Start date cannot be after end date");
				response.put("data", null);
				return response;
			}

			String startISO = sDate.toString();
			String endISO = eDate.toString();

			List<Object[]> results = itemMasterRepository.getMostSellingItems(startISO, endISO, userId);

			List<Map<String, Object>> items = results.stream().map(row -> {
				Map<String, Object> item = new HashMap<>();
				item.put("menuItemId", safeNumberToLong(row[0]));
				item.put("nameEnglish", row[1] != null ? row[1].toString() : "");
				item.put("qtyCurrent", safeNumberToInt(row[2]));
				item.put("qtyPrev", safeNumberToInt(row[3]));
				item.put("statusValue", row[4] != null ? row[4].toString() : "0");
				item.put("statusDirection", row[5] != null ? row[5].toString() : "neutral");
				return item;
			}).collect(Collectors.toList());

			response.put("success", true);
			response.put("message", "Data fetched successfully");
			response.put("data", items);

		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Error fetching data: " + e.getMessage());
			response.put("data", null);
			e.printStackTrace();
		}

		return response;
	}

	private Long safeNumberToLong(Object obj) {
		if (obj instanceof Number)
			return ((Number) obj).longValue();
		return null;
	}

	private Integer safeNumberToInt(Object obj) {
		if (obj instanceof Number)
			return ((Number) obj).intValue();
		return 0;
	}

	@Override
	public Map<String, Object> getSummery(Long userId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Map<String, Object> response = new HashMap<>();

		Integer eventCount = eventMasterRepository.getEventCountByUserId(userId);
		Integer menuCount = menuPreparationRepository.getMenuCountByUserId(userId);
		Integer quotationCount = eventFunctionQuotationRepository.getQuotationCount(userId);
		Integer menuAllocationCount = allocationRepository.getMenuAllocationCount(userId);
		Integer rawMatCount = eventRawMaterialRepository.getRawMatCount(userId);
		Integer invoiceCount = eventInvoiceRepository.getInvoiceCount(userId);
		Integer labourCount = eventLaborRepository.getLabourCount(userId);
		List<Object[]> result = new ArrayList<>();
		if (userId == -1) {
			result = userMasterRepository.getTop50Users();
		}
		List<Top50UserResponseDto> dtos = new ArrayList<>();
		if (!result.isEmpty()) {
			dtos = result.stream().map(obj -> {
				Top50UserResponseDto dto = new Top50UserResponseDto();

				dto.setUserId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
				dto.setUserName((String) obj[1]);
				dto.setCompanyName((String) obj[2]);
				dto.setCreatedAt(obj[3] != null ? formatDateTime(obj[3], dateFormatter) : null);
				dto.setStatus((String) obj[4]);

				return dto;
			}).collect(Collectors.toList());
		}
		response.put("topUserData", dtos);
		response.put("eventCount", eventCount);
		response.put("menuCount", menuCount);
		response.put("quotationCount", quotationCount);
		response.put("menuAllocationCount", menuAllocationCount);
		response.put("rawMaterialCount", rawMatCount);
		response.put("invoiceCount", invoiceCount);
		response.put("labourCount", labourCount);
		return response;
	}

	private String formatDateTime(Object obj, DateTimeFormatter formatter) {
		if (obj == null)
			return null;

		LocalDateTime dateTime = null;

		if (obj instanceof LocalDateTime) {
			dateTime = (LocalDateTime) obj;

		} else if (obj instanceof java.sql.Timestamp) {
			dateTime = ((java.sql.Timestamp) obj).toLocalDateTime();

		} else if (obj instanceof java.util.Date) {
			dateTime = new java.sql.Timestamp(((java.util.Date) obj).getTime()).toLocalDateTime();

		} else {
			dateTime = LocalDateTime.parse(obj.toString().replace(" ", "T"));
		}

		return dateTime.format(formatter);
	}

}
