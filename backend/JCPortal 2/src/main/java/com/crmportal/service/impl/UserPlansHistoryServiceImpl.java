package com.crmportal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.CoupenEntity;
import com.crmportal.entity.DatabasePlanningEntity;
import com.crmportal.entity.ExtraPaymentEntity;
import com.crmportal.entity.PlansEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserOtpEntity;
import com.crmportal.entity.UserPlansHistoryEntity;
import com.crmportal.repository.CoupenMasterRepository;
import com.crmportal.repository.ExtraPaymentMasterRepository;
import com.crmportal.repository.PlansRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserOtpRepository;
import com.crmportal.repository.UserPlansHistoryRepository;
import com.crmportal.request.dto.PlansRequestDto;
import com.crmportal.request.dto.UserPlansHistoryRequestDto;
import com.crmportal.response.dto.PlansResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.response.dto.UserPlansHistoryResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.PaymentInfoService;
import com.crmportal.service.PlansService;
import com.crmportal.service.UserMasterService;
import com.crmportal.service.UserPlansHistoryService;
import com.crmportal.utility.DateUtils;
import com.crmportal.utility.ResponseUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UserPlansHistoryServiceImpl implements UserPlansHistoryService {

	@Autowired
	UserPlansHistoryRepository userPlansHistoryRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	PlansRepository plansRepository;

	@Autowired
	UserMasterService userMasterService;

	@Autowired
	PaymentInfoService paymentInfoService;

	@Autowired
	PlansService plansService;

	@Autowired
	UserBasicDetailsMasterRepository basicDetailsMasterRepository;

	@Autowired
	CoupenMasterRepository coupenMasterRepository;

	@Autowired
	ExtraPaymentMasterRepository extraPaymentMasterRepository;

	@Autowired
	CommonService commonService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	UserOtpRepository userOtpRepository;

	@Value("${support.mobile}")
	private String mobile;

	@Override
	@Transactional
	public Boolean addUserPlan(@Valid UserPlansHistoryRequestDto request) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

		PlansEntity plan = plansRepository.findByIdAndIsDeleteFalse(request.getPlanId())
				.orElseThrow(() -> new RuntimeException("Plan not found with id: " + request.getPlanId()));

		Optional<UserPlansHistoryEntity> existingPlan = userPlansHistoryRepository.findByUserAndIsActiveTrue(user);

		if (existingPlan.isPresent()) {
			UserPlansHistoryEntity history = existingPlan.get();
			history.setIsActive(false);
			userPlansHistoryRepository.save(history);
		}

		UserPlansHistoryEntity entity = new UserPlansHistoryEntity();
		entity.setUser(user);
		entity.setPlan(plan);
		entity.setIsActive(true);

		entity.setPlanAmount(plan.getPrice().toString());
		entity.setPlanBaseAmount(request.getFinalTotal().toString());
		entity.setCgst(request.getCgst());
		entity.setCgstAmt(request.getCgstAmt());
		entity.setSgst(request.getSgst());
		entity.setSgstAmt(request.getSgstAmt());
		entity.setTotalPrice(request.getFinalTotal());

		if (request.getCoupenId() != null) {
			Optional<CoupenEntity> couponOpt = coupenMasterRepository.findByIdAndIsDeleteFalse(request.getCoupenId());

			if (couponOpt.isPresent()) {
				CoupenEntity coupon = couponOpt.get();
				entity.setCoupen(coupon);
				entity.setDiscountPrice(coupon.getPrice());
			}
		}

		if (request.getExtraPayId() != null) {
			ExtraPaymentEntity extraPayment = extraPaymentMasterRepository
					.findByIdAndIsDeleteFalse(request.getExtraPayId());

			if (extraPayment != null) {
				entity.setExtraPay(extraPayment);
				entity.setExtraAmount(extraPayment.getPrice().toString());
			}
		}

		UserBasicDetailsMasterEntity basicDetails = basicDetailsMasterRepository.findByUser(user);

		if (basicDetails != null && basicDetails.getRole() != null && basicDetails.getRole().getId() == 2) {

			String userCode = commonService.getLastestUserCode(plan.getName());
			user.setUserCode(userCode);
			userMasterRepository.save(user);
		}
		userPlansHistoryRepository.save(entity);
		if (request.getPaymentData() != null && Boolean.TRUE.equals(request.getPaymentData().getPaymentdone())) {
			entity.setPaymentdone(true);
			userPlansHistoryRepository.save(entity);
			paymentInfoService.savePaymentResponse(request.getPaymentData(), user, plan, entity, null, null, null, null,
					null);
		}

		return true;
	}

	@Override
	public List<UserPlansHistoryResponseDto> getPlanHistoryByUser(Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
		List<UserPlansHistoryEntity> entities = userPlansHistoryRepository.findAllByUserOrderByCreatedAtDesc(user);
		List<UserPlansHistoryResponseDto> historyResponseDtos = new ArrayList<>();
		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		for (UserPlansHistoryEntity entity : entities) {
			UserPlansHistoryResponseDto dto = new UserPlansHistoryResponseDto();
			if (entity.getEndDate() != null) {
				dto.setEndDate(entity.getEndDate().format(formatter));
			}
			if (entity.getStartDate() != null) {
				dto.setStartDate(entity.getStartDate().format(formatter));
			}
			dto.setIsActive(entity.getIsActive());
			PlansResponseDto plansResponseDto = plansService.getPlansById(entity.getPlan().getId());
			plansResponseDto.setPrice(plansResponseDto.getPrice());
			dto.setUserId(userId);
			dto.setPlan(plansResponseDto);
			dto.setId(entity.getId());
			dto.setPlanAmount(entity.getPlanAmount());
			dto.setPlanBaseAmount(entity.getPlanBaseAmount());

			historyResponseDtos.add(dto);
		}
		return historyResponseDtos;
	}

	@Override
	public Map<String, Object> getRenewalCustomerInfo(LocalDateTime startDate, LocalDateTime endDate,
			boolean isActive) {
		try {
			List<UserPlansHistoryEntity> entities = userPlansHistoryRepository.findByIsActive(isActive);
			if (entities.isEmpty()) {
				return ResponseUtils.createSuccessRespones("Data not found",
						"data not found for activeStatus " + isActive);
			}
			entities = filterPlans(entities, startDate, endDate);
			if (entities.isEmpty()) {
				return ResponseUtils.createSuccessRespones("Data not found", "data not found for the given data range");
			}
			return ResponseUtils.createSuccessRespones(createJsonNodeArray(entities),
					"Customer Renewal Information fetched successfully");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createFailedRespones(e.getLocalizedMessage());
		}
	}

	public List<UserPlansHistoryEntity> filterPlans(List<UserPlansHistoryEntity> plans, LocalDateTime startDate,
			LocalDateTime endDate) {
		return plans.stream().filter(plan -> {
			LocalDateTime planEnd = plan.getEndDate();

			if (planEnd == null) {
				return false;
			}

			return !planEnd.isBefore(startDate) && !planEnd.isAfter(endDate);
		}).collect(Collectors.toList());
	}

	private JsonNode createJsonNodeArray(List<UserPlansHistoryEntity> list) {
		ArrayNode arrayNode = objectMapper.createArrayNode();
		for (UserPlansHistoryEntity userPlansHistoryEntity : list) {
			ObjectNode node = objectMapper.createObjectNode();
			PlansEntity plan = userPlansHistoryEntity.getPlan();

			node.put("name", plan.getName());
			node.put("price", userPlansHistoryEntity.getTotalPrice());
			node.put("billingCycle", plan.getBillingCycle());
			node.put("startDate", DateUtils.formatLocalDateTime(userPlansHistoryEntity.getStartDate()));
			node.put("endDate", DateUtils.formatLocalDateTime(userPlansHistoryEntity.getEndDate()));
			node.put("customerName", userPlansHistoryEntity.getUser().getFirstName() + " "
					+ userPlansHistoryEntity.getUser().getLastName());

			arrayNode.add(node);
		}
		return arrayNode;
	}

	@Override
	@Transactional
	public Boolean updateUserPlanDate(String date, Long userId, String otp) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		UserMasterEntity superAdmin = userMasterRepository.findByContactNoAndIsDeleteFalse(mobile)
				.orElseThrow(() -> new RuntimeException("Mobile number not registered. Please sign up first."));
		if (otp == null || otp.trim().isEmpty() || otp.trim().equals("-1")) {
			String userOtp = String.format("%06d", new Random().nextInt(999999));
			LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);

			userOtpRepository.deleteByEmailAndIsUsedTrue(superAdmin.getEmail());
			userOtpRepository.flush();
			System.out.println("otp:-" + userOtp);
			UserOtpEntity otpEntity = new UserOtpEntity();
			otpEntity.setEmail(superAdmin.getEmail());
			otpEntity.setOtp(userOtp);
			otpEntity.setExpiryTime(expiryTime);
			otpEntity.setIsUsed(false);
			userOtpRepository.save(otpEntity);
			System.out.println("otpEntity:-" + otpEntity);
			String[] dataArr = { userOtp };

			commonService.sendOtpWhatsappAuthType("otp_send_general_whatsapp", mobile, dataArr);
//			commonService.sendMailForOtp("Super Admin", "info@justwedding.in", userOtp);

			return false;
		}

		UserOtpEntity otpEntity = userOtpRepository.findByEmailAndOtpAndIsUsedFalse(superAdmin.getEmail(), otp)
				.orElseThrow(() -> new RuntimeException("Invalid OTP"));

		if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("OTP expired");
		}

		if (date == null || date.trim().isEmpty()) {
			throw new RuntimeException("Please Input Date Field");
		}

		UserPlansHistoryEntity historyEntity = userPlansHistoryRepository.findByUserAndIsActiveTrue(user)
				.orElseThrow(() -> new RuntimeException("User Plan Not Found"));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate parsedDate = LocalDate.parse(date, formatter);
		LocalDateTime endDateTime = parsedDate.atStartOfDay();

		historyEntity.setStartDate(historyEntity.getEndDate());
		historyEntity.setEndDate(endDateTime);
		userPlansHistoryRepository.save(historyEntity);
		userOtpRepository.delete(otpEntity);
		return true;
	}
}
