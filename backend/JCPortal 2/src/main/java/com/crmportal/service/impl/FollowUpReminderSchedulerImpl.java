package com.crmportal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.crmportal.entity.FollowUpDetailsEntity;
import com.crmportal.mapper.FollowUpDetailsMapper;
import com.crmportal.repository.FollowUpDetailsRepository;
import com.crmportal.response.dto.FollowUpDetailsResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.FollowUpReminderScheduler;

@Service
public class FollowUpReminderSchedulerImpl implements FollowUpReminderScheduler {

	@Autowired
	private FollowUpDetailsRepository followUpDetailsRepository;
	
	@Autowired
	private FollowUpDetailsMapper followUpDetailsMapper;
	
	@Autowired
	CommonService commonService;
	
	@Scheduled(cron = "0 0 9 * * ?")
	public void sendFollowUpReminders() {
		LocalDateTime twoDaysFromNow = LocalDateTime.now().plusDays(2);
		
		List<FollowUpDetailsEntity> detailsEntities = followUpDetailsRepository.findByFollowUpDateAndIsDeleteFalse(twoDaysFromNow);
		List<FollowUpDetailsResponseDto> detailsResponseDtos = new ArrayList<>();
		
		for (FollowUpDetailsEntity followUpDetailsEntity : detailsEntities) {
			FollowUpDetailsResponseDto detailsResponseDto = followUpDetailsMapper.entityToResponse(followUpDetailsEntity);
			detailsResponseDtos.add(detailsResponseDto);
		}
		
//		try {
//			commonService.sendFollowUpRemainder(detailsResponseDtos);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	
	}
}