package com.crmportal.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.TemplateModuleMasterEntity;
import com.crmportal.mapper.TemplateModuleMasterMapper;
import com.crmportal.repository.TemplateModuleMasterRepository;
import com.crmportal.request.dto.TemplateModuleMasterRequestDto;
import com.crmportal.response.dto.TemplateModuleMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.TemplateModuleMasterService;

@Service
public class TemplateModuleMasterServiceImpl implements TemplateModuleMasterService {

	@Autowired
	TemplateModuleMasterRepository templateModuleMasterRepository;
	
	@Autowired
	TemplateModuleMasterMapper templateModuleMasterMapper;
	
	@Autowired
	CommonService commonService;
	
	@Override
	public TemplateModuleMasterResponseDto addOrUpdateTemplateModuleMaster(TemplateModuleMasterRequestDto request, Long id) {
		Optional<TemplateModuleMasterEntity> op = templateModuleMasterRepository.findByNameEnglishAndIsDeleteFalse(request.getNameEnglish());
		TemplateModuleMasterEntity entity = null;
		
		if(id == -1) {
			if(op.isPresent()) {
				throw new RuntimeException("Template Module with the name '"+ request.getNameEnglish()+"' is already exists.");
			}else {
				entity = templateModuleMasterMapper.requestToEntity(request);
			}
		}else {
			Optional<TemplateModuleMasterEntity> op1 = templateModuleMasterRepository.findByIdAndIsDeleteFalse(id);
			
			if(op1.isPresent()) {
				entity = op1.get();
				if(!entity.getNameEnglish().equalsIgnoreCase(request.getNameEnglish()) && op.isPresent()) {
					throw new RuntimeException("Template Module with the name '"+ request.getNameEnglish()+"' is already exists.");
				}else {
					entity.setNameEnglish(request.getNameEnglish());
					entity.setNameHindi(request.getNameHindi());
					entity.setNameGujarati(request.getNameGujarati());
					entity.setUpdatedAt(commonService.getCurrentDateTime());
				}
			}else {
				throw new RuntimeException("Template Module not found with id: " + id);
			}
		}
		
		entity = templateModuleMasterRepository.save(entity);
		TemplateModuleMasterResponseDto responseDto = templateModuleMasterMapper.entityToResponse(entity);
		return responseDto;
	}
	
	@Override
	public TemplateModuleMasterResponseDto getTemplateModuleById(Long id) {
		Optional<TemplateModuleMasterEntity> op = templateModuleMasterRepository.findByIdAndIsDeleteFalse(id);
		TemplateModuleMasterResponseDto response = null;
		
		if(op.isPresent()) {
			TemplateModuleMasterEntity entity = op.get();
			response = templateModuleMasterMapper.entityToResponse(entity);
		}
		
		return response;
	}
	
	@Override
	public List<TemplateModuleMasterResponseDto> getAllTemplateModuleMaster() {
		List<TemplateModuleMasterEntity> entities = templateModuleMasterRepository.findAllByIsDeleteFalse();
		List<TemplateModuleMasterResponseDto> response = templateModuleMasterMapper.entityToResponse(entities);
		
		return response;
	}
	
	@Override
	public Boolean deleteTemplateModuleById(Long id) {
		if(templateModuleMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			Optional<TemplateModuleMasterEntity> op = templateModuleMasterRepository.findByIdAndIsDeleteFalse(id);
			if(op.isPresent()) {
				TemplateModuleMasterEntity entity = op.get();
				entity.setIsDelete(true);
				templateModuleMasterRepository.save(entity);
				return true;
			}else {
				return false;
			}
		}else {
			return false;			
		}
	}
	
	@Override
	public Boolean updateTemplateModuleStatusById(Long id, Boolean status) {
		Optional<TemplateModuleMasterEntity> op = templateModuleMasterRepository.findByIdAndIsDeleteFalse(id);
		
		if(op.isPresent()) {
			TemplateModuleMasterEntity entity = op.get();
			entity.setIsActive(status);
			templateModuleMasterRepository.save(entity);

			return true;
		}else {
			return false;
		}
	}

}
