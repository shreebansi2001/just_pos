package com.crmportal.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.crmportal.utility.ZipUtil;

public class MySQLBackupService {

	private static final String MYSQL_DUMP_PATH = "D:\\Mysql\\bin\\mysqldump.exe";
	private static final String BACKUP_DIR = "D:\\db_backup\\";

	public static void backupDatabase() throws IOException, InterruptedException {

		String dbName = "your_db";
		String user = "root";
		String password = "your_password";

		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		String sqlFile = BACKUP_DIR + dbName + "_" + timestamp + ".sql";
		String zipFile = sqlFile + ".zip";

		new File(BACKUP_DIR).mkdirs();

		// Step 1: Create SQL dump
		ProcessBuilder pb = new ProcessBuilder(MYSQL_DUMP_PATH, "-u", user, "-p" + password, dbName);

		pb.redirectOutput(new File(sqlFile));

		Process process = pb.start();
		int exitCode = process.waitFor();

		if (exitCode == 0) {
			System.out.println("SQL backup created: " + sqlFile);

			// Step 2: Compress to ZIP
			ZipUtil.compress(sqlFile, zipFile);

			// Step 3: Delete raw SQL file
			new File(sqlFile).delete();

			System.out.println("ZIP backup created: " + zipFile);

		} else {
			System.out.println("mysqldump failed. Exit code: " + exitCode);
		}
	}
}