package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface MenuRawMaterialMasterDataExportService {

	String generateMenuRawMaterialMasterDataExcel(Long userId, HttpServletRequest request);

}
