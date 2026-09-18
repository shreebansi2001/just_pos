package com.crmportal.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.crmportal.request.dto.EventRawMaterialAppRequest;
import com.crmportal.request.dto.EventRawMaterialFunctionRequestDto;
import com.crmportal.request.dto.EventRawMaterialRequest;
import com.crmportal.response.dto.AgencyRawMaterialReportResponseDto;
import com.crmportal.response.dto.EventRawMaterialCategoryResponse;
import com.crmportal.response.dto.EventRawMaterialFunctionsDto;
import com.crmportal.response.dto.EventRawMaterialPartyDTO;
import com.crmportal.response.dto.EventRawMaterialResponse;
import com.crmportal.response.dto.GeneralFixResponseDto;
import com.crmportal.response.dto.SupplierRawMaterialResponseDto;

public interface EventRawMaterialService {

	List<EventRawMaterialResponse> getEventRawMaterial( Long rawMateriaCatlId, Long eventId, Long rawMateriaId);

	List<EventRawMaterialResponse> addOrUpdateEventRawMaterial(@Valid EventRawMaterialRequest request);

	List<EventRawMaterialCategoryResponse> getEventRawMaterialByEventId(Long eventId, List<Long> rawMaterialCatIds, Integer isAddStoreIssue);
	
	List<EventRawMaterialCategoryResponse> getEventRawMaterialByEventIdAndEventFunctionId(Long eventId, List<Long> eventFunctionId,
			List<Long> rawMaterialCatIds);
	
	List<EventRawMaterialPartyDTO> getGroupedPartyByEventAndFunction(Long eventId, Long eventFunctionId);

	List<EventRawMaterialPartyDTO> getGroupedPartyByEvent(Long eventId);

	List<AgencyRawMaterialReportResponseDto> mapAgencyRawMaterialReport(Long eventId, Integer lang, List<Long> agencyId, List<Long> itemId);

	
	
	//Below methods are being used in app
	List<EventRawMaterialFunctionsDto> getEventRawMaterialByEventRawMaterialId(Long eventId, Long rawMateriaId);
	
	boolean addEventRawMaterialFunctions(@Valid EventRawMaterialFunctionRequestDto request);
	
	boolean addEventRawMaterialForApp(@Valid EventRawMaterialAppRequest request);

	List<GeneralFixResponseDto> getGeneralFixItems(Long eventId, Long functionId, List<Long> rawMaterialCatIds,
			int lang, Integer pax, Long userid);

	List<GeneralFixResponseDto> getFunctions(Long eventId, List<Long> eventFunctionId, int lang);

	List<GeneralFixResponseDto> getFunctions(Long eventId, Long eventFunctionId, int lang);
	
	List<EventRawMaterialCategoryResponse> getEventRawMaterialCrockeryByEventId(Long eventId);

	List<EventRawMaterialCategoryResponse> getEventRawMaterialCrockerByEventIdAndEventFunctionId(Long eventId,
			Long eventFunctionId);

	List<GeneralFixResponseDto> getCrockerCategory(int lang);

	Map<String, Map<String, BigDecimal>> getAllCrockerCutlery(int lang);

	Map<String, Map<String, BigDecimal>> getFunctionwiseCrockerCutlery(int lang, Integer person, Map<String, Map<String, BigDecimal>> crockerCutleryData,Long userId);

	List<GeneralFixResponseDto> getCrockerItems(Long rawMaterialCatId, int lang, Integer person);

	Map<String, Map<String, List<SupplierRawMaterialResponseDto>>> getSupplierwiseRawMaterial(Long eventId, int lang, List<Long> agencyId);
	
	List<EventRawMaterialCategoryResponse> getDateWiseRawMaterial(List<Long> rawMaterialCatIds, LocalDateTime startDate,
			LocalDateTime endDate, Long userId);
}
