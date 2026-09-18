package com.crmportal.service;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.NamePlateImagesRequestDto;
import com.crmportal.response.dto.NamePlateImagesResponseDto;

@Service
public interface NamePlateImagesService {

	NamePlateImagesResponseDto addUpdateNamePlateImages(NamePlateImagesRequestDto request);

}
