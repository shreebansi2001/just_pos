package com.crmportal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.AssignEventToChildRequestDto;
import com.crmportal.request.dto.EventFunctionManagerAssignRequestDto;
import com.crmportal.request.dto.EventFunctionMasterRequestDto;
import com.crmportal.request.dto.EventFunctionVendorAssignmentRequestDto;
import com.crmportal.request.dto.EventMasterRequestDto;
import com.crmportal.request.dto.EventRemarksRequestDto;
import com.crmportal.request.dto.EventVendorAssignmentWrapperDto;
import com.crmportal.response.dto.BanquetShiftAvailabilityResponseDto;
import com.crmportal.response.dto.AssignEventToChildResponseDto;
import com.crmportal.response.dto.EventByDateResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionWithManagersResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.EventMenuAllocationOverviewResponseDto;
import com.crmportal.response.dto.EventOverViewResponseDto;
import com.crmportal.response.dto.ManagerEventsResponseDto;

@Service
public interface EventMasterService {

	EventMasterResponseDto addOrUpdateEventMaster(@Valid EventMasterRequestDto request, long parseLong);

	List<EventMasterResponseDto> getAllByUserId(Long userId, String partyName, Boolean isVisible, Boolean isChildUser, String month, String year, Integer status);

	EventMasterResponseDto getEventMasterById(Long eventId);

	boolean deleteEventById(Long eventId);

	EventMasterResponseDto changeEventStatus(Integer status, Long eventId);

	List<EventMasterResponseDto> getAllByPartyId(Long partyId, String partyName, Boolean isChildUser);

	Map<String, Object> getAllEventByFilter(Long userId, String partyName, String startDate, String endDate,
			String eventDate, Integer eventStatus, Boolean isChildUser);

	EventFunctionMasterResponseDto updateEventFunction(EventFunctionMasterRequestDto request, Long id);

	List<EventByDateResponseDto> getEventByDate(String startDate, String endDate, Long userId, Boolean isChildUser);

	List<EventFunctionMasterResponseDto> updateAllEventFunction(List<EventFunctionMasterRequestDto> request, Long id);

	String generateDatewiseOrderSummaryPDF(HttpServletRequest re, List<Map<String, Object>> datas, String startDate,
			String endDate) throws Exception;

	List<Long> findEventsByEventDate(LocalDateTime stDate, LocalDateTime edDate);

	EventOverViewResponseDto getEventOverview(Long partyId, Long eventId, Long userId);

	void syncMenuAllocationAndRawMaterial(Long eventId);

	Boolean addEventFunctionManagerAssign(EventFunctionManagerAssignRequestDto request);

	List<ManagerEventsResponseDto> getAllManagerEvents(Long managerId);

	List<EventMenuAllocationOverviewResponseDto> getEventVendorData(Long eventId, Long eventFunctionId, String type);

	Boolean addUpdateEventVendorData(EventVendorAssignmentWrapperDto request);

	EventFunctionWithManagersResponseDto getAllAssignFunctionByEvent(Long eventId);
	
	List<EventMasterResponseDto> getEventsByHallAndPassword(String hallName, String password);
	
	List<BanquetShiftAvailabilityResponseDto> getShiftsForEventForm(
	        Long hallId, String bookingDate, Long userId, Long eventId);
	
	
	AssignEventToChildResponseDto assignEventsToChildUser(AssignEventToChildRequestDto request);

	Boolean updateRemarks(EventRemarksRequestDto request);

	Boolean deleteEventVendorTaskImage(Long id);

}
