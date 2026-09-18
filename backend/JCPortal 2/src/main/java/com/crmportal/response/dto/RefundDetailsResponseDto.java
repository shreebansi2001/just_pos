package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundDetailsResponseDto {
	private Long id;
	
	private String refundPaymentMode;
	
	private BigDecimal amount;
	
	private String refundDate;
	
	private String remarks;
	
	private String refundType;
	
	private String refundDetails;
	
	private Boolean isDelete;
	
	private String createdAt;
	
	private String updatedAt;
	
	private String file;
	
	private Long user;
}
