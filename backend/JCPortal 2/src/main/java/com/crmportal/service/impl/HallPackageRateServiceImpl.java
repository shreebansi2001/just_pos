package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.crmportal.entity.BanquetHallMasterEntity;
import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.entity.HallPackageRateEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.BanquetHallMasterRepository;
import com.crmportal.repository.CustomPackageRepository;
import com.crmportal.repository.HallPackageRateRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.HallPackageRateRequestDto;
import com.crmportal.response.dto.HallPackagePriceResponseDto;
import com.crmportal.response.dto.HallPackageRateResponseDto;
import com.crmportal.service.HallPackageRateService;

@Service
public class HallPackageRateServiceImpl implements HallPackageRateService {

	@Autowired
	HallPackageRateRepository hallPackageRateRepository;

	@Autowired
	BanquetHallMasterRepository banquetHallMasterRepository;

	@Autowired
	CustomPackageRepository customPackageRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Override
	@Transactional
	public Boolean addOrUpdate(HallPackageRateRequestDto request) {

		validateRequest(request);

		BanquetHallMasterEntity hall = banquetHallMasterRepository.findById(request.getHallId())
				.orElseThrow(() -> new ValidationException("Hall not found with id: " + request.getHallId()));

		CustomPackageEntity customPackage = customPackageRepository.findById(request.getPackageId())
				.orElseThrow(() -> new ValidationException("Package not found with id: " + request.getPackageId()));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new ValidationException("User not found with id: " + request.getUserId()));

		HallPackageRateEntity entity;

		if (request.getId() != null && request.getId() != 0 && request.getId() != -1) {
			entity = hallPackageRateRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new ValidationException("Rate not found with id: " + request.getId()));
		} else {
			boolean duplicateExists = hallPackageRateRepository
					.findByHall_IdAndCustomPackage_IdAndTierLabelAndIsDeleteFalseAndUser(request.getHallId(),
							request.getPackageId(), request.getTierLabel().trim(), user)
					.isPresent();

			if (duplicateExists) {
				throw new ValidationException("A rate already exists for this hall, package and tier");
			}

			entity = new HallPackageRateEntity();
		}

		entity.setHall(hall);
		entity.setCustomPackage(customPackage);
		entity.setTierLabel(request.getTierLabel().trim());
		entity.setMinGuests(request.getMinGuests());
		entity.setPackageSequence(request.getPackageSequence());
		entity.setTierSequence(request.getTierSequence());
		entity.setPrice(request.getPrice());
		entity.setIsActive(true);
		entity.setUpdatedAt(LocalDateTime.now());
		entity.setUser(user);

		hallPackageRateRepository.save(entity);

		return true;
	}

	private void validateRequest(HallPackageRateRequestDto request) {
		if (request == null) {
			throw new ValidationException("Request body cannot be null");
		}
		if (request.getHallId() == null) {
			throw new ValidationException("hallId is required");
		}
		if (request.getPackageId() == null) {
			throw new ValidationException("packageId is required");
		}
		if (!StringUtils.hasText(request.getTierLabel())) {
			throw new ValidationException("tierLabel is required");
		}
		if (request.getMinGuests() == null) {
			throw new ValidationException("minGuests is required");
		}
		if (request.getMinGuests() < 0) {
			throw new ValidationException("minGuests cannot be negative");
		}
		if (request.getPrice() == null) {
			throw new ValidationException("price is required");
		}
		if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
			throw new ValidationException("price cannot be negative");
		}
	}

	@Override
	public List<HallPackageRateResponseDto> getAll(Long userId, Boolean isActive, Long hallId, Long packageId) {

		if (userId == null) {
			throw new ValidationException("userId is required");
		}

		List<HallPackageRateEntity> entities = hallPackageRateRepository.findAllByFilters(userId, isActive, hallId,
				packageId);

		return entities.stream().map(this::mapToResponseDto).collect(Collectors.toList());
	}

	private HallPackageRateResponseDto mapToResponseDto(HallPackageRateEntity entity) {
		HallPackageRateResponseDto dto = new HallPackageRateResponseDto();

		dto.setId(entity.getId());

		if (entity.getHall() != null) {
			dto.setHallId(entity.getHall().getId());
			dto.setHallName(entity.getHall().getHallName());
		}

		if (entity.getCustomPackage() != null) {
			dto.setPackageId(entity.getCustomPackage().getId());
			dto.setPackageName(entity.getCustomPackage().getNameEnglish());
			dto.setPackageNameHindi(entity.getCustomPackage().getNameHindi());
			dto.setPackageNameGujarati(entity.getCustomPackage().getNameGujarati());
		}

		dto.setPackageSequence(entity.getPackageSequence());
		dto.setTierLabel(entity.getTierLabel());
		dto.setMinGuests(entity.getMinGuests());
		dto.setTierSequence(entity.getTierSequence());
		dto.setPrice(entity.getPrice());
		dto.setIsActive(entity.getIsActive());

		return dto;
	}

	@Override
	public HallPackagePriceResponseDto getPrice(Long hallId, Long packageId, Integer functionPax) {

		if (hallId == null) {
			throw new ValidationException("hallId is required");
		}
		if (packageId == null) {
			throw new ValidationException("packageId is required");
		}
		if (functionPax == null) {
			throw new ValidationException("functionPax is required");
		}
		if (functionPax < 0) {
			throw new ValidationException("functionPax cannot be negative");
		}

		List<HallPackageRateEntity> matches = hallPackageRateRepository.findApplicableRates(hallId, packageId,
				functionPax);

		HallPackagePriceResponseDto dto = new HallPackagePriceResponseDto();
		dto.setHallId(hallId);
		dto.setPackageId(packageId);
		dto.setFunctionPax(functionPax);

		if (matches.isEmpty()) {
			dto.setPrice(null);
			dto.setFound(false);
			dto.setMessage("No applicable rate found for " + functionPax + " guests on this hall/package");
			return dto;
		}

		HallPackageRateEntity match = matches.get(0);
		dto.setTierLabel(match.getTierLabel());
		dto.setMinGuests(match.getMinGuests());
		dto.setPrice(match.getPrice());
		dto.setFound(true);
		dto.setMessage("Price found successfully");

		return dto;
	}

	@Override
	@Transactional
	public Boolean updateStatus(Long id, Boolean isActive) {

		if (id == null) {
			throw new ValidationException("id is required");
		}
		if (isActive == null) {
			throw new ValidationException("isActive is required");
		}

		HallPackageRateEntity entity = hallPackageRateRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new ValidationException("Rate not found with id: " + id));

		if (Boolean.TRUE.equals(entity.getIsDelete())) {
			throw new ValidationException("Cannot update status of a deleted rate");
		}

		entity.setIsActive(isActive);
		entity.setUpdatedAt(LocalDateTime.now());

		hallPackageRateRepository.save(entity);

		return true;
	}

	@Override
	@Transactional
	public Boolean delete(Long id) {

		if (id == null) {
			throw new ValidationException("id is required");
		}

		HallPackageRateEntity entity = hallPackageRateRepository.findById(id)
				.orElseThrow(() -> new ValidationException("Rate not found with id: " + id));

		if (Boolean.TRUE.equals(entity.getIsDelete())) {
			throw new ValidationException("Rate is already deleted");
		}

		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());

		hallPackageRateRepository.save(entity);

		return true;
	}
}