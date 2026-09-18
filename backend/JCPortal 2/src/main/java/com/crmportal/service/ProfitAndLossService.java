package com.crmportal.service;

import org.springframework.stereotype.Service;

import com.crmportal.response.dto.ProfitAndLossResponseDto;

@Service
public interface ProfitAndLossService {

	ProfitAndLossResponseDto getProfitAndLossData(String startDate, String endDate, Long userId);

}
