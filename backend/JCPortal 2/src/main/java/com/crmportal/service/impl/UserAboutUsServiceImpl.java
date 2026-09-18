package com.crmportal.service.impl;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.crmportal.entity.UserAboutUsEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.UserAboutUsRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.UserAboutUsRequestDto;
import com.crmportal.response.dto.UserAboutUsResponseDto;
import com.crmportal.service.UserAboutUsService;

@Service
public class UserAboutUsServiceImpl implements UserAboutUsService{

	@Autowired
	UserAboutUsRepository aboutUsRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Transactional
	@Modifying
	@Override
	public UserAboutUsResponseDto addOrUpdateAboutUs(@Valid UserAboutUsRequestDto request) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
		aboutUsRepository.deleteByUser(user);
		
		UserAboutUsEntity aboutUsEntity = new UserAboutUsEntity();
		aboutUsEntity.setNameEnglish(request.getNameEnglish());
		aboutUsEntity.setNameGujarati(request.getNameGujarati());
		aboutUsEntity.setNameHindi(request.getNameHindi());
		aboutUsEntity.setUser(user);
		aboutUsEntity = aboutUsRepository.save(aboutUsEntity);
		UserAboutUsResponseDto aboutUsResponseDto = new UserAboutUsResponseDto(aboutUsEntity.getId(), aboutUsEntity.getNameEnglish(), aboutUsEntity.getNameHindi(), aboutUsEntity.getNameGujarati(), user.getId());
		return aboutUsResponseDto;
	}

	@Override
	public UserAboutUsResponseDto getByUser(Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		
		UserAboutUsEntity aboutUsEntity = aboutUsRepository.findByUser(user);
		if(aboutUsEntity != null) {
			UserAboutUsResponseDto aboutUsResponseDto = new UserAboutUsResponseDto(aboutUsEntity.getId(), aboutUsEntity.getNameEnglish(), aboutUsEntity.getNameHindi(), aboutUsEntity.getNameGujarati(), user.getId());
			return aboutUsResponseDto;
		}else {
			return null;
		}
	}

}
