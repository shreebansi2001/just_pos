package com.crmportal.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserNotificationConfigEntity;
import com.crmportal.entity.UserUpgradedModuleEntity;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserNotificationConfigRepository;
import com.crmportal.repository.UserUpgradedModuleRepository;
import com.crmportal.service.CommonService;

@Component
public class SubscriptionReminderScheduler {

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private CommonService commonService;

	@Autowired
	UserUpgradedModuleRepository userUpgradedModuleRepository;

	@Autowired
	UserNotificationConfigRepository userNotificationConfigRepository;

	@Scheduled(cron = "0 0 11 * * ?")
	public void sendExpiryReminders() throws Exception {
		LocalDate today = LocalDate.now();
		LocalDateTime reminderStart = LocalDateTime.now();
		LocalDateTime reminderEnd = today.plusDays(7).atTime(23, 59, 59);

		List<UserMasterEntity> usersExpiringSoon = userMasterRepository
				.findByEndDateBetweenAndIsActiveTrueAndIsApproveTrueAndClientId(reminderStart, reminderEnd, 0L);

		for (UserMasterEntity user : usersExpiringSoon) {
			try {
				commonService.sendSubscriptionReminder(user);

			} catch (Exception e) {
				System.err.println("Failed to send reminder to " + user.getEmail() + ": " + e.getMessage());
			}
		}

		List<UserMasterEntity> overdueUsers = userMasterRepository
				.findByEndDateBeforeAndIsActiveTrueAndIsApproveTrue(reminderStart);
		for (UserMasterEntity user : overdueUsers) {
			user.setIsActive(false);
			user.setIsApprove(false);
			userMasterRepository.save(user);
			List<UserMasterEntity> entities = userMasterRepository.findAllByClientIdAndIsDeleteFalse(user.getId());
			for (UserMasterEntity userMasterEntity : entities) {
				userMasterEntity.setIsActive(false);
				userMasterEntity.setIsApprove(false);
				userMasterRepository.save(userMasterEntity);
			}
//			commonService.sendExpiredSubscriptionNotice(user);
		}
	}

	@Scheduled(cron = "0 0 11 * * ?")
	public void sendUpgradedModuleReminders() {

		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime reminderEnd = today.plusDays(7).atTime(23, 59, 59);
		String newLink = "https://app.justcatering.in/upgrade";
		// Expiring soon
		List<Object[]> expiringUsers = userUpgradedModuleRepository.findExpiringModules(now, reminderEnd);

		for (Object[] row : expiringUsers) {
			try {
				Long id = ((Number) row[0]).longValue();
				Long userId = ((Number) row[1]).longValue();
				Long moduleId = ((Number) row[2]).longValue();
				LocalDateTime endDate = (LocalDateTime) row[3];

				String email = (String) row[4];
				String firstName = (String) row[5];
				String lastName = (String) row[6];
				String moduleName = (String) row[7];

//				commonService.sendUpgradedModuleReminder(email, firstName, lastName, moduleName, endDate, newLink);

			} catch (Exception e) {
				System.err.println("Failed to send reminder: " + e.getMessage());
			}
		}

		// Expired modules
		List<Object[]> expiredUsers = userUpgradedModuleRepository.findExpiredModules(now);

		for (Object[] row : expiredUsers) {
			try {
				Long id = ((Number) row[0]).longValue();

				String email = (String) row[4];
				String firstName = (String) row[5];
				String lastName = (String) row[6];
				String moduleName = (String) row[7];

				// Update DB
				UserUpgradedModuleEntity entity = userUpgradedModuleRepository.findById(id).orElse(null);

				if (entity != null) {
					entity.setIsActive(false);
					userUpgradedModuleRepository.save(entity);
				}

//				commonService.sendExpiredUpgradedModuleNotice(email, firstName, lastName, moduleName, newLink);

			} catch (Exception e) {
				System.err.println("Failed to process expired module: " + e.getMessage());
			}
		}
	}

	@Scheduled(cron = "0 0 11 * * ?")
	public void sendUserNotificationReminders() {

		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime reminderEnd = today.plusDays(7).atTime(23, 59, 59);
		String newLink = "https://app.justcatering.in/integrations";
		// Expiring soon
		List<Object[]> expiringUsers = userNotificationConfigRepository.findExpiringModules(now, reminderEnd);

		for (Object[] row : expiringUsers) {
			try {
				Long id = ((Number) row[0]).longValue();
				Long userId = ((Number) row[1]).longValue();
				Long moduleId = ((Number) row[2]).longValue();
				LocalDateTime endDate = (LocalDateTime) row[3];

				String email = (String) row[4];
				String firstName = (String) row[5];
				String lastName = (String) row[6];
				String moduleName = (String) row[7];

//				commonService.sendUpgradedModuleReminder(email, firstName, lastName, moduleName, endDate, newLink);

			} catch (Exception e) {
				System.err.println("Failed to send reminder: " + e.getMessage());
			}
		}

		// Expired modules
		List<Object[]> expiredUsers = userNotificationConfigRepository.findExpiredModules(now);

		for (Object[] row : expiredUsers) {
			try {
				Long id = ((Number) row[0]).longValue();

				String email = (String) row[4];
				String firstName = (String) row[5];
				String lastName = (String) row[6];
				String moduleName = (String) row[7];

				// Update DB
				UserNotificationConfigEntity entity = userNotificationConfigRepository.findById(id).orElse(null);

				if (entity != null) {
					entity.setIsActive(false);
					userNotificationConfigRepository.save(entity);
				}

//				commonService.sendExpiredUpgradedModuleNotice(email, firstName, lastName, moduleName, newLink);

			} catch (Exception e) {
				System.err.println("Failed to process expired module: " + e.getMessage());
			}
		}
	}
}
