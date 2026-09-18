package com.crmportal.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.controller.EventFollowUpEntity;
import com.crmportal.response.dto.EventFollowupResponseDto;
import com.crmportal.service.EventFollowupExcelService;

@Service
public class EventFollowupExcelServiceImpl implements EventFollowupExcelService {

	@Autowired
	EventFollowUpServiceImpl eventFollowUpServiceImpl;

	@Override
	public byte[] generateExcel(List<EventFollowUpEntity> followups) {

		Workbook workbook = new XSSFWorkbook();

		try {

			Sheet sheet = workbook.createSheet("Followups");

			Row header = sheet.createRow(0);

			header.createCell(1).setCellValue("Sr No.");
			header.createCell(1).setCellValue("Event");
			header.createCell(2).setCellValue("Manager");
			header.createCell(3).setCellValue("Followup Date");
			header.createCell(4).setCellValue("Description");
			header.createCell(5).setCellValue("Created At");

			int rowNumber = 1;
			List<EventFollowupResponseDto> dtos = eventFollowUpServiceImpl.map(followups);
			for (EventFollowupResponseDto followup : dtos) {

				Row row = sheet.createRow(rowNumber++);

				row.createCell(0).setCellValue(rowNumber);

				row.createCell(1)
						.setCellValue(followup.getEventName() != null && !followup.getEventName().trim().isEmpty()
								? followup.getEventName()
								: "");

				row.createCell(2)
						.setCellValue(followup.getManagerName() != null && !followup.getManagerName().trim().isEmpty()
								? followup.getManagerName()
								: "");

				row.createCell(3).setCellValue(followup.getManagerId() != null ? followup.getManagerId() : 0);

				row.createCell(4)
						.setCellValue(followup.getFollowupDate() != null ? followup.getFollowupDate().toString() : "");

				row.createCell(5).setCellValue(followup.getDescription() != null ? followup.getDescription() : "");

				row.createCell(6)
						.setCellValue(followup.getCreatedAt() != null ? followup.getCreatedAt().toString() : "");
			}

			for (int i = 0; i <= 6; i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			try {
				workbook.write(outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return outputStream.toByteArray();

		} finally {

			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
