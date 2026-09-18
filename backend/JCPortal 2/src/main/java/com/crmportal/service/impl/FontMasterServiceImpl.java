package com.crmportal.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.ConfigurationUtilEntity;
import com.crmportal.entity.FontMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.FontMasterMapper;
import com.crmportal.repository.ConfigurationUtilEntityRepository;
import com.crmportal.repository.FontMasterRepository;
import com.crmportal.request.dto.FontMasterRequestDto;
import com.crmportal.response.dto.FontMasterResponseDto;
import com.crmportal.service.FontMasterService;
import com.crmportal.service.UserFileService;
import com.crmportal.utility.ResponseUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class FontMasterServiceImpl implements FontMasterService {

	@Autowired
	FontMasterRepository fontMasterRepository;

	@Autowired
	UserFileService userFileService;

	@Autowired
	FontMasterMapper fontMasterMapper;

	@Autowired
	ConfigurationUtilEntityRepository configurationUtilEntityRepository;

	@Override
	@Transactional
	public FontMasterResponseDto addOrUpdateFontMaster(@Valid FontMasterRequestDto request, Long fontId)
			throws IOException {

		FontMasterEntity fontMasterEntity;

		Optional<FontMasterEntity> op = fontMasterRepository.findByFontNameAndIsDeleteFalse(request.getFontName());

		if (fontId == -1) {
			fontMasterEntity = new FontMasterEntity();

			if (op.isPresent()) {
				throw new RuntimeException("Font already exist with this name : " + request.getFontName());
			}
		} else {
			fontMasterEntity = fontMasterRepository.findByFontIdAndIsDeleteFalse(fontId)
					.orElseThrow(() -> new RuntimeException("Font not found with id : " + fontId));

			if (op.isPresent() && fontMasterEntity.getFontId() != op.get().getFontId()) {
				throw new RuntimeException("Font already exist with this name : " + request.getFontName());
			}
		}

		fontMasterEntity = fontMasterMapper.requestToEntity(request);
		FontMasterEntity savedFontMasterEntity = fontMasterRepository.save(fontMasterEntity);

		userFileService.storeFile(Long.valueOf(1), ModuleName.FONTS.toString(), savedFontMasterEntity.getFontId(),
				FileType.OTHER.toString(), request.getFont());

		FontMasterResponseDto response = fontMasterMapper.entityToResponse(savedFontMasterEntity);
		return response;

	}

	@Override
	public List<FontMasterResponseDto> getAllActiveFonts() {
		List<FontMasterEntity> fonts = fontMasterRepository.findAllByIsActiveTrueAndIsDeleteFalse();
		List<FontMasterResponseDto> response = fontMasterMapper.entityToResponse(fonts);
		return response;
	}

	@Override
	public List<FontMasterResponseDto> getAllFonts() {
		List<FontMasterEntity> fonts = fontMasterRepository.findAllByIsDeleteFalse();
		List<FontMasterResponseDto> response = fontMasterMapper.entityToResponse(fonts);
		return response;
	}

	@Override
	public FontMasterResponseDto getFontById(Long fontId) {
		FontMasterEntity font = fontMasterRepository.findByFontIdAndIsDeleteFalse(fontId)
				.orElseThrow(() -> new RuntimeException("Font not found with id : " + fontId));
		FontMasterResponseDto response = fontMasterMapper.entityToResponse(font);
		return response;
	}

	@Override
	public Boolean deleteFontById(Long fontId) {
		FontMasterEntity font = fontMasterRepository.findByFontIdAndIsDeleteFalse(fontId)
				.orElseThrow(() -> new RuntimeException("Font not found with id : " + fontId));

		font.setIsDelete(true);

		font = fontMasterRepository.save(font);

		return true;
	}

	@Override
	public Boolean updateFontStatus(Long fontId, Boolean status) {
		FontMasterEntity font = fontMasterRepository.findByFontIdAndIsDeleteFalse(fontId)
				.orElseThrow(() -> new RuntimeException("Font not found with id : " + fontId));

		font.setIsActive(status);

		font = fontMasterRepository.save(font);

		return true;
	}
}
