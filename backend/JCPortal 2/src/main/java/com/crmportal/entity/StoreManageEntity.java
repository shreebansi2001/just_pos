package com.crmportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
@Table(name = "store_manage")
public class StoreManageEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_manage_id")
    private Long id;
	
	 @Column(name = "voucher_no")
	    private String voucherNo;

	    @Column(name = "manage_date")
	    private LocalDate manageDate;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_Id")
	    private UserMasterEntity user;
	    
	    @Column(name = "is_delete")
		private Boolean isDelete = false;
		
		@CreationTimestamp
		@Column(name = "created_at", columnDefinition = "DATETIME")
		private LocalDateTime createdAt;
		
		@Column(name = "updated_at", columnDefinition = "DATETIME")
		private LocalDateTime updatedAt;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "stock_type_id")
		private StockTypeEntity stockType;
}
