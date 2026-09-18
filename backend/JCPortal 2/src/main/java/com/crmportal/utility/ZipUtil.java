package com.crmportal.utility;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	public static void compress(String filePath, String zipFilePath) {

		byte[] buffer = new byte[1024];

		try (FileOutputStream fos = new FileOutputStream(zipFilePath);
				ZipOutputStream zos = new ZipOutputStream(fos);
				FileInputStream fis = new FileInputStream(filePath)) {

			ZipEntry ze = new ZipEntry(new File(filePath).getName());
			zos.putNextEntry(ze);

			int len;
			while ((len = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			zos.closeEntry();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}