package com.crmportal.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AdminTemplateModuleEntity;
import com.crmportal.entity.ExclusiveThemePaymentEntity;
import com.crmportal.mapper.ExclusiveThemePaymentMapper;
import com.crmportal.repository.AdminTemplateModuleRepository;
import com.crmportal.repository.ExclusiveThemePaymentRepository;
import com.crmportal.request.dto.ExclusivePaymentRequestDto;
import com.crmportal.request.dto.ExclusiveThemePaymentRequestDto;
import com.crmportal.response.dto.ExclusiveThemePaymentResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.ExclusiveThemePaymentService;

@Service
public class ExclusiveThemePaymentServiceImpl implements ExclusiveThemePaymentService {

	@Autowired
	ExclusiveThemePaymentRepository exclusiveThemePaymentRepository;

	@Autowired
	ExclusiveThemePaymentMapper exclusiveThemePaymentMapper;

	@Autowired
	AdminTemplateModuleRepository adminTemplateModuleRepository;

	@Autowired
	CommonService commonService;

	@Override
	public ExclusiveThemePaymentResponseDto addExclusiveThemePayment(ExclusivePaymentRequestDto request) {

		ExclusiveThemePaymentEntity entity = new ExclusiveThemePaymentEntity();

		entity = exclusiveThemePaymentMapper.requestToEntity(request);

		entity = exclusiveThemePaymentRepository.save(entity);

		ExclusiveThemePaymentResponseDto responseDto = exclusiveThemePaymentMapper.entityToResponse(entity);

		return responseDto;
	}

}
