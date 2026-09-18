package com.crmportal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.TapInquiryEntity;
import com.crmportal.repository.CityMasterRepository;
import com.crmportal.repository.StateMasterRepository;
import com.crmportal.repository.TapInquiryRepository;
import com.crmportal.request.dto.TapInquiryRequestDto;
import com.crmportal.response.dto.TapInquiryResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.TapInquiryService;

@Service
public class TapInquiryServiceImpl implements TapInquiryService {

	@Autowired
	TapInquiryRepository tapInquiryRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	CityMasterRepository cityMasterRepository;

	@Autowired
	StateMasterRepository stateMasterRepository;

	@Override
	public Boolean addTapInquiry(TapInquiryRequestDto request) {
		try {

			TapInquiryEntity entity = new TapInquiryEntity();

			BeanUtils.copyProperties(request, entity);

//			LocalDate formatedDate = commonService.dateFormatted(request.getEventDate());
			entity.setEventDate(request.getEventDate());
			entity.setIsDelete(false);
			entity.setUpdatedAt(LocalDateTime.now());

			tapInquiryRepository.save(entity);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<TapInquiryResponseDto> getAllInquiry() {

		List<TapInquiryEntity> inquiryList = tapInquiryRepository.findAllByIsDeleteFalse();

		List<TapInquiryResponseDto> responseList = new ArrayList<>();

		for (TapInquiryEntity entity : inquiryList) {

			TapInquiryResponseDto dto = new TapInquiryResponseDto();

			dto.setCompanyName(entity.getCompanyName());
			dto.setCountryCode(entity.getCountryCode());
			dto.setContactNo(entity.getContactNo());
			dto.setFunctionName(entity.getFunctionName());
			dto.setVenueAddress(entity.getVenueAddress());

			if (entity.getEventDate() != null) {
				dto.setEventDate(entity.getEventDate());
			}

			dto.setEventTime(entity.getEventTime());

			dto.setTotalTab(entity.getTotalTab());

			dto.setTimeSlot(entity.getTimeSlot());
			dto.setEstimatedBudget(entity.getEstimatedBudget());

			dto.setCityId(entity.getCityId());

			if (entity.getCityId() != null) {
				cityMasterRepository.findById(entity.getCityId()).ifPresent(city -> dto.setCityName(city.getName()));
			}

			dto.setStateId(entity.getStateId());

			if (entity.getStateId() != null) {
				stateMasterRepository.findById(entity.getStateId())
						.ifPresent(state -> dto.setStateName(state.getName()));
			}

			responseList.add(dto);
		}

		return responseList;
	}
}
