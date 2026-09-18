package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundDetailsRequestDto {
	private Long id;
	
	private String refundPaymentMode;
	
	private BigDecimal amount;
	
	private String refundDate;
	
	private String remarks;
	
	private String refundType;
	
	private String refundDetails;
	
	private MultipartFile file;
	
}
