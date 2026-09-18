package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BanquetHallMasterEntity;
import com.crmportal.entity.BanquetRightsEntity;
import com.crmportal.repository.BanquetHallMasterRepository;
import com.crmportal.repository.BanquetRightsRepository;
import com.crmportal.request.dto.BanquetRightsRequestDto;
import com.crmportal.response.dto.BanquetRightsResponseDto;
import com.crmportal.service.BanquetRightsService;

@Service
public class BanquetRightsServiceImpl implements BanquetRightsService {

	@Autowired
	private BanquetRightsRepository repository;

	@Autowired
	private BanquetHallMasterRepository hallRepository;

	@Override
	@Transactional
	public List<BanquetRightsResponseDto> add(List<BanquetRightsRequestDto> requests, Long userId) {

		List<BanquetRightsEntity> entities = new ArrayList<>();

		if (requests.isEmpty()) {
			return Collections.emptyList();
		}
		repository.deleteAllByUserId(userId);
		for (BanquetRightsRequestDto request : requests) {
			BanquetRightsEntity entity = new BanquetRightsEntity();
			BanquetHallMasterEntity hall = null;
			if (request.getBanquetHallId() != 0) {

				hall = hallRepository.findByIdAndIsDeleteFalse(request.getBanquetHallId())
						.orElseThrow(() -> new RuntimeException("Hall not found"));
			}

			entity.setUserId(userId);
			entity.setBanquetHall(hall);
			entity.setIsAllow(request.getIsAllow() != null ? request.getIsAllow() : false);
			entity.setIsDelete(false);
			entities.add(entity);
		}
		entities = repository.saveAll(entities);
		List<BanquetRightsResponseDto> dtos = new ArrayList<>();
		for (BanquetRightsEntity banquetRightsEntity : entities) {
			BanquetRightsResponseDto dto = toResponse(banquetRightsEntity);
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public List<BanquetRightsResponseDto> getByUser(Long userId, Long memberId) {

		List<BanquetHallMasterEntity> halls = hallRepository.findByUserIdAndIsDeleteFalse(userId);

		List<BanquetRightsEntity> rights = repository.findByUserIdAndIsDeleteFalse(memberId);

		Map<Long, Boolean> rightsMap = rights.stream()
		        .filter(r -> r.getBanquetHall() != null)
		        .collect(Collectors.toMap(
		                r -> r.getBanquetHall().getId(),
		                BanquetRightsEntity::getIsAllow
		        ));

		boolean odcAllow = rights.stream()
		        .filter(r -> r.getBanquetHall() == null)
		        .findFirst()
		        .map(BanquetRightsEntity::getIsAllow)
		        .orElse(false);
		


		List<BanquetRightsResponseDto> response = new ArrayList<>();

		//  ODC
		BanquetRightsResponseDto odcDto = new BanquetRightsResponseDto();
		odcDto.setBanquetHallId(0L);
		odcDto.setBanquetHallName("ODC");
		odcDto.setIsAllow(odcAllow);

		response.add(odcDto);

		for (BanquetHallMasterEntity hall : halls) {

			BanquetRightsResponseDto dto = new BanquetRightsResponseDto();

			dto.setBanquetHallId(hall.getId());
			dto.setBanquetHallName(hall.getHallName());

			dto.setIsAllow(rightsMap.getOrDefault(hall.getId(), false));

			response.add(dto);
		}

		return response;
	}

	@Override
	public Boolean delete(Long id) {

		BanquetRightsEntity entity = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Rights not found"));

		entity.setIsDelete(true);

		repository.save(entity);

		return true;
	}

	private BanquetRightsResponseDto toResponse(BanquetRightsEntity entity) {

		BanquetRightsResponseDto dto = new BanquetRightsResponseDto();

		dto.setUserId(entity.getUserId());

		dto.setBanquetHallId(entity.getBanquetHall() != null ? entity.getBanquetHall().getId() : null);

		dto.setBanquetHallName(entity.getBanquetHall() != null ? entity.getBanquetHall().getHallName() : "ODC");

		dto.setIsAllow(entity.getIsAllow());

		return dto;
	}
}