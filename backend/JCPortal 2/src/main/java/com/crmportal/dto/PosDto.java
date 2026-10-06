package com.crmportal.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PosDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderCreateRequest implements Serializable {
		private Long userId;
		private Long createdByUserId;
		private Long waiterId;
		private String outletCode;
		private String deliveryAddress;
		private String type; // dine-in, takeaway, delivery, catering
		private Long tableId;
		private String customerName;
		private String customerPhone;
		private Long reservationId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderItemUpdate implements Serializable {
		private String itemKey;
		private Long itemId;
		private String itemName;
		private Double price;
		private Integer qty;
		private Integer sentQty;
		private String unitMode; // plate, kg
		private Double weightKg;
		private String variantLabel;
		private String note;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DiscountUpdate implements Serializable {
		private String discountType; // pct or flat
		private Double discountVal;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class KotStatusUpdate implements Serializable {
		private String status; // new, preparing, ready, served
		private Long updatedByUserId;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InvoicePaymentRequest implements Serializable {
		private Long userId;
		private Long cashierId;
		private String paymentMode; // Cash, Card, UPI
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MoveTableRequest implements Serializable {
		private Long userId;
		private Long newTableId;
	}
}
