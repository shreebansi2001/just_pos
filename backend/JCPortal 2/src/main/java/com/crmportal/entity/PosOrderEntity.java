package com.crmportal.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pos_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosOrderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId = 1L; // Admin/Owner account (client_id = 0 in users table)

	@Column(name = "order_code", nullable = false, length = 40)
	private String orderCode;

	@Column(name = "order_type", nullable = false, length = 30)
	private String orderType = "dine-in"; // dine-in, takeaway, delivery, catering

	@Column(name = "outlet_code", length = 30)
	private String outletCode = "MAIN";

	@Column(name = "table_id")
	private Long tableId;

	@Column(name = "table_label", length = 30)
	private String tableLabel;

	@Column(name = "customer_name", length = 120)
	private String customerName;

	@Column(name = "customer_phone", length = 30)
	private String customerPhone;

	@Column(name = "delivery_address")
	private String deliveryAddress;

	// Staff Role-based User ID Tracking
	@Column(name = "created_by_user_id")
	private Long createdByUserId; // Waiter/Captain who punched order

	@Column(name = "waiter_id")
	private Long waiterId; // Assigned Captain/Waiter

	@Column(name = "cashier_id")
	private Long cashierId; // Cashier who settled payment

	@Column(name = "delivery_boy_id")
	private Long deliveryBoyId; // Delivery rider

	@Column(name = "approved_by_user_id")
	private Long approvedByUserId; // Admin/Manager who approved discount/void

	@Column(name = "updated_by_user_id")
	private Long updatedByUserId; // Staff member who last edited order

	// Delivery Tracking
	@Column(name = "delivery_status", length = 30)
	private String deliveryStatus = "unassigned";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delivery_assigned_at", columnDefinition = "DATETIME")
	private Date deliveryAssignedAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delivered_at", columnDefinition = "DATETIME")
	private Date deliveredAt;

	// Pricing & Status
	@Column(name = "discount_type", length = 20)
	private String discountType = "pct"; // pct or flat

	@Column(name = "discount_val")
	private Double discountVal = 0.0;

	@Column(name = "status", length = 30)
	private String status = "open"; // open, completed, cancelled

	@Column(name = "reservation_id")
	private Long reservationId;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<PosOrderItemEntity> items = new ArrayList<PosOrderItemEntity>();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", columnDefinition = "DATETIME")
	private Date createdAt = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private Date updatedAt = new Date();
}
