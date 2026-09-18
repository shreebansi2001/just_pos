package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AITemplateEntity;
import com.crmportal.entity.PaymentInfo;
import com.crmportal.entity.UpgradedModuleEntity;
import com.crmportal.entity.UserAssigneAITemplateEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserOtpEntity;
import com.crmportal.entity.UserUpgradedModuleEntity;
import com.crmportal.repository.AITemplateRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.PaymentInfoRepository;
import com.crmportal.repository.UserAssigneAiTemplateRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserOtpRepository;
import com.crmportal.request.dto.AITemplateRequestDto;
import com.crmportal.request.dto.GenerateAIRequestDto;
import com.crmportal.request.dto.UserUpgradedListOfModulePaymentRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.AIMenuCategoryWithItemsResponseDto;
import com.crmportal.response.dto.AITemplateResponseDto;
import com.crmportal.response.dto.AiEventFunctionMenuResponseDto;
import com.crmportal.response.dto.ChatResponse;
import com.crmportal.response.dto.CustomPackageResponseDto;
import com.crmportal.response.dto.MenuPreparationItemResponseDto;
import com.crmportal.response.dto.UserUpgradedModuleMail;
import com.crmportal.service.AITemplateService;
import com.crmportal.service.CommonService;
import com.crmportal.service.CustomPackageService;
import com.crmportal.service.MenuPreparationService;
import com.crmportal.service.PaymentInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AITemplateServiceImpl implements AITemplateService {

	@Autowired
	AITemplateRepository aiTemplateRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	UserAssigneAiTemplateRepository userAssigneAiTemplateRepository;

	@Autowired
	PaymentInfoService paymentInfoService;

	@Autowired
	PaymentInfoRepository paymentInfoRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	UserOtpRepository userOtpRepository;

	@Autowired
	MenuPreparationRepository menuPreparationRepository;
	
	@Autowired
	MenuPreparationService menuPreparationService;

	@Autowired
	Environment environment;

	@Autowired
	GeminiService geminiService;
	
	@Autowired
	CustomPackageService customPackageService;

	@Value("${support.mobile}")
	private String mobile;

	public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public Boolean addOrUpdate(AITemplateRequestDto request) {
		AITemplateEntity entity;

		boolean isNew = (request.getId() == null || request.getId() == 0 || request.getId() == -1);

		if (isNew) {
			aiTemplateRepository.findByNameEnglishAndIsDeleteFalse(request.getNameEnglish()).ifPresent(e -> {
				throw new RuntimeException("Template with same English name already exists!");
			});

			entity = new AITemplateEntity();
			entity.setCreatedAt(commonService.getCurrentDateTime());

		} else {
			entity = aiTemplateRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("AI Template Not Found With Id:-" + request.getId()));

			aiTemplateRepository.findByNameEnglishAndIsDeleteFalseAndIdNot(request.getNameEnglish(), request.getId())
					.ifPresent(e -> {
						throw new RuntimeException("Template with same English name already exists!");
					});

			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameHindi(request.getNameHindi());
		entity.setNameGujarati(request.getNameGujarati());

		entity.setInstructionEnglish(request.getInstructionEnglish());
		entity.setInstructionHindi(request.getInstructionHindi());
		entity.setInstructionGujarati(request.getInstructionGujarati());

		entity.setRuleEnglish(request.getRuleEnglish());
		entity.setRuleHindi(request.getRuleHindi());
		entity.setRuleGujarati(request.getRuleGujarati());
		entity.setBillingCycle(request.getBillingCycle());
		entity.setQueryInstruction(request.getQueryInstruction());
		entity.setIsActive(true);
		entity.setAiModel(request.getAiModel());
		entity.setPrice(request.getPrice());
		aiTemplateRepository.save(entity);

		return true;
	}

	@Override
	public List<AITemplateResponseDto> getAll(Boolean isActive) {
		List<AITemplateResponseDto> dtos = new ArrayList<>();
		List<AITemplateEntity> entities = aiTemplateRepository.findAllFiltered(isActive);
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (AITemplateEntity entity : entities) {
				AITemplateResponseDto dto = mapToDto(entity);
				dtos.add(dto);
			}
			return dtos;
		}
	}

	private AITemplateResponseDto mapToDto(AITemplateEntity entity) {
		AITemplateResponseDto dto = new AITemplateResponseDto();
		dto.setId(entity.getId());
		dto.setNameEnglish(entity.getNameEnglish());
		dto.setNameGujarati(entity.getNameGujarati());
		dto.setNameHindi(entity.getNameHindi());
		dto.setInstructionEnglish(entity.getInstructionEnglish());
		dto.setInstructionHindi(entity.getInstructionHindi());
		dto.setInstructionGujarati(entity.getInstructionGujarati());
		dto.setRuleEnglish(entity.getRuleEnglish());
		dto.setRuleHindi(entity.getRuleHindi());
		dto.setRuleGujarati(entity.getRuleGujarati());
		dto.setIsActive(entity.getIsActive());
		dto.setQueryInstruction(entity.getQueryInstruction());
		dto.setPrice(entity.getPrice());
		dto.setBillingCycle(entity.getBillingCycle());
		dto.setAiModel(entity.getAiModel());
		dto.setCreatedAt(entity.getCreatedAt().format(DATE_FMT));
		return dto;
	}

	@Override
	public AITemplateResponseDto getById(Long id) {
		AITemplateEntity entity = aiTemplateRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("AITemplate Not Found With Id:-" + id));
		AITemplateResponseDto dto = mapToDto(entity);
		return dto;
	}

	@Override
	public Boolean deleteById(Long id) {
		AITemplateEntity entity = aiTemplateRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("AITemplate Not Found With Id:-" + id));
		entity.setIsDelete(true);
		aiTemplateRepository.save(entity);

		List<UserAssigneAITemplateEntity> userUpgradedModuleEntity = userAssigneAiTemplateRepository
				.findByAiTemplateIdAndIsActiveTrue(entity.getId());
		if (!userUpgradedModuleEntity.isEmpty()) {
			for (UserAssigneAITemplateEntity userUpgradedModule : userUpgradedModuleEntity) {
				userUpgradedModule.setIsActive(false);
				userAssigneAiTemplateRepository.save(userUpgradedModule);
			}
		}

		return true;
	}

	@Override
	public Boolean isActive(Long id, Boolean isActive) {
		AITemplateEntity entity = aiTemplateRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("AITemplate Not Found With Id:-" + id));
		entity.setIsActive(isActive);
		aiTemplateRepository.save(entity);
		return true;
	}

	@Override
	public Boolean addUserAiTemplate(UserUpgradedModulePaymentRequestDto request) {
		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found With Id:- " + request.getUserId()));

		for (UserUpgradedListOfModulePaymentRequestDto moduleReq : request.getUserUpgradedModulePayments()) {

			AITemplateEntity moduleEntity = aiTemplateRepository
					.findByIdAndIsDeleteFalseAndIsActiveTrue(moduleReq.getUpgradeModuleId()).orElseThrow(
							() -> new RuntimeException("Ai Template Not Found: " + moduleReq.getUpgradeModuleId()));

			UserAssigneAITemplateEntity existing = userAssigneAiTemplateRepository
					.findByUserIdAndIsDeleteFalseAndIsActiveTrueAndAiTemplateId(userMasterEntity.getId(),
							moduleEntity.getId());

			if (existing != null) {
				existing.setIsActive(false);
				userAssigneAiTemplateRepository.save(existing);
			}

			UserAssigneAITemplateEntity entity = new UserAssigneAITemplateEntity();
			entity.setIsActive(false);
			entity.setIsDelete(false);
			entity.setUserId(userMasterEntity.getId());
			entity.setAiTemplateId(entity.getId());
			entity.setPayAmount(moduleReq.getPayAmnt());

			if (Boolean.FALSE.equals(moduleReq.getIsOnline())) {
				entity.setIsPayDone(true);
			}

			entity = userAssigneAiTemplateRepository.save(entity);

			if (moduleReq.getPaymentData() != null
					&& Boolean.TRUE.equals(moduleReq.getPaymentData().getPaymentdone())) {

				entity.setIsPayDone(true);
				entity = userAssigneAiTemplateRepository.save(entity);

				PaymentInfo p = paymentInfoService.savePaymentResponse(moduleReq.getPaymentData(), userMasterEntity,
						null, null, null, null, null, entity, moduleEntity);

				if (Boolean.FALSE.equals(moduleReq.getIsOnline())) {
					p.setPaymentType("Offline");
					paymentInfoRepository.save(p);
				}
			}
		}

		return true;
	}

	@Override
	public Boolean extendDate(Long userId, List<Long> moduleId, String endDate, String otp) {
		UserMasterEntity superAdmin = userMasterRepository.findByContactNoAndIsDeleteFalse(mobile)
				.orElseThrow(() -> new RuntimeException("Mobile number not registered. Please sign up first."));

		UserOtpEntity otpEntity = userOtpRepository.findByEmailAndOtpAndIsUsedFalse(superAdmin.getEmail(), otp)
				.orElseThrow(() -> new RuntimeException("Invalid OTP"));

		if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP expired");
		}

		if (endDate == null || endDate.trim().isEmpty()) {
			throw new RuntimeException("Please Input Date Field");
		}

		List<UserAssigneAITemplateEntity> entities = userAssigneAiTemplateRepository
				.findByUserIdAndIsDeleteFalseAndIsActiveTrueAndAiTemplateIdIn(userId, moduleId);

		if (entities.isEmpty()) {
			throw new RuntimeException("No active modules found");
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate parsedDate = LocalDate.parse(endDate, formatter);
		LocalDateTime endDateTime = parsedDate.atStartOfDay();

		for (UserAssigneAITemplateEntity entity : entities) {
			entity.setEndDate(endDateTime);
		}

		userAssigneAiTemplateRepository.saveAll(entities);

		userOtpRepository.delete(otpEntity);

		return true;
	}

	@Override
	public Boolean isActiveUserAI(Long userId, Boolean isActive, String otp, List<Long> moduleIds) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found With Id:- " + userId));

		UserMasterEntity superAdmin = userMasterRepository.findByContactNoAndIsDeleteFalse(mobile)
				.orElseThrow(() -> new RuntimeException("Mobile number not registered. Please sign up first."));

		UserOtpEntity otpEntity = userOtpRepository.findByEmailAndOtpAndIsUsedFalse(superAdmin.getEmail(), otp)
				.orElseThrow(() -> new RuntimeException("Invalid OTP"));

		if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP expired");
		}

		userOtpRepository.delete(otpEntity);

		List<UserAssigneAITemplateEntity> modules = userAssigneAiTemplateRepository
				.findByUserIdAndIsDeleteFalseAndStartDateIsNullAndEndDateIsNullAndAiTemplateIdIn(userId, moduleIds);

		if (modules == null || modules.isEmpty()) {
			throw new RuntimeException("No valid modules found or already expired.");
		}

		LocalDateTime startDate = commonService.getCurrentDateTime();

		for (UserAssigneAITemplateEntity module : modules) {

			module.setIsActive(isActive);
			module.setStartDate(startDate);

			AITemplateEntity upgradedModule = aiTemplateRepository
					.findByIdAndIsDeleteFalseAndIsActiveTrue(module.getAiTemplateId())
					.orElseThrow(() -> new RuntimeException(
							"Upgraded Module Not Found With Id:- " + module.getAiTemplateId()));

			String billingCycle = upgradedModule.getBillingCycle();

			if ("Week".equalsIgnoreCase(billingCycle) || "Weekly".equalsIgnoreCase(billingCycle)) {
				module.setEndDate(startDate.plusWeeks(1));
			} else if ("Month".equalsIgnoreCase(billingCycle) || "Monthly".equalsIgnoreCase(billingCycle)) {
				module.setEndDate(startDate.plusMonths(1));
			} else if ("Quarter".equalsIgnoreCase(billingCycle) || "Quarterly".equalsIgnoreCase(billingCycle)) {
				module.setEndDate(startDate.plusMonths(3));
			} else if ("Annually".equalsIgnoreCase(billingCycle) || "Annual".equalsIgnoreCase(billingCycle)) {
				module.setEndDate(startDate.plusYears(1));
			}

			UserUpgradedModuleMail mail = new UserUpgradedModuleMail();
			mail.setBillingCycle(billingCycle);
			mail.setModuleName(upgradedModule.getNameEnglish());
			mail.setEmail(user.getEmail());
			mail.setUserName(user.getFirstName() + " " + user.getLastName());

//			commonService.sendUserUpgradedModuleNotification(mail);
		}

		userAssigneAiTemplateRepository.saveAll(modules);

		return true;
	}

	@Override
	public Map<String, Object> generateAIData(GenerateAIRequestDto request) {

	    Map<String, Object> responseMap = new HashMap<>();

	    try {

	        UserMasterEntity user = userMasterRepository
	                .findByIdAndIsDeleteFalse(request.getUserId())
	                .orElseThrow(() ->
	                        new RuntimeException("User Not Found With Id:- " + request.getUserId()));

	        AITemplateEntity aiModule = aiTemplateRepository
	                .findByIdAndIsDeleteFalseAndIsActiveTrue(request.getAiTemplateId())
	                .orElseThrow(() ->
	                        new RuntimeException(
	                                "Upgraded Module Not Found With Id:- "
	                                        + request.getAiTemplateId()));

	        String userPrompt = "";

	        if (aiModule.getNameEnglish().equalsIgnoreCase("MENU_GENERATE")) {

	            CustomPackageResponseDto responseDto = customPackageService.getCustomPackageById(request.getPackageId());
	            ObjectMapper mapper = new ObjectMapper();
	            userPrompt = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDto);

	            userPrompt = "\n Function : "
	                    + request.getFunctionName()
	                    + " \n\n "+(request.getPriceRange() != null && !request.getPriceRange().equals("") ? "PRICE_RANGE : "+request.getPriceRange()+"\n\n" : "\n\n")
	                    + userPrompt;
	        } else if(aiModule.getNameEnglish().equalsIgnoreCase("MENU_GENERATE_FROM_EXISTING_MENU_PLANNING")) {
	        	List<AiEventFunctionMenuResponseDto> menuLst = menuPreparationService.getAiMenuData(request.getEventFunctionIds());
	        	String menuString = "";
	        	ObjectMapper mapper = new ObjectMapper();
	        	for (int i=0;i<menuLst.size();i++) {
	        		String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(menuLst.get(i));
	        		menuString = menuString + "\n \n Existing Menu "+(i+1)+" \n\n" +s ; 
	        	}
	        	
	        	userPrompt = "\n Function : "
	                    + request.getFunctionName()
	                    + " \n "
	                    + "\n\n"+""
	                    + menuString;
	        }

	        String aiResponse = geminiService.generateResponse(
	                aiModule.getInstructionEnglish(),
	                userPrompt
	        );

	        String cleanedResponse = aiResponse
	                .trim()
	                .replaceAll("^```json", "")
	                .replaceAll("^```", "")
	                .replaceAll("```$", "")
	                .trim();

	        System.out.println("===== AI RESPONSE START =====");
	        System.out.println(cleanedResponse);
	        System.out.println("===== AI RESPONSE END =====");

	        if (!cleanedResponse.startsWith("{")) {

	            responseMap.put("success", false);
	            responseMap.put("message", "Invalid JSON response from AI");
	            responseMap.put("rawResponse", cleanedResponse);

	            return responseMap;
	        }

	        JSONObject jsonObject = new JSONObject(cleanedResponse);

	        System.out.println("JsonData:- " + jsonObject);
	        
	        return jsonObject.toMap();

	    } catch (Exception e) {

	        e.printStackTrace();

	        responseMap.put("success", false);
	        responseMap.put("message", e.getMessage());
	    }

	    return responseMap;
	}
	private List<AIMenuCategoryWithItemsResponseDto> convertToItemDtoList(List<Object[]> rows) {

		return rows.stream().map(row -> {

			AIMenuCategoryWithItemsResponseDto dto = new AIMenuCategoryWithItemsResponseDto();

			dto.setMenuItemId(((Number) row[0]).longValue());
			dto.setMenuItemName((String) row[1]);
			dto.setMenuCategoryId(((Number) row[2]).longValue());
			dto.setMenuCategoryName((String) row[3]);

			return dto;

		}).collect(Collectors.toList());
	}
}
