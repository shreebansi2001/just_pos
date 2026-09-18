package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.crmportal.config.SecurityConfig;
import com.crmportal.controller.CityMasterController;
import com.crmportal.controller.MealTypeMasterController;
import com.crmportal.controller.PartyMasterController;
import com.crmportal.controller.ShiftController;
import com.crmportal.entity.CityMasterEntity;
import com.crmportal.entity.EventTypeMasterEntity;
import com.crmportal.entity.FollowUpDetailsEntity;
import com.crmportal.entity.LeadMasterEntity;
import com.crmportal.entity.LeadSourceEntity;
import com.crmportal.entity.LeadStatusEntity;
import com.crmportal.entity.LeadSubSourceEntity;
import com.crmportal.entity.PipelineCloseStageEntity;
import com.crmportal.entity.PipelineEntity;
import com.crmportal.entity.PipelineOpenStageEntity;
import com.crmportal.entity.PlansEntity;
import com.crmportal.entity.StateMasterEntity;
import com.crmportal.entity.UserLeadEventEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.FollowUpDetailsMapper;
import com.crmportal.mapper.LeadMasterMapper;
import com.crmportal.mapper.LeadSourceMapper;
import com.crmportal.mapper.LeadSubSourceMapper;
import com.crmportal.repository.CityMasterRepository;
import com.crmportal.repository.ConfigurationUtilEntityRepository;
import com.crmportal.repository.EventTypeMasterRepository;
import com.crmportal.repository.FollowUpDetailsRepository;
import com.crmportal.repository.FunctionMasterRepository;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.LeadSourceRepository;
import com.crmportal.repository.LeadStatusRepository;
import com.crmportal.repository.LeadSubSourceRepository;
import com.crmportal.repository.PipelineCloseStageRepository;
import com.crmportal.repository.PipelineOpenStageRepository;
import com.crmportal.repository.PipelineRepository;
import com.crmportal.repository.PlansRepository;
import com.crmportal.repository.StateMasterRepository;
import com.crmportal.repository.UserLeadEventRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.FollowUpDetailsRequestDto;
import com.crmportal.request.dto.LeadMasterRequestDto;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.AssignMemberResponseDTO;
import com.crmportal.response.dto.FollowUpDetailsResponseDto;
import com.crmportal.response.dto.FollowUpResponseDto;
import com.crmportal.response.dto.OrderSummaryResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.LeadMasterService;

import com.crmportal.utility.DateMapper;

@Service
public class LeadMasterServiceImpl implements LeadMasterService {

	private final CityMasterController cityMasterController;

	private final MealTypeMasterController mealTypeMasterController;

	private final PartyMasterController partyMasterController;

	private final ShiftController shiftController;

	private final ConfigurationUtilEntityRepository configurationUtilEntityRepository;

	private final SecurityConfig securityConfig;

	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Autowired
	FollowUpDetailsRepository followUpDetailsRepository;

	@Autowired
	LeadMasterMapper leadMasterMapper;

	@Autowired
	FollowUpDetailsMapper followUpDetailsMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	DateMapper dateMapperp;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	PlansRepository plansRepository;

	@Autowired
	CityMasterRepository cityMasterRepository;

	@Autowired
	StateMasterRepository stateMasterRepository;

	@Autowired
	UserLeadEventRepository userLeadEventRepository;

	@Autowired
	PipelineRepository pipelineRepository;

	@Autowired
	PipelineOpenStageRepository openStageRepository;

	@Autowired
	PipelineCloseStageRepository closeStageRepository;

	@Autowired
	LeadSourceRepository leadSourceRepository;

	@Autowired
	LeadStatusRepository leadStatusRepository;

	@Autowired
	LeadSourceMapper leadSourceMapper;

	@Autowired
	LeadSubSourceMapper leadSubSourceMapper;

	@Autowired
	EventTypeMasterRepository eventTypeMasterRepository;

	@Autowired
	FunctionMasterRepository functionMasterRepository;

	private static final DateTimeFormatter DATE_TIME_FORMATTER_12 = new DateTimeFormatterBuilder()
			.parseCaseInsensitive().appendPattern("dd/MM/yyyy hh:mm a").toFormatter(Locale.ENGLISH);

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	LeadMasterServiceImpl(SecurityConfig securityConfig,
			ConfigurationUtilEntityRepository configurationUtilEntityRepository, ShiftController shiftController,
			PartyMasterController partyMasterController, MealTypeMasterController mealTypeMasterController,
			CityMasterController cityMasterController) {
		this.securityConfig = securityConfig;
		this.configurationUtilEntityRepository = configurationUtilEntityRepository;
		this.shiftController = shiftController;
		this.partyMasterController = partyMasterController;
		this.mealTypeMasterController = mealTypeMasterController;
		this.cityMasterController = cityMasterController;
	}

	@Override
	@Transactional
	public LeadMasterResponseDto addOrUpdateLeadMaster(@Valid LeadMasterRequestDto request, Long id) {
		LeadSourceEntity leadSource = null;
		if (request.getLeadSourceId() != null) {
			leadSource = leadSourceRepository.findByLeadSourceIdAndIsDeletedFalse(request.getLeadSourceId())
					.orElseThrow(
							() -> new RuntimeException("Lead source not found with id : " + request.getLeadSourceId()));
		}

		LeadStatusEntity leadStatus = null;
		if (request.getLeadStatusId() != null) {
			leadStatus = leadStatusRepository.findByLeadStatusIdAndIsDeletedFalse(request.getLeadStatusId())
					.orElseThrow(
							() -> new RuntimeException("Lead status not found with id : " + request.getLeadStatusId()));
		}

		String dateFormat = "dd/MM/yyyy";
		LeadMasterEntity entity = null;
		String name = "";
		String email = "";

		if (id == -1) {
			entity = leadMasterMapper.requestToEntity(request);
		} else {
			Optional<LeadMasterEntity> optional = leadMasterRepository.findByIdAndIsDeleteFalse(id);
			if (!optional.isPresent()) {
				throw new RuntimeException("Lead not found with id " + id);
			}
			entity = optional.get();
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			leadMasterMapper.updateEntityFromRequest(request, entity);
		}

		/*
		 * PipelineEntity pipelineEntity; if (request.getPipelineId() != null) {
		 * pipelineEntity =
		 * pipelineRepository.findByIdAndIsDeleteFalse(request.getPipelineId()).
		 * orElseThrow( () -> new RuntimeException("Pipeline is not found with id : " +
		 * request.getPipelineId())); entity.setPipeline(pipelineEntity); }
		 * 
		 * PipelineOpenStageEntity openStageEntity; if (request.getOpenStageId() !=
		 * null) { openStageEntity =
		 * openStageRepository.findByIdAndIsDeleteFalse(request.getOpenStageId()).
		 * orElseThrow( () -> new RuntimeException("Stage is not found with id : " +
		 * request.getOpenStageId())); entity.setOpenStage(openStageEntity); }
		 * 
		 * PipelineCloseStageEntity closeStageEntity; if (request.getCloseStageId() !=
		 * null) { closeStageEntity =
		 * closeStageRepository.findByIdAndIsDeleteFalse(request.getCloseStageId()).
		 * orElseThrow( () -> new RuntimeException("Stage is not found with id : " +
		 * request.getCloseStageId())); entity.setCloseStage(closeStageEntity);
		 * entity.setActualCloseDate(commonService.getCurrentDateTime()); }
		 */

		if (request.getLeadAssignId() != null) {
			Optional<UserMasterEntity> userEntityOptional = userMasterRepository
					.findByIdAndIsDeleteFalse(request.getLeadAssignId());
			if (userEntityOptional.isPresent()) {
				UserMasterEntity userEntity = userEntityOptional.get();
				entity.setLeadAssign(userEntity);
				name = userEntity.getFirstName() + " " + userEntity.getLastName();
				email = userEntity.getEmail();
			}
		}

		if (request.getPlanId() != null) {
			Optional<PlansEntity> planOptional = plansRepository.findByIdAndIsDeleteFalse(request.getPlanId());
			if (planOptional.isPresent()) {
				PlansEntity planEntity = planOptional.get();
				entity.setPlan(planEntity);
			}
		}

		if (request.getCityId() != null) {
			Optional<CityMasterEntity> cityOptional = cityMasterRepository
					.findByIdAndIsDeleteFalse(request.getCityId());
			if (cityOptional.isPresent()) {
				CityMasterEntity cityEntity = cityOptional.get();
				entity.setCity(cityEntity);
			}
		}

		if (request.getStateId() != null) {
			Optional<StateMasterEntity> stateOptional = stateMasterRepository
					.findByIdAndIsDeleteFalse(request.getStateId());
			if (stateOptional.isPresent()) {
				StateMasterEntity stateEntity = stateOptional.get();
				entity.setState(stateEntity);
			}
		}

		entity.setEventTypeId(request.getEventTypeId());
		// entity.setFunctionId(request.getFunctionId());
		entity.setMaxPax(request.getMaxPax());
		entity.setMinPax(request.getMinPax());

		entity.setAnyFunctionWithUs(request.getAnyFunctionWithUs());

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		entity.setUser(user);
		// entity.setReferralSource(request.getReferralSource());
		entity.setLeadSource(leadSource);
		entity.setLeadStatus(leadStatus);
		// entity.setLeadSubSource(leadSubSource);

		entity = leadMasterRepository.save(entity);

		if (!request.getTentEventDate().isEmpty()) {
			userLeadEventRepository.deleteAllByLead(entity);
			List<UserLeadEventEntity> entities = new ArrayList<>();
			for (String date : request.getTentEventDate()) {

		        UserLeadEventEntity leadEventEntity = new UserLeadEventEntity();

		        leadEventEntity.setLead(entity);

		        // 01/09/2026 -> 2026-09-01 00:00:00
		        leadEventEntity.setTentEventDate(
		                dateMapperp.stringToLeadTentDateTime(date)
		        );

		        leadEventEntity.setUser(user);
		        leadEventEntity.setIsDelete(false);

		        entities.add(leadEventEntity);
		    }
			userLeadEventRepository.saveAll(entities);
		}
		List<FollowUpDetailsRequestDto> followUpRequests = request.getFollowUpDetails();
		List<FollowUpDetailsResponseDto> followUpResponse = new ArrayList<FollowUpDetailsResponseDto>();

		if (!followUpRequests.isEmpty()) {
			for (FollowUpDetailsRequestDto followUpDetailsRequestDto : followUpRequests) {
				FollowUpDetailsEntity followUpDetailsEntity = new FollowUpDetailsEntity();

				if (followUpDetailsRequestDto.getId() == 0) {
					followUpDetailsEntity = followUpDetailsMapper.requestToEntity(followUpDetailsRequestDto);
				} else {

					FollowUpDetailsEntity detailsEntity = followUpDetailsRepository
							.findById(followUpDetailsRequestDto.getId()).orElseThrow(() -> new RuntimeException(
									"Follow Up Details Not FOund with id : " + followUpDetailsRequestDto.getId()));

					followUpDetailsEntity = detailsEntity;

					followUpDetailsEntity.setIsDelete(false);
					followUpDetailsEntity.setUpdatedAt(commonService.getCurrentDateTime());
					followUpDetailsMapper.updateEntityFromRequest(followUpDetailsRequestDto, followUpDetailsEntity);

				}

				UserMasterEntity member = userMasterRepository.findById(followUpDetailsRequestDto.getMemberId())
						.orElseThrow(() -> new RuntimeException(
								"Member not found with id : " + followUpDetailsRequestDto.getMemberId()));

				followUpDetailsEntity.setFollowUpMember(member);
				followUpDetailsEntity.setLead(entity);
				followUpDetailsEntity
						.setFollowUpDate(dateMapperp.stringToDateTime(followUpDetailsRequestDto.getFollowUpDate()));
				followUpDetailsEntity = followUpDetailsRepository.save(followUpDetailsEntity);
				FollowUpDetailsResponseDto followUpDetailsResponseDto = followUpDetailsMapper
						.entityToResponse(followUpDetailsEntity);

				followUpDetailsResponseDto.setId(followUpDetailsEntity.getId());
				followUpDetailsResponseDto.setLeadId(entity.getId());
				followUpDetailsResponseDto.setMemberId(member.getId());
				followUpDetailsResponseDto.setMemberName(member.getFirstName() + " " + member.getLastName());
				followUpDetailsResponseDto
						.setCreatedAt(dateMapperp.dateTimeWithSecondToString(followUpDetailsEntity.getCreatedAt()));

				followUpResponse.add(followUpDetailsResponseDto);
			}
		}

		LeadMasterResponseDto responseDto = leadMasterMapper.entityToResponse(entity);
		responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
		responseDto.setFollowUpDetails(followUpResponse);

		responseDto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().format(formatter) : null);

		responseDto.setFollowUpDetails(followUpResponse);

		// Lead Assign
		if (entity.getLeadAssign() != null) {
			responseDto.setLeadAssignId(entity.getLeadAssign().getId());
			responseDto.setLeadAssignName(
					entity.getLeadAssign().getFirstName() + " " + entity.getLeadAssign().getLastName());
		}

		// Plan
		if (entity.getPlan() != null) {
			responseDto.setPlanId(entity.getPlan().getId());
			responseDto.setPlanName(entity.getPlan().getName());
		}

		// City
		if (entity.getCity() != null) {
			responseDto.setCityId(entity.getCity().getId());
			responseDto.setCityName(entity.getCity().getName());
		}

		// State
		if (entity.getState() != null) {
			responseDto.setStateId(entity.getState().getId());
			responseDto.setStateName(entity.getState().getName());
		}

		// Pipeline
		/*
		 * if (entity.getPipeline() != null) {
		 * responseDto.setPipelineId(entity.getPipeline().getId());
		 * responseDto.setPipelineName(entity.getPipeline().getPipelineName()); }
		 * 
		 * if (entity.getOpenStage() != null) {
		 * responseDto.setOpenStageId(entity.getOpenStage().getId());
		 * responseDto.setOpenStageName(entity.getOpenStage().getStage()); } if
		 * (entity.getCloseStage() != null) {
		 * responseDto.setCloseStageId(entity.getCloseStage().getId());
		 * responseDto.setCloseStageName(entity.getCloseStage().getStage()); } if
		 * (entity.getLeadFollowUpDate() != null) {
		 * responseDto.setLeadFollowUpDate(dateMapperp.dateTimeToString(entity.
		 * getLeadFollowUpDate())); }
		 */
		if (entity.getEventTypeId() != null) {
			responseDto.setEventTypeId(entity.getEventTypeId());
			responseDto.setEventTypeName(
					eventTypeMasterRepository.findByIdAndIsDeleteFalse(entity.getEventTypeId()).get().getNameEnglish());
		}

		/*
		 * if (entity.getFunctionId() != null) {
		 * responseDto.setFunctionId(entity.getFunctionId());
		 * responseDto.setFunctionName(
		 * functionMasterRepository.findByIdAndIsDeleteFalse(entity.getFunctionId()).get
		 * ().getNameEnglish()); }
		 */

		if (entity.getInquiryDate() != null) {
			responseDto.setInquiryDate(entity.getInquiryDate().format(formatter));
		}

		responseDto.setTentEventDate(mapTentDate(entity, user));

		//commonService.sendMailForLeadAssign(name, email, responseDto);

		return responseDto;
	}

	private List<String> mapTentDate(LeadMasterEntity lead, UserMasterEntity user) {

	    List<UserLeadEventEntity> tentDates =
	            userLeadEventRepository.findAllByLeadAndUser(lead, user);

	    if (tentDates.isEmpty()) {
	        return Collections.emptyList();
	    }

	    List<String> dates = new ArrayList<>();

	    for (UserLeadEventEntity event : tentDates) {
	        dates.add(event.getTentEventDate()
	                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	    }

	    return dates;
	}

	@Override
	public LeadMasterResponseDto getLeadById(Long id) {

		Optional<LeadMasterEntity> optional = leadMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!optional.isPresent()) {
			return null;
		}
		LeadMasterEntity leadMasterEntity = optional.get();
		LeadMasterResponseDto dto = leadMasterMapper.entityToResponse(leadMasterEntity);

		dto = leadMasterResponseDtoWithName(dto, leadMasterEntity);

		List<FollowUpDetailsEntity> detailsEntities = followUpDetailsRepository
				.findByLeadAndIsDeleteFalse(leadMasterEntity);
		List<FollowUpDetailsResponseDto> detailsResponseDtos = new ArrayList<>();

		if (detailsEntities != null) {
			for (FollowUpDetailsEntity followUpDetailsEntity : detailsEntities) {
				FollowUpDetailsResponseDto detailsResponseDto = followUpDetailsMapper
						.entityToResponse(followUpDetailsEntity);
				if (followUpDetailsEntity.getFollowUpMember() != null) {
					detailsResponseDto.setMemberId(followUpDetailsEntity.getFollowUpMember().getId());
					detailsResponseDto.setMemberName(followUpDetailsEntity.getFollowUpMember().getFirstName() + " "
							+ followUpDetailsEntity.getFollowUpMember().getLastName());
				}
				detailsResponseDto.setLeadId(leadMasterEntity.getId());
				detailsResponseDtos.add(detailsResponseDto);
			}
			dto.setFollowUpDetails(detailsResponseDtos);
		}

		if (leadMasterEntity.getCreatedAt() != null) {
			dto.setCreatedAt(leadMasterEntity.getCreatedAt().format(formatter));
		}

		if (leadMasterEntity.getEventTypeId() != null) {
			dto.setEventTypeId(leadMasterEntity.getEventTypeId());
			dto.setEventTypeName(eventTypeMasterRepository.findByIdAndIsDeleteFalse(leadMasterEntity.getEventTypeId())
					.get().getNameEnglish());
		}

		/*
		 * if (leadMasterEntity.getFunctionId() != null) {
		 * dto.setFunctionId(leadMasterEntity.getFunctionId());
		 * dto.setFunctionName(functionMasterRepository.findByIdAndIsDeleteFalse(
		 * leadMasterEntity.getFunctionId()) .get().getNameEnglish()); }
		 */

		if (leadMasterEntity.getInquiryDate() != null) {
			dto.setInquiryDate(leadMasterEntity.getInquiryDate().format(formatter));
		}

		dto.setTentEventDate(mapTentDate(leadMasterEntity, leadMasterEntity.getUser()));

		return dto;
	}

	@Override
	public Boolean deleteLeadById(Long id) {

		if (!leadMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			return false;
		}

		Optional<LeadMasterEntity> leadMasterEntityOptional = leadMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!leadMasterEntityOptional.isPresent()) {
			return null;
		}

		LeadMasterEntity leadMasterEntity = leadMasterEntityOptional.get();
		if (followUpDetailsRepository.existsByLeadAndIsDeleteFalse(leadMasterEntity)) {
			List<FollowUpDetailsEntity> followUpDetailsEntities = followUpDetailsRepository
					.findByLeadAndIsDeleteFalse(leadMasterEntity);
			if (followUpDetailsEntities != null) {
				for (FollowUpDetailsEntity followUpDetailsEntity : followUpDetailsEntities) {
					followUpDetailsEntity.setIsDelete(true);
					followUpDetailsRepository.save(followUpDetailsEntity);
				}
			}
		}

		leadMasterEntity.setIsDelete(true);
		leadMasterRepository.save(leadMasterEntity);

		return true;
	}

	@Override
	public Map<String, Object> getAllLeades(Long assignId, Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		Map<String, Object> response = new HashMap<>();
		List<LeadMasterResponseDto> responseDtos = new ArrayList<>();
		List<LeadMasterEntity> allLeads = new ArrayList<>();
		if (assignId == -1) {
			allLeads = leadMasterRepository.findAllByIsDeleteFalseAndUser(user);
		} else {
			allLeads = leadMasterRepository.findAllByIsDeleteFalseAndLeadAssign_idAndUser(assignId, user);
		}

		for (LeadMasterEntity leadMasterEntity : allLeads) {
			LeadMasterResponseDto dto = leadMasterMapper.entityToResponse(leadMasterEntity);

			dto = leadMasterResponseDtoWithName(dto, leadMasterEntity);

			List<FollowUpDetailsResponseDto> detailsResponseDtos = new ArrayList<>();
			if (followUpDetailsRepository.existsByLead(leadMasterEntity)) {
				List<FollowUpDetailsEntity> detailsEntities = followUpDetailsRepository
						.findByLeadAndIsDeleteFalse(leadMasterEntity);
				for (FollowUpDetailsEntity detailsEntity : detailsEntities) {
					FollowUpDetailsResponseDto followUpDetailsResponseDto = followUpDetailsMapper
							.entityToResponse(detailsEntity);
					if (detailsEntity.getFollowUpMember() != null) {
						followUpDetailsResponseDto.setMemberId(detailsEntity.getFollowUpMember().getId());
						followUpDetailsResponseDto.setMemberName(detailsEntity.getFollowUpMember().getFirstName() + " "
								+ detailsEntity.getFollowUpMember().getLastName());
					}
					followUpDetailsResponseDto.setLeadId(leadMasterEntity.getId());
					detailsResponseDtos.add(followUpDetailsResponseDto);
				}
			}
			dto.setFollowUpDetails(detailsResponseDtos);
			if (leadMasterEntity.getEventTypeId() != null) {
				dto.setEventTypeId(leadMasterEntity.getEventTypeId());
				dto.setEventTypeName(eventTypeMasterRepository
						.findByIdAndIsDeleteFalse(leadMasterEntity.getEventTypeId()).get().getNameEnglish());
			}

			/*
			 * if (leadMasterEntity.getFunctionId() != null) {
			 * dto.setFunctionId(leadMasterEntity.getFunctionId());
			 * dto.setFunctionName(functionMasterRepository.findByIdAndIsDeleteFalse(
			 * leadMasterEntity.getFunctionId()) .get().getNameEnglish()); }
			 */

			if (leadMasterEntity.getInquiryDate() != null) {
				dto.setInquiryDate(leadMasterEntity.getInquiryDate().format(formatter));
			}

			dto.setTentEventDate(mapTentDate(leadMasterEntity, leadMasterEntity.getUser()));
			responseDtos.add(dto);
		}

		response.put("All Leads", responseDtos);

		Long countAllLeads = leadMasterRepository.countByIsDeleteFalse(userId);
		response.put("All Leads Count", countAllLeads);

//		List<Object[]> countByLeadType = leadMasterRepository.countByLeadType();
//		if(countByLeadType != null) {				
//			for (Object[] object : countByLeadType) {
//				String leadType = (String) object[0];
//				Long leadCount = ((Number) object[1]).longValue();
//				response.put(leadType, leadCount);
//			}
//		}
//		
//		List<Object[]> countByLeadStatus = leadMasterRepository.countByLeadStatus();
//		if(countByLeadStatus != null) {				
//			for (Object[] object : countByLeadStatus) {
//				String leadStatus = (String) object[0];
//				Long leadCount = ((Number) object[1]).longValue();
//				response.put(leadStatus, leadCount);
//			}
//		}
//		
//		List<Object[]> countByLeadAssigned = leadMasterRepository.countByLeadAssigned();
//		if(countByLeadAssigned != null) {			
//			for (Object[] object : countByLeadAssigned) {
//				String leadAssigned = String.valueOf(object[0]);
//				Long leadCount = ((Number) object[1]).longValue();
//				response.put(leadAssigned, leadCount);
//			}
//		}

		response.put("Hot", 0L);
		response.put("Inquire", 0L);
		response.put("Cold", 0L);

		List<Object[]> leadTypes = leadMasterRepository.countByLeadType(userId);

		for (Object[] row : leadTypes) {
			String leadType = (String) row[0];
			Long count = ((Number) row[1]).longValue();
			response.put(leadType, count);
		}

		return response;
	}

	@Override
	public List<LeadMasterResponseDto> getLeadesByLeadType(String leadType, Long userId) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		List<LeadMasterResponseDto> responseDtos = new ArrayList<>();
		List<LeadMasterEntity> allLeads = leadMasterRepository.findByLeadTypeAndIsDeleteFalseAndUser(leadType, user);

		for (LeadMasterEntity leadMasterEntity : allLeads) {
			LeadMasterResponseDto dto = leadMasterMapper.entityToResponse(leadMasterEntity);

			dto = leadMasterResponseDtoWithName(dto, leadMasterEntity);

			List<FollowUpDetailsResponseDto> detailsResponseDtos = new ArrayList<>();
			if (followUpDetailsRepository.existsByLead(leadMasterEntity)) {
				List<FollowUpDetailsEntity> detailsEntities = followUpDetailsRepository
						.findByLeadAndIsDeleteFalse(leadMasterEntity);
				for (FollowUpDetailsEntity detailsEntity : detailsEntities) {
					FollowUpDetailsResponseDto followUpDetailsResponseDto = followUpDetailsMapper
							.entityToResponse(detailsEntity);
					followUpDetailsResponseDto.setMemberId(detailsEntity.getFollowUpMember().getId());
					followUpDetailsResponseDto.setLeadId(leadMasterEntity.getId());
					detailsResponseDtos.add(followUpDetailsResponseDto);
				}
			}
			dto.setFollowUpDetails(detailsResponseDtos);
			if (leadMasterEntity.getEventTypeId() != null) {
				dto.setEventTypeId(leadMasterEntity.getEventTypeId());
				dto.setEventTypeName(eventTypeMasterRepository
						.findByIdAndIsDeleteFalse(leadMasterEntity.getEventTypeId()).get().getNameEnglish());
			}

			/*
			 * if (leadMasterEntity.getFunctionId() != null) {
			 * dto.setFunctionId(leadMasterEntity.getFunctionId());
			 * dto.setFunctionName(functionMasterRepository.findByIdAndIsDeleteFalse(
			 * leadMasterEntity.getFunctionId()) .get().getNameEnglish()); }
			 */

			if (leadMasterEntity.getInquiryDate() != null) {
				dto.setInquiryDate(leadMasterEntity.getInquiryDate().format(formatter));
			}

			dto.setTentEventDate(mapTentDate(leadMasterEntity, leadMasterEntity.getUser()));
			responseDtos.add(dto);
		}

		return responseDtos;
	}

	@Override
	public List<LeadMasterResponseDto> getLeadesByLeadAssignedId(Long leadAssignedId, Long userId) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		List<LeadMasterResponseDto> responseDtos = new ArrayList<>();
		List<LeadMasterEntity> allLeads = leadMasterRepository.findByleadAssignIdAndIsDeleteFalseAndUser(leadAssignedId,
				user);

		for (LeadMasterEntity leadMasterEntity : allLeads) {
			LeadMasterResponseDto dto = leadMasterMapper.entityToResponse(leadMasterEntity);

			dto = leadMasterResponseDtoWithName(dto, leadMasterEntity);

			List<FollowUpDetailsResponseDto> detailsResponseDtos = new ArrayList<>();
			if (followUpDetailsRepository.existsByLead(leadMasterEntity)) {
				List<FollowUpDetailsEntity> detailsEntities = followUpDetailsRepository
						.findByLeadAndIsDeleteFalse(leadMasterEntity);
				for (FollowUpDetailsEntity detailsEntity : detailsEntities) {
					FollowUpDetailsResponseDto followUpDetailsResponseDto = followUpDetailsMapper
							.entityToResponse(detailsEntity);
					followUpDetailsResponseDto.setMemberId(detailsEntity.getFollowUpMember().getId());
					followUpDetailsResponseDto.setLeadId(leadMasterEntity.getId());
					detailsResponseDtos.add(followUpDetailsResponseDto);
				}
			}
			dto.setFollowUpDetails(detailsResponseDtos);
			if (leadMasterEntity.getEventTypeId() != null) {
				dto.setEventTypeId(leadMasterEntity.getEventTypeId());
				dto.setEventTypeName(eventTypeMasterRepository
						.findByIdAndIsDeleteFalse(leadMasterEntity.getEventTypeId()).get().getNameEnglish());
			}

			/*
			 * if (leadMasterEntity.getFunctionId() != null) {
			 * dto.setFunctionId(leadMasterEntity.getFunctionId());
			 * dto.setFunctionName(functionMasterRepository.findByIdAndIsDeleteFalse(
			 * leadMasterEntity.getFunctionId()) .get().getNameEnglish()); }
			 */

			if (leadMasterEntity.getInquiryDate() != null) {
				dto.setInquiryDate(leadMasterEntity.getInquiryDate().format(formatter));
			}

			dto.setTentEventDate(mapTentDate(leadMasterEntity, leadMasterEntity.getUser()));
			responseDtos.add(dto);
		}

		return responseDtos;
	}

	@Override
	public List<LeadMasterResponseDto> getLeadesByLeadStatus(String leadStatus, Long userId) {

		List<LeadMasterResponseDto> responseDtos = new ArrayList<>();
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		List<LeadMasterEntity> allLeads = null;
		if (leadStatus.equalsIgnoreCase("ALL")) {
			allLeads = leadMasterRepository.findAllByIsDeleteFalseAndUser(user);
		} else {
			allLeads = leadMasterRepository.findByLeadStatusAndIsDeleteFalseAndUser(leadStatus, user);
		}

		for (LeadMasterEntity leadMasterEntity : allLeads) {
			LeadMasterResponseDto dto = leadMasterMapper.entityToResponse(leadMasterEntity);

			dto = leadMasterResponseDtoWithName(dto, leadMasterEntity);

			List<FollowUpDetailsResponseDto> detailsResponseDtos = new ArrayList<>();
			if (followUpDetailsRepository.existsByLead(leadMasterEntity)) {
				List<FollowUpDetailsEntity> detailsEntities = followUpDetailsRepository
						.findByLeadAndIsDeleteFalse(leadMasterEntity);
				for (FollowUpDetailsEntity detailsEntity : detailsEntities) {
					FollowUpDetailsResponseDto followUpDetailsResponseDto = followUpDetailsMapper
							.entityToResponse(detailsEntity);
					followUpDetailsResponseDto.setMemberId(detailsEntity.getFollowUpMember().getId());
					followUpDetailsResponseDto.setLeadId(leadMasterEntity.getId());
					detailsResponseDtos.add(followUpDetailsResponseDto);
				}
			}
			dto.setFollowUpDetails(detailsResponseDtos);
			if (leadMasterEntity.getEventTypeId() != null) {
				dto.setEventTypeId(leadMasterEntity.getEventTypeId());
				dto.setEventTypeName(eventTypeMasterRepository
						.findByIdAndIsDeleteFalse(leadMasterEntity.getEventTypeId()).get().getNameEnglish());
			}

			/*
			 * if (leadMasterEntity.getFunctionId() != null) {
			 * dto.setFunctionId(leadMasterEntity.getFunctionId());
			 * dto.setFunctionName(functionMasterRepository.findByIdAndIsDeleteFalse(
			 * leadMasterEntity.getFunctionId()) .get().getNameEnglish()); }
			 */

			if (leadMasterEntity.getInquiryDate() != null) {
				dto.setInquiryDate(leadMasterEntity.getInquiryDate().format(formatter));
			}

			dto.setTentEventDate(mapTentDate(leadMasterEntity, leadMasterEntity.getUser()));
			responseDtos.add(dto);
		}

		return responseDtos;
	}

	@Override
	public String generateNewLeadCode(Long userId) {

		String leadCode = commonService.generateLeadCode(userId);

		return leadCode;
	}

	@Override
	public List<FollowUpDetailsResponseDto> getFilteredFolloUpDetails(String startDate, String endDate, Long leadId,
			Boolean isCreated) {

		List<FollowUpDetailsResponseDto> followUpDetailsResponseDtos = new ArrayList<>();
		List<FollowUpDetailsEntity> detailsEntities;

		LocalDateTime formatedStartDate = commonService.dateFormatted(startDate).atStartOfDay();
		LocalDateTime formatedEndDate = commonService.dateFormatted(startDate).atTime(23, 59, 59);

		if (isCreated) {
			detailsEntities = followUpDetailsRepository.findByLeadIdAndIsDeleteFalseAndCreatedAtBetween(leadId,
					formatedStartDate, formatedEndDate);
		} else {
			detailsEntities = followUpDetailsRepository.findByLeadIdAndIsDeleteFalseAndFollowUpDateBetween(leadId,
					formatedStartDate, formatedEndDate);
		}

		for (FollowUpDetailsEntity followUpDetailsEntity : detailsEntities) {
			FollowUpDetailsResponseDto detailsResponseDto = followUpDetailsMapper
					.entityToResponse(followUpDetailsEntity);
			detailsResponseDto.setMemberId(followUpDetailsEntity.getFollowUpMember().getId());
			detailsResponseDto.setLeadId(leadId);
			followUpDetailsResponseDtos.add(detailsResponseDto);
		}

		return followUpDetailsResponseDtos;
	}

	@Override
	public Boolean deleteFollowUpDetailsById(Long id) {

		FollowUpDetailsEntity entity = followUpDetailsRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Followup is not found with id : " + id));

		entity.setIsDelete(true);
		followUpDetailsRepository.save(entity);

		return true;
	}

	private Long safeId(Object obj) {
		try {
			return obj == null ? null : (Long) obj.getClass().getMethod("getId").invoke(obj);
		} catch (Exception e) {
			return null;
		}
	}

	private String safeName(Object obj) {
		try {
			return obj == null ? null : (String) obj.getClass().getMethod("getName").invoke(obj);
		} catch (Exception e) {
			return null;
		}
	}

	private LeadMasterResponseDto leadMasterResponseDtoWithName(LeadMasterResponseDto dto,
			LeadMasterEntity leadMasterEntity) {

		dto.setLeadAssignId(safeId(leadMasterEntity.getLeadAssign()));
		dto.setPlanId(safeId(leadMasterEntity.getPlan()));
		dto.setCityId(safeId(leadMasterEntity.getCity()));
		dto.setStateId(safeId(leadMasterEntity.getState()));

		dto.setLeadAssignName(leadMasterEntity.getLeadAssign() == null ? null
				: leadMasterEntity.getLeadAssign().getFirstName() + " "
						+ leadMasterEntity.getLeadAssign().getLastName());
		dto.setPlanName(safeName(leadMasterEntity.getPlan()));
		dto.setCityName(safeName(leadMasterEntity.getCity()));
		dto.setStateName(safeName(leadMasterEntity.getState()));

		/*
		 * if (leadMasterEntity.getPipeline() != null) {
		 * dto.setPipelineId(leadMasterEntity.getPipeline().getId());
		 * dto.setPipelineName(leadMasterEntity.getPipeline().getPipelineName()); }
		 * 
		 * if (leadMasterEntity.getOpenStage() != null) {
		 * dto.setOpenStageId(leadMasterEntity.getOpenStage().getId());
		 * dto.setOpenStageName(leadMasterEntity.getOpenStage().getStage()); } if
		 * (leadMasterEntity.getCloseStage() != null) {
		 * dto.setCloseStageId(leadMasterEntity.getCloseStage().getId());
		 * dto.setCloseStageName(leadMasterEntity.getCloseStage().getStage()); } if
		 * (leadMasterEntity.getLeadFollowUpDate() != null) {
		 * dto.setLeadFollowUpDate(dateMapperp.dateTimeToString(leadMasterEntity.
		 * getLeadFollowUpDate())); }
		 */

		dto.setUserId(leadMasterEntity.getUser().getId());

		if (leadMasterEntity.getEventTypeId() != null) {
			dto.setEventTypeId(leadMasterEntity.getEventTypeId());
			dto.setEventTypeName(eventTypeMasterRepository.findByIdAndIsDeleteFalse(leadMasterEntity.getEventTypeId())
					.get().getNameEnglish());
		}

		/*
		 * if (leadMasterEntity.getFunctionId() != null) {
		 * dto.setFunctionId(leadMasterEntity.getFunctionId());
		 * dto.setFunctionName(functionMasterRepository.findByIdAndIsDeleteFalse(
		 * leadMasterEntity.getFunctionId()) .get().getNameEnglish()); }
		 */

		if (leadMasterEntity.getInquiryDate() != null) {
			dto.setInquiryDate(leadMasterEntity.getInquiryDate().format(formatter));
		}

		dto.setTentEventDate(mapTentDate(leadMasterEntity, leadMasterEntity.getUser()));
		return dto;
	}

	@Override
	public List<LeadMasterResponseDto> assignMultipleLeadToMember(List<Long> leadIds, Long memberId, String description,
			String closeDate) {
		UserMasterEntity member = userMasterRepository.findById(memberId)
				.orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

		List<LeadMasterEntity> leads = leadMasterRepository.findAllById(leadIds);

		if (leads.size() != leadIds.size()) {
			Set<Long> foundIds = leads.stream().map(LeadMasterEntity::getId).collect(Collectors.toSet());
			List<Long> missingIds = leadIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());

			throw new RuntimeException("Leads not found for ids: " + missingIds);
		}

//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
//		LocalDateTime cdate = LocalDateTime.parse(closeDate, formatter);

		leads.forEach(lead -> {
			if (lead.getLeadAssign() == null || !lead.getLeadAssign().getId().equals(memberId)) {
				lead.setLeadAssign(member);
			}
			//lead.setDescription(description);
			//lead.setCloseDate(cdate);
		});

		List<LeadMasterEntity> savedLeads = leadMasterRepository.saveAll(leads);

		List<LeadMasterResponseDto> response = new ArrayList<>();

		for (LeadMasterEntity entity : savedLeads) {
			response.add(convertToDto(entity));
		}
		return response;
	}

	private LeadMasterResponseDto convertToDto(LeadMasterEntity entity) {

		LeadMasterResponseDto dto = new LeadMasterResponseDto();

		dto.setId(entity.getId());
		dto.setLeadCode(entity.getLeadCode());
		dto.setLeadType(entity.getLeadType());
		// dto.setLeadStatus(entity.getLeadStatus());
		dto.setLeadSource(leadSourceMapper.entityToResponse(entity.getLeadSource()));
		dto.setLeadRemark(entity.getLeadRemark());

		if (entity.getLeadAssign() != null) {
			dto.setLeadAssignId(entity.getLeadAssign().getId());
			dto.setLeadAssignName(entity.getLeadAssign().getFirstName() + " " + entity.getLeadAssign().getLastName());
		}

		if (entity.getPlan() != null) {
			dto.setPlanId(entity.getPlan().getId());
			dto.setPlanName(entity.getPlan().getName()); // adjust field if needed
		}

		dto.setClientName(entity.getClientName());
		dto.setEmailId(entity.getEmailId());
		dto.setContactNumber(entity.getContactNumber());

		if (entity.getCity() != null) {
			dto.setCityId(entity.getCity().getId());
			dto.setCityName(entity.getCity().getName());
		}

		if (entity.getState() != null) {
			dto.setStateId(entity.getState().getId());
			dto.setStateName(entity.getState().getName());
		}

		dto.setIsDelete(entity.getIsDelete());

		if (entity.getCreatedAt() != null) {
			dto.setCreatedAt(entity.getCreatedAt().toString());
		}
		dto.setUserId(entity.getUser().getId());
		if (entity.getEventTypeId() != null) {
			dto.setEventTypeId(entity.getEventTypeId());
			dto.setEventTypeName(
					eventTypeMasterRepository.findByIdAndIsDeleteFalse(entity.getEventTypeId()).get().getNameEnglish());
		}

		/*
		 * if (entity.getFunctionId() != null) {
		 * dto.setFunctionId(entity.getFunctionId()); dto.setFunctionName(
		 * functionMasterRepository.findByIdAndIsDeleteFalse(entity.getFunctionId()).get
		 * ().getNameEnglish()); }
		 */

		if (entity.getInquiryDate() != null) {
			dto.setInquiryDate(entity.getInquiryDate().format(formatter));
		}

		dto.setTentEventDate(mapTentDate(entity, entity.getUser()));
		dto.setFollowUpDetails(new ArrayList<>());

		return dto;
	}

	@Override
	public Map<String, Long> getCountLeadByLeadType(Long userId) {
		Map<String, Long> response = new HashMap<>();
		response.put("Hot", 0L);
		response.put("Inquire", 0L);
		response.put("Cold", 0L);

		List<Object[]> leadTypes = leadMasterRepository.countByLeadType(userId);

		for (Object[] row : leadTypes) {
			String leadType = (String) row[0];
			Long count = ((Number) row[1]).longValue();
			response.put(leadType, count);
		}

		return response;
	}

	@Override
	public Map<String, Long> getCountLeadByLeadStatus(Long userId) {

	    Map<String, Long> response = new HashMap<>();

	    response.put("Total Leads", 0L);
	    response.put("Open", 0L);
	    response.put("Won", 0L);
	    response.put("Lost", 0L);
	    response.put("Today's FUP", 0L);

	    // Total Leads
	    response.put("Total Leads", leadMasterRepository.countByIsDeleteFalse(userId));
	    
	    response.put("Open", leadMasterRepository.countByStatusTypeName(userId,"Open"));

	    response.put("Won", leadMasterRepository.countByStatusTypeName(userId,"Won"));
	    
	    response.put("Lost", leadMasterRepository.countByStatusTypeName(userId,"Lost"));

	    // Today's Follow Up
		
		  response.put("Today's FUP",
		  followUpDetailsRepository.countTodayFollowUp(userId));
		 

	    return response;
	}
	
	/*
	 * @Override public Map<String, Long> getCountLeadByLeadStatus(Long userId) {
	 * Map<String, Long> response = new HashMap<>(); response.put("Pending", 0L);
	 * response.put("Confirmed", 0L); response.put("Cancel", 0L);
	 * 
	 * List<Object[]> leadStatus = leadMasterRepository.countByLeadStatus(userId);
	 * 
	 * for (Object[] row : leadStatus) { String status = (String) row[0]; Long count
	 * = ((Number) row[1]).longValue(); response.put(status, count); }
	 * 
	 * return response; }
	 */

	@Override
	public AssignMemberResponseDTO getAllFollowUpByMemberId(Long memberId, Long userId) {
		UserMasterEntity user = userMasterRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + memberId));
		AssignMemberResponseDTO response = new AssignMemberResponseDTO();
		// Fetch member details only if a specific member is selected
	    if (memberId != -1) {
	        UserMasterEntity member = userMasterRepository.findById(memberId)
	                .orElseThrow(() -> new RuntimeException("Member not found with id : " + memberId));

	        response.setMemberId(member.getId());
	        response.setName(member.getFirstName() + " " + member.getLastName());
	        response.setEmail(member.getEmail());
	        response.setMobileNo(member.getContactNo());
	    }
		List<Object[]> rows = leadMasterRepository.getAllFollowUpsByMember(memberId, user.getId());

		
		
		List<FollowUpResponseDto> followUps = new ArrayList<>();
		/*
		 * if (rows == null || rows.isEmpty()) { response.setMemberId(memberId);
		 * response.setName(member.getFirstName() + " " + member.getLastName());
		 * response.setEmail(member.getEmail());
		 * response.setMobileNo(member.getContactNo()); } else {
		 */
			for (Object[] row : rows) {

				if (response.getMemberId() == null) {
					response.setMemberId(((Number) row[0]).longValue());
					String firstName = (String) row[1];
					String lastName = (String) row[2];
					response.setName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
					response.setEmail((String) row[3]);
					response.setMobileNo((String) row[4]);
				}

				FollowUpResponseDto dto = new FollowUpResponseDto();
				dto.setId(row[5] != null ? ((Number) row[5]).longValue() : null);
				dto.setFollowUpType((String) row[6]);
				dto.setFollowUpStatus((String) row[7]);
				dto.setFollowUpDate(row[8] != null ? row[8].toString() : null);
				dto.setClientRemarks((String) row[9]);
				dto.setEmployeeRemarks((String) row[10]);
				dto.setIsDelete((Boolean) row[11]);
				dto.setCreatedAt(row[12] != null ? row[12].toString() : null);
				followUps.add(dto);
			}
		//}
		response.setFollowUps(followUps);
		return response;
	}

	@Override
	public Boolean changeLeadStage(Long leadId, String stageType, Long stageId, String remark,
			FollowUpDetailsRequestDto followUpRequestDto, Long assignId) {

		UserMasterEntity assign = userMasterRepository.findByIdAndIsDeleteFalse(assignId)
				.orElseThrow(() -> new RuntimeException("Member not found with id : " + assignId));

		Optional<LeadMasterEntity> optional = leadMasterRepository.findByIdAndIsDeleteFalse(leadId);
		if (!optional.isPresent()) {
			throw new RuntimeException("Lead not found with id " + leadId);
		}

		LeadMasterEntity entity = optional.get();
		entity.setLeadAssign(assign);
		entity.setUpdatedAt(commonService.getCurrentDateTime());
		// entity.setOverallRemark(remark);

		/*
		 * if (stageType.equalsIgnoreCase("open_stage")) { PipelineOpenStageEntity
		 * openStageEntity = openStageRepository.findByIdAndIsDeleteFalse(stageId)
		 * .orElseThrow(() -> new RuntimeException("Open stage is not found with id : "
		 * + stageId)); entity.setOpenStage(openStageEntity);
		 * entity.setCloseStage(null);
		 * 
		 * } else if (stageType.equalsIgnoreCase("close_stage")) {
		 * PipelineCloseStageEntity closeStageEntity =
		 * closeStageRepository.findByIdAndIsDeleteFalse(stageId) .orElseThrow(() -> new
		 * RuntimeException("Close stage is not found with id : " + stageId));
		 * entity.setCloseStage(closeStageEntity); entity.setOpenStage(null); }
		 */

		leadMasterRepository.save(entity);

		if (followUpRequestDto.getMemberId() != 0 && followUpRequestDto.getLeadId() != 0) {
			UserMasterEntity member = userMasterRepository.findByIdAndIsDeleteFalse(followUpRequestDto.getMemberId())
					.orElseThrow(() -> new RuntimeException(
							"Member not found with id : " + followUpRequestDto.getMemberId()));
			FollowUpDetailsEntity followUpDetailsEntity = followUpDetailsMapper.requestToEntity(followUpRequestDto);
			followUpDetailsEntity.setLead(entity);
			followUpDetailsEntity.setFollowUpMember(member);
			followUpDetailsRepository.save(followUpDetailsEntity);
		}

		return true;
	}

	@Override
	public Boolean addOrUpdateFollowUp(List<FollowUpDetailsRequestDto> followUpRequests) {
		if (!followUpRequests.isEmpty()) {
			for (FollowUpDetailsRequestDto followUpDetailsRequestDto : followUpRequests) {
				FollowUpDetailsEntity followUpDetailsEntity = new FollowUpDetailsEntity();

				if (followUpDetailsRequestDto.getId() == 0) {
					followUpDetailsEntity = followUpDetailsMapper.requestToEntity(followUpDetailsRequestDto);
				} else {

					FollowUpDetailsEntity detailsEntity = followUpDetailsRepository
							.findById(followUpDetailsRequestDto.getId()).orElseThrow(() -> new RuntimeException(
									"Follow Up Details Not FOund with id : " + followUpDetailsRequestDto.getId()));

					followUpDetailsEntity = detailsEntity;

					followUpDetailsEntity.setIsDelete(false);
					followUpDetailsEntity.setUpdatedAt(commonService.getCurrentDateTime());
					followUpDetailsMapper.updateEntityFromRequest(followUpDetailsRequestDto, followUpDetailsEntity);

				}

				UserMasterEntity member = userMasterRepository.findById(followUpDetailsRequestDto.getMemberId())
						.orElseThrow(() -> new RuntimeException(
								"Member not found with id : " + followUpDetailsRequestDto.getMemberId()));

				LeadMasterEntity lead = leadMasterRepository
						.findByIdAndIsDeleteFalse(followUpDetailsRequestDto.getLeadId())
						.orElseThrow(() -> new RuntimeException(
								"Lead not found with id : " + followUpDetailsRequestDto.getLeadId()));

				followUpDetailsEntity.setFollowUpMember(member);
				followUpDetailsEntity.setLead(lead);
				followUpDetailsEntity
						.setFollowUpDate(dateMapperp.stringToDateTime(followUpDetailsRequestDto.getFollowUpDate()));
				followUpDetailsEntity = followUpDetailsRepository.save(followUpDetailsEntity);
			}
			return true;
		}
		return false;
	}

	
	  public List<LeadMasterResponseDto> getDatewiseLeadSummaryReport(LocalDate
	  firstDate, LocalDate lastDate, Long statusId, Long sourceId, String priority,
	  List<Long>leadAssignId,Long userid) {
	  
		  List<LeadMasterResponseDto> responseDtos = new ArrayList<>();
		  
		  Boolean flag = false;
		  if(leadAssignId == null || leadAssignId.isEmpty()) {
			  flag = true;
		  }
		  
		  List<Object[]> result =
		  leadMasterRepository.findDatewiseLeadSummary(firstDate,
		  lastDate,statusId,sourceId,priority,leadAssignId,userid, flag);
		  
		  for (Object[] row : result) { 
			  int index = 0; LeadMasterResponseDto dto = new
			  LeadMasterResponseDto();
			  
			  dto.setClientName(commonService.getString(row[index++]));
			  dto.setContactNumber(commonService.getString(row[index++]));
			  dto.setLeadAssignName(commonService.getString(row[index++]));
			  
			  dto.setLeadStatusName(commonService.getString(row[index++]));
			  dto.setLeadSourceName(commonService.getString(row[index++]));
			  
			  dto.setEventTypeName(commonService.getString(row[index++])); 
			  Object person = row[index++];
			  
			  if (person != null) { 
				  dto.setPerson(person.toString()); 
			  }else {
				  dto.setPerson(""); 
			  }
			  
			  Object inq_date = row[index++]; 
			  if (inq_date != null) {
				  dto.setInquiryDate(inq_date.toString()); 
			  }else{ 
				  dto.setInquiryDate(""); 
			  }
			  Object lead_remark = row[index++]; if (lead_remark != null) {
			  dto.setLeadRemark(lead_remark.toString()); }else { dto.setLeadRemark(""); }
			  responseDtos.add(dto); 
			  
			  Object lead_priority = row[index++];
			  
			  if (lead_priority != null) { 
				  dto.setLeadPriority(lead_priority.toString()); 
			  }else {
				  dto.setLeadPriority(""); 
			  }
		  }
	  
	  return responseDtos; }
	 
	
	@Transactional
	public String changeLeadStatus(List<Long> leadIds,Long leadStatusId) {

	    LeadStatusEntity leadStatus = leadStatusRepository
	            .findById(leadStatusId)
	            .orElseThrow(() -> new RuntimeException(
	                    "Lead Status not found with id : " + leadStatusId));

	    List<LeadMasterEntity> leads = leadMasterRepository.findAllById(leadIds);

	    if (leads.isEmpty()) {
	        throw new RuntimeException("No leads found.");
	    }

	    for (LeadMasterEntity lead : leads) {
	        lead.setLeadStatus(leadStatus);
	        lead.setUpdatedAt(commonService.getCurrentDateTime());
	    }

	    leadMasterRepository.saveAll(leads);

	    return "Lead status updated successfully.";
	}
	
	@Override
	public List<LeadMasterResponseDto> searchLeads(Long statusId, String priority, Long sourceId,Long userId) {

	    List<LeadMasterEntity> entities = leadMasterRepository.searchLeads(statusId,priority,sourceId,userId);

	    List<LeadMasterResponseDto> responseList = new ArrayList<>();

	    for (LeadMasterEntity entity : entities) {

	        LeadMasterResponseDto dto = leadMasterMapper.entityToResponse(entity);

	        if (entity.getLeadStatus() != null) {
	            dto.setLeadStatusId(entity.getLeadStatus().getLeadStatusId());
	            dto.setLeadStatusName(entity.getLeadStatus().getLeadStatus().getStatusType());
	        }

	        if (entity.getLeadSource() != null) {
	            dto.setLeadSourceId(entity.getLeadSource().getLeadSourceId());
	            dto.setLeadSourceName(entity.getLeadSource().getSourceName());
	        }

	        if (entity.getLeadAssign() != null) {
	            dto.setLeadAssignId(entity.getLeadAssign().getId());
	            dto.setLeadAssignName(entity.getLeadAssign().getFirstName() + " "
	                    + entity.getLeadAssign().getLastName());
	        }

	        if (entity.getPlan() != null) {
	            dto.setPlanId(entity.getPlan().getId());
	            dto.setPlanName(entity.getPlan().getName());
	        }

	        if (entity.getCity() != null) {
	            dto.setCityId(entity.getCity().getId());
	            dto.setCityName(entity.getCity().getName());
	        }

	        if (entity.getState() != null) {
	            dto.setStateId(entity.getState().getId());
	            dto.setStateName(entity.getState().getName());
	        }

	        responseList.add(dto);
	    }

	    return responseList;
	}
	
	public List<LeadMasterResponseDto> getDatewiseFollowupSummaryReport(LocalDate
			  firstDate, LocalDate lastDate, Long statusId, Long sourceId, String priority,
			  List<Long>leadAssignId,Long userid) {
			  
				  List<LeadMasterResponseDto> responseDtos = new ArrayList<>();
				  
				  Boolean flag = false;
				  if(leadAssignId == null || leadAssignId.isEmpty()) {
					  flag = true;
				  }
				  
				  List<Object[]> result =
				  leadMasterRepository.findDatewiseFollowupSummary(firstDate,
				  lastDate,statusId,sourceId,priority,leadAssignId,userid, flag);
				  
				  for (Object[] row : result) {

				        int index = 0;
				        LeadMasterResponseDto dto = new LeadMasterResponseDto();

				        dto.setClientName(commonService.getString(row[index++]));
				        dto.setContactNumber(commonService.getString(row[index++]));
				        dto.setCompanyName(commonService.getString(row[index++]));
				        dto.setLeadSourceName(commonService.getString(row[index++]));
				        dto.setInquiryDate(commonService.getString(row[index++]));
				        dto.setFollowUpDate(commonService.getString(row[index++]));
				        dto.setFollowUpMode(commonService.getString(row[index++]));
				        dto.setDiscussion(commonService.getString(row[index++]));
				        dto.setLeadStatusName(commonService.getString(row[index++]));
				        dto.setLeadPriority(commonService.getString(row[index++]));
				        dto.setDealValue(commonService.getString(row[index++]));
				        dto.setCoordinatorName(commonService.getString(row[index++]));

				        responseDtos.add(dto);
				    }
			  
			  return responseDtos; }
}
