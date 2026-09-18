package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface RawMaterialExcelService {

	String generateRawMaterialExcelType3(Long eventId, HttpServletRequest re, Integer lang, Long userid,
			List<Long> eventFunctionIds, List<Long> rawMaterialCatIds, Integer isCombo, Integer isWithPrice, Integer isAddStoreIssue);
}
