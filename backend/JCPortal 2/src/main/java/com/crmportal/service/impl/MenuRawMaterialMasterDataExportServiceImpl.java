package com.crmportal.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
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
import com.crmportal.repository.CaptainReceipeMasterRepository;
import com.crmportal.repository.CaptainReceipeRawMaterialItemRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuItemRawMaterialRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UnitRangeRepository;
import com.crmportal.repository.UnitStepwiseRangeRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.CaptainReceipeMasterExportDataResponseDto;
import com.crmportal.response.dto.CaptainReceipeRawMaterialExportDataResponseDto;
import com.crmportal.response.dto.MenuCategoryExportDataResponseDto;
import com.crmportal.response.dto.MenuItemExportDataResponseDto;
import com.crmportal.response.dto.MenuItemRawMaterialExportDataResponseDto;
import com.crmportal.response.dto.RawMaterialCatExportDataResponseDto;
import com.crmportal.response.dto.RawMaterialExportDataResponseDto;
import com.crmportal.response.dto.UnitExportDataResponseDto;
import com.crmportal.response.dto.UnitRangeExportDataResponseDto;
import com.crmportal.response.dto.UnitStepWiseRangeExportDataResponseDto;
import com.crmportal.service.MenuRawMaterialMasterDataExportService;

@Service
public class MenuRawMaterialMasterDataExportServiceImpl implements MenuRawMaterialMasterDataExportService {

	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	UnitMasterRepository unitMasterRepository;
	
	@Autowired
	UnitRangeRepository unitRangeRepository;
	
	@Autowired
	UnitStepwiseRangeRepository unitStepwiseRangeRepository;
	
	@Autowired
	RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;
	
	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;
	
	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;
	
	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;
	
	@Autowired
	MenuItemRawMaterialRepository menuItemRawMaterialRepository;
	
	@Autowired
	CaptainReceipeMasterRepository captainReceipeMasterRepository;
	
	@Autowired
	CaptainReceipeRawMaterialItemRepository captainReceipeRawMaterialItemRepository;
	
	@Autowired
	Environment environment;
	
	@Override
	public String generateMenuRawMaterialMasterDataExcel(Long userId, HttpServletRequest request) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String fileUrl = "";
		
	    try {
	    	HSSFWorkbook workbook = new HSSFWorkbook();
		    
			List<Object[]> unitRows = unitMasterRepository.getUnitExportData(userId);
			List<Object[]> unitRangeRows = unitRangeRepository.getUnitRangeExportData(userId);
			List<Object[]> unitStepwiseRangeRows = unitStepwiseRangeRepository.getUnitStepWiseRangeExportData(userId);
			List<Object[]> rawMaterialCatRows = rawMaterialCategoryMasterRepository.getRawMaterialCatExportData(userId);
			List<Object[]> rawMaterialRows = rawMaterialMasterRepository.getRawMaterialExportData(userId);
			List<Object[]> menuCatRows = menuCategoryMasterRepository.getMenuCategoryExportData(userId);
			List<Object[]> menuItemRows = menuItemMasterRepository.getMenuItemExportData(userId);
			List<Object[]> menuItemRawMaterialRows = menuItemRawMaterialRepository.getMenuItemRawMaterialExportData(userId);
			List<Object[]> captainReceipeRows = captainReceipeMasterRepository.getCaptainReceipeMasterExportData(userId);
			List<Object[]> captainReceipeRawMaterialRows = captainReceipeRawMaterialItemRepository.getCaptainReceipeRawMaterialExportData(userId);
			
			List<UnitExportDataResponseDto> unitData = mapUnitToResponseDto(unitRows);
			List<UnitRangeExportDataResponseDto> unitRangeData = mapUnitRangeToResponseDto(unitRangeRows);
			List<UnitStepWiseRangeExportDataResponseDto> unitStepwiseRangeData = mapUnitStepWiseRangeToResponseDto(unitStepwiseRangeRows);
			List<RawMaterialCatExportDataResponseDto> rawMaterialCatData = mapRawMaterialCatToResponseDto(rawMaterialCatRows);
			List<RawMaterialExportDataResponseDto> rawMaterialData = mapRawMaterialToResponseDto(rawMaterialRows);
			List<MenuCategoryExportDataResponseDto> menuCatData = mapMenuCategoryToResponseDto(menuCatRows);
			List<MenuItemExportDataResponseDto> menuItemData = mapMenuItemToResponseDto(menuItemRows);
			List<MenuItemRawMaterialExportDataResponseDto> menuItemRawMaterialData = mapMenuItemRawMaterialToResponseDto(menuItemRawMaterialRows);
			List<CaptainReceipeMasterExportDataResponseDto> captainReceipeData = mapCaptainReceipeToResponseDto(captainReceipeRows);
			List<CaptainReceipeRawMaterialExportDataResponseDto> captainReceipeRawMaterialData = mapCaptainReceipeRawMaterialToResponseDto(captainReceipeRawMaterialRows);
			
			getUnitSheet(unitData, workbook);
			getUnitRangeSheet(unitRangeData, workbook);
			getUnitStepwiseRangeSheet(unitStepwiseRangeData, workbook);
			getRawMaterialCatSheet(rawMaterialCatData, workbook);
			getRawMaterialSheet(rawMaterialData, workbook);
			getMenuCatSheet(menuCatData, workbook);
			getMenuItemSheet(menuItemData, workbook);
			getMenuItemRawMaterialSheet(menuItemRawMaterialData, workbook);
			getCaptainReceipeSheet(captainReceipeData, workbook);
			getCaptainReceipeRawMaterialSheet(captainReceipeRawMaterialData, workbook);
			
			workbook.write(out);
			out.flush();
			out.close();
		    
			String rootPath = request.getSession().getServletContext().getRealPath("/");

	        File dir = new File(rootPath + "resources/tempDownload/masterdata/");
	        if (!dir.exists()) dir.mkdirs();

	        String fileName = user.getUserBasicDetails().getCompanyName().trim().toUpperCase() + ".xls";
	        File file = new File(dir, fileName);
	        
	        Files.write(file.toPath(), out.toByteArray());

	        fileUrl = environment.getProperty("ws_image_path") + "/api/download/excel/masterdata/"
					+ fileName;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return fileUrl;
	}
	
	private void getUnitSheet(List<UnitExportDataResponseDto> unitData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("Unit");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"Unit", "Unit Symbol", "Is Parent", "Parent Unit", "Equivalent Value", "Decimal Limit", "Unit Name Gujarati",
        		"Unit Name Hindi", "Unit Symbol Gujarati", "Unit Symbol Hindi", "Type"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (UnitExportDataResponseDto entity : unitData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, nullSafe(entity.getNameEnglish()), dataStyle);
            createExcelCell(row, 1, nullSafe(entity.getSymbolEnglish()), dataStyle);
            createExcelCell(row, 2, entity.getIsParentUnit() != null ? entity.getIsParentUnit().toString() : null, dataStyle);
            createExcelCell(row, 3, nullSafe(entity.getParentNameEnglish()), dataStyle);
            createExcelCell(row, 4, entity.getEquivalentValue() != null ? entity.getEquivalentValue().toString() : "", dataStyle);
            createExcelCell(row, 5, entity.getDecimalLimit() != null ? entity.getDecimalLimit().toString() : "", dataStyle);
            createExcelCell(row, 6, nullSafe(entity.getNameGujarati()), dataStyle);
            createExcelCell(row, 7, nullSafe(entity.getNameHindi()), dataStyle);
            createExcelCell(row, 8, nullSafe(entity.getSymbolGujarati()), dataStyle);
            createExcelCell(row, 9, nullSafe(entity.getSymbolHindi()), dataStyle);
            createExcelCell(row, 10, nullSafe(entity.getRangeType()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}

	private void getUnitRangeSheet(List<UnitRangeExportDataResponseDto> unitRangeData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("unit_range_precision");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"Min value", "Max value", "Round value", "Unit", "Type"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (UnitRangeExportDataResponseDto entity : unitRangeData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, entity.getMinValue() != null ? entity.getMinValue().toString() : null, dataStyle);
            createExcelCell(row, 1, entity.getMaxValue() != null ? entity.getMaxValue().toString() : null, dataStyle);
            createExcelCell(row, 2, entity.getRoundValue() != null ? entity.getRoundValue().toString() : null, dataStyle);
            createExcelCell(row, 3, nullSafe(entity.getUnitNameEnglish()), dataStyle);
            createExcelCell(row, 4, nullSafe(entity.getRangeType()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}

	private void getUnitStepwiseRangeSheet(List<UnitStepWiseRangeExportDataResponseDto> unitStepwiseRangeData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("unit_stepwise");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"step", "unit"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (UnitStepWiseRangeExportDataResponseDto entity : unitStepwiseRangeData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, entity.getStepValue() != null ? entity.getStepValue().toString() : null, dataStyle);
            createExcelCell(row, 1, nullSafe(entity.getUnitNameEnglish()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}
	
	private void getRawMaterialCatSheet(List<RawMaterialCatExportDataResponseDto> rawMaterialCatData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("raw_material_category");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"categoryNameEnglish", "categoryNameGujarati", "categoryNameHindi", "category tpye"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (RawMaterialCatExportDataResponseDto entity : rawMaterialCatData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, nullSafe(entity.getCatNameEnglish()), dataStyle);
            createExcelCell(row, 1, nullSafe(entity.getCatNameGujarati()), dataStyle);
            createExcelCell(row, 2, nullSafe(entity.getCatNameHindi()), dataStyle);
            createExcelCell(row, 3, nullSafe(entity.getCatTypeEnglish()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}
	
	private void getRawMaterialSheet(List<RawMaterialExportDataResponseDto> rawMaterialData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("rawmaterial");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"categoryNameEnglish", "categoryNameGujarati", "categoryNameHindi", "Rate", "rawMaterialCat", "unit", "opb"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (RawMaterialExportDataResponseDto entity : rawMaterialData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, nullSafe(entity.getRawMaterialNameEnglish()), dataStyle);
            createExcelCell(row, 1, nullSafe(entity.getRawMaterialNameGujarati()), dataStyle);
            createExcelCell(row, 2, nullSafe(entity.getRawMaterialNameHindi()), dataStyle);
            createExcelCell(row, 3, entity.getSupplierRate() != null ? entity.getSupplierRate().toString() : "", dataStyle);
            createExcelCell(row, 4, nullSafe(entity.getRawCatNameEnglish()), dataStyle);
            createExcelCell(row, 5, nullSafe(entity.getUnitNameEnglish()), dataStyle);
            createExcelCell(row, 6, entity.getOpb() != null ? entity.getOpb().toString() : "", dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}
	
	private void getMenuCatSheet(List<MenuCategoryExportDataResponseDto> menuCatData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("menucategory");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"categoryNameEnglish", "categoryNameGujarati", "categoryNameHindi", "slogan"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (MenuCategoryExportDataResponseDto entity : menuCatData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, nullSafe(entity.getMenuCatNameEnglish()), dataStyle);
            createExcelCell(row, 1, nullSafe(entity.getMenuCatNameGujarati()), dataStyle);
            createExcelCell(row, 2, nullSafe(entity.getMenuCatNameHindi()), dataStyle);
            createExcelCell(row, 3, nullSafe(entity.getMenuSlogan()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}
	
	private void getMenuItemSheet(List<MenuItemExportDataResponseDto> menuItemData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("memuitems");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"itemNameEnglish", "itemNameGujarati", "itemNameHindi", "slogan", "menuCategory"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (MenuItemExportDataResponseDto entity : menuItemData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, nullSafe(entity.getItemNameEnglish()), dataStyle);
            createExcelCell(row, 1, nullSafe(entity.getItemNameGujarati()), dataStyle);
            createExcelCell(row, 2, nullSafe(entity.getItemNameHindi()), dataStyle);
            createExcelCell(row, 3, nullSafe(entity.getSlogan()), dataStyle);
            createExcelCell(row, 4, nullSafe(entity.getCatNameEnglish()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}

	private void getMenuItemRawMaterialSheet(List<MenuItemRawMaterialExportDataResponseDto> menuItemRawMaterialData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("menu_item_raw_material");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"Item Name", "Raw Material", "Weight", "Unit"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (MenuItemRawMaterialExportDataResponseDto entity : menuItemRawMaterialData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, nullSafe(entity.getItemNameEnglish()), dataStyle);
            createExcelCell(row, 1, nullSafe(entity.getRawMaterialNameEnglish()), dataStyle);
            createExcelCell(row, 2, entity.getWeight() != null ? entity.getWeight().toString() : "", dataStyle);
            createExcelCell(row, 3, nullSafe(entity.getUnitNameEnglish()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}
	
	private void getCaptainReceipeSheet(List<CaptainReceipeMasterExportDataResponseDto> captainReceipeData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("captain_receipe");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"Name", "Weight", "Rate", "Unit"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (CaptainReceipeMasterExportDataResponseDto dto : captainReceipeData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, nullSafe(dto.getName()), dataStyle);
            createExcelCell(row, 1, dto.getWeight() != null ? dto.getWeight().toString() : "", dataStyle);
            createExcelCell(row, 2, dto.getRate() != null ? dto.getRate().toString() : "", dataStyle);
            createExcelCell(row, 3, nullSafe(dto.getUnitName()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}

	private void getCaptainReceipeRawMaterialSheet(List<CaptainReceipeRawMaterialExportDataResponseDto> captainReceipeRawMaterialData, HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("captain_receipe_raw_material");
		
		// ---- Header Style ----
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // ---- Data Style ----
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // ---- Header Row ----
        String[] headers = {"Captain Receipe", "Raw Material", "Weight", "Unit"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ---- Data Rows ----
        int rowNum = 1;
        for (CaptainReceipeRawMaterialExportDataResponseDto dto : captainReceipeRawMaterialData) {
            HSSFRow row = sheet.createRow(rowNum);
            createExcelCell(row, 0, nullSafe(dto.getCaptainReceipeName()), dataStyle);
            createExcelCell(row, 1, nullSafe(dto.getRawMaterial()), dataStyle);
            createExcelCell(row, 2, dto.getWeight() != null ? dto.getWeight().toString() : "", dataStyle);
            createExcelCell(row, 3, nullSafe(dto.getUnitName()), dataStyle);
            
            rowNum++;
        }

        // ---- Auto size ----
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
		
	}
	
	private List<UnitExportDataResponseDto> mapUnitToResponseDto(List<Object[]> rows) {
		List<UnitExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			UnitExportDataResponseDto dto = new UnitExportDataResponseDto();

			dto.setNameEnglish(row[0] != null ? row[0].toString() : null);
			dto.setSymbolEnglish(row[1] != null ? row[1].toString() : null);
			dto.setNameHindi(row[2] != null ? row[2].toString() : null);
			dto.setSymbolHindi(row[3] != null ? row[3].toString() : null);
			dto.setNameGujarati(row[4] != null ? row[4].toString() : null);
			dto.setIsParentUnit(row[6] != null ? (Boolean) row[6] : false);
			dto.setParentNameEnglish(row[7] != null ? row[7].toString() : null);
			dto.setEquivalentValue(row[8] != null ? ((Number)row[8]).doubleValue() : null);
			dto.setDecimalLimit(row[9] != null ? ((Number)row[9]).intValue() : null);
			dto.setRangeType(row[10] != null ? row[10].toString() : null);
			
			response.add(dto);
		}

		return response;
	}
	
	private List<UnitRangeExportDataResponseDto> mapUnitRangeToResponseDto(List<Object[]> rows) {
		List<UnitRangeExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			UnitRangeExportDataResponseDto dto = new UnitRangeExportDataResponseDto();

			dto.setMaxValue(row[0] != null ? ((Number) row[0]).doubleValue() : null);
			dto.setMinValue(row[1] != null ? ((Number) row[1]).doubleValue() : null);
			dto.setRoundValue(row[2] != null ? ((Number) row[2]).doubleValue() : null);
			dto.setUnitNameEnglish(row[3] != null ? row[3].toString() : null);
			dto.setRangeType(row[4] != null ? row[4].toString() : null);

			response.add(dto);
		}

		return response;
	}
	
	private List<UnitStepWiseRangeExportDataResponseDto> mapUnitStepWiseRangeToResponseDto(List<Object[]> rows) {
		List<UnitStepWiseRangeExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			UnitStepWiseRangeExportDataResponseDto dto = new UnitStepWiseRangeExportDataResponseDto();

			dto.setStepValue(row[0] != null ? ((Number) row[0]).doubleValue() : null);
			dto.setUnitNameEnglish(row[1] != null ? row[1].toString() : null);

			response.add(dto);
		}

		return response;
	}
	
	private List<RawMaterialCatExportDataResponseDto> mapRawMaterialCatToResponseDto(List<Object[]> rows) {
		List<RawMaterialCatExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			RawMaterialCatExportDataResponseDto dto = new RawMaterialCatExportDataResponseDto();

			dto.setCatNameEnglish(row[0] != null ? row[0].toString() : null);
			dto.setCatNameGujarati(row[1] != null ? row[1].toString() : null);
			dto.setCatNameHindi(row[2] != null ? row[2].toString() : null);
			dto.setCatTypeEnglish(row[3] != null ? row[3].toString() : null);

			response.add(dto);
		}

		return response;
	}

	private List<RawMaterialExportDataResponseDto> mapRawMaterialToResponseDto(List<Object[]> rows) {
		List<RawMaterialExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			RawMaterialExportDataResponseDto dto = new RawMaterialExportDataResponseDto();

			dto.setRawMaterialNameEnglish(row[0] != null ? row[0].toString() : null);
			dto.setRawMaterialNameGujarati(row[1] != null ? row[1].toString() : null);
			dto.setRawMaterialNameHindi(row[2] != null ? row[2].toString() : null);
			dto.setSupplierRate(row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO);
			dto.setRawCatNameEnglish(row[4] != null ? row[4].toString() : null);
			dto.setUnitNameEnglish(row[5] != null ? row[5].toString() : null);
			dto.setOpb(row[6] != null ? (BigDecimal) row[6] : BigDecimal.ZERO);

			response.add(dto);
		}

		return response;
	}
	
	private List<MenuCategoryExportDataResponseDto> mapMenuCategoryToResponseDto(List<Object[]> rows) {
		List<MenuCategoryExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			MenuCategoryExportDataResponseDto dto = new MenuCategoryExportDataResponseDto();

			dto.setMenuCatNameEnglish(row[0] != null ? row[0].toString() : null);
			dto.setMenuCatNameHindi(row[1] != null ? row[1].toString() : null);
			dto.setMenuCatNameGujarati(row[2] != null ? row[2].toString() : null);
			dto.setMenuSlogan(row[3] != null ? row[3].toString() : null);

			response.add(dto);
		}

		return response;
	}
	
	private List<MenuItemExportDataResponseDto> mapMenuItemToResponseDto(List<Object[]> rows) {
		List<MenuItemExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			MenuItemExportDataResponseDto dto = new MenuItemExportDataResponseDto();

			dto.setItemNameEnglish(row[0] != null ? row[0].toString() : null);
			dto.setItemNameHindi(row[1] != null ? row[1].toString() : null);
			dto.setItemNameGujarati(row[2] != null ? row[2].toString() : null);
			dto.setSlogan(row[3] != null ? row[3].toString() : null);
			dto.setCatNameEnglish(row[4] != null ? row[4].toString() : null);

			response.add(dto);
		}

		return response;
	}
	
	private List<MenuItemRawMaterialExportDataResponseDto> mapMenuItemRawMaterialToResponseDto(List<Object[]> rows) {
		List<MenuItemRawMaterialExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			MenuItemRawMaterialExportDataResponseDto dto = new MenuItemRawMaterialExportDataResponseDto();

			dto.setItemNameEnglish(row[0] != null ? row[0].toString() : null);
			dto.setRawMaterialNameEnglish(row[1] != null ? row[1].toString() : null);
			dto.setWeight(row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO);
			dto.setUnitNameEnglish(row[3] != null ? row[3].toString() : null);

			response.add(dto);
		}

		return response;
	}
	
	private List<CaptainReceipeMasterExportDataResponseDto> mapCaptainReceipeToResponseDto(List<Object[]> rows) {
		List<CaptainReceipeMasterExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			CaptainReceipeMasterExportDataResponseDto dto = new CaptainReceipeMasterExportDataResponseDto();

			dto.setName(row[0] != null ? row[0].toString() : null);
			dto.setWeight(row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO);
			dto.setRate(row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO);
			dto.setUnitName(row[3] != null ? row[3].toString() : null);
			
			response.add(dto);
		}

		return response;
	}

	private List<CaptainReceipeRawMaterialExportDataResponseDto> mapCaptainReceipeRawMaterialToResponseDto(List<Object[]> rows) {
		List<CaptainReceipeRawMaterialExportDataResponseDto> response = new ArrayList<>();

		for (Object[] row : rows) {
			CaptainReceipeRawMaterialExportDataResponseDto dto = new CaptainReceipeRawMaterialExportDataResponseDto();

			dto.setCaptainReceipeName(row[0] != null ? row[0].toString() : null);
			dto.setRawMaterial(row[1] != null ? row[1].toString() : null);
			dto.setWeight(row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO);
			dto.setUnitName(row[3] != null ? row[3].toString() : null);
			
			response.add(dto);
		}

		return response;
	}
	
	private String nullSafe(String value) {
	    return value != null ? value : "";
	}
	
	private void createExcelCell(HSSFRow row, int col, String value, HSSFCellStyle style) {
	    HSSFCell cell = row.createCell(col);
	    cell.setCellValue(value);
	    cell.setCellStyle(style);
	}
}
