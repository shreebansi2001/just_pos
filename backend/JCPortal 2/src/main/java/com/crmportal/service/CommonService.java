package com.crmportal.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.FollowUpDetailsResponseDto;
import com.crmportal.response.dto.UserApprovedResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.response.dto.UserUpgradedModuleMail;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public interface CommonService {

	LocalDateTime getCurrentDateTime();

	String generateRandomPassword();

	Date stringToDate(String birthDate, String dateFormat) throws ParseException;

	String getLastestEventNo(Long userId);

	LocalDate dateFormatted(String inquiryDate);

	LocalDateTime dateTimeFormatted(String date);

	String sendOtpWhatsappAuthType(String string, String contactNo, String[] dataArr);

	void sendAccountApprovalEmail(UserApprovedResponseDto responseDto) throws MessagingException, IOException;

	void sendMailForOtp(String fullName, String otp, String otp2);

	void sendMailPassowrdChangeSuccessfully(UserMasterEntity user) throws MessagingException;

	String sendWhatsappMsg(String template_id, String contactNo, List<String> dataList, String filePath, Boolean isFile,
			Long userId) throws JsonProcessingException;

	String getLastestUserCode(String name);

	void sendSubscriptionReminder(UserMasterEntity user) throws Exception;

	void sendFollowUpRemainder(List<FollowUpDetailsResponseDto> detailsResponseDtos) throws Exception;

	void sendExpiredSubscriptionNotice(UserMasterEntity user) throws Exception;

	void sendRegistrationPendingNotice(UserMasterEntity entity) throws Exception;

	void sendVerificationMail(String string, UserMasterResponseDto responseDto, List<String> dataList, String string2,
			boolean b);

	String generateLeadCode(Long unitId);

	String dateTimeFormatted(LocalDateTime date);

	Long getLong(Object val);

	String getString(Object val);

	BigDecimal getBigDecimal(Object val);

	Integer getInteger(Object val);

	Boolean getBoolean(Object val);

	Double getDouble(Object val);

	LocalDateTime dateTimeFormatted(String date, boolean isEndDate);

	void sendMailForLeadAssign(String name, String email, LeadMasterResponseDto dto);

	LocalDateTime dateTimeFormatted2(String date);

	void sendMailInvoiceReport(PartyMasterEntity party, String invoiceUrl,
			UserBasicDetailsMasterEntity basicDetailsMasterEntity) throws MessagingException;

	String generateVendorInvoiceCode(Long userId);

	void sendUserUpgradedModuleNotification(UserUpgradedModuleMail mail);

	void sendUpgradedModuleReminder(String email, String firstName, String lastName, String moduleName,
			LocalDateTime endDate, String newLink);

	void sendExpiredUpgradedModuleNotice(String email, String firstName, String lastName, String moduleName,
			String newLink);

	String generateUniqueCode();

	void sendClientCredentials(UserMasterResponseDto responseDto, String email, String password, String uniqueCode)
			throws MessagingException;

	void sendEmailWithAttachment(String email, byte[] excel);

	String sendWhatsappTemplate(String string, String mobile);

	void sendEventInfoWhatsApp(String string, String partyMobileNo, List<String> dataList, String file, boolean b,
			Long userId);

}
