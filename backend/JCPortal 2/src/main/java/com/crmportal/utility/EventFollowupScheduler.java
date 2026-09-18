package com.crmportal.utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.crmportal.controller.EventFollowUpEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFollowupRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFollowupExcelService;

public class EventFollowupScheduler {

	@Autowired
	private EventFollowupRepository eventFollowupRepository;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private EventFollowupExcelService excelService;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;

	@Autowired
	CommonService commonService;

	@Scheduled(cron = "0 0 10 * * ?")
	public void sendDailyFollowupReport() {

		try {

			LocalDate today = LocalDate.now();

			System.out.println("Event followup cron started : " + today);

			List<UserMasterEntity> managers = userMasterRepository.findAllManagers();

			List<UserMasterEntity> admins = userMasterRepository.findAllAdmins();

			List<Long> userIds = new ArrayList<>();

			userIds.addAll(managers.stream().map(UserMasterEntity::getId).filter(Objects::nonNull)
					.collect(Collectors.toList()));

			userIds.addAll(
					admins.stream().map(UserMasterEntity::getId).filter(Objects::nonNull).collect(Collectors.toList()));

			if (userIds.isEmpty()) {
				System.out.println("No managers/admins found.");
				return;
			}

			List<UserBasicDetailsMasterEntity> basicDetailsList = userBasicDetailsMasterRepository
					.findByUser_IdIn(userIds);

			Map<Long, UserBasicDetailsMasterEntity> basicDetailsMap = basicDetailsList.stream()
					.filter(x -> x.getUser() != null).collect(Collectors.toMap(x -> x.getUser().getId(),
							Function.identity(), (existing, replacement) -> existing));

			for (UserMasterEntity manager : managers) {

				if (manager.getEmail() == null || manager.getEmail().trim().isEmpty()) {
					continue;
				}

				UserBasicDetailsMasterEntity basicDetails = basicDetailsMap.get(manager.getId());

				if (basicDetails == null) {
					continue;
				}

				Integer followupDay = basicDetails.getFollowupDay();

				if (followupDay == null || followupDay < 0) {
					followupDay = 0;
				}

				LocalDate startDate = today;
				LocalDate endDate = today.plusDays(followupDay);

				System.out.println("Manager ID: " + manager.getId() + " | followupDay: " + followupDay
						+ " | startDate: " + startDate + " | endDate: " + endDate);

				List<EventFollowUpEntity> followups = eventFollowupRepository.findAllWithFilters(null, manager.getId(),
						null, true, startDate, endDate);

				if (followups == null || followups.isEmpty()) {
					continue;
				}

				byte[] excel = excelService.generateExcel(followups);

				// Send manager email here
				commonService.sendEmailWithAttachment(manager.getEmail(), excel);
			}

			for (UserMasterEntity admin : admins) {

				if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
					continue;
				}

				UserBasicDetailsMasterEntity basicDetails = basicDetailsMap.get(admin.getId());

				if (basicDetails == null) {
					continue;
				}

				Integer followupDay = basicDetails.getFollowupDay();

				if (followupDay == null || followupDay < 0) {
					followupDay = 0;
				}

				LocalDate startDate = today;
				LocalDate endDate = today.plusDays(followupDay);

				System.out.println("Admin ID: " + admin.getId() + " | followupDay: " + followupDay + " | startDate: "
						+ startDate + " | endDate: " + endDate);

				List<EventFollowUpEntity> allFollowups = eventFollowupRepository.findAllWithFilters(admin.getId(), null,
						null, true, startDate, endDate);

				if (allFollowups == null || allFollowups.isEmpty()) {
					continue;
				}

				byte[] excel = excelService.generateExcel(allFollowups);

				// Send admin email here
				commonService.sendEmailWithAttachment(admin.getEmail(), excel);
			}

			System.out.println("Event followup cron completed successfully.");

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
