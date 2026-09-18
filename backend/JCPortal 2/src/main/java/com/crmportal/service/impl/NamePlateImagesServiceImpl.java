package com.crmportal.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.NamePlateImagesEntity;
import com.crmportal.repository.NamePlateImagesRepository;
import com.crmportal.request.dto.NamePlateImagesRequestDto;
import com.crmportal.response.dto.NamePlateImagesResponseDto;
import com.crmportal.service.NamePlateImagesService;
import com.crmportal.service.UserFileService;

@Service
public class NamePlateImagesServiceImpl implements NamePlateImagesService {

	@Autowired
	UserFileService userFileService;
	
	@Autowired
	UserFileServiceImpl userFileServiceImpl;
	
	@Autowired
	NamePlateImagesRepository namePlateImagesRepository;
	
	@Value("${app.image.path}")
	private String appImagePath;
	
	@Override
	@Transactional
	public NamePlateImagesResponseDto addUpdateNamePlateImages(NamePlateImagesRequestDto request) {
		try {

			if (request.getTemplateMasterId() == null) {
				throw new RuntimeException("Template master id is required");
			}

			if (request.getImages() == null || request.getImages().isEmpty()) {
				throw new RuntimeException("At least one image is required");
			}

			List<String> imagePaths = new ArrayList<>();

			List<NamePlateImagesEntity> oldImages = namePlateImagesRepository
					.findByTemplateMasterId(request.getTemplateMasterId());

			for (NamePlateImagesEntity oldImage : oldImages) {
				if (oldImage.getImagePath() != null && !oldImage.getImagePath().isEmpty()) {
					File oldFile = new File(appImagePath + oldImage.getImagePath());
					if (oldFile.exists()) {
						oldFile.delete();
					}
				}
			}

			namePlateImagesRepository.deleteAll(oldImages);

			String uploadDirectory = appImagePath + "nameplate/";

			File directory = new File(uploadDirectory);

			if (!directory.exists()) {
				directory.mkdirs();
			}

			for (MultipartFile file : request.getImages()) {
				if (file == null || file.isEmpty()) {
					continue;
				}

				String originalFileName = file.getOriginalFilename();

				String extension = "";

				if (originalFileName != null && originalFileName.contains(".")) {
					extension = originalFileName.substring(originalFileName.lastIndexOf("."));
				}

				String fileName = "nameplate_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString()
						+ extension;

				File destination = new File(directory, fileName);

				file.transferTo(destination);

				String imagePath = "/nameplate/" + fileName;

				NamePlateImagesEntity entity = new NamePlateImagesEntity();

				entity.setTemplateMasterId(request.getTemplateMasterId());
				entity.setImagePath(imagePath);
				entity.setCreatedAt(LocalDateTime.now());
				entity.setUpdatedAt(LocalDateTime.now());

				namePlateImagesRepository.save(entity);

				imagePaths.add(imagePath);
			}

			List<NamePlateImagesEntity> savedImages = namePlateImagesRepository
					.findByTemplateMasterId(request.getTemplateMasterId());

			Map<Long, String> imageMap = savedImages.stream()
					.collect(Collectors.toMap(NamePlateImagesEntity::getId, NamePlateImagesEntity::getImagePath));
			
			return new NamePlateImagesResponseDto(request.getTemplateMasterId(), imageMap);

		} catch (IOException e) {

			e.printStackTrace();

			throw new RuntimeException("Failed to upload name plate images", e);
		}
	}
}
