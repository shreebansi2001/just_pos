package com.crmportal.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateUtils {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// Multiple datetime formatters for flexibility
	private static final DateTimeFormatter[] DATETIME_FORMATTERS = {
			DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm a", Locale.ENGLISH),
			DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm a", Locale.ENGLISH),
			DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.ENGLISH),
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a", Locale.ENGLISH),
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"), DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm") };

	public static LocalDate parseDate(String dateString) {
		if (dateString == null || dateString.trim().isEmpty()) {
			return null;
		}
		try {
			return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
		} catch (DateTimeParseException e) {
			throw new RuntimeException("Invalid date format: " + dateString + ". Expected format: dd/MM/yyyy", e);
		}
	}

	public static LocalDateTime parseDateTime(String dateTimeString) {
		if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
			return null;
		}

		String trimmed = dateTimeString.trim();

		// Try each formatter until one works
		for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
			try {
				return LocalDateTime.parse(trimmed, formatter);
			} catch (DateTimeParseException e) {
				// Continue to next formatter
			}
		}

		// If none worked, throw exception
		throw new RuntimeException("Invalid datetime format: " + dateTimeString
				+ ". Expected formats: dd/MM/yyyy h:mm a, dd/MM/yyyy HH:mm, etc.");
	}

	public static String formatLocalDateTime(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return dateTime.format(formatter);
	}

	public static String formatLocalDate(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return date.format(formatter);
	}
}