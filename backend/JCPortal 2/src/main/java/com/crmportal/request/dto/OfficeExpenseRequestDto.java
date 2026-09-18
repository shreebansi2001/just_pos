package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.UserMasterEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeExpenseRequestDto {
	
	private Long id;
	
	private String title;
	
	private BigDecimal expenseAmount;
	
	private String expenseDate;
	
	private String dueDate;
	
	private String paidDate;
	
	private String paymentMode;
	
	private String expenseType;

	private String remarks;
	
	private Long incomeExpenseTypeId;
	
	private Long accountContactId;
	
	private Long userId;
	
	private Long adminId;
	
	private List<MultipartFile> doc;
}
