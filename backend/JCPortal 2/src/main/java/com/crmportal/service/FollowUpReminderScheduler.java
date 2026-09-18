package com.crmportal.service;

import org.springframework.stereotype.Service;

@Service
public interface FollowUpReminderScheduler {

	void sendFollowUpReminders();
}
