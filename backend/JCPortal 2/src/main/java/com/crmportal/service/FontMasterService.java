package com.crmportal.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.FontMasterRequestDto;
import com.crmportal.response.dto.FontMasterResponseDto;

@Service
public interface FontMasterService {

	FontMasterResponseDto addOrUpdateFontMaster(@Valid FontMasterRequestDto request, Long fontId) throws IOException;

	List<FontMasterResponseDto> getAllActiveFonts();

	FontMasterResponseDto getFontById(Long fontId);

	List<FontMasterResponseDto> getAllFonts();

	Boolean deleteFontById(Long fontId);

	Boolean updateFontStatus(Long fontId, Boolean status);

}
