package com.crmportal.service;

import com.crmportal.request.dto.GenerateLinkRequestDto;
import com.crmportal.request.dto.VerifyLinkRequestDto;
import com.crmportal.response.dto.MenuShareDataResponseDto;
import com.crmportal.response.dto.MenuShareLinkResponseDto;

public interface MenuShareLinkService {
    MenuShareLinkResponseDto generateLink(GenerateLinkRequestDto request);
    MenuShareDataResponseDto verifyAndGetData(VerifyLinkRequestDto request);
}