package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.controller.EventFollowUpEntity;

@Service
public interface EventFollowupExcelService {

	byte[] generateExcel(List<EventFollowUpEntity> followups);

}
