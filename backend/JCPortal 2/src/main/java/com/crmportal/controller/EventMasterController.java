package com.crmportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.UserNotificationConfigEntity;
import com.crmportal.repository.UserNotificationConfigRepository;
import com.crmportal.repository.UserUpgradedModuleRepository;
import com.crmportal.request.dto.AssignEventToChildRequestDto;
import com.crmportal.request.dto.EventFunctionManagerAssignRequestDto;
import com.crmportal.request.dto.EventFunctionMasterRequestDto;
import com.crmportal.request.dto.EventFunctionVendorAssignmentRequestDto;
import com.crmportal.request.dto.EventMasterRequestDto;
import com.crmportal.request.dto.EventRemarksRequestDto;
import com.crmportal.request.dto.EventVendorAssignmentWrapperDto;
import com.crmportal.response.dto.AssignEventToChildResponseDto;
import com.crmportal.response.dto.BanquetShiftAvailabilityResponseDto;
import com.crmportal.response.dto.EventByDateResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionWithManagersResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.EventMenuAllocationOverviewResponseDto;
import com.crmportal.response.dto.ManagerEventsResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFunctionMasterService;
import com.crmportal.service.EventMasterService;
import com.crmportal.utility.ConstantsPoc;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.geom.PageSize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({ "/v1/api/eventmaster" })
@CrossOrigin(origins = { "*" }, maxAge = 3600L)
public class EventMasterController {

	@Autowired
	EventMasterService eventMasterService;

	@Autowired
	UserUpgradedModuleRepository userUpgradedModuleRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	UserNotificationConfigRepository userNotificationConfigRepository;

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventMaster(@Valid @RequestBody EventMasterRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventMasterResponseDto eventMasterResponseDto = eventMasterService.addOrUpdateEventMaster(request,
					Long.parseLong("-1"));
			if (eventMasterResponseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_MASTER_CREATE_SUCCESS);
				response.put("data", eventMasterResponseDto);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_MASTER_CREATE_FAIL);
				response.put("success", false);
			}
			UserNotificationConfigEntity dto = userNotificationConfigRepository
					.findNotificationByUserAndUpgradeModule(request.getUserId(), 20L);
			if (dto != null) {
				if (eventMasterResponseDto.getIsEventStatusChanged()) {
					List<String> dataList = new ArrayList<>();
					String companyName = eventMasterResponseDto.getCompanyName() != null
							? eventMasterResponseDto.getCompanyName()
							: "NA";
					String partyName = eventMasterResponseDto.getPartyName() != null
							? eventMasterResponseDto.getPartyName()
							: "NA";
					String partyMobileNo = eventMasterResponseDto.getPartyMobileNo() != null
							? eventMasterResponseDto.getPartyMobileNo()
							: "NA";
					String managerName = eventMasterResponseDto.getManagerName() != null
							? eventMasterResponseDto.getManagerName()
							: "NA";
					String companyMobileNo = eventMasterResponseDto.getCompanyMobileNo() != null
							? eventMasterResponseDto.getCompanyMobileNo()
							: "NA";
					String managerMobileNo = eventMasterResponseDto.getManagerMobileNo() != null
							? eventMasterResponseDto.getManagerMobileNo()
							: "NA";
					dataList.add(partyName);
					dataList.add(companyName);
					dataList.add(managerName);
					dataList.add(managerMobileNo);
					dataList.add(companyMobileNo);
					dataList.add(companyName);

					if (eventMasterResponseDto.getStatus() == 0) {
						commonService.sendEventInfoWhatsApp("event_inquiry_msg", partyMobileNo, dataList, null, false,
								request.getUserId());
					} else if (eventMasterResponseDto.getStatus() == 1) {
						commonService.sendEventInfoWhatsApp("event_confirm_msg", partyMobileNo, dataList, null, false,
								request.getUserId());
					}
				}
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

	@PutMapping("/update")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateEventMaster(@Valid @RequestBody EventMasterRequestDto request,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventMasterResponseDto eventMasterResponseDto = eventMasterService.addOrUpdateEventMaster(request, id);
			if (eventMasterResponseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_MASTER_UPDATE_SUCCESS);
				response.put("data", eventMasterResponseDto);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_MASTER_UPDATE_FAILED);
				response.put("success", false);
			}

			UserNotificationConfigEntity dto = userNotificationConfigRepository
					.findNotificationByUserAndUpgradeModule(request.getUserId(), 20L);
			if (dto != null) {
				if (eventMasterResponseDto.getIsEventStatusChanged()) {
					List<String> dataList = new ArrayList<>();
					String companyName = eventMasterResponseDto.getCompanyName() != null
							? eventMasterResponseDto.getCompanyName()
							: "NA";
					String partyName = eventMasterResponseDto.getPartyName() != null
							? eventMasterResponseDto.getPartyName()
							: "NA";
					String partyMobileNo = eventMasterResponseDto.getPartyMobileNo() != null
							? eventMasterResponseDto.getPartyMobileNo()
							: "NA";
					String managerName = eventMasterResponseDto.getManagerName() != null
							? eventMasterResponseDto.getManagerName()
							: "NA";
					String companyMobileNo = eventMasterResponseDto.getCompanyMobileNo() != null
							? eventMasterResponseDto.getCompanyMobileNo()
							: "NA";
					String managerMobileNo = eventMasterResponseDto.getManagerMobileNo() != null
							? eventMasterResponseDto.getManagerMobileNo()
							: "NA";
					dataList.add(partyName);
					dataList.add(companyName);
					dataList.add(managerName);
					dataList.add(managerMobileNo);
					dataList.add(companyMobileNo);
					dataList.add(companyName);

					if (eventMasterResponseDto.getStatus() == 0) {
						commonService.sendEventInfoWhatsApp("event_inquiry_msg", partyMobileNo, dataList, null, false,
								request.getUserId());
					} else if (eventMasterResponseDto.getStatus() == 1) {
						commonService.sendEventInfoWhatsApp("event_confirm_msg", partyMobileNo, dataList, null, false,
								request.getUserId());
					}
				}
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

	@GetMapping("/getallbyuserid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllByUserId(@RequestParam(value = "userId") Long userId,
			@RequestParam(value = "partyName", required = false) String partyName,
			@RequestParam(value = "isChildUser", required = false, defaultValue = "false") Boolean isChildUser,
			@RequestParam(defaultValue = "true") Boolean isVisible, @RequestParam(defaultValue = "-1") String month,
			@RequestParam(defaultValue = "-1") String year,
			@RequestParam(value = "status", defaultValue = "-1") Integer status) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventMasterResponseDto> responseDtos = eventMasterService.getAllByUserId(userId, partyName, isVisible,
					isChildUser, month, year, status);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.EVENT_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> eventResp = new HashMap<>();
				eventResp.put("Event Details", responseDtos);
				response.put("data", eventResp);
				response.put("msg", ConstantsPoc.EVENT_MASTER_FOUND_SUCCESS);
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

	@GetMapping("/getalleventbyfilter")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllEventByFilter(@RequestParam(value = "userId") Long userId,
			@RequestParam(value = "partyName", required = false) String partyName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam("eventDate") String eventDate, @RequestParam("eventStatus") Integer eventStatus,
			@RequestParam(value = "isChildUser", required = false, defaultValue = "false") Boolean isChildUser) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> responseDtos = eventMasterService.getAllEventByFilter(userId, partyName, startDate,
					endDate, eventDate, eventStatus, isChildUser);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.EVENT_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				response.put("data", responseDtos);
				response.put("msg", ConstantsPoc.EVENT_MASTER_FOUND_SUCCESS);
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

	@GetMapping("/getallbypartyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllByPartyId(@RequestParam(value = "partyId") Long partyId,
			@RequestParam(value = "partyName", required = false) String partyName,
			@RequestParam(value = "isChildUser", required = false, defaultValue = "false") Boolean isChildUser) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventMasterResponseDto> responseDtos = eventMasterService.getAllByPartyId(partyId, partyName,
					isChildUser);
			if (responseDtos.isEmpty()) {
				response.put("msg", ConstantsPoc.EVENT_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> eventResp = new HashMap<>();
				eventResp.put("Event Details", responseDtos);
				response.put("data", eventResp);
				response.put("msg", ConstantsPoc.EVENT_MASTER_FOUND_SUCCESS);
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
	public ResponseEntity<Map<String, Object>> getEventMasterById(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventMasterResponseDto responseDto = eventMasterService.getEventMasterById(eventId);

			if (responseDto == null) {
				response.put("msg", ConstantsPoc.EVENT_MASTER_FOUND_FAIL);
				response.put("success", false);
			} else {
				Map<String, Object> eventResp = new HashMap<>();
				List<EventMasterResponseDto> responseDtos = new ArrayList<>();
				responseDtos.add(responseDto);
				eventResp.put("Event Details", responseDtos);
				response.put("data", eventResp);
				response.put("msg", ConstantsPoc.EVENT_MASTER_FOUND_SUCCESS);
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

	@DeleteMapping("/deleteeventbyid")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteEventById(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			boolean deleted = eventMasterService.deleteEventById(eventId);
			if (deleted) {
				response.put("msg", "Event deleted successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Event not found");
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

	@PutMapping("/changeeventstatus")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> changeEventStatus(@RequestParam("status") Integer status,
			@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventMasterResponseDto eventMasterResponseDto = eventMasterService.changeEventStatus(status, eventId);
			if (eventMasterResponseDto != null) {
				response.put("msg", ConstantsPoc.EVENT_MASTER_STATUS_UPDATE_SUCCESS);
				response.put("success", true);
			} else {
				response.put("msg", ConstantsPoc.EVENT_MASTER_STATUS_UPDATE_FAIL);
				response.put("success", false);
			}

			UserNotificationConfigEntity dto = userNotificationConfigRepository
					.findNotificationByUserAndUpgradeModule(eventMasterResponseDto.getUserId(), 20L);
			if (dto != null) {
				if (eventMasterResponseDto.getIsEventStatusChanged()) {
					List<String> dataList = new ArrayList<>();
					String companyName = eventMasterResponseDto.getCompanyName() != null
							? eventMasterResponseDto.getCompanyName()
							: "NA";
					String partyName = eventMasterResponseDto.getPartyName() != null
							? eventMasterResponseDto.getPartyName()
							: "NA";
					String partyMobileNo = eventMasterResponseDto.getPartyMobileNo() != null
							? eventMasterResponseDto.getPartyMobileNo()
							: "NA";
					String managerName = eventMasterResponseDto.getManagerName() != null
							? eventMasterResponseDto.getManagerName()
							: "NA";
					String companyMobileNo = eventMasterResponseDto.getCompanyMobileNo() != null
							? eventMasterResponseDto.getCompanyMobileNo()
							: "NA";
					String managerMobileNo = eventMasterResponseDto.getManagerMobileNo() != null
							? eventMasterResponseDto.getManagerMobileNo()
							: "NA";
					dataList.add(partyName);
					dataList.add(companyName);
					dataList.add(managerName);
					dataList.add(managerMobileNo);
					dataList.add(companyMobileNo);
					dataList.add(companyName);

					if (eventMasterResponseDto.getStatus() == 0) {
						commonService.sendEventInfoWhatsApp("event_inquiry_msg", partyMobileNo, dataList, null, false,
								eventMasterResponseDto.getUserId());
					} else if (eventMasterResponseDto.getStatus() == 1) {
						commonService.sendEventInfoWhatsApp("event_confirm_msg", partyMobileNo, dataList, null, false,
								eventMasterResponseDto.getUserId());
					}
				}
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

	@PutMapping("/updateeventfunction")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateEventFunction(@RequestBody EventFunctionMasterRequestDto request,
			@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFunctionMasterResponseDto eventFunctionMasterResponseDtos = eventMasterService
					.updateEventFunction(request, id);
			if (eventFunctionMasterResponseDtos != null) {
				response.put("success", true);
				response.put("msg", "Event Function Updatation Successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Event Function Updatation Failed.");
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

	@GetMapping("/geteventbydate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventData(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam("userId") Long userId,
			@RequestParam(value = "isChildUser", required = false, defaultValue = "false") Boolean isChildUser,
			HttpServletRequest re) {
		Map<String, Object> response = new HashMap<>();

		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formatedDate = currentDate.format(formatter);

		try {

			if (startDate == null || endDate == null) {
				startDate = formatedDate;
			}

			List<EventByDateResponseDto> eventByDateResponseDtos = eventMasterService.getEventByDate(startDate, endDate,
					userId, isChildUser);
			if (eventByDateResponseDtos != null) {
				List<Map<String, Object>> datas = new ArrayList<>();
				Map<String, Object> eventData;
				for (EventByDateResponseDto eventByDateResponseDto : eventByDateResponseDtos) {
					eventData = new HashMap<>();

					eventData.put("function_date", eventByDateResponseDto.getFunctionDate());
					eventData.put("event_name", eventByDateResponseDto.getEventName());
					eventData.put("session_date_time", eventByDateResponseDto.getSessionDateTime());
					eventData.put("pax", eventByDateResponseDto.getPax());
					eventData.put("venue_name", eventByDateResponseDto.getVenueName());
					eventData.put("guest_name", eventByDateResponseDto.getGuestName());
					eventData.put("mgr_name", eventByDateResponseDto.getMgrName());
					eventData.put("menu_releasing_status", eventByDateResponseDto.getMenuReleasingStatus());

					datas.add(eventData);
				}

				String fullUrl = eventMasterService.generateDatewiseOrderSummaryPDF(re, datas, startDate, endDate);

				response.put("url", fullUrl);
				response.put("success", true);
				response.put("msg", "Event Data Fetched Successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Event Data fetched Failed.");
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

	@PutMapping("/updatealleventfunction")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateAllEventFunction(
			@RequestBody List<EventFunctionMasterRequestDto> request, @RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventFunctionMasterResponseDto> eventFunctionMasterResponseDtos = eventMasterService
					.updateAllEventFunction(request, id);
			if (eventFunctionMasterResponseDtos != null) {
				response.put("success", true);
				response.put("msg", "Event Function Updatation Successfully.");
			} else {
				response.put("success", false);
				response.put("msg", "Event Function Updatation Failed.");
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

	@DeleteMapping("/syncmenuallocationandrawmaterial")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> syncMenuAllocationAndRawMaterial(@RequestParam("eventId") Long eventId) {

		Map<String, Object> response = new HashMap<>();

		try {
			eventMasterService.syncMenuAllocationAndRawMaterial(eventId);

			response.put("msg", "Menu Execution And Raw Material Distribution Sync Successfully");
			response.put("success", true);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/addfunctionmanagerassign")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addEventFunctionManagerAssign(
			@RequestBody EventFunctionManagerAssignRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = eventMasterService.addEventFunctionManagerAssign(request);
			if (isSuccess) {
				response.put("msg", "Event Function Assigned Successfully");
				response.put("success", true);
			} else {
				response.put("msg", "Event Function Assigned Failed");
				response.put("success", false);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("getallmanagerevents")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllManagerEvents(@RequestParam("managerId") Long managerId) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ManagerEventsResponseDto> dtos = eventMasterService.getAllManagerEvents(managerId);
			if (dtos.isEmpty()) {
				response.put("msg", "Manager Events Found Failed");
				response.put("success", false);
			} else {
				Map<String, Object> eventResp = new HashMap<>();
				eventResp.put("managerEvents", dtos);
				response.put("data", eventResp);
				response.put("msg", "Manager Events Found Successfully");
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/geteventvendordata")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getEventVendorData(@RequestParam("eventId") Long eventId,
			@RequestParam("eventFunctionId") Long eventFunctionId, @RequestParam("type") String type) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<EventMenuAllocationOverviewResponseDto> dto = eventMasterService.getEventVendorData(eventId,
					eventFunctionId, type);
			if (dto.isEmpty()) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				response.put("data", dto);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/addupdateeventvendordata", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addUpdateEventVendorData(
			@ModelAttribute EventVendorAssignmentWrapperDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isSuccess = eventMasterService.addUpdateEventVendorData(request);
			if (isSuccess) {
				response.put("msg", "Event Vendor Data Added/Updated Successfully");
				response.put("success", isSuccess);
			} else {
				response.put("msg", "Event Vendor Data Added/Updated Failed");
				response.put("success", isSuccess);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getallassignfunctionbyevent")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllAssignFunctionByEvent(@RequestParam("eventId") Long eventId) {
		Map<String, Object> response = new HashMap<>();
		try {
			EventFunctionWithManagersResponseDto dto = eventMasterService.getAllAssignFunctionByEvent(eventId);
			if (dto == null) {
				response.put("msg", "Data Not Found");
				response.put("success", false);
			} else {
				List<EventFunctionWithManagersResponseDto> dtos = new ArrayList<>();
				dtos.add(dto);
				Map<String, Object> eventResp = new HashMap<>();
				eventResp.put("eventDetails", eventResp);
				response.put("data", dtos);
				response.put("msg", "Data Found Successfully");
				response.put("success", true);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/get-events-by-hall")
	public ResponseEntity<Map<String, Object>> getEventsByHall(@RequestParam String hallName,
			@RequestParam String password) {

		Map<String, Object> response = new HashMap<>();
		try {
			response.put("success", true);
			response.put("msg", "Events fetched successfully");
			response.put("data", eventMasterService.getEventsByHallAndPassword(hallName, password));

			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	@PostMapping("/assigneventstochild")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> assignEventsToChild(@RequestBody AssignEventToChildRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			AssignEventToChildResponseDto dto = eventMasterService.assignEventsToChildUser(request);
			response.put("success", dto.getSuccess());
			response.put("msg", dto.getMessage());
			response.put("totalAssigned", dto.getTotalAssigned());
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ── Get shifts dropdown for event form ────────────────────────────────
	@GetMapping("/getshiftsforevent")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getShiftsForEvent(@RequestParam("hallId") Long hallId,
			@RequestParam("bookingDate") String bookingDate, @RequestParam("userId") Long userId,
			@RequestParam("eventId") Long eventId) {

		Map<String, Object> response = new HashMap<>();
		try {
			List<BanquetShiftAvailabilityResponseDto> shifts = eventMasterService.getShiftsForEventForm(hallId,
					bookingDate, userId, eventId);

			response.put("success", true);
			response.put("msg", "Shifts fetched successfully");
			response.put("data", shifts);
			return ResponseEntity.ok(response);

		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	@PutMapping("/updateremarks")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateRemarks(@RequestBody EventRemarksRequestDto request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isUpdated = eventMasterService.updateRemarks(request);

			if (isUpdated) {
				response.put("msg", "Event Remarks Successfully");
				response.put("success", isUpdated);
			} else {
				response.put("msg", "Event Remarks Failed");
				response.put("success", isUpdated);
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deleteeventvendortaskimage")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteEventVendorTaskImage(@RequestParam("id") Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			Boolean isDelete = eventMasterService.deleteEventVendorTaskImage(id);
			if (isDelete) {
				response.put("success", isDelete);
				response.put("msg", "Event Vendor Task Image Deteled Successfully");
			} else {
				response.put("success", isDelete);
				response.put("msg", "Event Vendor Task Image Deteled Failed");
			}
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (EntityNotFoundException e) {
			response.put("success", false);
			response.put("msg", e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("msg", "Something went wrong");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
