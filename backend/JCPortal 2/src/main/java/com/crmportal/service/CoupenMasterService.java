package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.CoupenMasterRequestDto;
import com.crmportal.response.dto.CoupenMasterResponseDto;

@Service
public interface CoupenMasterService {

	CoupenMasterResponseDto addOrUpdateCoupenMaster(CoupenMasterRequestDto request, Long id);

	Boolean deleteCoupenMaster(Long id);

	CoupenMasterResponseDto getCoupenMasterById(Long id);

	List<CoupenMasterResponseDto> getAllCoupenMaster();

}
