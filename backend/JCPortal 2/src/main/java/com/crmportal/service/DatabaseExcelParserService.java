package com.crmportal.service;

import java.io.InputStream;
import java.util.Map;

import com.crmportal.request.dto.DBExcelRequestDto;

public interface DatabaseExcelParserService {
	Map<String, Object> parseExcelToDb(InputStream inputStream, DBExcelRequestDto dbExcelRequestDto);
}
