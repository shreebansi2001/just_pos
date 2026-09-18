package com.crmportal.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserNotificationConfigEntity;
import com.crmportal.entity.UserPlansHistoryEntity;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserNotificationConfigRepository;
import com.crmportal.repository.UserPlansHistoryRepository;
import com.crmportal.repository.VendorPaymentRepository;
import com.crmportal.request.dto.LeadMasterResponseDto;
import com.crmportal.response.dto.FollowUpDetailsResponseDto;
import com.crmportal.response.dto.UserApprovedResponseDto;
import com.crmportal.response.dto.UserMasterResponseDto;
import com.crmportal.response.dto.UserUpgradedModuleMail;
import com.crmportal.service.CommonService;
import com.crmportal.service.UserFileService;
import com.itextpdf.io.source.ByteArrayOutputStream;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	Environment environment;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Value("${support.email}")
	private String supportEmail;

	@Value("${support.mobile}")
	private String supportMobile;

	@Autowired
	UserFileService userFileService;

	@Autowired
	UserPlansHistoryRepository userPlansHistoryRepository;

	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Autowired
	VendorPaymentRepository vendorPaymentRepository;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	UserNotificationConfigRepository userNotificationConfigRepository;

	@Override
	public LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now();
	}

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String PREFIX = "JCX";

	@Override
	public String generateRandomPassword() {
		String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		String specialCharacters = "!@#$%^&*";

		String combinedChars = upperCaseLetters + lowerCaseLetters + numbers + specialCharacters;
		Random random = new Random();
		StringBuilder password = new StringBuilder();

		// Ensure at least one character from each category
		password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
		password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
		password.append(numbers.charAt(random.nextInt(numbers.length())));
		password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

		// Fill the rest randomly
		for (int i = 4; i < 12; i++) {
			password.append(combinedChars.charAt(random.nextInt(combinedChars.length())));
		}

		// Shuffle the password
		List<Character> passwordChars = password.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
		Collections.shuffle(passwordChars);

		return passwordChars.stream().map(String::valueOf).collect(Collectors.joining());
	}

	@Override
	public Date stringToDate(String birthDate, String dateFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.parse(birthDate);
	}

	@Override
	public String getLastestEventNo(Long userId) {
		// Step 1: Get month letter prefix
		Calendar cal = Calendar.getInstance();
		String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
		String yearDigit = new SimpleDateFormat("YY").format(cal.getTime());

		String startup;
		switch (monthName.toLowerCase()) {
		case "jan":
			startup = "A";
			break;
		case "feb":
			startup = "B";
			break;
		case "mar":
			startup = "C";
			break;
		case "apr":
			startup = "D";
			break;
		case "may":
			startup = "E";
			break;
		case "jun":
			startup = "F";
			break;
		case "jul":
			startup = "G";
			break;
		case "aug":
			startup = "H";
			break;
		case "sep":
			startup = "I";
			break;
		case "sept":
			startup = "I";
			break;
		case "oct":
			startup = "J";
			break;
		case "nov":
			startup = "K";
			break;
		case "dec":
			startup = "L";
			break;
		default:
			throw new IllegalStateException("Invalid month: " + monthName);
		}

		String prefix = startup + yearDigit;
		String hisEventNo = "";
		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId).orElse(null);
		if (userMasterEntity != null) {
			hisEventNo = eventMasterRepository
					.findTopByEventNoStartingWithAndUserOrderByEventNoDesc(prefix, userMasterEntity)
					.map(EventMasterEntity::getEventNo).orElse(null);
		} else {
			hisEventNo = eventMasterRepository.findTopByEventNoStartingWithOrderByEventNoDesc(prefix)
					.map(EventMasterEntity::getEventNo).orElse(null);
		}
		// Step 2: Fetch last event number for this prefix

		int autoNo;
		if (hisEventNo == null) {
			autoNo = 1; // First event for this prefix
		} else {
			try {
				autoNo = Integer.parseInt(hisEventNo.substring(3)) + 1;
			} catch (NumberFormatException e) {
				throw new IllegalStateException("Invalid event no format in DB: " + hisEventNo, e);
			}
		}

		// Step 3: Format number with leading zeros
		String autoFormattedNumber = String.format("%04d", autoNo);

		// Step 4: Construct event number
		return prefix + autoFormattedNumber;
	}

	@Override
	public LocalDate dateFormatted(String date) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		try {
			return LocalDate.parse(date, inputFormatter);
		} catch (DateTimeParseException e) {
			throw new RuntimeException("Invalid date format: " + date);
		}
	}

	@Override
	public LocalDateTime dateTimeFormatted(String date) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		try {
			LocalDate localDate = LocalDate.parse(date, inputFormatter);
			return localDate.atStartOfDay();
		} catch (DateTimeParseException e) {
			throw new RuntimeException("Invalid date format: " + date);
		}
	}

	@Override
	public LocalDateTime dateTimeFormatted2(String date) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		try {
			LocalDate localDate = LocalDate.parse(date, inputFormatter);
			return localDate.atTime(LocalTime.MAX);
		} catch (DateTimeParseException e) {
			throw new RuntimeException("Invalid date format: " + date);
		}
	}

	@Override
	public String dateTimeFormatted(LocalDateTime date) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		try {
			return date.format(inputFormatter);
		} catch (DateTimeParseException e) {
			throw new RuntimeException("Invalid date format: " + date);
		}
	}

	@Override
	public LocalDateTime dateTimeFormatted(String date, boolean isEndDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		try {
			LocalDate localDate = LocalDate.parse(date, formatter);
			return isEndDate ? localDate.atTime(23, 59, 59) : localDate.atStartOfDay();
		} catch (DateTimeParseException e) {
			throw new RuntimeException("Invalid date format: " + date);
		}
	}

	// OTP
	public String sendOtpWhatsappAuthType(String templateId, String mob1, String[] valueArr) {
		RestTemplate restTemplate = new RestTemplate();

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("appkey", environment.getProperty("APP_KEY"));
		formData.add("authkey", environment.getProperty("AUTH_KEY"));
		String mobileNumber = mob1.startsWith("91") ? mob1 : "91" + mob1;
		formData.add("to", mobileNumber);
		formData.add("template_id", templateId);
		if (valueArr != null) {
			for (int i = 0; i < valueArr.length; i++) {
				formData.add("variables[{variableKey" + (i + 1) + "}]", valueArr[i]);
			}
		}
		formData.add("buttons[{b1_type}]", "url");
		formData.add("buttons[{b1_value}]", valueArr[0]);
		formData.add("language", "en");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

		ResponseEntity<String> response = restTemplate.exchange(environment.getProperty("API_URL"), HttpMethod.POST,
				request, String.class);

		return response.getBody();
	}

	public String sendWhatsappTemplate(String templateId, String mob1) {

		RestTemplate restTemplate = new RestTemplate();

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

		formData.add("appkey", environment.getProperty("APP_KEY"));

		formData.add("authkey", environment.getProperty("AUTH_KEY"));

		String mobileNumber = mob1.trim().replaceAll("[\\s+\\-()]", "");

		if (!mobileNumber.startsWith("91")) {
			mobileNumber = "91" + mobileNumber;
		}

		formData.add("to", mobileNumber);
		formData.add("template_id", templateId);
		formData.add("language", "en");

		// Public HTTPS URL of your template image
		formData.add("file", "https://cheeragskitchen.in/jcupload/jcx.png");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

		ResponseEntity<String> response = restTemplate.exchange(environment.getProperty("API_URL"), HttpMethod.POST,
				request, String.class);

		return response.getBody();
	}

	@Override
	public void sendMailForLeadAssign(String name, String email, LeadMasterResponseDto dto) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setTo(email);
			helper.setSubject("New Lead Assigned");

//			String htmlBody = buildLeadAssignEmailHtml(name, dto);
//			helper.setText(htmlBody, true); // true = send as HTML

			javaMailSender.send(mimeMessage);

		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send lead assignment email", e);
		}
	}

	@Override
	public void sendMailForOtp(String fullName, String email, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Web Minds Technology Pvt LTD - Password Reset OTP");

		String body = "Dear " + fullName + ",\n\n"
				+ "We received a request to reset your password. Please use the One-Time Password (OTP) below to proceed:\n\n"
				+ "OTP: " + otp + "\n\n"
				+ "This OTP is valid for the next 3 minutes. Do not share it with anyone for security reasons.\n\n"
				+ "If you did not request a password reset, please ignore this email or contact our support team immediately.\n\n"
				+ "Best Regards,\n" + "Team Web Minds Technology Pvt LTD";

		message.setText(body);
		javaMailSender.send(message);
	}

	@Async
	@Override
	public void sendAccountApprovalEmail(UserApprovedResponseDto responseDto) throws MessagingException, IOException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(responseDto.getEmail());
		helper.setSubject("🎉 Welcome to Web Minds Technology Pvt LTD - Your Account is Approved");

		String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "  <style>"
				+ "    body { font-family: Arial, sans-serif; background-color: #f4f6f9; padding: 20px; }"
				+ "    .container { max-width: 600px; margin: auto; background: #ffffff; border-radius: 10px; padding: 25px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }"
				+ "    .header { font-size: 22px; font-weight: bold; color: #0066cc; margin-bottom: 15px; text-align: center; }"
				+ "    .content { font-size: 15px; color: #333333; line-height: 1.6; }"
				+ "    .credentials { background: #eef4fb; padding: 12px; border-radius: 6px; margin: 15px 0; font-size: 14px; }"
				+ "    .credentials b { color: #0066cc; }"
				+ "    .footer { margin-top: 25px; font-size: 12px; color: #888888; text-align: center; }"
				+ "  </style>" + "</head>" + "<body>" + "  <div class='container'>"
				+ "    <div class='header'>Welcome to Web Minds Technology Pvt LTD 🎊</div>"
				+ "    <div class='content'>" + "      Dear " + responseDto.getFirstName() + " "
				+ responseDto.getLastName() + ",<br><br>"
				+ "      Congratulations! Your account has been <b>approved successfully</b>.<br><br>"
				+ "      Regards,<br>" + "      <b>Team Web Minds Technology Pvt LTD</b>" + "    </div>"
				+ "    <div class='footer'>"
				+ "      This is an automated message. Please do not reply directly to this email." + "    </div>"
				+ "  </div>" + "</body>" + "</html>";

		helper.setText(htmlContent, true);

		javaMailSender.send(mimeMessage);
	}

	@Override
	public void sendMailPassowrdChangeSuccessfully(UserMasterEntity user) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		helper.setTo(user.getEmail());
		helper.setSubject("Web Minds Technology Pvt LTD - Password Changed Successfully");

		String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "  <style>"
				+ "    body { font-family: Arial, sans-serif; color: #333; }"
				+ "    .container { padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background: #fafafa; }"
				+ "    .header { font-size: 18px; font-weight: bold; margin-bottom: 15px; color: #0066cc; }"
				+ "    .content { font-size: 14px; line-height: 1.6; }"
				+ "    .footer { margin-top: 20px; font-size: 12px; color: #888; }" + "  </style>" + "</head>"
				+ "<body>" + "  <div class='container'>" + "    <div class='header'>Password Changed Successfully</div>"
				+ "    <div class='content'>" + "      Dear " + user.getFirstName() + " " + user.getLastName()
				+ ",<br><br>" + "      Your account password has been changed successfully.<br><br>"
				+ "      If you made this change, no further action is required.<br><br>"
				+ "      <b>For your security:</b><br>" + "      - Do not share your password with anyone.<br>"
				+ "      - If you did not request this change, please reset your password immediately or contact our support team.<br><br>"
				+ "      Thank you for using <b>Web Minds Technology Pvt LTD</b> services." + "    </div>"
				+ "    <div class='footer'>"
				+ "      This is an automated message. Please do not reply directly to this email." + "    </div>"
				+ "  </div>" + "</body>" + "</html>";

		helper.setText(htmlContent, true);
		javaMailSender.send(mimeMessage);
	}

	@Override
	public void sendMailInvoiceReport(PartyMasterEntity party, String invoiceUrl,
			UserBasicDetailsMasterEntity basicDetailsMasterEntity) throws MessagingException {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		helper.setTo(party.getEmail());
		helper.setSubject(basicDetailsMasterEntity.getCompanyName() + " - Your Invoice is Ready");

		String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>"
				+ "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>" + "<style>"
				+ "body { margin:0; padding:0; font-family: Arial, sans-serif; background-color:#f4f6f9; }"
				+ ".container { max-width:600px; margin:20px auto; background:#ffffff; padding:20px; "
				+ "border-radius:8px; box-shadow:0 4px 8px rgba(0,0,0,0.05); }"
				+ ".header { text-align:center; font-size:20px; font-weight:bold; color:#0066cc; margin-bottom:20px; }"
				+ ".content { font-size:14px; color:#333333; line-height:1.6; }"
				+ ".btn { display:inline-block; padding:12px 20px; margin-top:20px; "
				+ "background-color:#0066cc; color:#ffffff; text-decoration:none; "
				+ "border-radius:5px; font-weight:bold; }"
				+ ".footer { margin-top:30px; font-size:12px; text-align:center; color:#888888; }"
				+ "@media screen and (max-width: 600px) {" + "  .container { padding:15px; }"
				+ "  .header { font-size:18px; }" + "}" + "</style>" + "</head>" + "<body>" + "<div class='container'>"
				+ "<div class='header'>Invoice Available</div>" + "<div class='content'>" + "Dear "
				+ party.getNameEnglish() + ",<br><br>" + "Your invoice has been generated successfully.<br><br>"
				+ "You can view or download your invoice by clicking the button below:" + "<br><br>" + "<center>"
				+ "<table align='center' cellspacing='0' cellpadding='0'>" + "<tr>"
				+ "<td align='center' bgcolor='#0066cc' style='border-radius:5px;'>" + "<a href='" + invoiceUrl
				+ "' target='_blank' " + "style='display:inline-block; padding:12px 20px; "
				+ "font-size:14px; color:#ffffff !important; " + "text-decoration:none; font-weight:bold;'>"
				+ "View Invoice</a>" + "</td>" + "</tr>" + "</table>" + "</center>" + "<br>"
				+ "If the button does not work, copy and paste the link below into your browser:<br>" + "<a href='"
				+ invoiceUrl + "'>" + invoiceUrl + "</a>" + "<br><br>" + "Thank you for choosing <b>"
				+ basicDetailsMasterEntity.getCompanyName() + "</b>." + "</div>" + "<div class='footer'>"
				+ "This is an automated email. Please do not reply directly to this message." + "</div>" + "</div>"
				+ "</body>" + "</html>";

		helper.setText(htmlContent, true);
		javaMailSender.send(mimeMessage);
	}

//	@Override
//	public String sendWhatsappMsg(String templateId, String mob1, List<String> valueArr, String filePath,
//			Boolean isFile, Long userId) {
//		if (!mob1.startsWith("91")) {
//			mob1 = "91" + mob1;
//		}
//		String appKey = environment.getProperty("APP_KEY");
//		String authKey = environment.getProperty("AUTH_KEY");
//		String appUrl = environment.getProperty("API_URL");
////		if (userId != -1) {
////			UserNotificationConfigEntity dto = userNotificationConfigRepository
////					.findNotificationByUserAndUpgradeModule(userId, 3L);
////			if (dto != null) {
////				appKey = dto.getKey1();
////				authKey = dto.getKey2();
////				appUrl = dto.getUrl();
////			}
////		}
//
//		RestTemplate restTemplate = new RestTemplate();
//		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
//		formData.add("appkey", appKey);
//		formData.add("authkey", authKey);
//		formData.add("to", mob1);
//		formData.add("template_id", templateId);
//		formData.add("language", "en");
//
//		for (int i = 0; i < valueArr.size(); i++) {
//			formData.add("variables[" + ("variableKey" + (i + 1)) + "]", valueArr.get(i));
//		}
//
//		if (isFile && filePath != null) {
//
//		    Resource fileResource;
//
//		    if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
//		        RestTemplate downloadRestTemplate = new RestTemplate();
//
//		        ResponseEntity<byte[]> pdfResponse = downloadRestTemplate.getForEntity(
//		                filePath,
//		                byte[].class
//		        );
//
//		        if (!pdfResponse.getStatusCode().is2xxSuccessful()
//		                || pdfResponse.getBody() == null) {
//		            throw new RuntimeException("Unable to download PDF from URL: " + filePath);
//		        }
//
//		        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
//
//		        ByteArrayResource byteResource = new ByteArrayResource(pdfResponse.getBody()) {
//		            @Override
//		            public String getFilename() {
//		                return fileName;
//		            }
//		        };
//
//		        fileResource = byteResource;
//
//		    } else {
//		        fileResource = new FileSystemResource(new File(filePath));
//		    }
//
//		    formData.add("file", fileResource);
//		}
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(formData, headers);
//
//		ResponseEntity<String> response = restTemplate.exchange(appUrl, HttpMethod.POST, request, String.class);
//
//		return response.getBody();
//	}
	private final OkHttpClient client = new OkHttpClient();

	@Override
	public String sendWhatsappMsg(String templateId, String mob1, List<String> valueArr, String fileUrl, Boolean isFile,
			Long userId) {

		String APP_KEY = environment.getProperty("APP_KEY");
		String AUTH_KEY = environment.getProperty("AUTH_KEY");
		String API_URL = environment.getProperty("API_URL");

		if (userId != -1) {
			UserNotificationConfigEntity dto = userNotificationConfigRepository
					.findNotificationByUserAndUpgradeModule(userId, 3L);

			if (dto != null) {
				APP_KEY = dto.getKey1();
				AUTH_KEY = dto.getKey2();
				API_URL = dto.getUrl();
			}
		}

		if (mob1 == null || mob1.trim().isEmpty()) {
			throw new RuntimeException("Mobile number is required");
		}

		mob1 = mob1.trim();

		if (!mob1.startsWith("91")) {
			mob1 = "91" + mob1;
		}

		MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("appkey", APP_KEY).addFormDataPart("authkey", AUTH_KEY).addFormDataPart("to", mob1)
				.addFormDataPart("template_id", templateId).addFormDataPart("language", "en");

		if (valueArr != null) {
			for (int i = 0; i < valueArr.size(); i++) {
				bodyBuilder.addFormDataPart("variables[{variableKey" + (i + 1) + "}]", valueArr.get(i));
			}
		}

		if (Boolean.TRUE.equals(isFile) && fileUrl != null && !fileUrl.isEmpty()) {
			bodyBuilder.addFormDataPart("file", fileUrl);
		}

		Request request = new Request.Builder().url(API_URL).post(bodyBuilder.build()).build();

		try (Response response = client.newCall(request).execute()) {

			String responseBody = response.body() != null ? response.body().string() : "";

			System.out.println("Response: " + response.code() + " - " + responseBody);

			return responseBody;

		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public String getLastestUserCode(String name) {
		String typeCode;
		switch (name.toLowerCase()) {
		case "lite":
			typeCode = "L";
			break;
		case "pro":
			typeCode = "P";
			break;
		case "e-lite":
			typeCode = "E";
			break;
		case "demo":
			typeCode = "D";
			break;
		case "onboarding":
			typeCode = "O";
			break;
		default:
			throw new IllegalArgumentException("Invalid user type: " + name);
		}

		String year = String.valueOf(java.time.Year.now().getValue()).substring(2);

		Optional<UserMasterEntity> lastUserOptional = userMasterRepository.findLastUserCode(typeCode);

		int nextCount = 1;
		if (lastUserOptional.isPresent() && lastUserOptional.get().getUserCode() != null) {
			String lastCode = lastUserOptional.get().getUserCode();
			String countStr = lastCode.substring(lastCode.length() - 4);
			nextCount = Integer.parseInt(countStr) + 1;
		}

		String formattedCount = String.format("%04d", nextCount);

		return "JC" + typeCode + year + formattedCount;
	}

	@Override
	public void sendFollowUpRemainder(List<FollowUpDetailsResponseDto> followUps) throws Exception {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(supportEmail);
		helper.setSubject("📅 Follow Up Reminder");

		String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" + "<h2>🔔 Follow-Up Reminder</h2>"
				+ "<p>Dear Super Admin,</p>"
				+ "<p>Please find attached the list of pending follow-ups requiring attention.</p>"
				+ "<p><b>Total Follow-Ups:</b> " + followUps.size() + "</p>"
				+ "<p>Kindly review and take necessary action.</p>" + "<br>" + "<p>Regards,<br><b>CRM System</b></p>"
				+ "</body></html>";

		byte[] excelStream = createFollowUpExcel(followUps);

		helper.addAttachment("Follow_Up_Remainder_List.xlsx", new ByteArrayResource(excelStream));

		helper.setText(htmlContent, true);
		javaMailSender.send(mimeMessage);

	}

	public void sendSubscriptionReminder(UserMasterEntity user) throws Exception {
//		sendEmailReminder(user);
	}

	private void sendEmailReminder(UserMasterEntity user) throws Exception {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		String renewalLink = "https://app.justcatering.in/price";
		helper.setTo(user.getEmail());
		helper.setSubject("⏳ Subscription Expiry Reminder");
		UserPlansHistoryEntity historyEntity = userPlansHistoryRepository.findByUserAndIsActiveTrue(user)
				.orElseThrow(() -> new RuntimeException("User Plan not Active"));
		String expiryDate = historyEntity.getEndDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
		String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>"
				+ "body { font-family: Arial, sans-serif; line-height: 1.6; }"
				+ ".container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 8px; }"
				+ ".btn { display: inline-block; background: #28a745; color: #fff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; }"
				+ ".highlight { color: #d9534f; font-weight: bold; }" + "</style>" + "</head>" + "<body>"
				+ "<div class='container'>" + "<h2>⏳ Subscription Expiry Reminder</h2>" + "<p>Dear <b>"
				+ user.getFirstName() + " " + user.getLastName() + "</b>,</p>"
				+ "<p>We hope you’ve been enjoying our services. This is a friendly reminder that your subscription is scheduled to "
				+ "<span class='highlight'>expire on " + expiryDate + "</span>.</p>"
				+ "<p>To ensure uninterrupted access to all features, please renew your subscription before the expiry date.</p>"
				+ "<p><a href='" + renewalLink + "' class='btn'>Renew Subscription</a></p>" + "<br>"
				+ "<p>If you face any issues or need assistance with renewal, please contact our support team at "
				+ "<a href='mailto:" + supportEmail + "'>" + supportEmail + "</a> or call us at " + "<a href='tel:"
				+ supportMobile + "'>" + supportMobile + "</a>.</p>" + "<br>"
				+ "<p>Thank you for choosing <b>Web Minds Technology Pvt LTD</b>. We look forward to serving you.</p>"
				+ "<p style='font-size: 12px; color: #666;'>If you have already renewed, please ignore this message.</p>"
				+ "</div>" + "</body>" + "</html>";

		helper.setText(htmlContent, true);
		javaMailSender.send(mimeMessage);
	}

	public void sendExpiredSubscriptionNotice(UserMasterEntity user) throws Exception {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(user.getEmail());
		helper.setSubject("⚠️ Your Subscription Has Expired");

		String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>"
				+ "body { font-family: Arial, sans-serif; line-height: 1.6; }"
				+ ".container { max-width: 600px; margin: auto; padding: 20px; "
				+ "border: 1px solid #eee; border-radius: 8px; }"
				+ ".btn { display: inline-block; background: #007bff; color: #fff; "
				+ "padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; }"
				+ ".highlight { color: #d9534f; font-weight: bold; }" + "</style>" + "</head>" + "<body>"
				+ "<div class='container'>" + "<h2>⚠️ Subscription Expired</h2>" + "<p>Dear <b>" + user.getFirstName()
				+ " " + user.getLastName() + "</b>,</p>"
				+ "<p>We noticed that your subscription has <span class='highlight'>expired</span> "
				+ "and your account has been temporarily <span class='highlight'>deactivated</span>.</p>"
				+ "<p>To regain access to all features and continue using our services, please contact our "
				+ "<b>support team</b> to renew or upgrade your subscription.</p>" + "<p><a href='mailto:"
				+ supportEmail + "' class='btn'>Contact Support Team</a></p>"
				+ "<p style='margin-top:10px; font-size:14px;'>📞 Support Mobile: " + "<a href='tel:" + supportMobile
				+ "'>" + supportMobile + "</a></p>" + "<br>"
				+ "<p>Thank you for being a valued member of <b>Web Minds Technology Pvt LTD</b>. "
				+ "We look forward to welcoming you back.</p>"
				+ "<p style='font-size: 12px; color: #666;'>If you have already renewed your subscription, "
				+ "please ignore this message.</p>" + "</div>" + "</body>" + "</html>";

		helper.setText(htmlContent, true);
		javaMailSender.send(mimeMessage);
	}

	public void sendRegistrationPendingNotice(UserMasterEntity user) throws Exception {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(user.getEmail());
		helper.setSubject("✅ Registration Successful – Awaiting Approval");

		String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>"
				+ "body { font-family: Arial, sans-serif; line-height: 1.6; }"
				+ ".container { max-width: 600px; margin: auto; padding: 20px; "
				+ "border: 1px solid #eee; border-radius: 8px; }" + ".highlight { color: #007bff; font-weight: bold; }"
				+ "</style>" + "</head>" + "<body>" + "<div class='container'>"
				+ "<h2>👋 Welcome to Web Minds Technology Pvt LTD</h2>" + "<p>Dear <b>" + user.getFirstName() + " "
				+ user.getLastName() + "</b>,</p>"
				+ "<p>Thank you for registering with <b>Web Minds Technology Pvt LTD</b>. "
				+ "Your registration was <span class='highlight'>successful</span> and is now under review by our team.</p>"
				+ "<p>Once your account is approved, you will receive your login details via email.</p>" + "<br>"
				+ "<p>We truly appreciate your patience and look forward to welcoming you on board.</p>"
				+ "<p style='margin-top:20px;'>Best regards,<br><b>Web Minds Technology Pvt LTD</b></p>" + "</div>"
				+ "</body>" + "</html>";

		helper.setText(htmlContent, true);
		javaMailSender.send(mimeMessage);
	}

	@Override
	public void sendVerificationMail(String string, UserMasterResponseDto responseDto, List<String> dataList,
			String string2, boolean b) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setTo(environment.getProperty("support.email"));
			helper.setSubject("New User Verification Mail");

			String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<meta charset='UTF-8'>"
					+ "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" + "<style>"
					+ "* { margin: 0; padding: 0; box-sizing: border-box; }"
					+ "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; "
					+ "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); "
					+ "padding: 40px 20px; min-height: 100vh; }"

					+ ".email-wrapper { max-width: 600px; margin: 0 auto; }"

					+ ".email-container { background: #ffffff; border-radius: 16px; "
					+ "overflow: hidden; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }"

					+ ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); "
					+ "padding: 40px 30px; text-align: center; position: relative; }"
					+ ".header::after { content: ''; position: absolute; bottom: -20px; left: 0; right: 0; "
					+ "height: 40px; background: #ffffff; border-radius: 50% 50% 0 0 / 20px 20px 0 0; }"

					+ ".header-icon { width: 80px; height: 80px; background: rgba(255,255,255,0.2); "
					+ "border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; "
					+ "font-size: 40px; margin-bottom: 15px; backdrop-filter: blur(10px); "
					+ "border: 3px solid rgba(255,255,255,0.3); }"

					+ ".header-title { color: #ffffff; font-size: 28px; font-weight: 700; "
					+ "text-shadow: 0 2px 4px rgba(0,0,0,0.1); }"

					+ ".content { padding: 50px 40px 30px; }"

					+ ".greeting { font-size: 18px; color: #333; margin-bottom: 20px; }"
					+ ".greeting strong { color: #667eea; }"

					+ ".message { font-size: 15px; color: #555; line-height: 1.8; margin-bottom: 30px; }"

					+ ".user-card { background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); "
					+ "border-radius: 12px; padding: 25px; margin: 25px 0; "
					+ "border-left: 5px solid #667eea; position: relative; overflow: hidden; }"
					+ ".user-card::before { content: ''; position: absolute; top: -50%; right: -50%; "
					+ "width: 200px; height: 200px; background: rgba(255,255,255,0.1); " + "border-radius: 50%; }"

					+ ".user-card-title { font-size: 18px; font-weight: 700; color: #333; "
					+ "margin-bottom: 20px; display: flex; align-items: center; gap: 10px; }"
					+ ".user-card-title::before { content: '👤'; font-size: 24px; }"

					+ ".info-row { display: flex; padding: 12px 0; border-bottom: 1px solid rgba(0,0,0,0.05); }"
					+ ".info-row:last-child { border-bottom: none; }"
					+ ".info-label { font-weight: 600; color: #333; min-width: 140px; font-size: 14px; }"
					+ ".info-value { color: #333; font-size: 14px; flex: 1; word-break: break-word; }"

					+ ".action-box { background: #fff3cd; border-left: 4px solid #ffc107; "
					+ "padding: 20px; border-radius: 8px; margin: 25px 0; }"
					+ ".action-box p { color: #856404; font-size: 14px; margin: 0; line-height: 1.6; }"

					+ ".cta-button { display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); "
					+ "color: #ffffff; text-decoration: none; padding: 15px 40px; border-radius: 50px; "
					+ "font-weight: 600; font-size: 16px; margin: 20px 0; "
					+ "box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4); "
					+ "transition: transform 0.3s ease; display: inline-block; }"

					+ ".signature { margin-top: 30px; padding-top: 20px; border-top: 2px solid #f0f0f0; }"
					+ ".signature p { color: #555; font-size: 15px; line-height: 1.8; }"
					+ ".company-name { color: #667eea; font-weight: 700; font-size: 16px; }"

					+ ".footer { background: #f8f9fa; padding: 25px; text-align: center; }"
					+ ".footer p { color: #999; font-size: 13px; line-height: 1.6; }"
					+ ".footer-links { margin-top: 15px; }"
					+ ".footer-links a { color: #667eea; text-decoration: none; margin: 0 10px; font-size: 12px; }"

					+ "@media only screen and (max-width: 600px) {" + ".content { padding: 30px 20px; }"
					+ ".header { padding: 30px 20px; }" + ".info-row { flex-direction: column; }"
					+ ".info-label { margin-bottom: 5px; }" + "}" + "</style>" + "</head>" + "<body>"

					+ "<div class='email-wrapper'>" + "<div class='email-container'>"

					+ "<div class='header'>" + "<div class='header-icon'>🎉</div>"
					+ "<h1 class='header-title'>New User Registration</h1>" + "</div>"

					+ "<div class='content'>" + "<p class='greeting'>Hello <strong>Super Admin</strong>,</p>"

					+ "<p class='message'>"
					+ "Great news! A new user has just registered on the <strong>Web Minds Technology Pvt LTD</strong>. "
					+ "This account is pending your review and approval to gain full access to the system." + "</p>"

					+ "<div class='user-card'>" + "<div class='user-card-title'>User Details</div>"
					+ "<div class='info-row'>" + "<span class='info-label'>👤 Full Name:</span>"
					+ "<span class='info-value'>" + responseDto.getFirstName() + " " + responseDto.getLastName()
					+ "</span>" + "</div>" + "<div class='info-row'>"
					+ "<span class='info-label'>📧 Email Address:</span>" + "<span class='info-value'>"
					+ responseDto.getEmail() + "</span>" + "</div>" + "<div class='info-row'>"
					+ "<span class='info-label'>📱 Mobile Number:</span>" + "<span class='info-value'>"
					+ responseDto.getContactNo() + "</span>" + "</div>" + "<div class='info-row'>"
					+ "<span class='info-label'>📅 Registration Date:</span>" + "<span class='info-value'>"
					+ LocalDate.now() + "</span>" + "</div>" + "</div>"

					+ "<div class='action-box'>"
					+ "<p>⚠️ <strong>Action Required:</strong> Please log in to the Super Admin Panel to review this user's information and approve their account to complete the registration process.</p>"
					+ "</div>"

					+ "<div style='text-align: center;'>" + "<a href='#' class='cta-button'>Review & Approve User</a>"
					+ "</div>"

					+ "<div class='signature'>" + "<p>Best regards,<br>"
					+ "<span class='company-name'>Web Minds Technology Pvt LTD</span><br>"
					+ "<small style='color: #999;'>Automated Notification Service</small></p>" + "</div>" + "</div>"

					+ "<div class='footer'>"
					+ "<p>🔒 This is an automated notification. Please do not reply to this email.</p>"
					+ "<p style='margin-top: 10px;'>© 2024 Web Minds Technology Pvt LTD. All rights reserved.</p>"
					+ "<div class='footer-links'>" + "<a href='#'>Privacy Policy</a> | "
					+ "<a href='#'>Terms of Service</a> | " + "<a href='#'>Contact Support</a>" + "</div>" + "</div>"

					+ "</div>" + "</div>"

					+ "</body>" + "</html>";

			helper.setText(htmlContent, true);
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String generateLeadCode(Long userId) {

		Calendar cal = Calendar.getInstance();
		String yearDigit = new SimpleDateFormat("YY").format(cal.getTime());

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		String lastInsertedLeadCode = leadMasterRepository.findLastInsertedLeadCode(userId, yearDigit);
		Integer newcode = 1;

		if (lastInsertedLeadCode != null) {
			newcode = Integer.parseInt(lastInsertedLeadCode.substring(lastInsertedLeadCode.length() - 5)) + 1;
		}

		String newcodeString = String.format("%05d", newcode);
		String id = String.format("%02d", userId);
		StringBuilder partycode = new StringBuilder("L");

		Optional<UserBasicDetailsMasterEntity> userBasicOptional = userBasicDetailsMasterRepository
				.findByUserAndIsDeleteFalse(user);
		String[] compnayDetail = userBasicOptional.get().getCompanyName().split(" ");

		for (String ch : compnayDetail) {
			partycode.append(ch.charAt(0));
		}

		partycode.append(yearDigit);
		partycode.append(newcodeString);
		return partycode.toString();
	}

	private byte[] createFollowUpExcel(List<FollowUpDetailsResponseDto> followUps) throws IOException {

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Follow Ups");

		// ✅ Header style
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);

		// ✅ Header Row
		Row headerRow = sheet.createRow(0);
		String[] headers = { "Follow-Up ID", "Follow-Up Type", "Follow-Up Date", "Client Remarks", "Employee Remarks" };

		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		// ✅ Data Rows
		int rowIdx = 1;
		for (FollowUpDetailsResponseDto f : followUps) {
			Row row = sheet.createRow(rowIdx++);

			row.createCell(0).setCellValue(f.getId());
			row.createCell(1).setCellValue(f.getFollowUpType());
			row.createCell(2).setCellValue(f.getFollowUpDate());
			row.createCell(3).setCellValue(f.getClientRemarks() != null ? f.getClientRemarks() : "");
			row.createCell(4).setCellValue(f.getEmployeeRemarks() != null ? f.getEmployeeRemarks() : "");
		}

		// ✅ Auto size columns
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		workbook.close();

		return out.toByteArray();
	}

	@Override
	public Long getLong(Object val) {
		if (val == null)
			return null;
		if (val instanceof Number)
			return ((Number) val).longValue();
		return Long.parseLong(val.toString());
	}

	@Override
	public String getString(Object val) {
		return val != null ? val.toString() : null;
	}

	@Override
	public BigDecimal getBigDecimal(Object val) {
		if (val == null)
			return BigDecimal.ZERO;
		if (val instanceof BigDecimal)
			return (BigDecimal) val;
		return new BigDecimal(val.toString());
	}

	@Override
	public Integer getInteger(Object val) {
		if (val == null)
			return null;
		if (val instanceof Number)
			return ((Number) val).intValue();
		if (val instanceof Integer)
			return ((Number) val).intValue();
		return Integer.parseInt(val.toString());
	}

	@Override
	public Double getDouble(Object val) {
		if (val == null)
			return null;
		if (val instanceof Double)
			return ((Double) val).doubleValue();
		return Double.parseDouble(val.toString());
	}

	@Override
	public Boolean getBoolean(Object val) {
		if (val == null)
			return null;
		if (val instanceof Boolean)
			return (Boolean) val;
		if (val instanceof Number)
			return ((Number) val).intValue() == 1;
		return Boolean.parseBoolean(val.toString());
	}

	private String buildLeadAssignEmailHtml(String name, LeadMasterResponseDto dto) {
		String emailId = (dto.getEmailId() != null && !dto.getEmailId().isEmpty()) ? dto.getEmailId() : "Not Available";

		return "<!DOCTYPE html>" + "<html lang='en'><head><meta charset='UTF-8'/></head>"
				+ "<body style='margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif;'>"
				+ "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f4f4f4;padding:30px 0;'>"
				+ "<tr><td align='center'>"
				+ "<table width='500' cellpadding='0' cellspacing='0' style='background-color:#1e1e1e;border-radius:10px;overflow:hidden;'>"
				+

				// Logo
				"<tr><td align='center' style='padding:25px 30px 15px 30px;'>"
				+ "<table cellpadding='0' cellspacing='0'><tr>" + "<td style='padding-right:10px;'>"
				+ "<svg width='50' height='45' viewBox='0 0 50 45' xmlns='http://www.w3.org/2000/svg'>"
				+ "<polygon points='25,2 48,43 2,43' fill='none' stroke='#4285F4' stroke-width='4'/>"
				+ "<polygon points='25,12 40,38 10,38' fill='#FBBC05'/>"
				+ "<circle cx='25' cy='28' r='5' fill='#34A853'/>"
				+ "<line x1='25' y1='2' x2='48' y2='43' stroke='#EA4335' stroke-width='3'/>" + "</svg></td>"
				+ "<td style='border-left:3px solid #EA4335;padding-left:10px;line-height:1.1;'>"
				+ "<span style='color:#4285F4;font-size:18px;font-weight:bold;letter-spacing:1px;'>AUTOMATE</span><br/>"
				+ "<span style='color:#FBBC05;font-size:18px;font-weight:bold;letter-spacing:1px;'>BUSINESS</span>"
				+ "</td></tr></table></td></tr>" +

				// Green Banner
				"<tr><td style='padding:0 30px 20px 30px;'>"
				+ "<div style='background-color:#22c55e;border-radius:6px;text-align:center;padding:14px 0;'>"
				+ "<span style='color:#ffffff;font-size:22px;font-weight:bold;'>New Lead Assigned</span>"
				+ "</div></td></tr>" +

				// Body
				"<tr><td style='padding:0 30px 25px 30px;color:#cccccc;font-size:15px;line-height:1.6;'>"
				+ "<p style='margin:0 0 15px 0;'>Hi <span style='color:#22c55e;font-weight:bold;'>" + name
				+ "</span>,</p>"
				+ "<p style='margin:0 0 20px 0;'>A new lead has been assigned to you. Please check and connect with the client as soon as possible. Below are the details of the lead:</p>"
				+

				// Lead Details Table
				"<table cellpadding='7' cellspacing='0' width='100%'>"
				+ "<tr><td style='color:#ffffff;font-weight:bold;width:45%;'>Lead ID:</td>"
				+ "<td style='color:#cccccc;'>" + dto.getLeadCode() + "</td></tr>"
				+ "<tr><td style='color:#ffffff;font-weight:bold;'>Company Name:</td>" + "<td style='color:#cccccc;'>"
				+ dto.getCompanyName() + "</td></tr>"
				+ "<tr><td style='color:#ffffff;font-weight:bold;'>Contact Name:</td>" + "<td style='color:#cccccc;'>"
				+ dto.getClientName() + "</td></tr>"
				+ "<tr><td style='color:#ffffff;font-weight:bold;'>Contact Number:</td>" + "<td style='color:#cccccc;'>"
				+ dto.getContactNumber() + "</td></tr>" + "<tr><td style='color:#ffffff;font-weight:bold;'>Email:</td>"
				+ "<td style='color:#cccccc;'>" + emailId + "</td></tr>"
				+ "<tr><td style='color:#ffffff;font-weight:bold;'>Source:</td>" + "<td style='color:#cccccc;'>"
				+ dto.getLeadSource() + "</td></tr>" + "</table>" +

				// CRM Button
				"<div style='margin-top:25px;'>"
				+ "<a href='https://app.justcatering.in/auth/login' style='display:inline-block;background-color:#22c55e;color:#ffffff;"
				+ "text-decoration:none;padding:12px 28px;border-radius:6px;font-size:15px;font-weight:bold;'>Open CRM App</a>"
				+ "</div></td></tr>" +

				// Footer
				"<tr><td style='padding:15px 30px 25px 30px;border-top:1px solid #333333;text-align:center;color:#888888;font-size:13px;'>"
				+ "This is an automated notification. Please do not reply." + "</td></tr>" +

				"</table></td></tr></table></body></html>";
	}

	@Override
	public String generateVendorInvoiceCode(Long userId) {

		String prefix = "V";

		String year = String.valueOf(LocalDate.now().getYear()).substring(2);

		String lastInvoiceCode = vendorPaymentRepository.findTopByUserIdOrderByIdDesc(userId);

		int nextSequence = 1;

		if (lastInvoiceCode != null && lastInvoiceCode.length() >= 3) {
			String lastSeq = lastInvoiceCode.substring(3);
			nextSequence = Integer.parseInt(lastSeq) + 1;
		}

		String sequence = String.format("%05d", nextSequence);

		return prefix + year + sequence;
	}

	@Override
	public void sendUserUpgradedModuleNotification(UserUpgradedModuleMail mail) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setTo(mail.getEmail());
			helper.setSubject("Web Minds Technology Pvt LTD - Your Module is Actived");

			String html = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "  <style>\n"
					+ "    body { font-family: Arial, sans-serif; background-color: #f4f6f9; padding: 20px; }\n"
					+ "    .container { max-width: 600px; margin: auto; background: #ffffff; border-radius: 10px; padding: 25px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }\n"
					+ "    .header { font-size: 22px; font-weight: bold; color: #28a745; margin-bottom: 15px; text-align: center; }\n"
					+ "    .content { font-size: 15px; color: #333; line-height: 1.6; }\n"
					+ "    .highlight { background: #eef9f1; padding: 12px; border-radius: 6px; margin: 15px 0; }\n"
					+ "    .footer { margin-top: 25px; font-size: 12px; color: #888; text-align: center; }\n"
					+ "  </style>\n" + "</head>\n" + "<body>\n" + "  <div class='container'>\n"
					+ "    <div class='header'>🎉 Module Activated Successfully</div>\n" + "    \n"
					+ "    <div class='content'>\n" + "      Dear " + mail.getUserName() + ",<br><br>\n" + "\n"
					+ "      Your <b>" + mail.getModuleName()
					+ "</b> module has been <b>successfully activated</b>.<br><br>\n" + "\n"
					+ "      <div class='highlight'>\n" + "        <b>Module:</b> {{ModuleName}}<br>\n"
					+ "        <b>Billing Cycle:</b>" + mail.getBillingCycle() + "\n  </div>\n" + "\n"
					+ "      You can now start using all the features included in this module.<br><br>\n" + "\n"
					+ "      Regards,<br>\n" + "      <b>Team Web Minds Technology Pvt LTD</b>\n" + "    </div>\n"
					+ "\n" + "    <div class='footer'>\n" + "      This is an automated message. Please do not reply.\n"
					+ "    </div>\n" + "  </div>\n" + "</body>\n" + "</html>";
			helper.setText(html, true);
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendUpgradedModuleReminder(String email, String firstName, String lastName, String moduleName,
			LocalDateTime endDate, String newLink) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setTo(email);
			helper.setSubject("⏳ Reminder: Your Module is Expiring Soon");

			String formattedDate = endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
			String renewalLink = newLink;

			String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>"
					+ "body { font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 0; }"
					+ ".container { max-width: 600px; margin: 40px auto; background: #ffffff; padding: 25px; border-radius: 10px; border: 1px solid #e0e0e0; }"
					+ ".header { text-align: center; color: #333; }"
					+ ".btn { display: inline-block; background: #28a745; color: #fff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; }"
					+ ".highlight { color: #ff9800; font-weight: bold; }"
					+ ".footer { font-size: 12px; color: #777; margin-top: 20px; }" + "</style>" + "</head>" + "<body>"
					+

					"<div class='container'>" +

					"<h2 class='header'>⏳ Module Expiry Reminder</h2>" +

					"<p>Dear <b>" + firstName + " " + lastName + "</b>,</p>" +

					"<p>This is a reminder that your module <b>\"" + moduleName + "\"</b> is scheduled to expire on "
					+ "<span class='highlight'>" + formattedDate + "</span>.</p>" +

					"<p>To continue using this module without interruption, please renew it before the expiry date.</p>"
					+

					"<p style='text-align:center; margin: 25px 0;'>" + "<a href='" + renewalLink
					+ "' class='btn'>Renew Module</a>" + "</p>" +

					"<p>If you need any assistance, feel free to contact our support team.</p>" +

					"<p>" + "📧 <a href='mailto:" + supportEmail + "'>" + supportEmail + "</a><br>" + "📞 <a href='tel:"
					+ supportMobile + "'>" + supportMobile + "</a>" + "</p>" +

					"<p>Thank you for choosing <b>Web Minds Technology Pvt LTD</b>.</p>" +

					"<p class='footer'>If you have already renewed, please ignore this email.</p>" +

					"</div>" +

					"</body>" + "</html>";

			helper.setText(htmlContent, true);
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendExpiredUpgradedModuleNotice(String email, String firstName, String lastName, String moduleName,
			String newLink) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setTo(email);
			helper.setSubject("⚠️ Module Access Expired");

			String renewalLink = newLink;

			String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>"
					+ "body { font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 0; }"
					+ ".container { max-width: 600px; margin: 40px auto; background: #ffffff; padding: 25px; border-radius: 10px; border: 1px solid #e0e0e0; }"
					+ ".header { text-align: center; color: #d9534f; }"
					+ ".btn { display: inline-block; background: #007bff; color: #fff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; }"
					+ ".highlight { color: #d9534f; font-weight: bold; }"
					+ ".footer { font-size: 12px; color: #777; margin-top: 20px; }" + "</style>" + "</head>" + "<body>"
					+

					"<div class='container'>" +

					"<h2 class='header'>⚠️ Module Expired</h2>" +

					"<p>Dear <b>" + firstName + " " + lastName + "</b>,</p>" +

					"<p>Your access to the module <b>\"" + moduleName + "\"</b> has "
					+ "<span class='highlight'>expired</span>.</p>" +

					"<p>This module is now temporarily unavailable in your account.</p>" +

					"<p>To restore access, please renew the module.</p>" +

					"<p style='text-align:center; margin: 25px 0;'>" + "<a href='" + renewalLink
					+ "' class='btn'>Renew Module</a>" + "</p>" +

					"<p>If you need help, our support team is here for you:</p>" +

					"<p>" + "📧 <a href='mailto:" + supportEmail + "'>" + supportEmail + "</a><br>" + "📞 <a href='tel:"
					+ supportMobile + "'>" + supportMobile + "</a>" + "</p>" +

					"<p>We value your association with <b>Web Minds Technology Pvt LTD</b> and hope to serve you again soon.</p>"
					+

					"<p class='footer'>If you have already renewed, please ignore this message.</p>" +

					"</div>" +

					"</body>" + "</html>";

			helper.setText(htmlContent, true);
			javaMailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String generateUniqueCode() {
		String code;

		do {
			int number = 100000 + RANDOM.nextInt(900000);
			code = PREFIX + number;
		} while (userMasterRepository.existsByUniqueCode(code));

		return code;
	}

	@Override
	public void sendClientCredentials(UserMasterResponseDto user, String email, String password, String uniqueCode)
			throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(email);
		helper.setSubject("🔐 Your Login Credentials - Web Minds Technology Pvt LTD");

		String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<style>"
				+ "body { font-family: Arial, sans-serif; line-height: 1.6; background-color: #f8f9fa; }"
				+ ".container { max-width: 600px; margin: auto; padding: 20px; "
				+ "background: #ffffff; border: 1px solid #ddd; border-radius: 8px; }"
				+ ".header { text-align: center; color: #007bff; }"
				+ ".credentials { background: #f1f8ff; padding: 15px; border-radius: 6px; "
				+ "border: 1px solid #cce5ff; margin: 20px 0; }" + ".credentials p { margin: 8px 0; }"
				+ ".highlight { color: #007bff; font-weight: bold; }"
				+ ".footer { margin-top: 25px; font-size: 14px; color: #555; }" + "</style>" + "</head>" + "<body>"
				+ "<div class='container'>"

				+ "<h2 class='header'>👋 Welcome to Web Minds Technology Pvt LTD</h2>"

				+ "<p>Dear <b>" + user.getFirstName() + " " + user.getLastName() + "</b>,</p>"

				+ "<p>Your account has been <span class='highlight'>approved successfully</span>. "
				+ "Below are your login credentials:</p>"

				+ "<div class='credentials'>" + "<p><b>Username:</b> " + email + "</p>" + "<p><b>Password:</b> "
				+ password + "</p>" + "<p><b>Unique Code:</b> " + uniqueCode + "</p>" + "</div>"

				+ "<p>Please keep these credentials secure and do not share them with anyone.</p>"
				+ "<p>For security purposes, we recommend changing your password after your first login.</p>"

				+ "<div class='footer'>" + "<p><b>Best Regards,</b><br>" + "Web Minds Technology Pvt LTD<br>"
				+ "🌐 www.webmindstechnology.in</p>" + "</div>"

				+ "</div>" + "</body>" + "</html>";

		helper.setText(htmlContent, true);
		javaMailSender.send(mimeMessage);

	}

	@Override
	public void sendEmailWithAttachment(String email, byte[] excel) {

		try {

			MimeMessage message = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(email);

			helper.setSubject("Event Follow-Up Report");

			String htmlContent = "<!DOCTYPE html>" + "<html>" + "<head>" + "<meta charset='UTF-8'>" + "<style>" +

					"body {" + "    font-family: Arial, Helvetica, sans-serif;" + "    background-color: #f5f6fa;"
					+ "    margin: 0;" + "    padding: 0;" + "}" +

					".container {" + "    max-width: 650px;" + "    margin: 30px auto;" + "    background: #ffffff;"
					+ "    border-radius: 8px;" + "    overflow: hidden;"
					+ "    box-shadow: 0 2px 8px rgba(0,0,0,0.08);" + "}" +

					".header {" + "    background-color: #1f4e78;" + "    color: #ffffff;" + "    padding: 25px;"
					+ "    text-align: center;" + "}" +

					".header h1 {" + "    margin: 0;" + "    font-size: 24px;" + "}" +

					".content {" + "    padding: 30px;" + "    color: #333333;" + "    line-height: 1.6;" + "}" +

					".report-box {" + "    background-color: #f1f6fb;" + "    border-left: 4px solid #1f4e78;"
					+ "    padding: 15px;" + "    margin: 20px 0;" + "}" +

					".attachment {" + "    font-weight: bold;" + "    color: #1f4e78;" + "}" +

					"</style>" + "</head>" + "<body>" +

					"<div class='container'>" +

					"<div class='header'>" + "<h1>Event Follow-Up Report</h1>" + "</div>" +

					"<div class='content'>" +

					"<p>Hello,</p>" +

					"<p>" + "Please find attached the latest " + "<strong>Event Follow-Up Report</strong>." + "</p>" +

					"<div class='report-box'>" + "<strong>Report Details</strong><br>"
					+ "The attached Excel file contains the event follow-up details " + "scheduled for your attention."
					+ "</div>" +

					"<p>" + "Please review the attached report and take the necessary action "
					+ "on the pending follow-ups." + "</p>" +

					"<p class='attachment'>" + "📎 Attachment: Event-Followup-Report.xlsx" + "</p>" +

					"<p>" + "If you have any questions or need further assistance, "
					+ "please contact the administrator." + "</p>" +

					"</div>" +

					"</div>" +

					"</body>" + "</html>";

			helper.setText(htmlContent, true);

			helper.addAttachment("Event-Followup-Report.xlsx", new ByteArrayResource(excel));

			javaMailSender.send(message);

			System.out.println("Event follow-up report email sent successfully to: " + email);

		} catch (Exception e) {

			System.err.println("Failed to send event follow-up report to: " + email);

			e.printStackTrace();
		}
	}

	@Override
	public void sendEventInfoWhatsApp(String templateId, String mob1, List<String> valueArr, String file, boolean b,
			Long userId) {

		String APP_KEY = environment.getProperty("APP_KEY");
		String AUTH_KEY = environment.getProperty("AUTH_KEY");
		String API_URL = environment.getProperty("API_URL");

		if (userId != -1) {

			UserNotificationConfigEntity dto = userNotificationConfigRepository
					.findNotificationByUserAndUpgradeModule(userId, 3L);

			if (dto != null) {
				APP_KEY = dto.getKey1();
				AUTH_KEY = dto.getKey2();
				API_URL = dto.getUrl();
			}
		}

		// Normalize and validate mobile number
		mob1 = formatMobileNumber(mob1);

		System.out.println("WhatsApp Mobile Number: " + mob1);

		MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("appkey", APP_KEY).addFormDataPart("authkey", AUTH_KEY).addFormDataPart("to", mob1)
				.addFormDataPart("template_id", templateId).addFormDataPart("language", "en");

		if (valueArr != null) {
			for (int i = 0; i < valueArr.size(); i++) {

				String value = valueArr.get(i);

				bodyBuilder.addFormDataPart("variables[{variableKey" + (i + 1) + "}]", value != null ? value : "");
			}
		}

		Request request = new Request.Builder().url(API_URL).post(bodyBuilder.build()).build();

		try (Response response = client.newCall(request).execute()) {

			String responseBody = response.body() != null ? response.body().string() : "";

			System.out.println("Request Mobile: " + mob1);
			System.out.println("Response: " + response.code() + " - " + responseBody);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String formatMobileNumber(String mobile) {

		if (mobile == null || mobile.trim().isEmpty()) {
			throw new RuntimeException("Mobile number is required");
		}

		// Remove spaces, +, -, brackets etc.
		mobile = mobile.replaceAll("[^0-9]", "");

		// Remove leading 00 (international prefix)
		if (mobile.startsWith("00")) {
			mobile = mobile.substring(2);
		}

		// If Indian 10-digit number
		if (mobile.length() == 10) {
			mobile = "91" + mobile;
		}

		// Validate Indian number with country code
		if (!mobile.matches("^91[6-9][0-9]{9}$")) {
			throw new RuntimeException("Invalid mobile number format: " + mobile);
		}

		return mobile;
	}
}
