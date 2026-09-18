package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.mail.Multipart;
import javax.persistence.Column;
import javax.persistence.FetchType;
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
public class UserAmcRequestDto {
	private Long id;
	
	private String amcType;

	private BigDecimal amcAmount;

	private String amcRemarks;

	private String amcDate;

	private BigDecimal amcRecivableAmount;

	private String amcRecivableDate;

	private String status;

	private MultipartFile file;
	
}
