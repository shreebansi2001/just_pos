package com.crmportal.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;

@Service
public interface EventCopyService {

	EventMasterEntity copyEvent(Long eventId, Long childUserId, BigDecimal bigDecimal);
}
