package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crmportal.entity.TermsAndConditionFeaturesEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserTermsAndConditionEntity;
import com.crmportal.repository.TermsAndConditionFeaturesRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserTermsAndConditionRepository;
import com.crmportal.request.dto.TermsAndConditionFeaturesRequestDto;
import com.crmportal.request.dto.UserTermsAndConditionRequestDto;
import com.crmportal.response.dto.UserTermsAndConditionResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.UserTermsAndConditionService;

@Service
public class UserTermsAndConditionServiceImpl implements UserTermsAndConditionService {

	@Autowired
	UserTermsAndConditionRepository userTermsAndConditionRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	TermsAndConditionFeaturesRepository termsAndConditionFeaturesRepository;

	@Autowired
	CommonService commonService;

	@Override
	@Transactional
	public Boolean addUpdate(UserTermsAndConditionRequestDto request) {
		if (request == null) {
			return false;
		}
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
		UserTermsAndConditionEntity entity;
		if (request.getId() == -1) {
			entity = new UserTermsAndConditionEntity();
		} else {
			entity = userTermsAndConditionRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Terms & Condition not found with id: " + request.getId()));
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}
		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameHindi(request.getNameHindi());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setUser(user);
		entity = userTermsAndConditionRepository.save(entity);

		termsAndConditionFeaturesRepository.deleteAllByUserTermsConditionId(entity.getId());

		List<TermsAndConditionFeaturesEntity> entities = new ArrayList<>();
		for (TermsAndConditionFeaturesRequestDto termscondition : request.getTermsAndConditionFeatures()) {
			TermsAndConditionFeaturesEntity featuresEntity = new TermsAndConditionFeaturesEntity();
			featuresEntity.setDescription(termscondition.getDescription());
			featuresEntity.setDescriptionGujarati(termscondition.getDescription_gujarati());
			featuresEntity.setDescriptionHindi(termscondition.getDescription_hindi());
			featuresEntity.setIsDelete(false);
			featuresEntity.setUserTermsConditionId(entity.getId());
			entities.add(featuresEntity);
		}
		termsAndConditionFeaturesRepository.saveAll(entities);
		return true;
	}

	@Override
	public Map<String, Object> getAll(Long userId, String moduleName, Boolean isActive) {

		List<Object[]> pageResult = userTermsAndConditionRepository.findAllWithFeatures(userId, moduleName, isActive);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Map<Long, UserTermsAndConditionResponseDto> dtoMap = new LinkedHashMap<>();

		for (Object[] row : pageResult) {

		    UserTermsAndConditionEntity user = (UserTermsAndConditionEntity) row[0];
		    TermsAndConditionFeaturesEntity feature = (TermsAndConditionFeaturesEntity) row[1];

		    UserTermsAndConditionResponseDto dto = dtoMap.get(user.getId());

		    if (dto == null) {
		        dto = new UserTermsAndConditionResponseDto();
		        dto.setId(user.getId());
		        dto.setIsActive(user.getIsActive());
		        dto.setIsDelete(user.getIsDelete());
		        dto.setNameEnglish(user.getNameEnglish());
		        dto.setNameHindi(user.getNameHindi());
		        dto.setNameGujarati(user.getNameGujarati());
		        dto.setUserId(user.getUser().getId());

		        if (user.getCreatedAt() != null) {
		            dto.setCreatedAt(user.getCreatedAt().format(formatter));
		        }

		        dto.setFeatures(new ArrayList<>());
		        dtoMap.put(user.getId(), dto);
		    }

		    if (feature != null && feature.getDescription() != null) {
		        TermsAndConditionFeaturesRequestDto featureDto = new TermsAndConditionFeaturesRequestDto();
		        featureDto.setDescription(feature.getDescription());
		        featureDto.setDescription_hindi(feature.getDescriptionHindi());
		        featureDto.setDescription_gujarati(feature.getDescriptionGujarati());

		        dto.getFeatures().add(featureDto);
		    }
		}

		List<UserTermsAndConditionResponseDto> dtos = new ArrayList<>(dtoMap.values());

		Map<String, Object> response = new HashMap<>();
		response.put("TermsAndConditions", dtos);
		return response;
	}

	@Override
	@Transactional
	public Boolean delete(Long id) {

		UserTermsAndConditionEntity entity = userTermsAndConditionRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Terms & Condition not found with id: " + id));

		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());

		userTermsAndConditionRepository.save(entity);
		termsAndConditionFeaturesRepository.deleteAllByUserTermsConditionId(id);
		return true;
	}

	@Override
	public Boolean isActive(Long id, Boolean isActive) {
		UserTermsAndConditionEntity entity = userTermsAndConditionRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Terms & Condition not found with id: " + id));

		entity.setIsActive(isActive);
		entity.setUpdatedAt(LocalDateTime.now());

		userTermsAndConditionRepository.save(entity);

		return true;
	}
}
