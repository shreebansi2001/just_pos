package com.crmportal.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.enums.EntryType;

@Service
public interface ReceivablePayableService {

	Map<String, Object> getByMonthWise(EntryType entryType, String startDate, String endDate, Long userId);

}
