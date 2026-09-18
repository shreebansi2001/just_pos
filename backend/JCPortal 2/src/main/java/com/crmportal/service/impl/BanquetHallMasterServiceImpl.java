package com.crmportal.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.poifs.macros.Module.ModuleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.BanquetHallImageEntity;
import com.crmportal.entity.BanquetHallMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.repository.BanquetHallImageRepository;
import com.crmportal.repository.BanquetHallMasterRepository;
import com.crmportal.request.dto.BanquetHallRequestDto;
import com.crmportal.response.dto.BanquetHallImageResponseDto;
import com.crmportal.response.dto.BanquetHallResponseDto;
import com.crmportal.service.BanquetHallMasterService;
import com.crmportal.service.UserFileService;

@Service
@Transactional
public class BanquetHallMasterServiceImpl implements BanquetHallMasterService {

	@Autowired
	private BanquetHallMasterRepository hallRepository;

	@Autowired
	private BanquetHallImageRepository imageRepository;

	@Autowired
	private Environment environment;

	@Autowired
	UserFileService userFileService;

	// BCrypt encoder — no extra dependency needed if spring-security is already in
	// pom
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// ── Add or Update ─────────────────────────────────────────────────────────
	@Override
	public BanquetHallResponseDto addOrUpdate(BanquetHallRequestDto request, List<MultipartFile> images) {

		BanquetHallMasterEntity entity;

		if (request.getId() == null || request.getId() == 0 || request.getId() == -1) {
			entity = new BanquetHallMasterEntity();
		} else {
			entity = hallRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Hall not found with id: " + request.getId()));
		}

		entity.setHallName(request.getHallName());
		entity.setCapacity(request.getCapacity());
		entity.setMorningPrice(request.getMorningPrice());
		entity.setEveningPrice(request.getEveningPrice());
		entity.setFullDayPrice(request.getFullDayPrice());
		entity.setExhibitionPrice(request.getExhibitionPrice());
		entity.setCorporatePrice(request.getCorporatePrice());
		entity.setExtraChargesPerHr(request.getExtraChargesPerHr());
		entity.setUserId(request.getUserId());
		entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

		entity.setIsDelete(false);

		// ── Encrypt password only if provided ────────────────────────────
		if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
			entity.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		entity = hallRepository.save(entity);

		// ── Save new images ───────────────────────────────────────────────
		if (images != null && !images.isEmpty()) {
			for (MultipartFile file : images) {
				if (file != null && !file.isEmpty()) {
					BanquetHallImageEntity img = new BanquetHallImageEntity();
					img.setHall(entity);
					img.setIsDelete(false);
					img = imageRepository.save(img);
					try {
						userFileService.storeFile(request.getUserId(), ModuleName.BANQUETHALL.toString(), img.getId(),
								FileType.IMAGE.toString(), file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return toResponse(entity);
	}

	// ── Get All ───────────────────────────────────────────────────────────────
	@Override
	public List<BanquetHallResponseDto> getAll(Long userId) {
		return hallRepository.findByUserIdAndIsDeleteFalse(userId).stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	// ── Get by ID ─────────────────────────────────────────────────────────────
	@Override
	public BanquetHallResponseDto getById(Long id, Long userId) {
		BanquetHallMasterEntity entity = hallRepository.findByIdAndUserIdAndIsDeleteFalse(id, userId)
				.orElseThrow(() -> new RuntimeException("Hall not found with id: " + id));
		return toResponse(entity);
	}

	// ── Delete Hall ───────────────────────────────────────────────────────────
	@Override
	public Boolean delete(Long id, Long userId) {
		BanquetHallMasterEntity entity = hallRepository.findByIdAndUserIdAndIsDeleteFalse(id, userId)
				.orElseThrow(() -> new RuntimeException("Hall not found with id: " + id));
		entity.setIsDelete(true);
		// soft-delete all images too
		imageRepository.softDeleteAllByHallId(id);
		hallRepository.save(entity);
		return true;
	}

	// ── Delete single image ───────────────────────────────────────────────────
	@Override
	public Boolean deleteImage(Long imageId) {
		BanquetHallImageEntity img = imageRepository.findById(imageId)
				.orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
		img.setIsDelete(true);
		imageRepository.save(img);
		return true;
	}

	// ── Verify Password ───────────────────────────────────────────────────────
	@Override
	public Boolean verifyPassword(String hallName, String rawPassword) {
		BanquetHallMasterEntity entity = hallRepository.findByHallNameAndIsDeleteFalse(hallName)
				.orElseThrow(() -> new RuntimeException("Hall not found: " + hallName));

		if (entity.getPassword() == null || entity.getPassword().isEmpty()) {
			throw new RuntimeException("No password set for this hall");
		}

		return passwordEncoder.matches(rawPassword, entity.getPassword());
	}


	// ── Entity → Response ─────────────────────────────────────────────────────
	private BanquetHallResponseDto toResponse(BanquetHallMasterEntity entity) {

		BanquetHallResponseDto dto = new BanquetHallResponseDto();
		dto.setId(entity.getId());
		dto.setHallName(entity.getHallName());
		dto.setCapacity(entity.getCapacity());
		dto.setMorningPrice(entity.getMorningPrice());
		dto.setEveningPrice(entity.getEveningPrice());
		dto.setFullDayPrice(entity.getFullDayPrice());
		dto.setExhibitionPrice(entity.getExhibitionPrice());
		dto.setCorporatePrice(entity.getCorporatePrice());
		dto.setExtraChargesPerHr(entity.getExtraChargesPerHr());
		dto.setUserId(entity.getUserId());
		dto.setIsActive(entity.getIsActive());
		dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);

		// ── Images ────────────────────────────────────────────────────────
		List<BanquetHallImageResponseDto> imgDtos = imageRepository.findByHallIdAndIsDeleteFalse(entity.getId())
				.stream().map(img -> {
					BanquetHallImageResponseDto iDto = new BanquetHallImageResponseDto();
					iDto.setId(img.getId());
					iDto.setImagePath(img.getImagePath());
					iDto.setImageUrl(environment.getProperty("app.image.url") + img.getImagePath());
					return iDto;
				}).collect(Collectors.toList());

		dto.setImages(imgDtos);

		// NOTE: password is intentionally NOT included in response
		return dto;
	}

	@Override
	public BanquetHallResponseDto toggleStatus(Long id) {
		BanquetHallMasterEntity entity = hallRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Hall not found with id: " + id));

		// flip: true → false, false → true
		entity.setIsActive(entity.getIsActive() == null || !entity.getIsActive());
		hallRepository.save(entity);
		return toResponse(entity);
	}
}