package com.crmportal.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.crmportal.request.dto.ConfigurationUtilDto;

public interface UserConfigurationService {

    Map<String, Object> saveUserConfig(ConfigurationUtilDto dto);
    
    Map<String, Object> getUserConfig(Long user);
}
