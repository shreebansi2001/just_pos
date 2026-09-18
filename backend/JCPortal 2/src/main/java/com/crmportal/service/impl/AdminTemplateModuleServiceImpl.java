package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AdminTemplateModuleEntity;
import com.crmportal.entity.ExclusiveThemePaymentEntity;
import com.crmportal.entity.NamePlateImagesEntity;
import com.crmportal.entity.TemplateMappingEntity;
import com.crmportal.entity.TemplateMasterEntity;
import com.crmportal.entity.TemplateModuleMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.AdminTemplateModuleMapper;
import com.crmportal.mapper.TemplateMasterMapper;
import com.crmportal.mapper.TemplateModuleMasterMapper;
import com.crmportal.repository.*;
import com.crmportal.request.dto.AdminTemplateModuleFontAndFontSizeRequestDto;
import com.crmportal.request.dto.AdminTemplateModuleRequestDTO;
import com.crmportal.response.dto.AdminTemplateModuleResponseDto;
import com.crmportal.response.dto.TemplateMappingResponseDto;
import com.crmportal.response.dto.TemplateMasterResponseDto;
import com.crmportal.response.dto.TemplateModuleMasterResponseDto;
import com.crmportal.service.AdminTemplateModuleService;
import com.crmportal.service.CommonService;

import net.bytebuddy.implementation.bytecode.Throw;

@Service
public class AdminTemplateModuleServiceImpl implements AdminTemplateModuleService {

	private final PlanInformationEntityRepository planInformationEntityRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	TemplateModuleMasterRepository templateModuleMasterRepository;

	@Autowired
	TemplateMasterRepository templateMasterRepository;

	@Autowired
	AdminTemplateModuleRepository adminTemplateModuleRepository;

	@Autowired
	AdminTemplateModuleMapper adminTemplateModuleMapper;

	@Autowired
	TemplateMappingRepository templateMappingRepository;

	@Autowired
	TemplateModuleMasterMapper templateModuleMasterMapper;

	@Autowired
	TemplateMasterMapper templateMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	Environment environment;

	@Autowired
	ExclusiveThemePaymentRepository exclusiveThemePaymentRepository;
	
	@Autowired
	NamePlateImagesRepository namePlateImageRepository;

	AdminTemplateModuleServiceImpl(PlanInformationEntityRepository planInformationEntityRepository) {
		this.planInformationEntityRepository = planInformationEntityRepository;
	}

	@Override
	public Boolean addOrUpdateAdminTemplateModule(List<AdminTemplateModuleRequestDTO> requests, Long id) {

		for (AdminTemplateModuleRequestDTO request : requests) {

			UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
					.orElseThrow(() -> new RuntimeException("User Id not found with : " + request.getUserId()));

			TemplateModuleMasterEntity templateModuleMasterEntity = templateModuleMasterRepository
					.findByIdAndIsDeleteFalse(request.getTemplateModuleMasterId())
					.orElseThrow(() -> new RuntimeException(
							"Template module master not found with : " + request.getTemplateModuleMasterId()));

			TemplateMasterEntity templateMasterEntity = templateMasterRepository
					.findByIdAndIsDeleteFalse(request.getTemplateMasterId()).orElseThrow(() -> new RuntimeException(
							"Template master not found with : " + request.getTemplateMasterId()));

			TemplateMappingEntity templateMappingEntity = Optional
					.ofNullable(templateMappingRepository
							.findByIdAndIsDeleteFalse(templateMasterEntity.getTemplateMapping().getId()))
					.orElseThrow(() -> new RuntimeException("Template Mapping not found"));

			List<AdminTemplateModuleEntity> adminTemplateModuleEntity = adminTemplateModuleRepository
					.findByTemplateMasterAndUserIdAndIsDeleteFalse(templateMasterEntity, user.getId());
			if (!adminTemplateModuleEntity.isEmpty()) {
				throw new RuntimeException("Template Already Assigned");
			}

			AdminTemplateModuleEntity entity = adminTemplateModuleMapper.requestToEntity(request);

			entity.setTemplateMaster(templateMasterEntity);
			entity.setTemplateModuleMaster(templateModuleMasterEntity);
			entity.setTemplateMapping(templateMappingEntity);
			entity.setCatFontId(templateMasterEntity.getCatFontId());
			entity.setItemFontId(templateMasterEntity.getItemFontId());
			entity.setSloganFontId(templateMasterEntity.getSloganFontId());
			entity.setCatFontSize(templateMasterEntity.getCatFontSize());
			entity.setItemFontSize(templateMasterEntity.getItemFontSize());
			entity.setSloganFontSize(templateMasterEntity.getSloganFontSize());
			entity.setUserId(request.getUserId());
			adminTemplateModuleRepository.save(entity);
		}

		return true;
	}

	@Override
	public List<AdminTemplateModuleResponseDto> getAllAdminTemplateModule(Long userId, Long templateModuleId,
			Boolean isExclusive) {

		List<AdminTemplateModuleResponseDto> responseDtos = new ArrayList<>();
		List<AdminTemplateModuleEntity> entitys = new ArrayList<>();

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId).orElse(null);

		if (templateModuleId != null) {
			entitys = adminTemplateModuleRepository
					.findAllByUserIdAndIsDeleteFalseAndTemplateModuleMaster_IdOrderByTemplateMapping_SortorderAsc(
							userId, templateModuleId);
		} else {
			if (isExclusive == null || !isExclusive) {
				entitys = adminTemplateModuleRepository
						.findAllByUserIdAndIsDeleteFalseOrderByTemplateModuleMaster_IdAscTemplateMapping_SortorderAsc(
								userId);
			} else {
				entitys = adminTemplateModuleRepository.getAllExclusiveThemeByUserIdAndIsDeleteFalse(userId);
			}
		}

		for (AdminTemplateModuleEntity entity : entitys) {
			
			AdminTemplateModuleResponseDto responseDto = adminTemplateModuleMapper.entityToResponse(entity);

			TemplateMasterResponseDto templateMaster = responseDto.getTemplateMaster();
			
			String templateName = templateMaster.getName() != null
			        ? templateMaster.getName().trim()
			        : "";

			Set<String> counterNamePlateTemplates = new HashSet<String>(
			        Arrays.asList(
			                "Counter Name Plate",
			                "Ridhi Sidhi Name Plate",
			                "Paariso Name Plate",
			                "Paariso Name Plate - 2",
			                "Amoncar Name Plate A3 (3 X 3)",
			                "Amoncar Name Plate (A4 Page 2 X 2)",
			                "Amoncar Name Plate (A3 Page Tent 6 X 3)",
			                "Amoncar Name Plate (A4 Page Tent 4 X 2)",
			                "Amoncar Name Plate (A3 Page 2 X 2)",
			                "Amoncar Name Plate A4",
			                "Amoncar Name Plate A3"
			        )
			);
			
			boolean isCounterNamePlate = false;
			
			for (String name : counterNamePlateTemplates) {
			    if (name.equalsIgnoreCase(templateName)) {
			        isCounterNamePlate = true;
			        break;
			    }
			}

			templateMaster.setDummyPdf(environment.getProperty("app.image.url") + templateMaster.getDummyPdf());
			templateMaster.setFrontPage(environment.getProperty("app.image.url") + templateMaster.getFrontPage());
			templateMaster
					.setSecondFrontPage(environment.getProperty("app.image.url") + templateMaster.getSecondFrontPage());
			templateMaster.setWatermark(environment.getProperty("app.image.url") + templateMaster.getWatermark());
			templateMaster.setLastMainPage(environment.getProperty("app.image.url") + templateMaster.getLastMainPage());
			templateMaster.setIsDefault(responseDto.getTemplateMaster().getIsDefault());
			templateMaster.setDescription(responseDto.getTemplateMaster().getDescription());
			templateMaster.setPrice(responseDto.getTemplateMaster().getPrice());
			templateMaster.setNamePlateBg(environment.getProperty("app.image.url") + templateMaster.getNamePlateBg());
			templateMaster.setCatFontId(templateMaster.getCatFontId());
			templateMaster.setItemFontId(templateMaster.getItemFontId());
			templateMaster.setSloganFontId(templateMaster.getSloganFontId());
			templateMaster.setCatFontSize(templateMaster.getCatFontSize());
			templateMaster.setItemFontSize(templateMaster.getItemFontSize());
			templateMaster.setSloganFontSize(templateMaster.getSloganFontSize());
			templateMaster.setIsCounterNamePlate(isCounterNamePlate);
			
			List<NamePlateImagesEntity> namePlateImages = namePlateImageRepository
					.findByTemplateMasterId(entity.getTemplateMaster().getId());
			
			if(namePlateImages != null && !namePlateImages.isEmpty()) {
				Map<Long, String> imageMap = namePlateImages.stream()
			            .collect(Collectors.toMap(
			                    NamePlateImagesEntity::getId,
			                    image -> environment.getProperty("app.image.url") + image.getImagePath()
			            ));

				templateMaster.setNamePlateImages(imageMap);
			}
			
			responseDto.setTemplateMaster(templateMaster);

			ExclusiveThemePaymentEntity exclusiveThemePaymentEntity = exclusiveThemePaymentRepository
					.findByAdminTemplateId(entity.getId()).orElse(null);
			if (exclusiveThemePaymentEntity != null) {
				responseDto.setIsPayment(exclusiveThemePaymentEntity.getIsPayment());
			} else {
				responseDto.setIsPayment(false);
			}
			TemplateMappingResponseDto mappingResponseDto = new TemplateMappingResponseDto();
			mappingResponseDto.setId(entity.getTemplateMapping().getId());
			mappingResponseDto.setNameEnglish(entity.getTemplateMapping().getNameEnglish());
			mappingResponseDto.setNameGujarati(entity.getTemplateMapping().getNameGujarati());
			mappingResponseDto.setNameHindi(entity.getTemplateMapping().getNameHindi());
			mappingResponseDto.setNamePlateType(entity.getTemplateMapping().getNamePlateType());
			mappingResponseDto.setSortorder(entity.getTemplateMapping().getSortorder());
			mappingResponseDto.setTemplateModuleId(entity.getTemplateMapping().getTemplateModule().getId());
			mappingResponseDto.setIsDate(entity.getTemplateMapping().getIsDate());
			mappingResponseDto
					.setTemplateModuleNameEnglish(entity.getTemplateMapping().getTemplateModule().getNameEnglish());
			mappingResponseDto
					.setTemplateModuleNameHindi(entity.getTemplateMapping().getTemplateModule().getNameHindi());
			mappingResponseDto
					.setTemplateModuleNameGujarati(entity.getTemplateMapping().getTemplateModule().getNameGujarati());
			responseDto.setTemplateMappingResponseDto(mappingResponseDto);
			responseDto.setUserName(userMasterEntity.getFirstName() + " " + userMasterEntity.getLastName());
			responseDto.setEmail(userMasterEntity.getEmail());
			responseDto.setMobileNo(userMasterEntity.getContactNo());
			responseDtos.add(responseDto);
		}

		return responseDtos;
	}

	@Override
	public AdminTemplateModuleResponseDto getAdminTemplateModuleById(Long id, Long userId) {
		AdminTemplateModuleEntity entity = adminTemplateModuleRepository.findByIdAndUserIdAndIsDeleteFalse(id, userId)
				.orElseThrow(() -> new RuntimeException("Admin Template Module not found with id : " + id));
		AdminTemplateModuleResponseDto response = adminTemplateModuleMapper.entityToResponse(entity);
		return response;
	}

	@Override
	public AdminTemplateModuleResponseDto getAdminTemplateModuleById(Long id) {
		AdminTemplateModuleEntity entity = adminTemplateModuleRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Admin Template Module not found with id : " + id));
		AdminTemplateModuleResponseDto response = adminTemplateModuleMapper.entityToResponse(entity);

		TemplateModuleMasterResponseDto templateModuleMasterResponseDto = templateModuleMasterMapper
				.entityToResponse(entity.getTemplateModuleMaster());

		TemplateMappingResponseDto templateMappingResponseDto = new TemplateMappingResponseDto();
		templateMappingResponseDto.setId(entity.getTemplateMapping().getId());
		templateMappingResponseDto.setNameEnglish(entity.getTemplateMapping().getNameEnglish());
		templateMappingResponseDto.setNameHindi(entity.getTemplateMapping().getNameHindi());
		templateMappingResponseDto.setNameGujarati(entity.getTemplateMapping().getNameGujarati());

		TemplateMasterResponseDto templateMasterResponseDto = templateMasterMapper
				.entityToResponse(entity.getTemplateMaster(), null);

		response.setTemplateModuleMaster(templateModuleMasterResponseDto);
		response.setTemplateMappingResponseDto(templateMappingResponseDto);
		response.setTemplateMaster(templateMasterResponseDto);

		return response;
	}

	@Override
	public Boolean updateAdminTemplateModuleStatusById(Long id, Boolean status) {
		Optional<AdminTemplateModuleEntity> op = adminTemplateModuleRepository.findByIdAndIsDeleteFalse(id);
		Boolean isSuccess = false;

		if (op.isPresent()) {
			AdminTemplateModuleEntity entity = op.get();
			entity.setIsActive(status);
			adminTemplateModuleRepository.save(entity);
			isSuccess = true;
		}

		return isSuccess;
	}

	@Override
	public Boolean deleteAdminTemplateModuleById(Long id) {
		Optional<AdminTemplateModuleEntity> op = adminTemplateModuleRepository.findByIdAndIsDeleteFalse(id);
		Boolean isSuccess = false;
		if (op.isPresent()) {
			AdminTemplateModuleEntity entity = op.get();
			entity.setIsDelete(true);
			adminTemplateModuleRepository.save(entity);
			isSuccess = true;
		} else {
			throw new RuntimeException("Admin Template Module not found with id : " + id);
		}
		return isSuccess;
	}

	@Transactional
	@Override
	public Integer allocateThemes(Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Id not found with : " + userId));
		List<TemplateModuleMasterEntity> templateModuleMasterEntities = templateModuleMasterRepository
				.getTemplateModuleMasterExcluseExclusiveTheme();

		int allocatedCount = 0;
		if (templateModuleMasterEntities.isEmpty()) {
			throw new RuntimeException("Templates modules not found");
		} else {
			for (TemplateModuleMasterEntity entity : templateModuleMasterEntities) {
				List<TemplateMappingEntity> templateMappingEntities = templateMappingRepository
						.findTemplates(entity.getId());
				for (TemplateMappingEntity templateMappingEntity : templateMappingEntities) {
					List<TemplateMasterEntity> templateMasterEntities = templateMasterRepository
							.findByTemplateMappingAndIsDeleteFalseAndIsActiveTrue(templateMappingEntity);

					for (TemplateMasterEntity templateMaster : templateMasterEntities) {
						AdminTemplateModuleEntity adminTemplate = new AdminTemplateModuleEntity();
						adminTemplate.setTemplateMapping(templateMappingEntity);
						adminTemplate.setTemplateMaster(templateMaster);
						adminTemplate.setTemplateModuleMaster(entity);
						adminTemplate.setCatFontId(templateMaster.getCatFontId());
						adminTemplate.setItemFontId(templateMaster.getItemFontId());
						adminTemplate.setSloganFontId(templateMaster.getSloganFontId());
						adminTemplate.setCatFontSize(templateMaster.getCatFontSize());
						adminTemplate.setItemFontSize(templateMaster.getItemFontSize());
						adminTemplate.setSloganFontSize(templateMaster.getSloganFontSize());
						adminTemplate.setUserId(userId);
						adminTemplateModuleRepository.save(adminTemplate);
						allocatedCount++;
					}
				}
			}
			AdminTemplateModuleEntity adminTemplate = new AdminTemplateModuleEntity();
			TemplateMasterEntity templateMaster = templateMasterRepository.findByIdAndIsDeleteFalse(Long.valueOf(40))
					.orElseThrow(() -> new RuntimeException("Template Master not found"));
			adminTemplate.setTemplateMapping(templateMaster.getTemplateMapping());
			adminTemplate.setTemplateMaster(templateMaster);
			adminTemplate.setTemplateModuleMaster(templateMaster.getTemplateModuleMaster());
			adminTemplate.setCatFontId(templateMaster.getCatFontId());
			adminTemplate.setItemFontId(templateMaster.getItemFontId());
			adminTemplate.setSloganFontId(templateMaster.getSloganFontId());
			adminTemplate.setCatFontSize(templateMaster.getCatFontSize());
			adminTemplate.setItemFontSize(templateMaster.getItemFontSize());
			adminTemplate.setSloganFontSize(templateMaster.getSloganFontSize());
			adminTemplate.setUserId(userId);
		}
		return allocatedCount;
	}

	@Override
	public Boolean updateAdminTemplateModuleFontAndFontSizeById(
			@Valid List<AdminTemplateModuleFontAndFontSizeRequestDto> requests) {
		List<AdminTemplateModuleEntity> entities = new ArrayList<>();

		if (requests == null || requests.isEmpty()) {
			throw new IllegalArgumentException("Request list cannot be empty");
		}

		for (AdminTemplateModuleFontAndFontSizeRequestDto request : requests) {
			AdminTemplateModuleEntity entity = adminTemplateModuleRepository
					.findByIdAndIsDeleteFalse(request.getAdminTemplateModuleId())
					.orElseThrow(() -> new RuntimeException(
							"Admin template module not found with id : " + request.getAdminTemplateModuleId()));

			entity.setCatFontId(request.getCatFontId());
			entity.setItemFontId(request.getItemFontId());
			entity.setSloganFontId(request.getSloganFontId());
			entity.setCatFontSize(request.getCatFontSize());
			entity.setItemFontSize(request.getItemFontSize());
			entity.setSloganFontSize(request.getSloganFontSize());

			entities.add(entity);
		}
		entities = adminTemplateModuleRepository.saveAll(entities);
		return true;

	}
}
