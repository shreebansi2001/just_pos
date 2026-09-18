package com.crmportal.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.UserLogsEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.UserLogsEntityRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.InActiveUserResponseDto;
import com.crmportal.service.UserLogsService;
import com.crmportal.utility.ResponseUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class UserLogsServiceImpl implements UserLogsService {
	@Autowired
	private UserLogsEntityRepository userLogsEntityRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private Environment environment;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Override
	public void saveUserLogs(String user, String eventType, String description, HttpServletRequest httpServletRequest,
			Long eventId) {
		UserLogsEntity userLogsEntity = new UserLogsEntity();

		userLogsEntity.setUser(user);
		userLogsEntity.setEventType(eventType);
		userLogsEntity.setIpAddress(getIpAddress(httpServletRequest));
		userLogsEntity.setDescription(description);
		userLogsEntity.setCreateAt(LocalDateTime.now());
		userLogsEntity.setEventId(eventId);

		userLogsEntityRepository.save(userLogsEntity);
	}

	@Override
	public Map<String, Object> getAllLogs(String user, String startDate, String endDate, String eventType, Long eventId,
			Long userId) {

		LocalDateTime start = null;
		LocalDateTime end = null;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		try {
			if (startDate != null && !startDate.isEmpty()) {
				start = LocalDate.parse(startDate, formatter).atStartOfDay();
			}

			if (endDate != null && !endDate.isEmpty()) {
				end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
			}
		} catch (Exception e) {
			throw new RuntimeException("Invalid date format. Expected: dd/MM/yyyy");
		}

		UserMasterEntity entity = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		List<String> userEmails;

		if (user == null || user.trim().isEmpty()) {

			userEmails = userMasterRepository.findUserEmailsByAdminId(userId);

			// Include admin's own email
			if (entity.getEmail() != null && !entity.getEmail().trim().isEmpty()) {

				userEmails.add(entity.getEmail().trim());
			}

		} else {

			userEmails = Collections.singletonList(user.trim());
		}

		if (userEmails == null || userEmails.isEmpty()) {
			return ResponseUtils.createSuccessRespones(createJsonNodeArray(Collections.emptyList()),
					"Data Fetched Successfully");
		}

		return ResponseUtils.createSuccessRespones(
				createJsonNodeArray(
						userLogsEntityRepository.findLogsWithFilters(userEmails, start, end, eventType, eventId)),
				"Data Fetched Successfully");
	}

	private JsonNode createJsonNodeArray(List<UserLogsEntity> list) {
		ArrayNode arrayNode = objectMapper.createArrayNode();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
		for (UserLogsEntity logsEntity : list) {
			ObjectNode node = objectMapper.createObjectNode();
			node.put("id", logsEntity.getId());
			node.put("user", logsEntity.getUser());
			node.put("eventType", logsEntity.getEventType());
			node.put("createAt", logsEntity.getCreateAt().format(formatter));
			node.put("ipAddress", logsEntity.getIpAddress());
			node.put("description", logsEntity.getDescription());
			node.put("isActive", logsEntity.getIsActive());

			arrayNode.add(node);
		}
		return arrayNode;
	}

	public String getIpAddress(HttpServletRequest httpServletRequest) {
		String ipAddress = httpServletRequest.getHeader("X-Forwarded-For");
		if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = ipAddress.split(",")[0].trim();
		} else {
			ipAddress = httpServletRequest.getRemoteAddr();
		}
		return ipAddress;
	}

	@Override
	public String inactiveUserExcel(HttpServletRequest request) {
		try {

			String rootPath = request.getSession().getServletContext().getRealPath("/");

			String folderName = "InActiveUser";

			File dir = new File(rootPath, "resources/tempDownload/" + folderName);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileName = "inactive-user-" + System.currentTimeMillis() + ".xlsx";
			File file = new File(dir, fileName);

			LocalDateTime sevendays = LocalDateTime.now().minusDays(7);
			List<Object[]> result = userLogsEntityRepository.findInactiveUsers(sevendays);

			List<InActiveUserResponseDto> data = new ArrayList<>();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
			for (Object[] row : result) {
				InActiveUserResponseDto dto = new InActiveUserResponseDto(row[0] != null ? row[0].toString() : "",
						row[1] != null ? row[1].toString() : "", row[2] != null ? row[2].toString() : "",
						row[3] != null ? row[3].toString() : "",
						row[4] != null ? ((Timestamp) row[4]).toLocalDateTime().format(formatter) : "");
				data.add(dto);
			}

			try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

				XSSFSheet sheet = workbook.createSheet("Last 7 Days Inactive User");

				int rowNum = 0;

				Row titleRow = sheet.createRow(rowNum++);
				titleRow.createCell(0).setCellValue("Last 7 Days Inactive User");

				String[] headers = { "No.", "User Name", "Company Name", "Mobile No", "Email ID", "Last Active Date" };

				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

				Row headerRow = sheet.createRow(rowNum++);
				for (int i = 0; i < headers.length; i++) {
					headerRow.createCell(i).setCellValue(headers[i]);
				}

				int cnt = 1;

				if (data != null) {
					for (InActiveUserResponseDto row : data) {
						Row r = sheet.createRow(rowNum++);

						r.createCell(0).setCellValue(cnt++);

						r.createCell(1).setCellValue(row.getUserName() != null ? row.getUserName() : "");

						r.createCell(2).setCellValue(row.getCompanyName() != null ? row.getCompanyName() : "");

						r.createCell(3).setCellValue(row.getMobileNo() != null ? row.getMobileNo() : "");

						r.createCell(4).setCellValue(row.getEmailid() != null ? row.getEmailid() : "");

						r.createCell(5).setCellValue(row.getCreatedAt() != null ? row.getCreatedAt() : "");
					}
				}

				for (int i = 0; i < headers.length; i++) {
					sheet.autoSizeColumn(i);
				}

				workbook.write(baos);
				byte[] excel = baos.toByteArray();

				Files.write(file.toPath(), excel);

				String fileUrl = environment.getProperty("ws_image_path") + "/api/download/excel/" + folderName + "/"
						+ fileName;

				return fileUrl;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error generating inactive user Excel file", e);
		}
	}
}
