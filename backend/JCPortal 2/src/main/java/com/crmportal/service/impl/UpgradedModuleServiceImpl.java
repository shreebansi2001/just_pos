package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crmportal.entity.PaymentInfo;
import com.crmportal.entity.UpgradedModuleEntity;
import com.crmportal.entity.UpgradedModuleFeaturesEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserOtpEntity;
import com.crmportal.entity.UserUpgradedModuleEntity;
import com.crmportal.repository.PaymentInfoRepository;
import com.crmportal.repository.UpgradedModuleFeaturesRepository;
import com.crmportal.repository.UpgradedModuleRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserOtpRepository;
import com.crmportal.repository.UserUpgradedModuleRepository;
import com.crmportal.request.dto.UpgradedModuleFeaturesRequestDto;
import com.crmportal.request.dto.UpgradedModuleRequestDto;
import com.crmportal.request.dto.UserUpgradedListOfModulePaymentRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.UpgradedModuleFeaturesResponseDto;
import com.crmportal.response.dto.UpgradedModuleResponseDto;
import com.crmportal.response.dto.UserUpgradedModuleMail;
import com.crmportal.service.CommonService;
import com.crmportal.service.PaymentInfoService;
import com.crmportal.service.UpgradedModuleService;

@Service
public class UpgradedModuleServiceImpl implements UpgradedModuleService {

	@Autowired
	UpgradedModuleRepository upgradedModuleRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	UpgradedModuleFeaturesRepository upgradedModuleFeaturesRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	PaymentInfoService paymentInfoService;

	@Autowired
	UserUpgradedModuleRepository userUpgradedModuleRepository;

	@Autowired
	PaymentInfoRepository paymentInfoRepository;

	@Value("${support.mobile}")
	private String mobile;

	@Autowired
	UserOtpRepository userOtpRepository;

	@Override
	@Transactional
	public Boolean addOrUpgradedModule(UpgradedModuleRequestDto request) {

		if (request.getId() == -1) {
			if (upgradedModuleRepository.existsByModuleNameAndIsDeleteFalse(request.getModuleName())) {
				throw new RuntimeException("Module name already exists!");
			}
		} else {
			if (upgradedModuleRepository.existsByModuleNameAndIdNotAndIsDeleteFalse(request.getModuleName(),
					request.getId())) {
				throw new RuntimeException("Module name already exists!");
			}
		}

		UpgradedModuleEntity entity;

		if (request.getId() == -1) {
			entity = new UpgradedModuleEntity();
		} else {
			entity = upgradedModuleRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Upgraded Module Not Found With Id:-" + request.getId()));
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		entity.setDescription(request.getDescription());
		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setBillingCycle(request.getBillingCycle());
		entity.setModuleName(request.getModuleName());
		entity.setPrice(request.getPrice());
		entity.setIsConfig(request.getIsConfig());

		UpgradedModuleEntity savedEntity = upgradedModuleRepository.save(entity);

		if (request.getUpgradedModuleFeatures() != null && !request.getUpgradedModuleFeatures().isEmpty()) {

			upgradedModuleFeaturesRepository.deleteAllByUpgradedModuleId(savedEntity.getId());

			List<UpgradedModuleFeaturesEntity> entityFeaturesEntities = new ArrayList<>();

			for (UpgradedModuleFeaturesRequestDto dto : request.getUpgradedModuleFeatures()) {

				UpgradedModuleFeaturesEntity entityFeaturesEntity;

				if (dto.getId() == -1) {
					entityFeaturesEntity = new UpgradedModuleFeaturesEntity();
				} else {
					entityFeaturesEntity = upgradedModuleFeaturesRepository.findByIdAndIsDeleteFalse(dto.getId())
							.orElseThrow(() -> new RuntimeException(
									"Upgraded Module Features Not Found With Id:-" + dto.getId()));

					entityFeaturesEntity.setUpdatedAt(commonService.getCurrentDateTime());
				}

				entityFeaturesEntity.setFeatureText(dto.getFeatureText());
				entityFeaturesEntity.setIsDelete(false);
				entityFeaturesEntity.setUpgradedModuleId(savedEntity.getId());

				entityFeaturesEntities.add(entityFeaturesEntity);
			}

			upgradedModuleFeaturesRepository.saveAll(entityFeaturesEntities);
		}

		return true;
	}

	@Override
	public List<UpgradedModuleResponseDto> getAll(Boolean isActive, Long userId, Boolean isConfig) {

		List<Object[]> data = upgradedModuleRepository.getAllModules(isActive, userId, isConfig);

		Map<Long, UpgradedModuleResponseDto> moduleMap = new LinkedHashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		for (Object[] row : data) {

			Long moduleId = ((Number) row[0]).longValue();

			UpgradedModuleResponseDto moduleDto = moduleMap.computeIfAbsent(moduleId, id -> {
				UpgradedModuleResponseDto dto = new UpgradedModuleResponseDto();

				dto.setId(id);
				dto.setModuleName((String) row[1]);
				dto.setPrice((BigDecimal) row[2]);
				dto.setDescription((String) row[3]);
				dto.setIsActive((Boolean) row[4]);
				dto.setBillingCycle((String) row[6]);
				dto.setIsConfig((Boolean) row[7]);
				dto.setIsPurchase(row[12] != null && ((Number) row[12]).intValue() == 1);

				if (row[13] != null) {
				    dto.setUncId(((Number) row[13]).longValue());
				}
				dto.setKey1((String) row[14]);
				dto.setKey2((String) row[15]);
				dto.setUrl((String) row[16]);

				if (row[17] != null) {
				    LocalDateTime dateTime = ((Timestamp) row[17]).toLocalDateTime();
				    dto.setStartDate(dateTime.format(formatter));
				}

				if (row[18] != null) {
				    LocalDateTime dateTime = ((Timestamp) row[18]).toLocalDateTime();
				    dto.setEndDate(dateTime.format(formatter));
				}

				if (row[5] != null) {
					LocalDateTime dateTime = ((Timestamp) row[5]).toLocalDateTime();
					dto.setCreatedAt(dateTime.format(formatter));
				}

				dto.setUpgradedModuleFeatures(new ArrayList<>());

				return dto;
			});

			if (row[8] != null) {
				UpgradedModuleFeaturesResponseDto feature = new UpgradedModuleFeaturesResponseDto();

				feature.setId(((Number) row[8]).longValue());
				feature.setFeatureText((String) row[9]);

				if (row[10] != null) {
					LocalDateTime dateTime = ((Timestamp) row[10]).toLocalDateTime();
					feature.setCreatedAt(dateTime.format(formatter));
				}

				feature.setUpgradedModuleId(((Number) row[11]).longValue());
				feature.setUpgradedModuleName((String) row[1]);

				moduleDto.getUpgradedModuleFeatures().add(feature);
			}
		}

		return new ArrayList<>(moduleMap.values());
	}

	@Override
	public Boolean addUserUpgradeModule(UserUpgradedModulePaymentRequestDto request) {

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found With Id:- " + request.getUserId()));

		for (UserUpgradedListOfModulePaymentRequestDto moduleReq : request.getUserUpgradedModulePayments()) {

			UpgradedModuleEntity moduleEntity = upgradedModuleRepository
					.findByIdAndIsDeleteFalseAndIsActiveTrue(moduleReq.getUpgradeModuleId())
					.orElseThrow(() -> new RuntimeException("Module Not Found: " + moduleReq.getUpgradeModuleId()));

			UserUpgradedModuleEntity existing = userUpgradedModuleRepository
					.findByUserIdAndIsDeleteFalseAndIsActiveTrueAndUpgradeModuleId(userMasterEntity.getId(),
							moduleEntity.getId());

			if (existing != null) {
				existing.setIsActive(false);
				userUpgradedModuleRepository.save(existing);
			}

			UserUpgradedModuleEntity entity = new UserUpgradedModuleEntity();
			entity.setIsActive(false);
			entity.setIsDelete(false);
			entity.setUserId(userMasterEntity.getId());
			entity.setUpgradeModuleId(moduleEntity.getId());
			entity.setPayAmount(moduleReq.getPayAmnt());

			if (Boolean.FALSE.equals(moduleReq.getIsOnline())) {
				entity.setIsPayDone(true);
			}

			entity = userUpgradedModuleRepository.save(entity);

			if (moduleReq.getPaymentData() != null
					&& Boolean.TRUE.equals(moduleReq.getPaymentData().getPaymentdone())) {

				entity.setIsPayDone(true);
				entity = userUpgradedModuleRepository.save(entity);

				PaymentInfo p = paymentInfoService.savePaymentResponse(moduleReq.getPaymentData(), userMasterEntity,
						null, null, moduleEntity, entity, null,null,null);

				if (Boolean.FALSE.equals(moduleReq.getIsOnline())) {
					p.setPaymentType("Offline");
					paymentInfoRepository.save(p);
				}
			}
		}

		return true;
	}

	@Override
	public Boolean isActive(Long userId, Boolean isActive, String otp, List<Long> moduleIds) {

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

		List<UserUpgradedModuleEntity> modules = userUpgradedModuleRepository
				.findByUserIdAndIsDeleteFalseAndStartDateIsNullAndEndDateIsNullAndUpgradeModuleIdIn(userId, moduleIds);

		if (modules == null || modules.isEmpty()) {
			throw new RuntimeException("No valid modules found or already expired.");
		}

		LocalDateTime startDate = commonService.getCurrentDateTime();

		for (UserUpgradedModuleEntity module : modules) {

			module.setIsActive(isActive);
			module.setStartDate(startDate);

			UpgradedModuleEntity upgradedModule = upgradedModuleRepository
					.findByIdAndIsDeleteFalseAndIsActiveTrue(module.getUpgradeModuleId())
					.orElseThrow(() -> new RuntimeException(
							"Upgraded Module Not Found With Id:- " + module.getUpgradeModuleId()));

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
			mail.setModuleName(upgradedModule.getModuleName());
			mail.setEmail(user.getEmail());
			mail.setUserName(user.getFirstName() + " " + user.getLastName());

//			commonService.sendUserUpgradedModuleNotification(mail);
		}

		userUpgradedModuleRepository.saveAll(modules);

		return true;
	}

	@Override
	@Transactional
	public Boolean deleteById(Long id) {

		UpgradedModuleEntity entity = upgradedModuleRepository.findByIdAndIsDeleteFalse(id).orElse(null);

		if (entity == null) {
			return false;
		} else {
			entity.setIsDelete(true);
			upgradedModuleRepository.save(entity);
			upgradedModuleFeaturesRepository.deleteAllByUpgradedModuleId(entity.getId());
			List<UserUpgradedModuleEntity> userUpgradedModuleEntity = userUpgradedModuleRepository
					.findByUpgradeModuleIdAndIsActiveTrue(entity.getId());
			if (!userUpgradedModuleEntity.isEmpty()) {
				for (UserUpgradedModuleEntity userUpgradedModule : userUpgradedModuleEntity) {
					userUpgradedModule.setIsActive(false);
					userUpgradedModuleRepository.save(userUpgradedModule);
				}
			}
			return true;
		}
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

		List<UserUpgradedModuleEntity> entities = userUpgradedModuleRepository
				.findByUserIdAndIsDeleteFalseAndIsActiveTrueAndUpgradeModuleIdIn(userId, moduleId);

		if (entities.isEmpty()) {
			throw new RuntimeException("No active modules found");
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate parsedDate = LocalDate.parse(endDate, formatter);
		LocalDateTime endDateTime = parsedDate.atStartOfDay();

		for (UserUpgradedModuleEntity entity : entities) {
			entity.setEndDate(endDateTime);
		}

		userUpgradedModuleRepository.saveAll(entities);

		userOtpRepository.delete(otpEntity);

		return true;
	}
}
