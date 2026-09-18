package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.ExpenseEntity;
import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDetailRequestDto {

	private Long id;
	
	private String expenseDate;
	
	private String perticular;

	private String paymentMode;
	
	private BigDecimal amount;

	private String remarks;
	
	private Long expenseId;
	
	private Long accountContactId;
	
	private Long userId;
	
	private Long km;
	
	private MultipartFile file;
}
