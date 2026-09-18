package com.crmportal.response.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentOrderDto {
	private Long userId;
	private String paymentorderId;
	private String payid;
	private String paysignature;
	private float orderAmount;
	private float paidAmount;
	private float surcharge;
	private String internalorderId;
	private boolean paymentdone;
	private LocalDateTime paymentdonetimestamp;
	private String username;
	private String mobileno;
	private String emailid;
	
	
	public PaymentOrderDto(boolean paymentdone) {
		this.paymentdone = paymentdone;
	}


	public PaymentOrderDto(Long userId, String paymentorderId, String payid, String paysignature, float orderAmount,
			float paidAmount, float surcharge, String internalorderId, boolean paymentdone, LocalDateTime paymentdonetimestamp,
			String username,String mobileno,String emailid) {
		super();
		this.userId = userId;
		this.paymentorderId = paymentorderId;
		this.payid = payid;
		this.paysignature = paysignature;
		this.orderAmount = orderAmount;
		this.paidAmount = paidAmount;
		this.surcharge = surcharge;
		this.internalorderId = internalorderId;
		this.paymentdone = paymentdone;
		this.paymentdonetimestamp = paymentdonetimestamp;
		this.username = username;
		this.mobileno = mobileno;
		this.emailid = emailid;
	}
	
}

