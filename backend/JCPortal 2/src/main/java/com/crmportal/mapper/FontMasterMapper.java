package com.crmportal.mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.crmportal.entity.FontMasterEntity;
import com.crmportal.request.dto.FontMasterRequestDto;
import com.crmportal.response.dto.FontMasterResponseDto;

@Component
public class FontMasterMapper {

	@Autowired
	Environment environment;
	
	public FontMasterEntity requestToEntity(FontMasterRequestDto request) {
		FontMasterEntity entity = new FontMasterEntity();
		entity.setFontName(request.getFontName());
		return entity;
	}
	
	public FontMasterResponseDto entityToResponse(FontMasterEntity entity) {
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
		
		FontMasterResponseDto response = new FontMasterResponseDto();
		response.setFontId(entity.getFontId());
		response.setFontName(entity.getFontName());
		response.setFontPath(environment.getProperty("ws_image_path") + entity.getFontPath());
		response.setIsActive(entity.getIsActive());
		response.setIsDelete(entity.getIsDelete());
		response.setCreatedDate(entity.getCreatedAt().format(outputFormatter));
		response.setUpdatedDate(entity.getUpdatedAt() != null ? entity.getUpdatedAt().format(outputFormatter) : null);
		
		return response;
	}
	
	public List<FontMasterResponseDto> entityToResponse(List<FontMasterEntity> entities) {
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
		
		return entities.stream().map((entity) -> {
			FontMasterResponseDto response = new FontMasterResponseDto();
			response.setFontId(entity.getFontId());
			response.setFontName(entity.getFontName());
			response.setFontPath(environment.getProperty("ws_image_path") + entity.getFontPath());
			response.setIsActive(entity.getIsActive());
			response.setIsDelete(entity.getIsDelete());
			response.setCreatedDate(entity.getCreatedAt().format(outputFormatter));
			response.setUpdatedDate(entity.getUpdatedAt() != null ? entity.getUpdatedAt().format(outputFormatter) : null);
			
			return response;
		})
		.collect(Collectors.toList());
	}
}
