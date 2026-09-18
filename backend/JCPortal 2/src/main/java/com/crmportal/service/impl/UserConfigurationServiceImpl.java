package com.crmportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.ConfigurationUtilEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.ConfigurationUtilMapper;
import com.crmportal.repository.ConfigurationUtilEntityRepository;
import com.crmportal.repository.FontMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.ConfigurationUtilDto;
import com.crmportal.service.UserConfigurationService;
import com.crmportal.service.UserFileService;
import com.crmportal.utility.ResponseUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class UserConfigurationServiceImpl implements UserConfigurationService {

	@Autowired
	private ConfigurationUtilEntityRepository configurationUtilEntityRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;
	@Autowired
	private Environment environment;

	@Autowired
	UserFileService userFileService;

	@Autowired
	FontMasterRepository fontMasterRepository;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public Map<String, Object> saveUserConfig(ConfigurationUtilDto dto) {
		String errorMessage = null;
		try {
			UserMasterEntity userMasterEntity = getUserMasterEntity(dto.getUser());
			if (Objects.isNull(userMasterEntity)) {
				return ResponseUtils.createFailedRespones("User not found",
						"User not found for userId " + dto.getUser());
			}
			ConfigurationUtilEntity entityFromRequest = ConfigurationUtilMapper.toEntity(dto);
			ConfigurationUtilEntity entityFromDb = configurationUtilEntityRepository.findByUser(dto.getUser())
					.orElse(null);
			if (Objects.nonNull(entityFromDb)) {
				entityFromRequest.setId(entityFromDb.getId());
			}
			ConfigurationUtilEntity entity = configurationUtilEntityRepository.save(entityFromRequest);
//			 if(file != null && !file.isEmpty()) {
//		        	try {
//		        		userFileService.storeFile(dto.getUser(), ModuleName.UTILITY.toString(),entity.getId(), FileType.IMAGE.toString(), file);
//					} catch (IOException e) {
//						throw new RuntimeException(e);
//					}
//		        }
			return createSuccessResposne(entity.getId());
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Error while saving userConfiguration, Error: " + e.getLocalizedMessage();
		}
		return ResponseUtils.createFailedRespones(errorMessage);
	}

	private UserMasterEntity getUserMasterEntity(Long id) {
		return userMasterRepository.findById(id).orElse(null);
	}

	private Map<String, Object> createSuccessResposne(Long moduleId) {
		Map<String, Object> response = new LinkedHashMap<>();

		response.put("msg", "Data saved successfully");
		response.put("success", true);

		return response;
	}

	@Override
	public Map<String, Object> getUserConfig(Long user) {
		String errorMessage = null;
		try {
			ConfigurationUtilEntity entity = configurationUtilEntityRepository.findByUser(user).orElse(null);
			if (Objects.isNull(entity)) {
				return ResponseUtils.createFailedRespones("configuration not found for user " + user);
			}
			JsonNode jsonNode = createUserConfigJsonData(entity);
			return ResponseUtils.createSuccessRespones(jsonNode, "Data fetched successfully");
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "Error while fetching userConfiguration, Error: " + e.getLocalizedMessage();
		}
		return ResponseUtils.createFailedRespones(errorMessage);
	}

	public JsonNode createUserConfigJsonData(ConfigurationUtilEntity entity) {
		ObjectNode json = OBJECT_MAPPER.createObjectNode();

		json.put("moduleId", entity.getId());
		json.put("user", entity.getUser());

		json.put("counterNamePlate", entity.getCounterNamePlate());
		json.put("dateFormat", entity.getDateFormat());
		json.put("timeZone", entity.getTimeZone());
		json.put("timeFormat", entity.getTimeFormat());
		json.put("pageSize", entity.getPageSize());

		json.put("twoLanguageDefault", entity.getTwoLanguageDefault());
		json.put("twoLanguagePreferred", entity.getTwoLanguagePreferred());

		json.put("choiceOfMenu", entity.getChoiceOfMenu());
		json.put("directShare", entity.getDirectShare());
		json.put("sacNumber", entity.getSacNumber());

		json.put("displayMaxPerson", entity.getDisplayMaxPerson());
		json.put("displayAutoTime", entity.getDisplayAutoTime());
		json.put("totalRawMaterialReport", entity.getTotalRawMaterialReport());
		json.put("editRawmaterialQuantityBeforeGenReport", entity.getEditRawmaterialQuantityBeforeGenReport());

		json.put("fontColor", entity.getFontColor());
		json.put("bgColor", entity.getBgColor());
		json.put("combineReportConfiguration", entity.getCombineReportConfiguration());
		json.put("bgImage", environment.getProperty("app.image.url") + entity.getBgImage());

		json.put("isDelete", entity.getIsDelete());
		json.put("isActive", entity.getIsActive());
		json.put("createdAt", entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null);
		json.put("updatedAt", entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null);
		json.put("catFontId", entity.getCatFontId());
		json.put("catFontName", entity.getCatFontId() == null ? null
				: fontMasterRepository.findByFontIdAndIsDeleteFalse(entity.getCatFontId()).get().getFontName());
		json.put("itemFontId", entity.getItemFontId());
		json.put("itemFontName", entity.getItemFontId() == null ? null
				: fontMasterRepository.findByFontIdAndIsDeleteFalse(entity.getItemFontId()).get().getFontName());
		json.put("sloganFontId", entity.getSloganFontId());
		json.put("sloganFontName", entity.getSloganFontId() == null ? null
				: fontMasterRepository.findByFontIdAndIsDeleteFalse(entity.getSloganFontId()).get().getFontName());
		json.put("catFontSize", entity.getCatFontSize());
		json.put("itemFontSize", entity.getItemFontSize());
		json.put("sloganFontSize", entity.getSloganFontSize());

		return json;
	}

}