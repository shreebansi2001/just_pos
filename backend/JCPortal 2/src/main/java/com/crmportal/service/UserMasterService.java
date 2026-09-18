package com.crmportal.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UpdateUserMemberRequestDto;
import com.crmportal.request.dto.UserMasterRequestDto;
import com.crmportal.response.dto.ParentUserResponseDto;
import com.crmportal.response.dto.PartyMasterResponseDto;
import com.crmportal.response.dto.PlanUserSummaryResponseDto;
import com.crmportal.response.dto.ReportingManagerResponseDTO;
import com.crmportal.response.dto.UserAdminResponseDto;
import com.crmportal.response.dto.UserApprovedResponseDto;
import com.crmportal.response.dto.UserLogsResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.response.dto.UserResponseDTO;

@Service
public interface UserMasterService {

	UserMasterResponseDto addOrUpdateUserMaster(@Valid UserMasterRequestDto request, Long id);

	UserMasterResponseDto loginWithPassword(@Valid String email, String password, String otp, HttpServletRequest re, String uniqueCode, String softType);

	Map<String, Object> getAllUserMaster(Long userId, String userName);

	UserMasterResponseDto getUserMasterById(Long id);

	List<UserMasterResponseDto> getManagerAndAdminUsersByClientUserId(Long clientUserId);

	PlanUserSummaryResponseDto getAllAdminUserMaster(Long roleId, String userName, String type);

	Boolean changePassword(String oldPassword, String newPassword, String conPassword, Long userId);

	Boolean forgotPassword(String emailId);

	Boolean verifyOtp(String email, String otp);

	Boolean resetPassword(String newPassword, String conPassword, String emailId);

	Boolean isApproved(Boolean isApprove, Long userId, String otp);

	Boolean loginWithOtp(String mobileNo, HttpServletRequest re);

	Boolean loginWithCode(@Valid String request);

	UserMasterResponseDto verifyOtpForMobile(String mobileNo, String otp,String uniqueCode, String softType);

	PartyMasterResponseDto verifyOtpForCode(String code, String otp);

	Boolean updateMember(@Valid UpdateUserMemberRequestDto request, Long userId);

	UserAdminResponseDto getMemberById(Long id);

	Boolean deleteUserDownPaymentById(Long id);

	Boolean deleteUserDocumentById(Long id);

	Boolean deleteUserAmcById(Long id);

	Boolean deleteUserRefundById(Long id);

	Boolean deleteBasicFileById(Long id, String fileType);

	Object refreshToken(String oldToken);

	List<UserResponseDTO> getAllManagers();

	List<UserResponseDTO> getAllRolesExcludeManager();

	Boolean deleteUserOfferById(Long id);

	List<ReportingManagerResponseDTO> getAllClientByReportingManager(Long reportingManagerId);

	Boolean deleteUserById(Long userId, String otp,Boolean isActive);

	Boolean userBlock(Long userId, String otp);

	Boolean convertUserType(Long userId, String type, String otp);

	Map<String, Object> getAllLogsUser(Boolean isActive);

	Boolean isVisibleByUserId(Long userId);
	
	Boolean updateIsVisible(Long userId, Boolean isVisible);

	boolean isChildUserExist(Long userId);

	List<ParentUserResponseDto> getParentUser(Long roleId, Long userId);

	Boolean isInquiryVisible(Long userId, Boolean isVisible);

}
