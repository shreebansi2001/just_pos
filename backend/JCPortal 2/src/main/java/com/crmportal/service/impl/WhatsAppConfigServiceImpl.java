package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.request.dto.PdfSendWhatsappRequestDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.WhatsAppConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class WhatsAppConfigServiceImpl implements WhatsAppConfigService {

	@Autowired
	CommonService commonService;

	@Override
	public Boolean sendPdf(PdfSendWhatsappRequestDto request) {

		try {
			List<String> dataList = new ArrayList<>();
			dataList.add(request.getPartyName());
			dataList.add(request.getModuleName());
			dataList.add(request.getCompanyName());
			dataList.add(request.getCompanyName());
			dataList.add(request.getCompanyMobileNo());
			String response = commonService.sendWhatsappMsg("send_pdf_client_vendor", request.getMobileNo(), dataList, request.getUrl(),
					true, request.getUserId());
			return true;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return false;
		}

	}

}
