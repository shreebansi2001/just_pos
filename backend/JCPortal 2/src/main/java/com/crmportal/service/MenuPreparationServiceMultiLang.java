package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.element.Paragraph;

@Service
public interface MenuPreparationServiceMultiLang {

	String generateExclusiveReport(Long eventId, Long eventFunctionId, Integer isCategorySlogan,
			Integer isCategoryInstruction, Long isCategoryImage, Integer isItemSlogan, Integer isItemInstruction, HttpServletRequest re, int i,
			Integer lang);
}
