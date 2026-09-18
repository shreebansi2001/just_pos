package com.crmportal.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtility {

	public static Map<String, List<List<String>>> readExcel(InputStream inputStream, int sheetCount) {
		Map<String, List<List<String>>> map = new HashMap<>();
		try (Workbook workbook = WorkbookFactory.create(inputStream)) {
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				String sheetName = sheet.getSheetName();
				List<List<String>> excelRowList = new ArrayList<>();
				for (Row row : sheet) {
					List<String> excelRow = createExcelRow(row);
					if (CollectionUtils.isNotEmpty(excelRow))
						excelRowList.add(excelRow);
				}
				map.put(sheetName, excelRowList);
			}
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}

	private static List<String> formatList(List<String> row) {
	    return row.stream()
	            .map(s -> s.trim().toUpperCase())
	            .filter(StringUtils::isNotEmpty)
	            .collect(Collectors.toList());
	}

	private static List<String> createExcelRow(Row row) {
		List<String> excelRow = new ArrayList<>();
		for (Cell cell : row) {
			switch (cell.getCellType()) {
			case STRING:
				excelRow.add(cell.getStringCellValue());
				break;
			case NUMERIC:
				excelRow.add(String.valueOf(cell.getNumericCellValue()));
				break;
			case BOOLEAN:
				excelRow.add(String.valueOf(cell.getBooleanCellValue()));
				break;
			case FORMULA:
				excelRow.add(String.valueOf(cell.getCellFormula()));
				break;
			case BLANK:
				System.err.println("Blank Cell");
				break;
			default:
				throw new RuntimeException("Unknown cellType " + cell.getCellType().getClass().getName());
			}
		}
		return formatList(excelRow);
	}
}
