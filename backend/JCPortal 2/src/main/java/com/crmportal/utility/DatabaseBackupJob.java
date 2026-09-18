package com.crmportal.utility;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crmportal.service.impl.BackupRetentionService;
import com.crmportal.service.impl.MySQLBackupService;

@Component
public class DatabaseBackupJob {

//	@Scheduled(cron = "0 0 2 ? * MON")
//	public void runBackup() {
//
//		System.out.println("Starting Monday 2:00 AM backup...");
//
//		try {
//			MySQLBackupService.backupDatabase();
//			BackupRetentionService.cleanOldBackups();
//
//			System.out.println("Backup completed successfully.");
//
//		} catch (Exception e) {
//			System.out.println("Backup failed!");
//			e.printStackTrace();
//		}
//	}
}