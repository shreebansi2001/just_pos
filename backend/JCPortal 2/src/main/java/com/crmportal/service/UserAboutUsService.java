package com.crmportal.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.UserAboutUsRequestDto;
import com.crmportal.response.dto.UserAboutUsResponseDto;

@Service
public interface UserAboutUsService {

	UserAboutUsResponseDto addOrUpdateAboutUs(@Valid UserAboutUsRequestDto request);

	UserAboutUsResponseDto getByUser(Long userId);

}
