package com.crmportal.service;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.PdfSendWhatsappRequestDto;

@Service
public interface WhatsAppConfigService {

	Boolean sendPdf(PdfSendWhatsappRequestDto request);

}
