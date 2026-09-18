package com.crmportal.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.AdminTemplateModuleEntity;
import com.crmportal.entity.NamePlateImagesEntity;
import com.crmportal.entity.TemplateMappingEntity;
import com.crmportal.entity.TemplateMasterEntity;
import com.crmportal.entity.TemplateModuleMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.TemplateMasterMapper;
import com.crmportal.mapper.TemplateModuleMasterMapper;
import com.crmportal.repository.AdminTemplateModuleRepository;
import com.crmportal.repository.NamePlateImagesRepository;
import com.crmportal.repository.TemplateMappingRepository;
import com.crmportal.repository.TemplateMasterRepository;
import com.crmportal.repository.TemplateModuleMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.TemplateMasterRequestDto;
import com.crmportal.response.dto.TemplateMappingResponseDto;
import com.crmportal.response.dto.TemplateMasterResponseDto;
import com.crmportal.response.dto.TemplateModuleMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.TemplateMasterService;
import com.crmportal.service.UserFileService;

@Service
public class TemplateMasterServiceImpl implements TemplateMasterService {

	@Autowired
	TemplateMasterRepository templateMasterRepository;

	@Autowired
	TemplateMasterMapper templateMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	TemplateModuleMasterRepository templateModuleMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	TemplateMappingRepository templateMappingRepository;

	@Autowired
	TemplateModuleMasterMapper templateModuleMasterMapper;

	@Autowired
	UserFileService userFileService;
	
	@Autowired
	UserFileServiceImpl userFileServiceImpl;

	@Value("${app.image.url}")
	private String path;
	
	@Autowired
	AdminTemplateModuleRepository adminTemplateModuleRepository;

	@Autowired
	NamePlateImagesRepository namePlateImagesRepository;
	
	@Override
	@Transactional
	public TemplateMasterResponseDto addOrUpdateTemplateMaster(TemplateMasterRequestDto request,
			List<MultipartFile> menuReportPages, MultipartFile dummyPdf, List<MultipartFile> namePlateBg, Long id) {
		Optional<TemplateMasterEntity> op = templateMasterRepository.findByNameAndIsDeleteFalse(request.getName());
		TemplateMasterEntity entity = null;
		Optional<TemplateModuleMasterEntity> moduleMasterEntity = templateModuleMasterRepository
				.findByIdAndIsDeleteFalse(request.getTemplateModuleId());

		Optional<TemplateMappingEntity> templateMappingEntity = Optional
				.ofNullable(templateMappingRepository.findByIdAndIsDeleteFalse(request.getTemplateMappingId()));
		
//		System.out.println("size : " + menuReportPages.size() );
		try {
			if (id == -1) {
				if (op.isPresent()) {
					throw new RuntimeException(
							"Template Module with the name '" + request.getName() + "' is already exists.");
				} else {
					entity = new TemplateMasterEntity();
				}
			} else {
				Optional<TemplateMasterEntity> op1 = templateMasterRepository.findByIdAndIsDeleteFalse(id);

				if (op1.isPresent()) {
					entity = op1.get();
					if (!entity.getName().equalsIgnoreCase(request.getName()) && op.isPresent()) {
						throw new RuntimeException(
								"Template with the name '" + request.getName() + "' is already exists.");
					} else {
						List<AdminTemplateModuleEntity> adminTemplates = adminTemplateModuleRepository.findAllByTemplateMasterAndIsDeleteFalse(entity);
						adminTemplates.stream().forEach(template -> template.setTemplateMapping(templateMappingEntity.isPresent() ? templateMappingEntity.get() : null));
						if (!adminTemplates.isEmpty()) {
							adminTemplateModuleRepository.saveAll(adminTemplates);
						}
						entity.setUpdateAt(commonService.getCurrentDateTime());
					}
				} else {
					throw new RuntimeException("Template not found with id: " + id);
				}
			}
			System.out.println("template mapping : " + templateMappingEntity.get().getNameEnglish());
			entity.setContentFontColor(request.getContentFontColor());
			entity.setHeadingFontColor(request.getHeadingFontColor());
			entity.setDescriptionFontColor(request.getDescriptionFontColor());
			entity.setIsNamePlate(request.getIsNamePlate());
			entity.setName(request.getName());
			entity.setTemplateModuleMaster(moduleMasterEntity.isPresent() ? moduleMasterEntity.get() : null);
			entity.setTemplateMapping(templateMappingEntity.isPresent() ? templateMappingEntity.get() : null);
			entity.setUserId(request.getUserId());
			entity.setIsDefault(request.getIsDefault());
			entity.setPrice(request.getPrice());
			entity.setDescription(request.getDescription());
			entity.setCatFontId(request.getCatFontId());
			entity.setItemFontId(request.getItemFontId());
			entity.setSloganFontId(request.getSloganFontId());
			entity.setCatFontSize(request.getCatFontSize());
			entity.setItemFontSize(request.getItemFontSize());
			entity.setSloganFontSize(request.getSloganFontSize());
			entity = templateMasterRepository.save(entity);

			if (request.getIsNamePlate()) {
				if (namePlateBg != null && !namePlateBg.isEmpty()) {
					String fileType = "";
					
					if(id != -1) {
						userFileServiceImpl.deleteOldFile(entity.getNamePlateBg());
						entity.setNamePlateBg(null);
						
						userFileServiceImpl.deleteOldFile(entity.getNamePlateCoverBg());
						entity.setNamePlateCoverBg(null);
						
						entity = templateMasterRepository.save(entity);
					}
					
					for (MultipartFile file : namePlateBg) {
						String baseName = FilenameUtils.getBaseName(file.getOriginalFilename());

						switch (baseName.trim()) {
							case "1":
								fileType = FileType.NAMEPLATE_PAGE.toString();
								break;
							case "2":
								fileType = FileType.NAMEPLATE_COVER_PAGE.toString();
								break;
						}
						userFileService.storeFile(entity.getUserId(), ModuleName.REPORTPAGE.toString(), entity.getId(),
								fileType, file);
					}
				}
			}

			if (menuReportPages != null && !menuReportPages.isEmpty()) {
				String fileType = "";

				if(id != -1) {
					userFileServiceImpl.deleteOldFile(entity.getFrontPage());
					entity.setFrontPage(null);
					
					userFileServiceImpl.deleteOldFile(entity.getSecondFrontPage());
					entity.setSecondFrontPage(null);
					
					userFileServiceImpl.deleteOldFile(entity.getWatermark());
					entity.setWatermark(null);
					
					userFileServiceImpl.deleteOldFile(entity.getLastMainPage());
					entity.setLastMainPage(null);
					
					userFileServiceImpl.deleteOldFile(entity.getCatBgPage());
					entity.setCatBgPage(null);

					userFileServiceImpl.deleteOldFile(entity.getExtraPage());
					entity.setExtraPage(null);
					
					entity = templateMasterRepository.save(entity);
				}
				
				for (MultipartFile file : menuReportPages) {
					String baseName = FilenameUtils.getBaseName(file.getOriginalFilename());

					switch (baseName.trim()) {
					case "1":
						fileType = FileType.FRONT_PAGE.toString();
						break;
					case "2":
						fileType = FileType.DETAILS_PAGE.toString();
						break;
					case "3":
						fileType = FileType.WATERMARK.toString();
						break;
					case "4":
						fileType = FileType.LAST_PAGE.toString();
						break;
					case "5":
						fileType = FileType.CATEGORY_BG_PAGE.toString();
						break;
					case "6":
						fileType = FileType.EXTRA_PAGE.toString();
						break;

					default:
						throw new IllegalArgumentException("Invalid report page file name");
					}
					userFileService.storeFile(entity.getUserId(), ModuleName.REPORTPAGE.toString(), entity.getId(),
							fileType, file);
				}
			}
			if (dummyPdf != null && !dummyPdf.isEmpty()) {
				userFileService.storeFile(entity.getUserId(), ModuleName.REPORTPAGE.toString(), entity.getId(),
						FileType.OTHER.toString(), dummyPdf);
			}

			return getTemplateMasterById(entity.getId());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to store report page file", e);
		}
	}

	@Override
	public List<TemplateMasterResponseDto> getAllTemplateMaster() {
		List<TemplateMasterEntity> templates = templateMasterRepository.findAllByIsDeleteFalse();
		List<TemplateMasterResponseDto> response = null;

		if (!templates.isEmpty()) {
			response = templateMasterMapper.entityToResponse(templates, path);
			for (TemplateMasterResponseDto templateMasterResponseDto : response) {
				List<NamePlateImagesEntity> namePlateImages = namePlateImagesRepository
						.findByTemplateMasterId(templateMasterResponseDto.getId());
				
				templateMasterResponseDto.setDummyPdf(path + templateMasterResponseDto.getDummyPdf());
				templateMasterResponseDto.setFrontPage(path + templateMasterResponseDto.getFrontPage());
				templateMasterResponseDto.setSecondFrontPage(path + templateMasterResponseDto.getSecondFrontPage());
				templateMasterResponseDto.setWatermark(path + templateMasterResponseDto.getWatermark());
				templateMasterResponseDto.setLastMainPage(path + templateMasterResponseDto.getLastMainPage());
				templateMasterResponseDto.setCatBgPage(path + templateMasterResponseDto.getCatBgPage());
				templateMasterResponseDto.setExtraPage(path + templateMasterResponseDto.getExtraPage());

				if(namePlateImages != null && !namePlateImages.isEmpty()) {
					if (namePlateImages != null && !namePlateImages.isEmpty()) {
					    Map<Long, String> imageMap = namePlateImages.stream()
					            .collect(Collectors.toMap(
					                    NamePlateImagesEntity::getId,
					                    image -> path + image.getImagePath()
					            ));

					    templateMasterResponseDto.setNamePlateImages(imageMap);
					}
				}
			}
		} else {
			response = new ArrayList<>();
		}

		return response;
	}

	@Override
	public TemplateMasterResponseDto getTemplateMasterById(Long id) {
		Optional<TemplateMasterEntity> op = templateMasterRepository.findByIdAndIsDeleteFalse(id);
		TemplateMasterResponseDto response = null;

		if (op.isPresent()) {
			TemplateMasterEntity entity = op.get();
			response = templateMasterMapper.entityToResponse(entity, path);
			List<NamePlateImagesEntity> namePlateImages = namePlateImagesRepository
					.findByTemplateMasterId(entity.getId());
			
			if(namePlateImages != null && !namePlateImages.isEmpty()) {
				Map<Long, String> imageMap = namePlateImages.stream()
			            .collect(Collectors.toMap(
			                    NamePlateImagesEntity::getId,
			                    image -> path + image.getImagePath()
			            ));

				response.setNamePlateImages(imageMap);
			}
		}

		return response;
	}

	@Override
	public Boolean deleteTemplateById(Long id) {
		if (templateMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			Optional<TemplateMasterEntity> op = templateMasterRepository.findByIdAndIsDeleteFalse(id);

			if (op.isPresent()) {
				TemplateMasterEntity template = op.get();
				template.setIsDelete(true);
				templateMasterRepository.save(template);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public Boolean updateTemplateMasterStatusById(Long id, Boolean status) {
		Boolean isUpdated = false;
		Optional<TemplateMasterEntity> op = templateMasterRepository.findByIdAndIsDeleteFalse(id);

		if (op.isPresent()) {
			TemplateMasterEntity entity = op.get();
			entity.setIsActive(status);
			templateMasterRepository.save(entity);
			isUpdated = true;
		}

		return isUpdated;
	}

	@Override
	public List<TemplateMasterResponseDto> getAllTemplateMasterByModuleId(Long moduleId, Boolean isNamePlate,
			Long userId) {

		List<Object[]> rows = templateMasterRepository.findAllTemplatesWithSelectionNative(moduleId, isNamePlate,
				userId);

		List<TemplateMasterResponseDto> response = new ArrayList<>();

		for (Object[] r : rows) {

			TemplateMasterResponseDto dto = new TemplateMasterResponseDto();

			dto.setId(((Number) r[0]).longValue());
			dto.setName((String) r[1]);
			dto.setCreatedAt(r[2] != null ? r[2].toString() : null);
			dto.setHeadingFontColor((String) r[3]);
			dto.setContentFontColor((String) r[4]);
			dto.setFrontPage(path + (String) r[5]);
			dto.setSecondFrontPage(path + (String) r[6]);
			dto.setWatermark(path + (String) r[7]);
			dto.setLastMainPage(path + (String) r[8]);
			dto.setIsNamePlate((Boolean) r[9]);
			dto.setNamePlateBg(path + (String) r[10]);
			dto.setCatBgPage(path + (String) r[11]);
			dto.setExtraPage(path + (String) r[12]);
			dto.setDummyPdf(path + (String) r[13]);
			dto.setIsActive((Boolean) r[14]);
			dto.setIsDelete((Boolean) r[15]);
			dto.setIsSelected(r[24] != null && ((Number) r[24]).intValue() == 1);
			dto.setDescriptionFontColor((String) r[25]);

			// Module DTO
			TemplateModuleMasterResponseDto moduleDto = new TemplateModuleMasterResponseDto();
			moduleDto.setId(((Number) r[16]).longValue());
			moduleDto.setNameEnglish((String) r[17]);
			moduleDto.setNameHindi((String) r[18]);
			moduleDto.setNameGujarati((String) r[19]);
			dto.setTemplateModuleMaster(moduleDto);

			// Mapping DTO
			TemplateMappingResponseDto mappingDto = new TemplateMappingResponseDto();
			mappingDto.setId(((Number) r[20]).longValue());
			mappingDto.setNameEnglish((String) r[21]);
			mappingDto.setNameHindi((String) r[22]);
			mappingDto.setNameGujarati((String) r[23]);
			mappingDto.setTemplateModuleId(((Number) r[16]).longValue());
			mappingDto.setTemplateModuleNameEnglish((String) r[17]);
			mappingDto.setTemplateModuleNameHindi((String) r[18]);
			mappingDto.setTemplateModuleNameGujarati((String) r[19]);

			dto.setTemplateMapping(mappingDto);

			response.add(dto);
		}

		return response;
	}

	@Override
	public List<TemplateMasterResponseDto> getAllTemplateMasterByTemplateMappingId(Long id) {
		TemplateModuleMasterEntity entity = templateModuleMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Template Mapping not found with id: " + id));
		List<TemplateMasterEntity> responseEntity = templateMasterRepository
				.findByTemplateModuleMasterAndIsDeleteFalse(entity);
		List<TemplateMasterResponseDto> response = new ArrayList<>();

		for (TemplateMasterEntity templateMasterEntity : responseEntity) {
			TemplateMappingResponseDto templateMappingResponseDto = new TemplateMappingResponseDto();

			TemplateMasterResponseDto dto = templateMasterMapper.entityToResponse(templateMasterEntity, path);
			dto.setTemplateModuleMaster(templateModuleMasterMapper.entityToResponse(entity));

			templateMappingResponseDto.setId(templateMasterEntity.getTemplateMapping().getId());
			templateMappingResponseDto.setNameEnglish(templateMasterEntity.getTemplateMapping().getNameEnglish());
			templateMappingResponseDto.setNameHindi(templateMasterEntity.getTemplateMapping().getNameHindi());
			templateMappingResponseDto.setNameGujarati(templateMasterEntity.getTemplateMapping().getNameGujarati());

			dto.setTemplateMapping(templateMappingResponseDto);

			response.add(dto);
		}

		return response;
	}

	@Override
	public List<TemplateMasterResponseDto> getAllTemplateMasterByModuleIdAndUserId(Long moduleId, Long userId) {
		TemplateModuleMasterEntity entity = templateModuleMasterRepository.findByIdAndIsDeleteFalse(moduleId)
				.orElseThrow(() -> new RuntimeException("Template Module not found with id: " + moduleId));
		UserMasterEntity entity1 = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		List<TemplateMasterEntity> responseEntity = templateMasterRepository
				.findByTemplateModuleMasterAndUserIdAndIsDeleteFalse(entity, userId);
		List<TemplateMasterResponseDto> response = new ArrayList<>();

		for (TemplateMasterEntity templateMasterEntity : responseEntity) {
			TemplateMappingResponseDto templateMappingResponseDto = new TemplateMappingResponseDto();

			TemplateMasterResponseDto dto = templateMasterMapper.entityToResponse(templateMasterEntity, path);
			dto.setTemplateModuleMaster(templateModuleMasterMapper.entityToResponse(entity));

			templateMappingResponseDto.setId(templateMasterEntity.getTemplateMapping().getId());
			templateMappingResponseDto.setNameEnglish(templateMasterEntity.getTemplateMapping().getNameEnglish());
			templateMappingResponseDto.setNameHindi(templateMasterEntity.getTemplateMapping().getNameHindi());
			templateMappingResponseDto.setNameGujarati(templateMasterEntity.getTemplateMapping().getNameGujarati());

			dto.setTemplateMapping(templateMappingResponseDto);

			response.add(dto);
		}

		return response;
	}
}
