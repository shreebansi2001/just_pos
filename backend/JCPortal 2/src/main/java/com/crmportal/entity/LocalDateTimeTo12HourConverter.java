package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Custom JPA Converter for 12-hour format
@Converter
public class LocalDateTimeTo12HourConverter implements AttributeConverter<LocalDateTime, String> {

	private static final DateTimeFormatter TWELVE_HOUR_FORMATTER = 
	        DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
	    
	    @Override
	    public String convertToDatabaseColumn(LocalDateTime attribute) {
	        return attribute != null ? attribute.format(TWELVE_HOUR_FORMATTER) : null;
	    }
	    
	    @Override
	    public LocalDateTime convertToEntityAttribute(String dbData) {
	        return dbData != null ? LocalDateTime.parse(dbData, TWELVE_HOUR_FORMATTER) : null;
	    }
}
