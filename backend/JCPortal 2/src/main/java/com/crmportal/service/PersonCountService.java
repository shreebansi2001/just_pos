package com.crmportal.service;

import org.springframework.stereotype.Service;

import com.crmportal.response.dto.PersonCountResponse;

@Service
public interface PersonCountService {

	PersonCountResponse fetchFromCamera();

	void fetchAndSave();

}
