package com.crmportal.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crmportal.service.PersonCountService;

@Component
public class CameraScheduler {

	@Autowired
	private PersonCountService personCountService;

//	@Scheduled(fixedDelay = 5000)
//	public void syncCameraData() {
//		
//		personCountService.fetchAndSave();
//	
//	}

}