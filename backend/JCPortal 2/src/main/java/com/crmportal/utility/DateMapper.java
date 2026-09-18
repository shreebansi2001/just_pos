package com.crmportal.utility;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Component
public class DateMapper {

    // Top-level date (dd/MM/yyyy)
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

    // Top-level date-time 12-hour clock with AM/PM (case insensitive)
    private static final DateTimeFormatter DATE_TIME_FORMATTER_12 =
            new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd/MM/yyyy hh:mm a")
                .toFormatter(Locale.ENGLISH);

    // Top-level date-time 24-hour clock
    private static final DateTimeFormatter DATE_TIME_FORMATTER_24 =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ENGLISH);
    
    // Event function date-time (yyyy-MM-dd HH:mm)
    private static final DateTimeFormatter FUNCTION_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH);

    private static final DateTimeFormatter DATE_TIME_TO_STRING_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    
    // Specific formatter for function date input (dd/MM/yyyy hh:mm a) - case insensitive
    private static final DateTimeFormatter FUNCTION_INPUT_FORMATTER =
            new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("dd/MM/yyyy hh:mm a")
                .toFormatter(Locale.ENGLISH);

    // ------------------- Optional Date -------------------
    @Named("stringToDate")
    public LocalDate stringToDate(String date) {
        if (date == null || date.trim().isEmpty()) return null;
        date = date.trim();
        if (date.isEmpty()) return null;
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Unable to parse date: '" + date + "'", e);
        }
    }

    // ------------------- Top-level DateTime -------------------
    @Named("stringToDateTime")
    public LocalDateTime stringToDateTime(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) return null;
        dateTime = dateTime.trim();
        try {
            return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER_12);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER_24);
            } catch (DateTimeParseException ex) {
                throw new RuntimeException("Unable to parse dateTime: '" + dateTime + "'", ex);
            }
        }
    }
    
    @Named("dateStringToDateTime")
    public LocalDateTime dateStringToDateTime(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) return null;
        dateTime = dateTime.trim();
        try {
            return LocalDate.parse(dateTime, DATE_FORMATTER).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Unable to parse dateTime: '" + dateTime + "'", e);
        }
    }

    // ------------------- Event Function DateTime -------------------
    @Named("stringToFunctionDateTime")
    public LocalDateTime stringToFunctionDateTime(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) return null;
        dateTime = dateTime.trim();
        try {
            return LocalDateTime.parse(dateTime, FUNCTION_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Unable to parse function dateTime: '" + dateTime + "'", e);
        }
    }

    @Named("stringToFunctionInputDateTime")
    public LocalDateTime stringToFunctionInputDateTime(String dateTime) {
        
        if (dateTime == null) {
            return null;
        }
        
        if (dateTime.trim().isEmpty()) {
            return null;
        }

        // Debug the first few characters

        String cleanedDateTime = cleanStringAggressively(dateTime);

        if (cleanedDateTime.isEmpty()) {
            return null;
        }

        try {
            LocalDateTime result = LocalDateTime.parse(cleanedDateTime, FUNCTION_INPUT_FORMATTER);
            return result;
        } catch (DateTimeParseException e) {
            return tryAlternativeParsing(cleanedDateTime, e);
        }
    }

    private LocalDateTime tryAlternativeParsing(String dateTime, DateTimeParseException originalException) {
        try {
            // Try without AM/PM (24-hour format)
            return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER_24);
        } catch (DateTimeParseException e) {
            
            try {
                // Try manual parsing
                return manualParseDateTime(dateTime);
            } catch (Exception manualException) {
                
                // Provide detailed error information
                String errorDetails = "\nOriginal error: " + originalException.getMessage() +
                                    "\nString: '" + dateTime + "'" +
                                    "\nLength: " + dateTime.length() +
                                    "\nCharacter codes: " + getCharacterCodes(dateTime);
                
                throw new RuntimeException("Failed to parse dateTime after all attempts." + errorDetails, originalException);
            }
        }
    }

    private String cleanStringAggressively(String input) {
        if (input == null) return "";
        
        // Remove BOM and other non-printable characters
        String cleaned = input.trim()
            .replace("\uFEFF", "") // Remove BOM
            .replace("\u200B", "") // Remove zero-width space
            .replaceAll("[^\\p{Print}]", "") // Remove non-printable characters
            .replaceAll("\\s+", " ") // Normalize whitespace
            .replace("P.M.", "PM")
            .replace("A.M.", "AM")
            .replace("p.m.", "PM")
            .replace("a.m.", "AM");
            
        return cleaned.trim();
    }

    private LocalDateTime manualParseDateTime(String dateTime) {
        try {
            // Expected format: "14/09/2025 10:00 PM"
            String[] dateTimeParts = dateTime.split(" ");
            
            if (dateTimeParts.length < 3) {
                throw new DateTimeParseException("Not enough parts for manual parsing", dateTime, 0);
            }
            
            String datePart = dateTimeParts[0]; // "14/09/2025"
            String timePart = dateTimeParts[1]; // "10:00"
            String amPmPart = dateTimeParts[2].toUpperCase(); // "PM"
            
            // Parse date
            String[] dateComponents = datePart.split("/");
            if (dateComponents.length != 3) {
                throw new DateTimeParseException("Invalid date format", dateTime, 0);
            }
            
            int day = Integer.parseInt(dateComponents[0]);
            int month = Integer.parseInt(dateComponents[1]);
            int year = Integer.parseInt(dateComponents[2]);
            
            // Parse time
            String[] timeComponents = timePart.split(":");
            if (timeComponents.length != 2) {
                throw new DateTimeParseException("Invalid time format", dateTime, 0);
            }
            
            int hour = Integer.parseInt(timeComponents[0]);
            int minute = Integer.parseInt(timeComponents[1]);
            
            // Adjust for AM/PM
            if ("PM".equals(amPmPart) && hour < 12) {
                hour += 12;
            } else if ("AM".equals(amPmPart) && hour == 12) {
                hour = 0;
            }
            
            return LocalDateTime.of(year, month, day, hour, minute);
            
        } catch (Exception e) {
            throw new DateTimeParseException("Manual parsing failed: " + e.getMessage(), dateTime, 0, e);
        }
    }

    private String getFirstCharactersDebug(String input) {
        if (input == null || input.isEmpty()) return "null or empty";
        
        StringBuilder debug = new StringBuilder();
        for (int i = 0; i < Math.min(input.length(), 5); i++) {
            char c = input.charAt(i);
            debug.append(String.format("'%c'(%d) ", c, (int) c));
        }
        return debug.toString();
    }

    private String getCharacterCodes(String input) {
        if (input == null || input.isEmpty()) return "null or empty";
        
        StringBuilder codes = new StringBuilder();
        for (int i = 0; i < Math.min(input.length(), 20); i++) {
            char c = input.charAt(i);
            codes.append(String.format("%d:'%c'(%d) ", i, c, (int) c));
        }
        if (input.length() > 20) codes.append("...");
        return codes.toString();
    }

    // ------------------- Convert back to String -------------------
    @Named("dateToString")
    public String dateToString(LocalDate date) {
        return (date != null) ? date.format(DATE_FORMATTER) : null;
    }

    @Named("dateTimeToString")
    public String dateTimeToString(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_TIME_FORMATTER_12) : null;
    }

    @Named("functionDateTimeToString")
    public String functionDateTimeToString(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(FUNCTION_DATE_TIME_FORMATTER) : null;
    }
    
    @Named("dateTimeWithSecondToString")
    public String dateTimeWithSecondToString(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(DATE_TIME_TO_STRING_FORMATTER) : null;
    }

    @Named("functionInputDateTimeToString")
    public String functionInputDateTimeToString(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(FUNCTION_INPUT_FORMATTER) : null;
    }
    
    @Named("stringToLeadTentDateTime")
    public LocalDateTime stringToLeadTentDateTime(String dateTime) {

        if (dateTime == null || dateTime.trim().isEmpty()) {
            return null;
        }

        dateTime = dateTime.trim();

        try {

            // Date only: 01/09/2026
            if (dateTime.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return LocalDate.parse(
                        dateTime,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ).atStartOfDay();
            }

            // Date + Time: 01/09/2026 10:30:00
            return LocalDateTime.parse(
                    dateTime,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to parse dateTime: '" + dateTime + "'", e
            );
        }
    }
}
