package com.crmportal.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.response.dto.UserBasicFileResponseDto;

@Service
public interface UserFileService {

	Map<String, Object> storeFile(Long userId, String name, Long moduleRecordId, String name2, MultipartFile file) throws IOException;
}
