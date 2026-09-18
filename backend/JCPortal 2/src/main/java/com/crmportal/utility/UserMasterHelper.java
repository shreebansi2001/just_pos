package com.crmportal.utility;

import com.crmportal.entity.*;
import com.crmportal.repository.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.CommonService;
import com.crmportal.service.PlansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UserMasterHelper {

	public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
	public static final DateTimeFormatter DATETIME_SEC_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	@Autowired
	private UserPlansHistoryRepository userPlansHistoryRepository;
	@Autowired
	private UserOtpRepository userOtpRepository;
	@Autowired
	private PlansService plansService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private UserMasterRepository userMasterRepository;
	@Autowired
	private Environment environment;

	@Value("${support.mobile}")
	private String supportMobile;

	public UserPlansHistoryResponseDto buildUserPlanHistoryDto(UserPlansHistoryEntity history) {
		UserPlansHistoryResponseDto dto = new UserPlansHistoryResponseDto();
		if (history == null) {
			return dto;
		}

		PlansResponseDto plan = history.getPlan() != null ? plansService.getPlansById(history.getPlan().getId()) : null;

		dto.setId(history.getId());
		dto.setIsActive(history.getIsActive());
		dto.setPlan(plan);
		dto.setPlanBaseAmount(history.getPlanBaseAmount());
		dto.setPlanAmount(history.getPlanAmount());
		dto.setStartDate(history.getStartDate() != null ? history.getStartDate().format(DATETIME_FMT) : null);
		dto.setEndDate(history.getEndDate() != null ? history.getEndDate().format(DATETIME_FMT) : null);

		if (history.getUser() != null) {
			dto.setUserId(history.getUser().getId());
		}
		return dto;
	}

	public UserPlansHistoryResponseDto buildUserPlanHistoryDto(UserPlansHistoryEntity history,
			UserMasterResponseDto response) {

		UserPlansHistoryResponseDto dto = buildUserPlanHistoryDto(history);
		response.setPlan(dto.getPlan());
		return dto;
	}

	public UserPlansHistoryEntity getActivePlanHistory(UserMasterEntity user) {
		return userPlansHistoryRepository.findByUserAndIsActiveTrue(user).orElse(null);
	}

	public UserPlansHistoryEntity getEffectivePlanHistory(UserMasterEntity user) {
		if (user.getClientId() != null && user.getClientId() > 0) {
			UserMasterEntity admin = userMasterRepository.findByIdAndIsDeleteFalse(user.getClientId())
					.orElseThrow(() -> new RuntimeException("Admin not found for clientId: " + user.getClientId()));
			return getActivePlanHistory(admin);
		}
		return getActivePlanHistory(user);
	}

	public void assertSubscriptionActive(UserPlansHistoryEntity history) {
		if (history == null)
			return;
		if (history.getEndDate() != null && history.getEndDate().isBefore(commonService.getCurrentDateTime())) {
			throw new RuntimeException("Your subscription has expired. Please renew to continue.");
		}
	}

	public String generateAndSendOtp(String email, String mobileNo, int ttlMinutes) {
		String otp = String.format("%06d", new Random().nextInt(999999));
		LocalDateTime expiry = LocalDateTime.now().plusMinutes(ttlMinutes);

		userOtpRepository.deleteByEmailAndIsUsedTrue(email);

		UserOtpEntity entity = new UserOtpEntity();
		entity.setEmail(email);
		entity.setOtp(otp);
		entity.setIsUsed(false);
		entity.setExpiryTime(expiry);
		userOtpRepository.save(entity);

		String[] dataArr = { otp };
		commonService.sendOtpWhatsappAuthType("otp_send_general_whatsapp", mobileNo, dataArr);

		return otp;
	}

	public UserOtpEntity verifyAndConsumeOtp(String email, String otp) {
		UserOtpEntity entity = userOtpRepository.findByEmailAndOtpAndIsUsedFalse(email, otp)
				.orElseThrow(() -> new RuntimeException("Invalid OTP"));

		if (entity.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP expired");
		}

		userOtpRepository.delete(entity);
		return entity;
	}

	public boolean sendSuperAdminOtpIfMissing(String otp) {
		if (otp == null || otp.trim().isEmpty() || otp.trim().equals("-1")) {
			UserMasterEntity superAdmin = userMasterRepository.findByContactNoAndIsDeleteFalse(supportMobile)
					.orElseThrow(() -> new RuntimeException("Super admin not found"));

			generateAndSendOtp(superAdmin.getEmail(), supportMobile, 1);
			return false;
		}
		return true;
	}

	public LocalDateTime calcPlanEndDate(String billingCycle, LocalDateTime start) {
		if (billingCycle == null)
			return start;

		switch (billingCycle.toLowerCase()) {
		case "week":
		case "weekly":
			return start.plusWeeks(1);
		case "month":
		case "monthly":
			return start.plusMonths(1);
		case "quarter":
		case "quarterly":
			return start.plusMonths(3);
		case "annually":
		case "annual":
			return start.plusYears(1);
		default:
			return start;
		}
	}

	public String resolveUserName(Long userId) {
		if (userId == null || userId == 0)
			return "";
		return userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.map(u -> (u.getFirstName() + " " + u.getLastName()).trim()).orElse("");
	}

	public String buildImageUrl(String relativePath) {
		String base = environment.getProperty("app.image.url", "");
		return base + (relativePath != null ? relativePath : "");
	}
}