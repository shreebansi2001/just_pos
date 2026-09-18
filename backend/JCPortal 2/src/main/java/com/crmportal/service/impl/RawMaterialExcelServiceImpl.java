package com.crmportal.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.EventRawMaterialCategoryResponse;
import com.crmportal.response.dto.EventRawMaterialInfoDto;
import com.crmportal.response.dto.UnitExportDataResponseDto;
import com.crmportal.service.EventRawMaterialService;
import com.crmportal.service.RawMaterialExcelService;

@Service
public class RawMaterialExcelServiceImpl implements RawMaterialExcelService {

	@Autowired
	Environment environment;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	EventRawMaterialService eventRawMaterialService;
	
	@Override
	public String generateRawMaterialExcelType3(Long eventId, HttpServletRequest request, Integer lang, Long userId,
	        List<Long> eventFunctionIds, List<Long> rawMaterialCatIds, Integer isCombo, Integer isWithPrice, Integer isAddStoreIssue) {

	    UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
	            .orElseThrow(() -> new RuntimeException("User not found with id : " + userId));

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    String fileUrl = "";

	    try {
	        HSSFWorkbook workbook = new HSSFWorkbook();

	        List<EventRawMaterialCategoryResponse> data;

	        if (eventFunctionIds == null || eventFunctionIds.isEmpty()) {
	            data = eventRawMaterialService.getEventRawMaterialByEventId(eventId, rawMaterialCatIds, isAddStoreIssue);
	        } else {
	            data = eventRawMaterialService.getEventRawMaterialByEventIdAndEventFunctionId(
	                    eventId, eventFunctionIds, rawMaterialCatIds);
	        }

	        if(data == null) {
	        	return "Data not found.";
	        }
	        // Generate Excel Sheet(s)
	        getRawMaterialSheet(data, workbook, lang, isCombo, isWithPrice);

	        // Write workbook to output stream
	        workbook.write(out);
	        workbook.close();

	        String rootPath = request.getSession().getServletContext().getRealPath("/");

	        File dir = new File(rootPath + "resources/tempDownload/masterdata/");
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }

	        String fileName = user.getUserBasicDetails().getCompanyName().trim().toUpperCase() + ".xls";
	        File file = new File(dir, fileName);

	        Files.write(file.toPath(), out.toByteArray());

	        fileUrl = environment.getProperty("ws_image_path")
	                + "/api/download/excel/masterdata/" + fileName;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return fileUrl;
	}
	
	private void getRawMaterialSheet(List<EventRawMaterialCategoryResponse> data,
	        HSSFWorkbook workbook, Integer lang, Integer isCombo, Integer isWithPrice) {

	    if (isCombo == 0) {

	        // One sheet per category
	        for (EventRawMaterialCategoryResponse category : data) {

	            String sheetName = getCategoryName(category, lang);
	            if (sheetName.length() > 31) {
	                sheetName = sheetName.substring(0, 31);
	            }

	            HSSFSheet sheet = workbook.createSheet(sheetName);

	            createItemTable(sheet, workbook, category.getRawMaterials(), lang, isWithPrice);
	        }

	    } else {

	        // Single sheet
	        HSSFSheet sheet = workbook.createSheet("Raw Material");

	        HSSFFont boldFont = workbook.createFont();
	        boldFont.setBold(true);

	        HSSFCellStyle categoryStyle = workbook.createCellStyle();
	        categoryStyle.setFont(boldFont);

	        int rowNum = 0;

	        for (EventRawMaterialCategoryResponse category : data) {

	            // Category Heading
	            HSSFRow catRow = sheet.createRow(rowNum++);
	            HSSFCell catCell = catRow.createCell(0);
	            catCell.setCellValue(getCategoryName(category, lang));
	            catCell.setCellStyle(categoryStyle);

	            // Table Header
	            HSSFRow header = sheet.createRow(rowNum++);
	            header.createCell(0).setCellValue("Item Name");
	            header.createCell(1).setCellValue("Qty");

	            if (isWithPrice == 1) {
	                header.createCell(2).setCellValue("Price");
	            }

	            // Data
	            for (EventRawMaterialInfoDto item : category.getRawMaterials()) {

	                HSSFRow row = sheet.createRow(rowNum++);

	                row.createCell(0).setCellValue(getItemName(item, lang));
	                row.createCell(1).setCellValue(item.getQty() + " " + nullSafe(item.getUnitName()));

	                if (isWithPrice == 1) {
	                    row.createCell(2).setCellValue(item.getTotalPrice() == null ? 0 : item.getTotalPrice());
	                }
	            }

	            rowNum++;
	        }

	        for (int i = 0; i < (isWithPrice == 1 ? 3 : 2); i++) {
	            sheet.autoSizeColumn(i);
	        }
	    }
	}
	
	private String nullSafe(String value) {
	    return value != null ? value : "";
	}
	
	private void createExcelCell(HSSFRow row, int col, String value, HSSFCellStyle style) {
	    HSSFCell cell = row.createCell(col);
	    cell.setCellValue(value);
	    cell.setCellStyle(style);
	}
	
	private void createItemTable(HSSFSheet sheet,
	        HSSFWorkbook workbook,
	        List<EventRawMaterialInfoDto> items,
	        Integer lang,
	        Integer isWithPrice) {

	    HSSFFont boldFont = workbook.createFont();
	    boldFont.setBold(true);

	    HSSFCellStyle headerStyle = workbook.createCellStyle();
	    headerStyle.setFont(boldFont);

	    int rowNum = 0;

	    HSSFRow header = sheet.createRow(rowNum++);

	    HSSFCell c0 = header.createCell(0);
	    c0.setCellValue("Item Name");
	    c0.setCellStyle(headerStyle);

	    HSSFCell c1 = header.createCell(1);
	    c1.setCellValue("Qty");
	    c1.setCellStyle(headerStyle);

	    if (isWithPrice == 1) {
	        HSSFCell c2 = header.createCell(2);
	        c2.setCellValue("Price");
	        c2.setCellStyle(headerStyle);
	    }

	    for (EventRawMaterialInfoDto item : items) {

	        HSSFRow row = sheet.createRow(rowNum++);

	        row.createCell(0).setCellValue(getItemName(item, lang));
	        row.createCell(1).setCellValue(item.getQty() + " " + nullSafe(item.getUnitName()));

	        if (isWithPrice == 1) {
	            row.createCell(2).setCellValue(item.getTotalPrice() == null ? 0 : item.getTotalPrice());
	        }
	    }

	    for (int i = 0; i < (isWithPrice == 1 ? 3 : 2); i++) {
	        sheet.autoSizeColumn(i);
	    }
	}
	
	private String getCategoryName(EventRawMaterialCategoryResponse category, Integer lang) {

	    if (lang == 1) {
	        return nullSafe(category.getCategoryNameHindi());
	    } else if (lang == 2) {
	        return nullSafe(category.getCategoryNameGujarati());
	    }
	    return nullSafe(category.getCategoryNameEnglish());
	}

	private String getItemName(EventRawMaterialInfoDto item, Integer lang) {

	    if (lang == 1) {
	        return nullSafe(item.getRawMaterialNameHindi());
	    } else if (lang == 2) {
	        return nullSafe(item.getRawMaterialNameGujarati());
	    }
	    return nullSafe(item.getRawMaterialNameEnglish());
	}
}
