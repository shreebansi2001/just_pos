package com.crmportal.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.crmportal.entity.CityMasterEntity;
import com.crmportal.entity.CountryMasterEntity;
import com.crmportal.entity.ExtraPaymentEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.PaymentInfo;
import com.crmportal.entity.PlansEntity;
import com.crmportal.entity.RefundDetailsEntity;
import com.crmportal.entity.RoleHierarchyEntity;
import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.StateMasterEntity;
import com.crmportal.entity.UserAmcEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserBasicFileEntity;
import com.crmportal.entity.UserDocumentInfoEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserOfferEntity;
import com.crmportal.entity.UserPlansHistoryEntity;
import com.crmportal.entity.UserRightsMasterEntity;
import com.crmportal.entity.UserRightsModuleEntity;
import com.crmportal.entity.UserRightsPagesEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.PartyMasterMapper;
import com.crmportal.mapper.UserBasicDetailsMasterMapper;
import com.crmportal.mapper.UserMasterMapper;
import com.crmportal.repository.AdminTemplateModuleRepository;
import com.crmportal.repository.CityMasterRepository;
import com.crmportal.repository.CountryMasterRepository;
import com.crmportal.repository.CoupenMasterRepository;
import com.crmportal.repository.DatabasePlanningEntityRepository;
import com.crmportal.repository.ExtraPaymentMasterRepository;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.ModuleRightsRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PaymentInfoRepository;
import com.crmportal.repository.PlansRepository;
import com.crmportal.repository.RefundDetailsRepository;
import com.crmportal.repository.RoleHierarchyRepository;
import com.crmportal.repository.RoleMasterRepository;
import com.crmportal.repository.StateMasterRepository;
import com.crmportal.repository.StockTypeRightsRepository;
import com.crmportal.repository.UserAmcRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserBasicFileRepository;
import com.crmportal.repository.UserDocumentInfoRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserOfferRepository;
import com.crmportal.repository.UserOtpRepository;
import com.crmportal.repository.UserPlansHistoryRepository;
import com.crmportal.repository.UserRightsMasterRepository;
import com.crmportal.repository.UserRightsPagesRepository;
import com.crmportal.repository.UserUpgradedModuleRepository;
import com.crmportal.request.dto.RefundDetailsRequestDto;
import com.crmportal.request.dto.UpdateUserMemberRequestDto;
import com.crmportal.request.dto.UserAmcRequestDto;
import com.crmportal.request.dto.UserDocumentRequestDto;
import com.crmportal.request.dto.UserDownPaymentRequestDto;
import com.crmportal.request.dto.UserMasterRequestDto;
import com.crmportal.request.dto.UserOfferRequestDto;
import com.crmportal.request.dto.UserOfferResponseDto;
import com.crmportal.response.dto.DataBaseResponse;
import com.crmportal.response.dto.FileWithIdResponseDto;
import com.crmportal.response.dto.ParentUserResponseDto;
import com.crmportal.response.dto.PartyMasterResponseDto;
import com.crmportal.response.dto.PlanUserSummaryResponseDto;
import com.crmportal.response.dto.PlansResponseDto;
import com.crmportal.response.dto.RefundDetailsResponseDto;
import com.crmportal.response.dto.ReportingManagerResponseDTO;
import com.crmportal.response.dto.UserAdminResponseDto;
import com.crmportal.response.dto.UserAmcResponseDto;
import com.crmportal.response.dto.UserApprovedResponseDto;
import com.crmportal.response.dto.UserAssignedAllModuleWiseThemeResponseDto;
import com.crmportal.response.dto.UserAssignedThemeResponseDto;
import com.crmportal.response.dto.UserDocumentResponseDto;
import com.crmportal.response.dto.UserDownPaymentResponseDto;
import com.crmportal.response.dto.UserLogsResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.response.dto.UserPlansHistoryResponseDto;
import com.crmportal.response.dto.UserResponseDTO;
import com.crmportal.response.dto.UserRightsMasterResponseDto;
import com.crmportal.response.dto.UserRightsPageWithModuleResponseDto;
import com.crmportal.response.dto.UserUpgradedModuleResponseDto;
import com.crmportal.service.*;
import com.crmportal.service.CommonService;
import com.crmportal.service.PlansService;
import com.crmportal.service.UserFileService;
import com.crmportal.service.UserMasterService;
import com.crmportal.service.BanquetRightsService;
import com.crmportal.utility.DateMapper;
import com.crmportal.utility.JwtUtil;
import com.crmportal.utility.RoleReportRightsHelper;
import com.crmportal.utility.UserMasterHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class UserMasterServiceImpl implements UserMasterService {

	private final ZohoOAuthService zohoOAuthService;

	// ── Repositories ──────────────────────────────────────────────────────────

	@Autowired
	UserMasterRepository userMasterRepository;
	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;
	@Autowired
	CountryMasterRepository countryMasterRepository;
	@Autowired
	StateMasterRepository stateMasterRepository;
	@Autowired
	CityMasterRepository cityMasterRepository;
	@Autowired
	PlansRepository plansRepository;
	@Autowired
	RoleMasterRepository roleMasterRepository;
	@Autowired
	UserOtpRepository userOtpRepository;
	@Autowired
	UserPlansHistoryRepository userPlansHistoryRepository;
	@Autowired
	UserDocumentInfoRepository userDocumentInfoRepository;
	@Autowired
	PaymentInfoRepository infoRepository;
	@Autowired
	PaymentInfoRepository paymentInfoRepository;
	@Autowired
	UserRightsMasterRepository userRightsMasterRepository;
	@Autowired
	UserRightsPagesRepository userRightsPagesRepository;
	@Autowired
	UserAmcRepository userAmcRepository;
	@Autowired
	RefundDetailsRepository refundDetailsRepository;
	@Autowired
	ModuleRightsRepository moduleRightsRepository;
	@Autowired
	UserBasicFileRepository basicFileRepository;
	@Autowired
	DatabasePlanningEntityRepository databasePlanningEntityRepository;
	@Autowired
	AdminTemplateModuleRepository adminTemplateModuleRepository;
	@Autowired
	CoupenMasterRepository coupenMasterRepository;
	@Autowired
	UserOfferRepository offerRepository;
	@Autowired
	PartyMasterRepository partyMasterRepository;
	@Autowired
	LeadMasterRepository leadMasterRepository;
	@Autowired
	ExtraPaymentMasterRepository extraPaymentMasterRepository;
	@Autowired
	UserUpgradedModuleRepository upgradedModuleRepository;
	@Autowired
	private RoleReportRightsHelper roleReportRightsHelper;
	@Autowired
	RoleHierarchyRepository roleHierarchyRepository;

	@Autowired
	private UserLogsServiceImpl userLogsServiceImpl;

	@Autowired
	UserMasterMapper userMasterMapper;
	@Autowired
	PartyMasterMapper partyMasterMapper;
	@Autowired
	UserBasicDetailsMasterMapper userBasicDetailsMasterMapper;

	@Autowired
	CommonService commonService;
	@Autowired
	PlansService plansService;
	@Autowired
	UserFileService userFileService;
	@Autowired
	UserRightsServiceImpl userRightsServiceImpl;
	@Autowired
	JwtUtil jwtUtil;
	@Autowired
	DateMapper dateMapper;
	@Autowired
	Environment environment;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	@Autowired
	JavaMailSender javaMailSender;
	@Autowired
	BanquetRightsService banquetRightsService;
	@Autowired
	UserMasterHelper helper;
	@Autowired
	StockTypeService stockTypeService;

	@Value("${support.mobile}")
	private String mobile;

	private static final Logger log = LoggerFactory.getLogger(UserMasterService.class);

	UserMasterServiceImpl(ZohoOAuthService zohoOAuthService) {
		this.zohoOAuthService = zohoOAuthService;
	}

	@Override
	public UserMasterResponseDto addOrUpdateUserMaster(@Valid UserMasterRequestDto request, Long id) {
		String encryptedPassword = "";
		String uniqueCode = "";
		if (id == -1) {
			userMasterRepository.findByEmailAndIsDeleteFalse(request.getEmail()).ifPresent(u -> {
				throw new RuntimeException("User with email already exists: " + request.getEmail());
			});
			userMasterRepository.findByContactNoAndIsDeleteFalse(request.getContactNo()).ifPresent(u -> {
				throw new RuntimeException("User with Mobile No already exists: " + request.getContactNo());
			});
			String password = request.getPassword().trim();
			String confirmPassword = request.getConfirmPassword().trim();
			if (!password.equals(confirmPassword))
				throw new RuntimeException("Password and Confirm Password do not match.");
			encryptedPassword = passwordEncoder.encode(password);
			if (request.getClientId() == 0) {
				uniqueCode = commonService.generateUniqueCode();
			} else {
				UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getClientId())
						.orElse(null);
				uniqueCode = user.getUniqueCode();
			}
		} else {
			userMasterRepository.findByEmailAndIsDeleteFalse(request.getEmail()).ifPresent(u -> {
				if (!u.getId().equals(id))
					throw new RuntimeException("User with email already exists: " + request.getEmail());
			});
		}

		CountryMasterEntity country = request.getCountryId() != null
				? countryMasterRepository.findByIdAndIsDeleteFalse(request.getCountryId()).orElseThrow(
						() -> new RuntimeException("Country not found with id: " + request.getCountryId()))
				: null;

		StateMasterEntity state = request.getStateId() != null
				? stateMasterRepository.findByIdAndIsDeleteFalse(request.getStateId()).orElseThrow(
						() -> new RuntimeException("State not found with id: " + request.getStateId()))
				: null;

		CityMasterEntity city = request.getCityId() != null
				? cityMasterRepository.findByIdAndIsDeleteFalse(request.getCityId()).orElseThrow(
						() -> new RuntimeException("City not found with id: " + request.getCityId()))
				: null;

		RoleMasterEntity role = roleMasterRepository.findByIdAndIsDeleteFalse(request.getRoleId())
				.orElseThrow(() -> new RuntimeException("Role not found with id: " + request.getRoleId()));

		UserMasterEntity entity;
		UserBasicDetailsMasterEntity basicDetails;

		if (id == -1) {
			entity = userMasterMapper.requestToEntity(request);
			basicDetails = userBasicDetailsMasterMapper.requestToEntity(request);
			entity.setUserCode("");
			entity.setIsBlock(false);
			entity.setPassword(encryptedPassword);
//			entity.setIsActive(true);
//			entity.setIsApprove(true);
			basicDetails.setType("demo");
			basicDetails.setFollowupDay(4);
			if (request.getRoleId() == 1) {
				entity.setClientId(-1L);
				basicDetails.setReportingManagerId(0L);
			}
			if (request.getRoleId() == 2) {
				entity.setClientId(0L);
			} else {
				entity.setClientId(request.getClientId());
				entity.setIsActive(true);
				entity.setIsApprove(true);
				entity.setIschilduser(request.getIschilduser());
				UserBasicDetailsMasterEntity adminDeails = userBasicDetailsMasterRepository
						.findByUser_IdAndIsDeleteFalse(request.getClientId());
				basicDetails.setCompanyEmail(adminDeails.getCompanyEmail());
			}
			entity.setUniqueCode(uniqueCode);
			entity.setIsInquiryVisible(true);
		} else {
			entity = getUser(id);
			basicDetails = userBasicDetailsMasterRepository.findByUserAndIsDeleteFalse(entity)
					.orElseThrow(() -> new RuntimeException("User basic details not found for id: " + id));

			entity.setFirstName(request.getFirstName());
			entity.setLastName(request.getLastName());
			entity.setContactNo(request.getContactNo());
			entity.setEmail(request.getEmail());
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			entity.setIschilduser(request.getIschilduser());
			basicDetails.setFollowupDay(request.getFollowupDay());
			basicDetails.setAddress(request.getAddress());
			basicDetails.setCompanyEmail(request.getCompanyEmail());
			basicDetails.setCountryCode(request.getCountryCode());
			basicDetails.setCompanyName(request.getCompanyName());
			basicDetails.setReportingManagerId(request.getReportingManagerId());
			basicDetails.setGstNumber(request.getGstNumber());
			basicDetails.setPanNumber(request.getPanNumber());
			basicDetails.setCinNumber(request.getCinNumber());
			basicDetails.setFdaLincense(request.getFdaLincense());
			basicDetails.setFssaiNumber(request.getFssaiNumber());
			basicDetails.setOfficeNo(request.getOfficeNo());
			basicDetails.setHsnNumber(request.getHsnNumber());
			basicDetails.setUpdatedAt(commonService.getCurrentDateTime());
		}

		basicDetails.setSoftType(request.getSoftType());
		entity = userMasterRepository.save(entity);
//		try {
//			commonService.sendRegistrationPendingNotice(entity);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		basicDetails.setCity(city);
		basicDetails.setState(state);
		basicDetails.setCountry(country);
		basicDetails.setRole(role);
		basicDetails.setUser(entity);
		userBasicDetailsMasterRepository.save(basicDetails);

		return userMasterMapper.entityToResponse(entity);
	}

	@Override
	public UserMasterResponseDto loginWithPassword(String email, String password, String otp, HttpServletRequest re,
			String uniqueCode, String softType) {

		UserMasterEntity user = userMasterRepository.findByEmailAndIsDeleteFalse(email)
				.orElseThrow(() -> new RuntimeException("Email not registered. Please sign up first."));

		UserMasterEntity admin = user.getClientId() > 0 ? userMasterRepository
				.findByIdAndIsDeleteFalse(user.getClientId()).orElseThrow(() -> new RuntimeException("Admin Not Found"))
				: user;
		UserBasicDetailsMasterEntity basicDetails = userBasicDetailsMasterRepository.findByUserAndIsDeleteFalse(user)
				.orElseThrow(() -> new RuntimeException("User basic details not found"));

		if (!basicDetails.getSoftType().equalsIgnoreCase(softType)) {
			throw new RuntimeException("Access denied. This account is not authorized to access this application.");
		}

		if (admin.getIsBlock())
			throw new RuntimeException("Your account has been blocked. Please contact support.");

		String loggedInIp = userLogsServiceImpl.getIpAddress(re);
		if (admin.getAllowedip() != null && !"".equals(admin.getAllowedip())
				&& !loggedInIp.equals(admin.getAllowedip()))
			throw new RuntimeException("Your ip has been blocked. Please contact support.");

		if (!passwordEncoder.matches(password, user.getPassword()))
			throw new RuntimeException("Invalid email or password");

		if (uniqueCode == null || uniqueCode.trim().isEmpty()) {
			throw new RuntimeException("Unique code is required");
		}

		if (!uniqueCode.equals(user.getUniqueCode())) {
			throw new RuntimeException("Invalid unique code");
		}

		if (!Objects.equals(uniqueCode, user.getUniqueCode())) {
			throw new RuntimeException("Invalid unique code");
		}

//		if (user.getClientId() == -1) {
//			helper.verifyAndConsumeOtp(user.getEmail(), otp);
//		}

		Long ownerId = user.getClientId() == -1 ? user.getId() : user.getClientId();

		UserMasterResponseDto response = userMasterMapper.entityToResponse(user);
		response.setLogo(helper.buildImageUrl(user.getLogo()));

		UserPlansHistoryEntity historyEntity = helper.getEffectivePlanHistory(user);
		UserPlansHistoryResponseDto planDto = helper.buildUserPlanHistoryDto(historyEntity, response);
		response.setUserPlan(planDto);
		response.setIsVisible(user.getIsVisible());
		response.setIsInquiryVisible(user.getIsInquiryVisible());
		response.setUserRights(getUserRightsByRole(basicDetails.getRole().getId()));
		response.setRoleReportRights(
				roleReportRightsHelper.getForRoleWithOwner(basicDetails.getRole().getId(), ownerId));
		response.setLang(basicDetails.getLang());
		response.setBanquetRights(banquetRightsService.getByUser(ownerId, user.getId()));
		response.setUserUpgradedModule(setUserUpgradedModule(admin));
		response.setThemeColor(basicDetails.getThemeColor());
		response.setSoftType(basicDetails.getSoftType());
		response.setUniqueCode(user.getUniqueCode());
		response.setStockTypeRights(stockTypeService.getAllStockTypeRights(user.getId()));
		String token = jwtUtil.generateToken(response);
		response.setToken(token);
		response.setFollowupDay(basicDetails.getFollowupDay());
		response.setTokenType("Bearer");
		response.setExpiresIn(jwtUtil.getExpirationTime());

		log.info("User login successful for email: {}", email);
		return response;
	}

	public UserMasterResponseDto refreshToken(String oldToken) {

		if (!jwtUtil.isTokenSignatureValid(oldToken))
			throw new RuntimeException("Invalid token signature");

		String email = jwtUtil.extractUsernameAllowExpired(oldToken);
		System.out.println("email:- " + email);
		UserMasterEntity user = userMasterRepository.findByEmailAndIsDeleteFalse(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		UserMasterResponseDto response = userMasterMapper.entityToResponse(user);

		response.setLogo(helper.buildImageUrl(user.getLogo()));

		UserPlansHistoryEntity history = helper.getEffectivePlanHistory(user);
		response.setUserPlan(helper.buildUserPlanHistoryDto(history, response));

		if (user.getCreatedAt() != null)
			response.setCreatedAt(user.getCreatedAt().format(UserMasterHelper.DATE_FMT));

		UserBasicDetailsMasterEntity basicDetails = userBasicDetailsMasterRepository.findByUserAndIsDeleteFalse(user)
				.orElseThrow(() -> new RuntimeException("User basic details not found"));

		// Same ownerId logic as loginWithPassword
		Long ownerId = (user.getClientId() == null || user.getClientId() == -1) ? user.getId() : user.getClientId();

		response.setUserRights(getUserRightsByRole(basicDetails.getRole().getId()));
		response.setIsVisible(user.getIsVisible());
		response.setIsInquiryVisible(user.getIsInquiryVisible());
		response.setFollowupDay(basicDetails.getFollowupDay());
		// Use ownerId-scoped role report rights (same as login)
		response.setRoleReportRights(
				roleReportRightsHelper.getForRoleWithOwner(basicDetails.getRole().getId(), ownerId));
		response.setBanquetRights(banquetRightsService.getByUser(ownerId, user.getId()));
		response.setLang(basicDetails.getLang());
		response.setThemeColor(basicDetails.getThemeColor());
		response.setStockTypeRights(stockTypeService.getAllStockTypeRights(user.getId()));
		response.setSoftType(basicDetails.getSoftType());
		response.setUniqueCode(user.getUniqueCode());
		UserMasterEntity effectiveUser = user.getClientId() != null && user.getClientId() > 0 ? userMasterRepository
				.findByIdAndIsDeleteFalse(user.getClientId()).orElseThrow(() -> new RuntimeException("Admin Not Found"))
				: user;

		response.setUserUpgradedModule(setUserUpgradedModule(effectiveUser));
		response.setGstNumber(basicDetails.getGstNumber());
		response.setPanNumber(basicDetails.getPanNumber());
		response.setFdaLincense(basicDetails.getFdaLincense());
		response.setCinNumber(basicDetails.getCinNumber());
		response.setFssaiNumber(basicDetails.getFssaiNumber());
		response.setHsnNumber(basicDetails.getHsnNumber());
		if (user.getId() != 1)
			response.setDatabase(getUserDatabase(user.getId()));

		String newToken = jwtUtil.generateToken(response);
		response.setToken(newToken);
		response.setTokenType("Bearer");
		response.setExpiresIn(jwtUtil.getExpirationTime());

		return response;
	}

	@Override
	public Map<String, Object> getAllUserMaster(Long userId, String userName) {

		Map<String, Object> finalResponse = new HashMap<>();
		DateTimeFormatter fmt = UserMasterHelper.DATE_FMT;

		UserMasterEntity masterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

		finalResponse.put("LeadType", buildLeadTypeMap(null, masterEntity));
		finalResponse.put("LeadStatus", buildLeadStatusMap(null, masterEntity));

		List<UserMasterEntity> entities = new ArrayList<>();
		Long ownerId = masterEntity.getClientId() > 0 ? masterEntity.getClientId() : masterEntity.getId();

		if (userName == null || userName.trim().isEmpty()) {
			entities.addAll(userMasterRepository.findAllByClientIdAndIsDeleteFalse(ownerId));
		} else {
			String keyword = userName.trim().toLowerCase();
			userMasterRepository.findAllByClientIdAndIsDeleteFalse(masterEntity.getId()).stream()
					.filter(m -> (m.getFirstName() != null && m.getFirstName().toLowerCase().contains(keyword))
							|| (m.getUserBasicDetails().getRole().getName() != null
									&& m.getUserBasicDetails().getRole().getName().toLowerCase().contains(keyword)))
					.forEach(entities::add);
		}

		if (entities.isEmpty()) {
			finalResponse.put("UserDetails", Collections.emptyList());
			return finalResponse;
		}

		List<UserMasterResponseDto> userDtos = new ArrayList<>();

		for (UserMasterEntity ume : entities) {
			UserBasicDetailsMasterEntity details = userBasicDetailsMasterRepository.findByUser(ume);
			UserMasterResponseDto response = userMasterMapper.entityToResponse(ume);
			response.setLogo(helper.buildImageUrl(ume.getLogo()));
			response.setLang(details.getLang());
			response.setSoftType(details.getSoftType());
			response.setUniqueCode(ume.getUniqueCode());
			response.setIsInquiryVisible(ume.getIsInquiryVisible());
			UserPlansHistoryEntity history = helper.getEffectivePlanHistory(ume);
			response.setUserPlan(helper.buildUserPlanHistoryDto(history, response));
			response.setStockTypeRights(stockTypeService.getAllStockTypeRights(ume.getId()));
			if (details.getRole() != null)
				response.setUserRights(userRightsServiceImpl.getUserRightsByRoleId(details.getRole().getId()));

			if (ume.getCreatedAt() != null)
				response.setCreatedAt(ume.getCreatedAt().format(fmt));

			Map<String, Long> tMap = buildLeadTypeMap(ume.getId(), masterEntity);
			Map<String, Long> sMap = buildLeadStatusMap(ume.getId(), masterEntity);
			response.setLeadType(tMap);
			response.setLeadStatus(sMap);
			response.setFollowupDay(details.getFollowupDay());
			long totalLeads = sMap.values().stream().mapToLong(Long::longValue).sum();
			long confirmedLeads = sMap.getOrDefault("Confirmed", 0L);
			double efficiency = totalLeads > 0 ? Math.round((confirmedLeads * 100.0) / totalLeads) : 0;
			response.setEfficiencyBreakdown(efficiency);
			response.setServiceQualityRation(
					BigDecimal.valueOf((efficiency * 5.0) / 100.0).setScale(2, RoundingMode.HALF_UP).doubleValue());

			userDtos.add(response);
		}

		finalResponse.put("UserDetails", userDtos);
		return finalResponse;
	}

	@Override
	public UserMasterResponseDto getUserMasterById(Long id) {

		Optional<UserMasterEntity> opt = userMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!opt.isPresent())
			return null;

		UserMasterEntity ume = opt.get();
		UserMasterResponseDto response = userMasterMapper.entityToResponse(ume);
		response.setLogo(helper.buildImageUrl(ume.getLogo()));

		UserPlansHistoryEntity history = helper.getEffectivePlanHistory(ume);
		response.setUserPlan(helper.buildUserPlanHistoryDto(history, response));

		if (ume.getCreatedAt() != null)
			response.setCreatedAt(ume.getCreatedAt().format(UserMasterHelper.DATE_FMT));

		UserBasicDetailsMasterEntity basicDetails = userBasicDetailsMasterRepository.findByUserAndIsDeleteFalse(ume)
				.orElseThrow(() -> new RuntimeException("User basic details not found"));

		// Same ownerId logic as loginWithPassword
		Long ownerId = (ume.getClientId() == null || ume.getClientId() == -1) ? ume.getId() : ume.getClientId();

		response.setUserRights(getUserRightsByRole(basicDetails.getRole().getId()));
		response.setIsVisible(ume.getIsVisible());
		response.setIsInquiryVisible(ume.getIsInquiryVisible());
		response.setFollowupDay(basicDetails.getFollowupDay());
		// Use ownerId-scoped role report rights (same as login)
		response.setRoleReportRights(
				roleReportRightsHelper.getForRoleWithOwner(basicDetails.getRole().getId(), ownerId));
		response.setBanquetRights(banquetRightsService.getByUser(ownerId, ume.getId()));
		response.setLang(basicDetails.getLang());
		response.setThemeColor(basicDetails.getThemeColor());
		response.setSoftType(basicDetails.getSoftType());
		response.setUniqueCode(ume.getUniqueCode());
		UserMasterEntity effectiveUser = ume.getClientId() != null && ume.getClientId() > 0 ? userMasterRepository
				.findByIdAndIsDeleteFalse(ume.getClientId()).orElseThrow(() -> new RuntimeException("Admin Not Found"))
				: ume;
		response.setStockTypeRights(stockTypeService.getAllStockTypeRights(ume.getId()));
		response.setUserUpgradedModule(setUserUpgradedModule(effectiveUser));
		response.setGstNumber(basicDetails.getGstNumber());
		response.setPanNumber(basicDetails.getPanNumber());
		response.setFdaLincense(basicDetails.getFdaLincense());
		response.setCinNumber(basicDetails.getCinNumber());
		response.setFssaiNumber(basicDetails.getFssaiNumber());
		response.setHsnNumber(basicDetails.getHsnNumber());
		if (ume.getId() != 1)
			response.setDatabase(getUserDatabase(ume.getId()));

		return response;
	}

	@Override
	public List<UserMasterResponseDto> getManagerAndAdminUsersByClientUserId(Long clientUserId) {

		List<String> roleNames = Arrays.asList("Super Admin");
		List<UserMasterEntity> entities = userMasterRepository.findManagersAndAdminsByClientId(clientUserId, roleNames);

		if (entities.isEmpty())
			return Collections.emptyList();

		List<UserMasterResponseDto> result = new ArrayList<>();
		for (UserMasterEntity ume : entities) {
			UserBasicDetailsMasterEntity details = userBasicDetailsMasterRepository.findByUser(ume);
			UserMasterResponseDto response = userMasterMapper.entityToResponse(ume);
			response.setLogo(helper.buildImageUrl(ume.getLogo()));
			response.setLang(details.getLang());
			response.setGstNumber(details.getGstNumber());
			response.setFollowupDay(details.getFollowupDay());
			UserPlansHistoryEntity history = helper.getActivePlanHistory(ume);
			response.setUserPlan(helper.buildUserPlanHistoryDto(history, response));

			if (ume.getCreatedAt() != null)
				response.setCreatedAt(ume.getCreatedAt().format(UserMasterHelper.DATE_FMT));

			response.setDatabase(getUserDatabase(ume.getId()));
			result.add(response);
		}
		return result;
	}

	@Override
	public PlanUserSummaryResponseDto getAllAdminUserMaster(Long roleId, String userName, String type) {

		PlanUserSummaryResponseDto res = new PlanUserSummaryResponseDto();

		RoleMasterEntity role = roleMasterRepository.findById(roleId)
				.orElseThrow(() -> new RuntimeException("Role not found with id : " + roleId));

		List<UserBasicDetailsMasterEntity> userDetails;
		if (type == null || type.trim().isEmpty() || type.equalsIgnoreCase("all")) {
			userDetails = userBasicDetailsMasterRepository.findAllByRoleAndIsDeleteFalse(role);
		} else {
			userDetails = userBasicDetailsMasterRepository.findAllByRoleAndTypeIgnoreCaseAndIsDeleteFalse(role, type);
		}
		if (userDetails.isEmpty())
			throw new RuntimeException("User Details not found");

		String keyword = (userName != null) ? userName.trim().toLowerCase() : null;
		List<UserMasterEntity> filtered = userDetails.stream().map(UserBasicDetailsMasterEntity::getUser).filter(
				u -> keyword == null || (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(keyword)))
				.collect(Collectors.toList());

		if (filtered.isEmpty())
			throw new RuntimeException("User not found");

		List<UserMasterResponseDto> dtos = new ArrayList<>();
		for (UserMasterEntity entity : filtered) {
			UserBasicDetailsMasterEntity details = userBasicDetailsMasterRepository.findByUser(entity);
			UserMasterResponseDto response = userMasterMapper.entityToResponse(entity);
			response.setLogo(helper.buildImageUrl(entity.getLogo()));
			response.setLang(details.getLang());
			response.setSoftType(details.getSoftType());
			response.setUniqueCode(entity.getUniqueCode());
			response.setFollowupDay(details.getFollowupDay());
			UserPlansHistoryEntity history = helper.getActivePlanHistory(entity);
			response.setUserPlan(helper.buildUserPlanHistoryDto(history, response));

			if (entity.getCreatedAt() != null)
				response.setCreatedAt(entity.getCreatedAt().format(UserMasterHelper.DATE_FMT));

			response.setDatabase(getUserDatabase(entity.getId()));
			dtos.add(response);
		}
		res.setUsers(dtos);

		List<Object[]> paymentResults = paymentInfoRepository.getPaymentSummary(type, false, true);
		double totalAmount = 0, paidAmount = 0, unpaidAmount = 0;
		if (paymentResults != null && !paymentResults.isEmpty()) {
			Object[] row = paymentResults.get(0);
			totalAmount = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
			paidAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
			unpaidAmount = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
		}
		res.setTotalAmount(totalAmount);
		res.setTotalPaidAmount(paidAmount);
		res.setTotalUnPaidAmount(unpaidAmount);
		res.setTotalUser(userBasicDetailsMasterRepository.getActiveUserByRoleAndType(role.getId(), type, false, true));

		return res;
	}

	@Override
	public Boolean changePassword(String oldPassword, String newPassword, String conPassword, Long userId) {

		UserMasterEntity user = getUser(userId);
		if (!passwordEncoder.matches(oldPassword, user.getPassword()))
			throw new RuntimeException("Invalid old password");
		if (!newPassword.equals(conPassword))
			throw new RuntimeException("New password and confirm password do not match");

		helper.assertSubscriptionActive(helper.getEffectivePlanHistory(user));

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setIsFirstTime(false);
		userMasterRepository.save(user);
		System.out.println("Saved");
		try {
//			commonService.sendMailPassowrdChangeSuccessfully(user);
			String fullName = user.getFirstName() + " " + user.getLastName();
			commonService.sendWhatsappMsg("user_change_password_success", user.getContactNo(), Arrays.asList(fullName),
					"", false, -1L);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	@Transactional
	public Boolean forgotPassword(String emailId) {

		UserMasterEntity user = userMasterRepository.findByEmailAndIsDeleteFalseAndIsActiveTrueAndIsApproveTrue(emailId)
				.orElseThrow(() -> new RuntimeException("Email address not registered. Please sign up first."));

		helper.assertSubscriptionActive(helper.getEffectivePlanHistory(user));

		String otp = helper.generateAndSendOtp(emailId, user.getContactNo(), 1);
//		commonService.sendMailForOtp(user.getFirstName() + " " + user.getLastName(), emailId, otp);

		return true;
	}

	@Override
	@Transactional
	public Boolean verifyOtp(String emailId, String otp) {
		helper.verifyAndConsumeOtp(emailId, otp);
		return true;
	}

	@Override
	public Boolean resetPassword(String newPassword, String conPassword, String emailId) {

		UserMasterEntity user = userMasterRepository.findByEmailAndIsDeleteFalseAndIsActiveTrueAndIsApproveTrue(emailId)
				.orElseThrow(() -> new RuntimeException("Email address not registered. Please sign up first."));

		helper.assertSubscriptionActive(helper.getEffectivePlanHistory(user));

		if (!newPassword.equals(conPassword))
			throw new RuntimeException("New password and confirm password do not match");

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setIsFirstTime(false);
		userMasterRepository.save(user);

		try {
//			commonService.sendMailPassowrdChangeSuccessfully(user);
			commonService.sendWhatsappMsg("user_change_password_success", user.getContactNo(),
					Arrays.asList(user.getFirstName() + " " + user.getLastName()), "", false, -1L);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public Boolean isApproved(Boolean isApprove, Long userId, String otp) {

		UserMasterEntity superAdmin = userMasterRepository.findByContactNoAndIsDeleteFalse(mobile)
				.orElseThrow(() -> new RuntimeException("Mobile number not registered."));

		helper.verifyAndConsumeOtp(superAdmin.getEmail(), otp);

		UserMasterEntity user = getUser(userId);
		user.setIsBlock(false);
		user.setIsApprove(isApprove);
		user.setIsActive(isApprove);

		UserPlansHistoryEntity history = helper.getActivePlanHistory(user);
		if (history == null)
			throw new RuntimeException("Your subscription has expired. Please renew to continue.");

		LocalDateTime start = commonService.getCurrentDateTime();
		history.setStartDate(start);
		history.setEndDate(helper.calcPlanEndDate(history.getPlan().getBillingCycle(), start));

		userMasterRepository.save(user);
		userPlansHistoryRepository.save(history);
		userMasterRepository.updateIsApproveAndIsActive(userId);
		UserApprovedResponseDto dto = new UserApprovedResponseDto();
		dto.setEmail(user.getEmail());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
//		try {
//			commonService.sendAccountApprovalEmail(dto);
//		} catch (MessagingException | IOException e) {
//			e.printStackTrace();
//		}

		return true;
	}

	@Override
	@Transactional
	public Boolean loginWithOtp(String mobileNo, HttpServletRequest re) {

		UserMasterEntity user = userMasterRepository.findByContactNoAndIsDeleteFalse(mobileNo)
				.orElseThrow(() -> new RuntimeException("Mobile number not registered. Please sign up first."));

		if (user.getIsBlock())
			throw new RuntimeException("Your account has been blocked. Please contact support.");

		String loggedInIp = userLogsServiceImpl.getIpAddress(re);
		if (user.getAllowedip() != null && !"".equals(user.getAllowedip()) && loggedInIp.equals(user.getAllowedip()))
			throw new RuntimeException("Your ip has been blocked. Please contact support.");

		helper.generateAndSendOtp(user.getEmail(), mobileNo, 1);
		return true;
	}

	@Override
	@Transactional
	public Boolean loginWithCode(String code) {

		PartyMasterEntity party = partyMasterRepository.findByPartyCodeAndIsDeleteFalse(code)
				.orElseThrow(() -> new RuntimeException("Party is not found with code : " + code));

		helper.generateAndSendOtp(party.getEmail(), party.getMobileno(), 3);
		return true;
	}

	@Override
	public UserMasterResponseDto verifyOtpForMobile(String mobileNo, String otp, String uniqueCode, String SoftType) {

		UserMasterEntity user = userMasterRepository.findByContactNoAndIsDeleteFalse(mobileNo)
				.orElseThrow(() -> new RuntimeException("Mobile number not registered. Please sign up first."));

		UserBasicDetailsMasterEntity basicDetails = userBasicDetailsMasterRepository.findByUserAndIsDeleteFalse(user)
				.orElseThrow(() -> new RuntimeException("User basic details not found"));

		UserMasterEntity admin = user.getClientId() > 0 ? userMasterRepository
				.findByIdAndIsDeleteFalse(user.getClientId()).orElseThrow(() -> new RuntimeException("Admin Not Found"))
				: user;

		if (!basicDetails.getSoftType().equals(SoftType)) {
			throw new RuntimeException("Access denied. This account is not authorized to access this application.");
		}

		if (uniqueCode == null || uniqueCode.trim().isEmpty()) {
			throw new RuntimeException("Unique code is required");
		}

		if (!uniqueCode.equals(user.getUniqueCode())) {
			throw new RuntimeException("Invalid unique code");
		}

		if (!Objects.equals(uniqueCode, user.getUniqueCode())) {
			throw new RuntimeException("Invalid unique code");
		}

		helper.verifyAndConsumeOtp(user.getEmail(), otp);
		Long ownerId = user.getClientId() == -1 ? user.getId() : user.getClientId();
		UserMasterResponseDto responseDto = userMasterMapper.entityToResponse(user);
		responseDto.setLogo(helper.buildImageUrl(user.getLogo()));
		responseDto.setLang(basicDetails.getLang());
		responseDto.setUserRights(getUserRightsByRole(basicDetails.getRole().getId()));
		responseDto.setBanquetRights(banquetRightsService.getByUser(ownerId, user.getId()));
		responseDto.setRoleReportRights(roleReportRightsHelper.getForRoleOnly(basicDetails.getRole().getId()));
		responseDto.setUserUpgradedModule(setUserUpgradedModule(admin));
		responseDto.setGstNumber(basicDetails.getGstNumber());
		responseDto.setStockTypeRights(stockTypeService.getAllStockTypeRights(user.getId()));
		responseDto.setIsVisible(user.getIsVisible());
		responseDto.setIsInquiryVisible(user.getIsInquiryVisible());
		responseDto.setFollowupDay(basicDetails.getFollowupDay());
		UserPlansHistoryEntity history = helper.getEffectivePlanHistory(user);
		if (history != null)
			responseDto.setPlan(plansService.getPlansById(history.getPlan().getId()));

		String token = jwtUtil.generateToken(responseDto);
		responseDto.setToken(token);
		responseDto.setTokenType("Bearer");
		responseDto.setExpiresIn(jwtUtil.getExpirationTime());
		responseDto.setDatabase(getUserDatabase(user.getId()));
		responseDto.setThemeColor(basicDetails.getThemeColor());
		responseDto.setSoftType(basicDetails.getSoftType());
		responseDto.setUniqueCode(user.getUniqueCode());
		log.info("User login successful for Mobile No: {}", mobileNo);
		return responseDto;
	}

	@Override
	public PartyMasterResponseDto verifyOtpForCode(String code, String otp) {

		PartyMasterEntity party = partyMasterRepository.findByPartyCodeAndIsDeleteFalse(code)
				.orElseThrow(() -> new RuntimeException("Party is not find with this code : " + code));

		helper.verifyAndConsumeOtp(party.getEmail(), otp);

		PartyMasterResponseDto responseDto = partyMasterMapper.entityToResponse(party);
		String token = jwtUtil.generateToken(responseDto);
		responseDto.setToken(token);
		responseDto.setTokenType("Bearer");
		responseDto.setExpiresIn(jwtUtil.getExpirationTime());

		log.info("User login successful for code: {}", code);
		return responseDto;
	}

	@Override
	public Boolean updateMember(@Valid UpdateUserMemberRequestDto request, Long userId) {

		UserMasterEntity user = getUser(userId);
		PlansEntity plan = plansRepository.findByIdAndIsDeleteFalse(request.getPlanId())
				.orElseThrow(() -> new RuntimeException("Plan not found with id: " + request.getPlanId()));

		UserPlansHistoryEntity historyEntity = helper.getActivePlanHistory(user);
		UserBasicDetailsMasterEntity basicDetails = userBasicDetailsMasterRepository.findByUser(user);

		updateBasicDetails(basicDetails, request);
		updateUserDetails(user, request);
		updatePlanHistory(user, plan, historyEntity, basicDetails, request);

		if (request.getUserDocuments() != null && !request.getUserDocuments().isEmpty())
			updateUserDocuments(user, request.getUserDocuments());

		if (request.getUserAmcs() != null && !request.getUserAmcs().isEmpty())
			updateUserAmcs(user, request.getUserAmcs());

		if (request.getRefundDetails() != null && !request.getRefundDetails().isEmpty())
			updateRefundDetails(user, request.getRefundDetails());

		if (request.getUserDownPayments() != null && !request.getUserDownPayments().isEmpty())
			updateUserDownPayments(user, request.getUserDownPayments());

		if (request.getUserOffer() != null && !request.getUserOffer().isEmpty())
			updateUserOffers(user, request.getUserOffer());

		return true;
	}

	@Override
	public UserAdminResponseDto getMemberById(Long id) {

		UserMasterEntity user = getUser(id);
		UserBasicDetailsMasterEntity basicDetails = getBasicDetails(user);
		UserPlansHistoryEntity history = helper.getActivePlanHistory(user);
		List<PaymentInfo> payments = infoRepository.findAllByUserAndIsDeleteFalse(user);
		List<UserDocumentInfoEntity> documents = userDocumentInfoRepository.findAllByUserAndIsDeleteFalse(user);
		List<UserAmcEntity> useramc = userAmcRepository.findAllByUserAndIsDeleteFalse(user);
		List<RefundDetailsEntity> refundDetails = refundDetailsRepository.findAllByUserAndIsDeleteFalse(user);
		List<UserBasicFileEntity> basicFiles = basicFileRepository.findAllByUserIdAndIsDeleteFalse(user.getId());
		List<UserOfferEntity> offers = offerRepository.findAllByUserIdAndIsDeleteFalseAndIsActiveTrue(user.getId());

		List<FileWithIdResponseDto> fileDtos = new ArrayList<>();
		for (UserBasicFileEntity f : basicFiles) {
			FileWithIdResponseDto dto = new FileWithIdResponseDto();
			dto.setFile(f.getFile());
			dto.setFileId(f.getId());
			dto.setFileType(f.getModuleType());
			fileDtos.add(dto);
		}

		UserAdminResponseDto dto = new UserAdminResponseDto();
		populateBasicUserInfo(dto, user, basicDetails);
		dto.setUserPlan(buildUserPlanResponse(history));
		dto.setDownPayment(buildDownPaymentList(payments));
		dto.setUserDocument(buildDocumentList(documents));
		dto.setUserAmc(buildUserAmcList(useramc));
		dto.setRefundDetails(buildRefundDetailsList(refundDetails));
		dto.setUserOffers(buildUserOfferList(offers));
		dto.setFiles(fileDtos);
		dto.setUserThemes(buildResponse(adminTemplateModuleRepository.getAllUserTemplates(user.getId())));
		dto.setDatabase(getUserDatabase(user.getId()));
		dto.setFollowUpDay(basicDetails.getFollowupDay());

		return dto;
	}

	@Override
	@Transactional
	public Boolean deleteUserById(Long userId, String otp, Boolean isAdmin) {

		if (isAdmin) {

			if (!helper.sendSuperAdminOtpIfMissing(otp))
				return false;

			UserMasterEntity superAdmin = userMasterRepository.findByContactNoAndIsDeleteFalse(mobile)
					.orElseThrow(() -> new RuntimeException("Super admin not found"));
			helper.verifyAndConsumeOtp(superAdmin.getEmail(), otp);
		}

		UserMasterEntity user = getUser(userId);
		UserBasicDetailsMasterEntity basicDetails = getBasicDetails(user);

		user.setIsActive(false);
		user.setIsDelete(true);
		user.setIsApprove(false);
		basicDetails.setIsDelete(true);

		userBasicDetailsMasterRepository.save(basicDetails);
		userMasterRepository.save(user);
		return true;
	}

	@Override
	@Transactional
	public Boolean userBlock(Long userId, String otp) {

		if (!helper.sendSuperAdminOtpIfMissing(otp))
			return false;

		UserMasterEntity superAdmin = userMasterRepository.findByContactNoAndIsDeleteFalse(mobile)
				.orElseThrow(() -> new RuntimeException("Super admin not found"));
		helper.verifyAndConsumeOtp(superAdmin.getEmail(), otp);

		UserMasterEntity user = getUser(userId);
		user.setIsActive(false);
		user.setIsApprove(false);
		user.setIsBlock(true);
		userMasterRepository.save(user);
		return true;
	}

	@Override
	@Transactional
	public Boolean convertUserType(Long userId, String type, String otp) {

		if (!helper.sendSuperAdminOtpIfMissing(otp))
			return false;

		UserMasterEntity superAdmin = userMasterRepository.findByContactNoAndIsDeleteFalse(mobile)
				.orElseThrow(() -> new RuntimeException("Super admin not found"));
		helper.verifyAndConsumeOtp(superAdmin.getEmail(), otp);

		UserBasicDetailsMasterEntity userBasicDetails = userBasicDetailsMasterRepository
				.findByUserAndIsDeleteFalse(getUser(userId))
				.orElseThrow(() -> new RuntimeException("User details not found for userId : " + userId));

		userBasicDetails.setType(type);
		userBasicDetailsMasterRepository.save(userBasicDetails);
		return true;
	}

	@Override
	public Boolean deleteUserDownPaymentById(Long id) {
		if (!infoRepository.existsByIdAndIsDeleteFalse(id))
			return false;
		PaymentInfo info = infoRepository.findByIdAndIsDeleteFalse(id);
		info.setIsDelete(true);
		infoRepository.save(info);
		return true;
	}

	@Override
	public Boolean deleteUserDocumentById(Long id) {
		if (!userDocumentInfoRepository.existsByIdAndIsDeleteFalse(id))
			return false;
		UserDocumentInfoEntity doc = userDocumentInfoRepository.findByIdAndIsDeleteFalse(id);
		doc.setIsDelete(true);
		userDocumentInfoRepository.save(doc);
		return true;
	}

	@Override
	public Boolean deleteUserAmcById(Long id) {
		if (!userAmcRepository.existsByIdAndIsDeleteFalse(id))
			return false;
		UserAmcEntity amc = userAmcRepository.findByIdAndIsDeleteFalse(id);
		amc.setIsDelete(true);
		userAmcRepository.save(amc);
		return true;
	}

	@Override
	public Boolean deleteUserRefundById(Long id) {
		if (!refundDetailsRepository.existsByIdAndIsDeleteFalse(id))
			return false;
		RefundDetailsEntity ref = refundDetailsRepository.findByIdAndIsDeleteFalse(id);
		ref.setIsDelete(true);
		refundDetailsRepository.save(ref);
		return true;
	}

	@Override
	public Boolean deleteBasicFileById(Long id, String fileType) {
		if (!basicFileRepository.existsByIdAndIsDeleteFalseAndModuleType(id, fileType))
			return false;
		UserBasicFileEntity file = basicFileRepository.findByIdAndIsDeleteFalseAndModuleType(id, fileType).get();
		file.setIsDelete(true);
		basicFileRepository.save(file);
		return true;
	}

	@Override
	public Boolean deleteUserOfferById(Long id) {
		if (!offerRepository.existsByIdAndIsDeleteFalse(id))
			return false;
		UserOfferEntity offer = offerRepository.findByIdAndIsDeleteFalse(id).get();
		offer.setIsDelete(true);
		offerRepository.save(offer);
		return true;
	}

	@Override
	public List<UserResponseDTO> getAllManagers() {
		return mapUserRows(userMasterRepository.getAllManagers());
	}

	@Override
	public List<UserResponseDTO> getAllRolesExcludeManager() {
		return mapUserRows(userMasterRepository.getAllRolesExcludesAdminManager());
	}

	private List<UserResponseDTO> mapUserRows(List<Object[]> rows) {
		if (rows == null || rows.isEmpty())
			return new ArrayList<>();
		List<UserResponseDTO> list = new ArrayList<>();
		for (Object[] row : rows) {
			UserResponseDTO dto = new UserResponseDTO();
			dto.setUserId(((BigInteger) row[0]).longValue());
			dto.setName(row[1] + " " + row[2]);
			dto.setEmail((String) row[3]);
			dto.setContactNo((String) row[4]);
			dto.setPre_fix((String) row[5]);
			dto.setRoleId(((BigInteger) row[6]).longValue());
			dto.setRoleName((String) row[7]);
			list.add(dto);
		}
		return list;
	}

	@Override
	public List<ReportingManagerResponseDTO> getAllClientByReportingManager(Long reportingManagerId) {

		UserMasterEntity rm = userMasterRepository.findByIdAndIsDeleteFalse(reportingManagerId)
				.orElseThrow(() -> new RuntimeException("Reporting Manager not found with Id : " + reportingManagerId));

		List<Object[]> rows = userMasterRepository.getAllClientsByReportingManagerId(reportingManagerId);
		List<ReportingManagerResponseDTO> list = new ArrayList<>();

		for (Object[] row : rows) {
			ReportingManagerResponseDTO response = new ReportingManagerResponseDTO();
			response.setId(((Number) row[0]).longValue());
			response.setName(((row[1] != null ? row[1] : "") + " " + (row[2] != null ? row[2] : "")).trim());
			response.setEmail(row[3] != null ? row[3].toString() : null);
			response.setContactNo(row[4] != null ? row[4].toString() : null);
			response.setPre_fix(row[5] != null ? row[5].toString() : null);
			response.setCreatedAt(
					row[12] != null ? ((Timestamp) row[12]).toLocalDateTime().format(UserMasterHelper.DATE_FMT) : null);
			response.setIsApprove(row[13] != null ? (Boolean) row[13] : null);
			response.setCompanyName(row[14] != null ? row[14].toString() : null);
			list.add(response);
		}
		return list;
	}

	private UserMasterEntity getUser(Long id) {
		return userMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	private UserBasicDetailsMasterEntity getBasicDetails(UserMasterEntity user) {
		return userBasicDetailsMasterRepository.findByUser(user);
	}

	private DataBaseResponse getUserDatabase(Long id) {
		List<Object[]> list = databasePlanningEntityRepository.getDbNameByUser(id);
		if (list == null || list.isEmpty())
			return null;
		Object[] obj = list.get(0);
		DataBaseResponse res = new DataBaseResponse();
		res.setId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
		res.setDbName((String) obj[1]);
		res.setState((String) obj[2]);
		res.setUuid((String) obj[3]);
		res.setInstructions((String) obj[4]);
		res.setIsPublished((Boolean) obj[5]);
		res.setUserId(obj[6] != null ? ((Number) obj[6]).longValue() : null);
		res.setUserName(obj[7] + " " + obj[8]);
		res.setParentDbId(obj[9] != null ? ((Number) obj[9]).longValue() : null);
		res.setParentDbName((String) obj[10]);
		return res;
	}

	private List<UserRightsPageWithModuleResponseDto> getUserRightsByRole(Long roleId) {
		List<UserRightsMasterEntity> rightsList = userRightsMasterRepository.findAllByRoleid(roleId);
		if (rightsList.isEmpty())
			return new ArrayList<>();

		Map<Long, List<UserRightsMasterEntity>> grouped = rightsList.stream()
				.collect(Collectors.groupingBy(UserRightsMasterEntity::getModuleId));

		List<UserRightsPageWithModuleResponseDto> response = new ArrayList<>();
		for (Map.Entry<Long, List<UserRightsMasterEntity>> entry : grouped.entrySet()) {
			Long moduleId = entry.getKey();
			String modName = moduleRightsRepository.findById(moduleId).map(UserRightsModuleEntity::getName)
					.orElse("Unknown Module");
			List<UserRightsMasterResponseDto> pageRights = entry.getValue().stream()
					.map(e -> new UserRightsMasterResponseDto(e.getId(), e.getPageid(), getPageName(e.getPageid()),
							e.getAddaccess(), e.getEditaccess(), e.getDeleteaccess(), e.getViewaccess()))
					.collect(Collectors.toList());
			response.add(new UserRightsPageWithModuleResponseDto(moduleId, modName, pageRights));
		}
		return response;
	}

	private String getPageName(Long pageId) {
		if (pageId == null)
			return "Unknown Page";
		return userRightsPagesRepository.findById(pageId).map(UserRightsPagesEntity::getPagename)
				.orElse("Unknown Page");
	}

	private void populateBasicUserInfo(UserAdminResponseDto dto, UserMasterEntity user,
			UserBasicDetailsMasterEntity details) {

		dto.setId(user.getId());
		dto.setClientId(user.getClientId());
		dto.setUserCode(user.getUserCode());
		dto.setEmail(user.getEmail());
		dto.setContactNo(user.getContactNo());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setIsActive(user.getIsActive());
		dto.setIsApprove(user.getIsApprove());
		dto.setCreatedAt(user.getCreatedAt().format(UserMasterHelper.DATE_FMT));
		dto.setMemberType(details.getMemberType());
		dto.setAddress(details.getAddress());
		dto.setCompanyName(details.getCompanyName());
		dto.setCompanyEmail(details.getCompanyEmail());
		dto.setCountryCode(details.getCountryCode());
		dto.setOfficeNo(details.getOfficeNo());
		dto.setSoftType(details.getSoftType());
		dto.setUniqueCode(user.getUniqueCode());
		dto.setReportingManagerId(details.getReportingManagerId());
		dto.setReportingManagerName(helper.resolveUserName(details.getReportingManagerId()));

		dto.setManagerId(details.getManagerId());
		dto.setManagerName(helper.resolveUserName(details.getManagerId()));

		dto.setSalesId(details.getSalesId());
		dto.setSalesName(helper.resolveUserName(details.getSalesId()));

		dto.setSalesReq(details.getSalesReq());
		dto.setManagerReq(details.getManagerReq());
		dto.setOverAllRemarks(details.getOverAllRemarks());
		dto.setProfile(details.getProfile());
		dto.setDateOfBirth(dateMapper.dateToString(details.getDateOfBirth()));
		dto.setStateId(details.getState().getId());
		dto.setStateName(details.getState().getName());
		dto.setCityName(details.getCity().getName());
		dto.setType(details.getType());
		dto.setServices(details.getServices());
		dto.setLang(details.getLang());
	}

	private UserPlansHistoryResponseDto buildUserPlanResponse(UserPlansHistoryEntity history) {
		return helper.buildUserPlanHistoryDto(history);
	}

	private List<UserDownPaymentResponseDto> buildDownPaymentList(List<PaymentInfo> payments) {
		if (payments == null || payments.isEmpty())
			return Collections.emptyList();
		return payments.stream().map(pay -> {
			UserDownPaymentResponseDto dto = new UserDownPaymentResponseDto();
			dto.setId(pay.getId());
			dto.setPayid(pay.getPayid());
			dto.setAmount(pay.getAmount());
			dto.setPaidAmount(pay.getPaidamount());
			dto.setPaymentDone(pay.getPaymentdone());
			dto.setPaymentType(pay.getPaymentType());
			dto.setRemarks(pay.getRemarks());
			dto.setDocPath(helper.buildImageUrl(pay.getDocPath()));
			dto.setTransactionDateTime(pay.getPaymentdonetimestamp().format(UserMasterHelper.DATETIME_FMT));
			return dto;
		}).collect(Collectors.toList());
	}

	private List<UserDocumentResponseDto> buildDocumentList(List<UserDocumentInfoEntity> documents) {
		if (documents == null || documents.isEmpty())
			return Collections.emptyList();
		return documents.stream().map(doc -> {
			UserDocumentResponseDto dto = new UserDocumentResponseDto();
			dto.setId(doc.getId());
			dto.setKycNo(doc.getKycNo());
			dto.setKycType(doc.getKycType());
			dto.setDocPath(helper.buildImageUrl(doc.getDocPath()));
			return dto;
		}).collect(Collectors.toList());
	}

	private List<UserAmcResponseDto> buildUserAmcList(List<UserAmcEntity> userAmcs) {
		if (userAmcs == null || userAmcs.isEmpty())
			return Collections.emptyList();
		return userAmcs.stream().map(doc -> {
			UserAmcResponseDto dto = new UserAmcResponseDto();
			dto.setId(doc.getId());
			dto.setAmcAmount(doc.getAmcAmount());
			dto.setAmcDate(UserMasterHelper.DATE_FMT.format(doc.getAmcDate()));
			dto.setAmcRecivableAmount(doc.getAmcRecivableAmount());
			dto.setAmcRecivableDate(UserMasterHelper.DATE_FMT.format(doc.getAmcRecivableDate()));
			dto.setStatus(doc.getStatus());
			dto.setAmcType(doc.getAmcType());
			dto.setAmcRemarks(dto.getAmcRemarks());
			dto.setUserId(doc.getUser().getId());
			dto.setFile(helper.buildImageUrl(doc.getFile()));
			return dto;
		}).collect(Collectors.toList());
	}

	private List<RefundDetailsResponseDto> buildRefundDetailsList(List<RefundDetailsEntity> refunds) {
		if (refunds == null || refunds.isEmpty())
			return Collections.emptyList();
		return refunds.stream().map(doc -> {
			RefundDetailsResponseDto dto = new RefundDetailsResponseDto();
			dto.setId(doc.getId());
			dto.setAmount(doc.getAmount());
			dto.setRefundDate(UserMasterHelper.DATE_FMT.format(doc.getRefundDate()));
			dto.setRefundDetails(doc.getRefundDetails());
			dto.setRefundPaymentMode(doc.getRefundPaymentMode());
			dto.setRefundType(doc.getRefundType());
			dto.setRemarks(doc.getRemarks());
			dto.setUser(doc.getUser().getId());
			dto.setUpdatedAt(UserMasterHelper.DATETIME_SEC_FMT.format(doc.getUpdatedAt()));
			dto.setFile(helper.buildImageUrl(doc.getFile()));
			return dto;
		}).collect(Collectors.toList());
	}

	private List<UserOfferResponseDto> buildUserOfferList(List<UserOfferEntity> offers) {
		if (offers == null || offers.isEmpty())
			return Collections.emptyList();
		return offers.stream().map(o -> {
			UserOfferResponseDto dto = new UserOfferResponseDto();
			dto.setId(o.getId());
			dto.setIsActive(o.getIsActive());
			dto.setIsDelete(o.getIsDelete());
			dto.setOfferExpireDate(o.getOfferExpireDate().format(UserMasterHelper.DATETIME_SEC_FMT));
			dto.setCreatedAt(o.getCreatedAt().format(UserMasterHelper.DATE_FMT));
			dto.setOfferId(o.getOfferId());
			return dto;
		}).collect(Collectors.toList());
	}

	public List<UserAssignedAllModuleWiseThemeResponseDto> buildResponse(List<Object[]> rows) {
		Map<String, UserAssignedAllModuleWiseThemeResponseDto> moduleMap = rows.stream().collect(Collectors.toMap(
				row -> String.valueOf(row[0]),
				row -> new UserAssignedAllModuleWiseThemeResponseDto(String.valueOf(row[0]), (String) row[1],
						new ArrayList<>(Arrays.asList(new UserAssignedThemeResponseDto(((Number) row[4]).longValue(),
								((Number) row[2]).longValue(), (String) row[3])))),
				(existing, incoming) -> {
					existing.getThemes().addAll(incoming.getThemes());
					return existing;
				}, LinkedHashMap::new));
		return new ArrayList<>(moduleMap.values());
	}

	private Map<String, Long> buildLeadTypeMap(Long assignId, UserMasterEntity user) {
		Map<String, Long> map = new HashMap<>();
		map.put("Hot", 0L);
		map.put("Inquire", 0L);
		map.put("Cold", 0L);
		List<Object[]> rows = assignId == null ? leadMasterRepository.countByLeadType(user.getId())
				: leadMasterRepository.countByLeadTypeAndLeadAssignId(assignId, user.getId());
		for (Object[] row : rows)
			map.put((String) row[0], ((Number) row[1]).longValue());
		return map;
	}

	private Map<String, Long> buildLeadStatusMap(Long assignId, UserMasterEntity user) {
		Map<String, Long> map = new HashMap<>();
		/*
		 * map.put("Pending", 0L); map.put("Confirmed", 0L); map.put("Cancel", 0L);
		 * map.put("Open", 0L); map.put("Closed", 0L); List<Object[]> rows = assignId ==
		 * null ? leadMasterRepository.countByLeadStatus(user.getId()) :
		 * leadMasterRepository.countByLeadStatusAndLeadAssignId(assignId,
		 * user.getId()); for (Object[] row : rows) map.put((String) row[0], ((Number)
		 * row[1]).longValue());
		 */
		return map;
	}

	private void updateUserOffers(UserMasterEntity user, List<UserOfferRequestDto> userOffer) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
		for (UserOfferRequestDto req : userOffer) {
			UserOfferEntity entity = req.getId() == -1 ? new UserOfferEntity()
					: offerRepository.findByIdAndIsDeleteFalseAndIsActiveTrue(req.getId())
							.orElseThrow(() -> new RuntimeException("User Offer not found with id " + req.getId()));
			entity.setIsActive(true);
			entity.setIsDelete(false);
			entity.setOfferExpireDate(LocalDateTime.parse(req.getOfferExpireDate(), fmt));
			entity.setOfferId(req.getOfferId());
			entity.setUserId(user.getId());
			offerRepository.save(entity);
		}
	}

	private Boolean updatePlanHistory(UserMasterEntity user, PlansEntity plan, UserPlansHistoryEntity historyEntity,
			UserBasicDetailsMasterEntity basicDetails, UpdateUserMemberRequestDto request) {

		if (basicDetails.getRole() == null || basicDetails.getRole().getId() != 2)
			return true;

		if (historyEntity == null) {
			updateUserCode(user, plan);
			userPlansHistoryRepository.save(buildUserPlanHistory(user, plan, request));
			return true;
		}

		if (!plan.getId().equals(historyEntity.getPlan().getId())) {
			updateUserCode(user, plan);
			historyEntity.setIsActive(false);
			userPlansHistoryRepository.save(historyEntity);
			userPlansHistoryRepository.save(buildUserPlanHistory(user, plan, request));
		} else {
			historyEntity.setPlanAmount(request.getPlanAmount());
			historyEntity.setPlanBaseAmount(request.getPlanBaseAmount());
			userPlansHistoryRepository.save(historyEntity);
		}
		return true;
	}

	private void updateUserCode(UserMasterEntity user, PlansEntity plan) {
		user.setUserCode(commonService.getLastestUserCode(plan.getName()));
	}

	private UserPlansHistoryEntity buildUserPlanHistory(UserMasterEntity user, PlansEntity plan,
			UpdateUserMemberRequestDto request) {
		UserPlansHistoryEntity history = new UserPlansHistoryEntity();
		history.setIsActive(true);
		history.setUser(user);
		history.setPlan(plan);
		history.setPlanBaseAmount(request.getPlanBaseAmount());
		history.setPlanAmount(request.getPlanAmount());
		history.setCgst(request.getCgst());
		history.setCgstAmt(request.getCgstAmt());
		history.setSgst(request.getSgst());
		history.setSgstAmt(request.getSgstAmt());
		history.setTotalPrice(request.getFinalTotal());
		setCouponDetails(history, request);
		setExtraPaymentDetails(history, request);
		return history;
	}

	private void setCouponDetails(UserPlansHistoryEntity history, UpdateUserMemberRequestDto request) {
		if (request.getCoupenId() == null)
			return;
		coupenMasterRepository.findByIdAndIsDeleteFalse(request.getCoupenId()).ifPresent(coupon -> {
			history.setCoupen(coupon);
			history.setDiscountPrice(coupon.getPrice());
		});
	}

	private void setExtraPaymentDetails(UserPlansHistoryEntity history, UpdateUserMemberRequestDto request) {
		if (request.getExtraPayId() == null)
			return;
		ExtraPaymentEntity ep = extraPaymentMasterRepository.findByIdAndIsDeleteFalse(request.getExtraPayId());
		if (ep != null) {
			history.setExtraPay(ep);
			history.setExtraAmount(ep.getPrice().toString());
		}
	}

	private Boolean updateUserDetails(UserMasterEntity user, UpdateUserMemberRequestDto request) {
		user.setPreFix(request.getPreFix());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setContactNo(request.getContactNo());
		userMasterRepository.save(user);
		if (request.getFiles() != null && !request.getFiles().isEmpty()) {
			request.getFiles().forEach(file -> {
				try {
					userFileService.storeFile(user.getId(), ModuleName.USERBASICFILE.toString(), file.getFileId(),
							file.getFileType(), file.getFile());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
		return true;
	}

	private Boolean updateBasicDetails(UserBasicDetailsMasterEntity basicDetails, UpdateUserMemberRequestDto request) {
		basicDetails.setAddress(request.getAddress());
		if (request.getCityId() != null) {
			basicDetails.setCity(cityMasterRepository.findByIdAndIsDeleteFalse(request.getCityId())
					.orElseThrow(() -> new RuntimeException("City not found with id: " + request.getCityId())));
		}
		basicDetails
				.setReportingManagerId(request.getReportingManagerId() == null ? 0 : request.getReportingManagerId());
		basicDetails.setMemberType(request.getMemberType());
		basicDetails.setProfile(request.getProfile());
		basicDetails.setSalesId(request.getSalesId() == null ? 0 : request.getSalesId());
		basicDetails.setManagerId(request.getManagerId() == null ? 0 : request.getManagerId());
		basicDetails.setDateOfBirth(dateMapper.stringToDate(request.getDateOfBirth()));
		basicDetails.setSalesReq(request.getSalesReq());
		basicDetails.setManagerReq(request.getManagerReq());
		basicDetails.setOverAllRemarks(request.getOverAllRemarks());
		basicDetails.setType(request.getType());
		basicDetails.setServices(request.getServices());
		basicDetails.setFollowupDay(request.getFollowupDay());
		basicDetails.setLang(request.getLang());
		basicDetails.setThemeColor(request.getThemeColor());
		basicDetails.setSoftType(request.getSoftType());
		userBasicDetailsMasterRepository.save(basicDetails);
		return true;
	}

	private Boolean updateUserDocuments(UserMasterEntity user, List<UserDocumentRequestDto> documents) {
		for (UserDocumentRequestDto doc : documents) {
			UserDocumentInfoEntity entity = doc.getId() == 0 ? new UserDocumentInfoEntity()
					: userDocumentInfoRepository.findByIdAndIsDeleteFalseAndUser(doc.getId(), user);
			entity.setKycNo(doc.getKycNo());
			entity.setKycType(doc.getKycType());
			entity.setUser(user);
			entity = userDocumentInfoRepository.save(entity);
			if (doc.getDocPath() != null && !doc.getDocPath().isEmpty()) {
				try {
					userFileService.storeFile(user.getId(), ModuleName.USERDOCUMENT.toString(), entity.getId(),
							FileType.KYC.toString(), doc.getDocPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}

	private void updateUserDownPayments(UserMasterEntity user, List<UserDownPaymentRequestDto> payments) {
		for (UserDownPaymentRequestDto dto : payments) {
			PaymentInfo payment = dto.getId() == 0 ? new PaymentInfo()
					: infoRepository.findByIdAndUser(dto.getId(), user);
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate date = parseDate(dto.getTransactionDate(), fmt);
			payment.setPaymentType(dto.getPaymentType());
			payment.setPaymentdonetimestamp(date.atTime(LocalTime.now()));
			payment.setRemarks(dto.getRemarks());
			payment.setPaymentdone(true);
			payment.setAmount(dto.getAmount());
			payment.setPaidamount(dto.getPaidAmount());
			payment.setPayid(dto.getPayid());
			payment.setUser(user);
			payment.setUserPlanHist(helper.getActivePlanHistory(user));
			payment = infoRepository.save(payment);
			if (dto.getDocPath() != null && !dto.getDocPath().isEmpty()) {
				try {
					userFileService.storeFile(user.getId(), ModuleName.USERDOWNPAYMENT.toString(), payment.getId(),
							FileType.INVOICE.toString(), dto.getDocPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private Boolean updateUserAmcs(UserMasterEntity user, List<UserAmcRequestDto> userAmcs) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		for (UserAmcRequestDto dto : userAmcs) {
			UserAmcEntity userAmc = dto.getId() == 0 ? new UserAmcEntity()
					: userAmcRepository.findByIdAndUserAndIsDeleteFalse(dto.getId(), user);
			userAmc.setAmcType(dto.getAmcType());
			userAmc.setAmcAmount(dto.getAmcAmount());
			userAmc.setAmcDate(parseDate(dto.getAmcDate(), fmt));
			userAmc.setAmcRecivableAmount(dto.getAmcRecivableAmount());
			userAmc.setAmcRecivableDate(parseDate(dto.getAmcRecivableDate(), fmt));
			userAmc.setStatus(dto.getStatus());
			userAmc.setUser(user);
			userAmc = userAmcRepository.save(userAmc);
			if (dto.getFile() != null && !dto.getFile().isEmpty()) {
				try {
					userFileService.storeFile(user.getId(), ModuleName.USERAMCFILE.toString(), userAmc.getId(),
							FileType.AMC_DOC.toString(), dto.getFile());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}

	private Boolean updateRefundDetails(UserMasterEntity user, List<RefundDetailsRequestDto> refundDetails) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		for (RefundDetailsRequestDto dto : refundDetails) {
			RefundDetailsEntity entity = dto.getId() == 0 ? new RefundDetailsEntity()
					: refundDetailsRepository.findByIdAndUserAndIsDeleteFalse(dto.getId(), user);
			entity.setRefundPaymentMode(dto.getRefundPaymentMode());
			entity.setAmount(dto.getAmount());
			entity.setRefundDate(parseDate(dto.getRefundDate(), fmt));
			entity.setRemarks(dto.getRemarks());
			entity.setRefundType(dto.getRefundType());
			entity.setRefundDetails(dto.getRefundDetails());
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			entity.setUser(user);
			entity = refundDetailsRepository.save(entity);
			if (dto.getFile() != null && !dto.getFile().isEmpty()) {
				try {
					userFileService.storeFile(user.getId(), ModuleName.REFUNDDETAILFILE.toString(), entity.getId(),
							FileType.REFUND_DOC.toString(), dto.getFile());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}

	private List<UserUpgradedModuleResponseDto> setUserUpgradedModule(UserMasterEntity user) {
		List<Object[]> list = upgradedModuleRepository.getActiveUpgradedModuleByUser(user.getId());
		if (list == null || list.isEmpty())
			return null;

		DateTimeFormatter fmt = UserMasterHelper.DATE_FMT;
		List<UserUpgradedModuleResponseDto> result = new ArrayList<>();
		for (Object[] obj : list) {
			UserUpgradedModuleResponseDto dto = new UserUpgradedModuleResponseDto();
			dto.setId(obj[0] != null ? ((Number) obj[0]).longValue() : null);
			dto.setUserId(obj[1] != null ? ((Number) obj[1]).longValue() : null);
			dto.setUpgradeModuleId(obj[2] != null ? ((Number) obj[2]).longValue() : null);
			dto.setModuleName((String) obj[3]);
			dto.setStartDate(obj[4] != null ? ((Timestamp) obj[4]).toLocalDateTime().format(fmt) : null);
			dto.setEndDate(obj[5] != null ? ((Timestamp) obj[5]).toLocalDateTime().format(fmt) : null);
			dto.setIsActive(obj[6] != null ? (Boolean) obj[6] : null);
			dto.setIsPayDone(obj[8] != null ? (Boolean) obj[8] : null);
			dto.setPayAmount(obj[9] != null ? (BigDecimal) obj[9] : null);
			dto.setBillingCycle((String) obj[10]);
			result.add(dto);
		}
		return result;
	}

	private LocalDate parseDate(String date, DateTimeFormatter formatter) {
		return Optional.ofNullable(date).map(d -> LocalDate.parse(d, formatter)).orElse(null);
	}

	private UserApprovedResponseDto mapToUserApprovedResponse(UserMasterEntity user, String plainPassword) {
		if (user == null)
			return null;
		UserApprovedResponseDto dto = new UserApprovedResponseDto();
		dto.setEmail(user.getEmail());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setPassword(plainPassword);
		return dto;
	}

	private void validateUserStatusAndSubscription(UserMasterEntity user, UserPlansHistoryEntity history) {
		if (!user.getIsActive())
			throw new RuntimeException("Account is deactivated. Please contact administrator.");
		if (!user.getIsApprove())
			throw new RuntimeException("Account is pending approval. Please contact administrator.");
		helper.assertSubscriptionActive(history);
	}

	@Override
	public Map<String, Object> getAllLogsUser(Boolean isActive) {
		Map<String, Object> response = new HashMap<>();

		LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
		List<UserLogsResponseDto> allResult = userMasterRepository.getUserStatus(last7Days, null);

		List<UserLogsResponseDto> filteredResult = allResult;

		if (isActive != null) {
			String statusFilter = isActive ? "ACTIVE" : "DEACTIVE";
			filteredResult = allResult.stream().filter(u -> statusFilter.equalsIgnoreCase(u.getStatus()))
					.collect(Collectors.toList());
		}

		Map<String, Long> statusCount = allResult.stream().filter(u -> u.getStatus() != null)
				.collect(Collectors.groupingBy(u -> u.getStatus().toUpperCase(), Collectors.counting()));

		response.put("userLogs", filteredResult);
		response.put("totalCnt", allResult.size());
		response.put("totalActiveCnt", statusCount.getOrDefault("ACTIVE", 0L));
		response.put("totalDeactiveCnt", statusCount.getOrDefault("DEACTIVE", 0L));

		return response;
	}

	@Override
	public Boolean isVisibleByUserId(Long userId) {
		Boolean isVisible = userMasterRepository.getIsVisibleByUserId(userId);
		if (Objects.isNull(isVisible))
			throw new RuntimeException("Data not found for given userId");
		return isVisible;
	}

	@Override
	public Boolean updateIsVisible(Long userId, Boolean isVisible) {
		int updatedRows = userMasterRepository.updateIsVisibleByUserId(userId, isVisible);
		return updatedRows > 0;
	}

	@Override
	public boolean isChildUserExist(Long userId) {

		return userMasterRepository.isChildUserExist(userId);
	}

	@Override
	public List<ParentUserResponseDto> getParentUser(Long roleId, Long userId) {

		RoleMasterEntity role = roleMasterRepository.findByIdAndIsDeleteFalse(roleId)
				.orElseThrow(() -> new RuntimeException("Role not found"));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<RoleHierarchyEntity> hierarchies = roleHierarchyRepository.findByChildRoleAndUserAndIsDeleteFalse(role,
				user);

		List<ParentUserResponseDto> response = new ArrayList<>();

		for (RoleHierarchyEntity hierarchy : hierarchies) {

			RoleMasterEntity parentRole = hierarchy.getParentRole();

			List<UserBasicDetailsMasterEntity> parentUsers = userBasicDetailsMasterRepository
					.findAllByRoleAndIsDeleteFalse(parentRole);

			for (UserBasicDetailsMasterEntity details : parentUsers) {

				UserMasterEntity u = details.getUser();

				ParentUserResponseDto dto = new ParentUserResponseDto();

				dto.setUserId(u.getId());
				dto.setUserName(u.getFirstName() + " " + u.getLastName());
				dto.setMobileNo(u.getContactNo());
				dto.setRoleId(parentRole.getId());
				dto.setRoleName(parentRole.getName());

				response.add(dto);
			}
		}

		return response;
	}

	@Override
	public Boolean isInquiryVisible(Long userId, Boolean isVisible) {
		UserMasterEntity masterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		masterEntity.setIsInquiryVisible(isVisible);
		userMasterRepository.save(masterEntity);
		return true;
	}
}