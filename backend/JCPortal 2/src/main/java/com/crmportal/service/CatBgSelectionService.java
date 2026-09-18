package com.crmportal.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.crmportal.request.dto.CatBgSelectionRequestDto;
import com.crmportal.response.dto.CatBgSelectionResponseDto;

public interface CatBgSelectionService {

	CatBgSelectionResponseDto addOrUpdate(CatBgSelectionRequestDto request, MultipartFile image, Long userId);

	List<CatBgSelectionResponseDto> getAll(Long userId, Boolean isCatImg);

	List<CatBgSelectionResponseDto> getByType(Long userId, Boolean isCatImg);

	CatBgSelectionResponseDto getById(Long id);

	Boolean delete(Long id);
}