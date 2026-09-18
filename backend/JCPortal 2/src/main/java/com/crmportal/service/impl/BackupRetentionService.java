package com.crmportal.service.impl;

import java.io.File;
import java.util.Arrays;

public class BackupRetentionService {

	private static final String BACKUP_DIR = "D:\\db_backup\\";
	private static final int KEEP_DAYS = 7; 

	public static void cleanOldBackups() {

		File folder = new File(BACKUP_DIR);

		File[] files = folder.listFiles();

		if (files == null)
			return;

		long cutoff = System.currentTimeMillis() - (KEEP_DAYS * 24L * 60 * 60 * 1000);

		Arrays.stream(files).forEach(file -> {

			if (file.lastModified() < cutoff) {
				if (file.delete()) {
					System.out.println("Deleted old backup: " + file.getName());
				}
			}
		});
	}
}
