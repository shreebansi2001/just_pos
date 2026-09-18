package com.crmportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.crmportal.request.dto.CreatePaymentSessionRequestDto;
import com.crmportal.request.dto.ZohoPaymentSessionRequest;
import com.crmportal.response.dto.ZohoPaymentSessionResponse;
import com.crmportal.service.ZohoOAuthService;

import java.math.RoundingMode;
import java.util.stream.Collectors;

@Service
public class ZohoPaymentSessionService {

    @Value("${zoho.pay.base-url}")
    private String baseUrl;

    @Value("${zoho.account-id}")
    private String accountId;

    @Autowired
    private ZohoOAuthService oAuthService;
    private final RestTemplate restTemplate = new RestTemplate();

    public ZohoPaymentSessionResponse createSession(CreatePaymentSessionRequestDto dto) {

        String url = baseUrl + "/paymentsessions?account_id=" + accountId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(
            "Authorization",
            "Zoho-oauthtoken " + oAuthService.getAccessToken()
        );

        ZohoPaymentSessionRequest zoho = new ZohoPaymentSessionRequest();
        zoho.setAmount(dto.getAmount().setScale(2, RoundingMode.HALF_UP));
        zoho.setCurrency(dto.getCurrency());
        zoho.setExpiresIn(dto.getExpiresIn() != null ? dto.getExpiresIn() : 900);
        zoho.setDescription(dto.getDescription());
        zoho.setInvoiceNumber(dto.getInvoiceNumber());
        zoho.setReferenceNumber(dto.getReferenceNumber());

        if (dto.getMetaData() != null && !dto.getMetaData().isEmpty()) {
            zoho.setMetaData(
                dto.getMetaData().stream().map(m -> {
                    ZohoPaymentSessionRequest.MetaData md =
                            new ZohoPaymentSessionRequest.MetaData();
                    md.setKey(m.getKey());
                    md.setValue(m.getValue());
                    return md;
                }).collect(Collectors.toList())
            );
        }

        HttpEntity<ZohoPaymentSessionRequest> entity =
                new HttpEntity<>(zoho, headers);

        ResponseEntity<ZohoPaymentSessionResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        ZohoPaymentSessionResponse.class
                );

        return response.getBody();
    }
}
