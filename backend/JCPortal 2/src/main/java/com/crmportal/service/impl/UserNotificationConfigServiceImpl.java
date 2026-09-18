package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crmportal.entity.PaymentInfo;
import com.crmportal.entity.UpgradedModuleEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserNotificationConfigEntity;
import com.crmportal.entity.UserOtpEntity;
import com.crmportal.entity.UserUpgradedModuleEntity;
import com.crmportal.repository.PaymentInfoRepository;
import com.crmportal.repository.UpgradedModuleRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserNotificationConfigRepository;
import com.crmportal.repository.UserOtpRepository;
import com.crmportal.request.dto.UpdateUserNotificationRequestDto;
import com.crmportal.request.dto.UserUpgradedListOfModulePaymentRequestDto;
import com.crmportal.request.dto.UserUpgradedModulePaymentRequestDto;
import com.crmportal.response.dto.UserNotificationConfigResponseDto;
import com.crmportal.response.dto.UserUpgradedModuleMail;
import com.crmportal.service.CommonService;
import com.crmportal.service.PaymentInfoService;
import com.crmportal.service.UserNotificationConfigService;

@Service
public class UserNotificationConfigServiceImpl implements UserNotificationConfigService {

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	UserNotificationConfigRepository userNotificationConfigRepository;

	@Autowired
	UpgradedModuleRepository upgradedModuleRepository;

	@Autowired
	PaymentInfoService paymentInfoService;

	@Autowired
	PaymentInfoRepository paymentInfoRepository;

	@Value("${support.mobile}")
	private String mobile;

	@Autowired
	CommonService commonService;

	@Autowired
	UserOtpRepository userOtpRepository;

	@Override
	public Boolean addUserNotification(UserUpgradedModulePaymentRequestDto request) {

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found With Id:- " + request.getUserId()));

		for (UserUpgradedListOfModulePaymentRequestDto moduleReq : request.getUserUpgradedModulePayments()) {

			UpgradedModuleEntity moduleEntity = upgradedModuleRepository
					.findByIdAndIsDeleteFalseAndIsActiveTrue(moduleReq.getUpgradeModuleId())
					.orElseThrow(() -> new RuntimeException("Module Not Found: " + moduleReq.getUpgradeModuleId()));

			UserNotificationConfigEntity existing = userNotificationConfigRepository
					.findByUserIdAndIsDeleteFalseAndIsActiveTrueAndUpgradeModuleId(userMasterEntity.getId(),
							moduleEntity.getId());

			if (existing != null) {
				existing.setIsActive(false);
				userNotificationConfigRepository.save(existing);
			}

			UserNotificationConfigEntity entity = new UserNotificationConfigEntity();
			entity.setIsDelete(false);
			entity.setUserId(userMasterEntity.getId());
			entity.setUpgradeModuleId(moduleEntity.getId());
			entity.setPayAmount(moduleReq.getPayAmnt());

			if (Boolean.FALSE.equals(moduleReq.getIsOnline())) {
				entity.setIsActive(false);
				entity.setIsPayDone(true);
			} else {
				String billingCycle = moduleEntity.getBillingCycle();
				entity.setIsActive(true);
				LocalDateTime startDate = commonService.getCurrentDateTime();

				if ("Week".equalsIgnoreCase(billingCycle) || "Weekly".equalsIgnoreCase(billingCycle)) {
					entity.setEndDate(startDate.plusWeeks(1));
				} else if ("Month".equalsIgnoreCase(billingCycle) || "Monthly".equalsIgnoreCase(billingCycle)) {
					entity.setEndDate(startDate.plusMonths(1));
				} else if ("Quarter".equalsIgnoreCase(billingCycle) || "Quarterly".equalsIgnoreCase(billingCycle)) {
					entity.setEndDate(startDate.plusMonths(3));
				} else if ("Annually".equalsIgnoreCase(billingCycle) || "Annual".equalsIgnoreCase(billingCycle)) {
					entity.setEndDate(startDate.plusYears(1));
				}
			}

			entity = userNotificationConfigRepository.save(entity);

			if (moduleReq.getPaymentData() != null
					&& Boolean.TRUE.equals(moduleReq.getPaymentData().getPaymentdone())) {

				entity.setIsPayDone(true);
				entity = userNotificationConfigRepository.save(entity);

				PaymentInfo p = paymentInfoService.savePaymentResponse(moduleReq.getPaymentData(), userMasterEntity,
						null, null, moduleEntity, null, entity, null, null);

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

		List<UserNotificationConfigEntity> modules = userNotificationConfigRepository
				.findByUserIdAndIsDeleteFalseAndStartDateIsNullAndEndDateIsNullAndUpgradeModuleIdIn(userId, moduleIds);

		if (modules == null || modules.isEmpty()) {
			throw new RuntimeException("No valid modules found or already expired.");
		}

		LocalDateTime startDate = commonService.getCurrentDateTime();

		for (UserNotificationConfigEntity module : modules) {

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

		userNotificationConfigRepository.saveAll(modules);

		return true;
	}

	@Override
	public Boolean updateUserNotification(UpdateUserNotificationRequestDto request) {

		if (request == null) {
			throw new IllegalArgumentException("Request must not be null");
		}

		UserNotificationConfigEntity entity = userNotificationConfigRepository
				.findByIdAndIsDeleteFalseAndIsActiveTrueAndIsPayDoneTrue(request.getId())
				.orElseThrow(() -> new IllegalStateException("Module Not Found: " + request.getId()));

		entity.setKey1(request.getKey1());
		entity.setKey2(request.getKey2());
		entity.setUrl(request.getUrl());

		userNotificationConfigRepository.save(entity);

		return true;
	}

	@Override
	public List<UserNotificationConfigResponseDto> getAllByUser(Long userId) {

		List<Object[]> results = userNotificationConfigRepository.findAllValidConfigsByUser(userId);

		List<UserNotificationConfigResponseDto> responseList = new ArrayList<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		for (Object[] obj : results) {

			UserNotificationConfigResponseDto dto = new UserNotificationConfigResponseDto();

			dto.setId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
			dto.setKey1((String) obj[1]);
			dto.setKey2((String) obj[2]);
			dto.setUrl((String) obj[3]);
			dto.setIsActive(obj[4] != null ? (Boolean) obj[4] : null);
			dto.setIsDelete(obj[5] != null ? (Boolean) obj[5] : null);
			dto.setIsPayDone(obj[6] != null ? (Boolean) obj[6] : null);
			dto.setModuleId(obj[7] != null ? ((Number) obj[7]).longValue() : null);
			dto.setModuleName((String) obj[8]);

			LocalDateTime startDate = obj[9] != null ? ((java.sql.Timestamp) obj[9]).toLocalDateTime() : null;

			LocalDateTime endDate = obj[10] != null ? ((java.sql.Timestamp) obj[10]).toLocalDateTime() : null;

			dto.setStartDate(startDate != null ? startDate.format(formatter) : null);
			dto.setEndDate(endDate != null ? endDate.format(formatter) : null);

			dto.setUserId(obj[11] != null ? ((Number) obj[11]).longValue() : null);
			dto.setPayAmount(obj[12] != null ? (BigDecimal) obj[12] : null);

			responseList.add(dto);
		}

		return responseList;
	}

	@Override
	public UserNotificationConfigResponseDto getNotification(Long userId, Long upgradeModuleId) {

		List<Object[]> results = userNotificationConfigRepository.findlValidConfigByUserAndUpgradeModule(userId,
				upgradeModuleId);

		if (results == null || results.isEmpty()) {
			return null;
		}

		Object[] obj = results.get(0);

		UserNotificationConfigResponseDto dto = new UserNotificationConfigResponseDto();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		dto.setId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
		dto.setKey1(obj[1] != null ? obj[1].toString() : null);
		dto.setKey2(obj[2] != null ? obj[2].toString() : null);
		dto.setUrl(obj[3] != null ? obj[3].toString() : null);

		dto.setIsActive(obj[4] != null ? (Boolean) obj[4] : null);
		dto.setIsDelete(obj[5] != null ? (Boolean) obj[5] : null);
		dto.setIsPayDone(obj[6] != null ? (Boolean) obj[6] : null);

		dto.setModuleId(obj[7] != null ? ((Number) obj[7]).longValue() : null);

		dto.setModuleName(obj[8] != null ? obj[8].toString() : null);

		LocalDateTime startDate = obj[9] != null ? ((java.sql.Timestamp) obj[9]).toLocalDateTime() : null;

		LocalDateTime endDate = obj[10] != null ? ((java.sql.Timestamp) obj[10]).toLocalDateTime() : null;

		dto.setStartDate(startDate != null ? startDate.format(formatter) : null);

		dto.setEndDate(endDate != null ? endDate.format(formatter) : null);

		dto.setUserId(obj[11] != null ? ((Number) obj[11]).longValue() : null);

		dto.setPayAmount(obj[12] != null ? (BigDecimal) obj[12] : null);

		return dto;
	}

}
