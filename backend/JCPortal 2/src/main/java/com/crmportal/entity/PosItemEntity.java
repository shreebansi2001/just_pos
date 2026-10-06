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
@Table(name = "pos_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PosItemEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId = 1L; // Admin/Owner account

	@Column(name = "code", length = 30)
	private String code;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "category_id")
	private Long categoryId;

	@Column(name = "category_code", length = 30)
	private String categoryCode;

	@Column(name = "is_veg")
	private Boolean veg = true;

	@Column(name = "pricing_type", length = 30)
	private String pricingType = "portion"; // 'portion' (per plate), 'kg' (bulk weight), 'both' (dual mode)

	@Column(name = "price")
	private Double price = 0.0; // Standard portion / plate price

	@Column(name = "price_per_kg")
	private Double pricePerKg; // Rate per Kg when sold in bulk / weight

	@Column(name = "portion_weight_grams")
	private Integer portionWeightGrams; // Approximate weight in grams for 1 plate

	@Column(name = "tax_type", length = 20)
	private String taxType = "exclusive"; // 'exclusive', 'inclusive'

	@Column(name = "gst_rate")
	private Double gstRate = 5.0; // GST percentage (5, 12, 18)

	@Column(name = "tag", length = 200)
	private String tag;

	@Column(name = "station", length = 60)
	private String station = "Kitchen"; // 'Kitchen', 'Bar', 'Dessert Counter', 'Live Counter'

	@Column(name = "is_active")
	private Boolean active = true;

	@Column(name = "created_by_user_id")
	private Long createdByUserId;

	@Column(name = "updated_by_user_id")
	private Long updatedByUserId;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<PosItemVariantEntity> variants = new ArrayList<PosItemVariantEntity>();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at",columnDefinition = "DATETIME")
	private Date createdAt = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at",columnDefinition = "DATETIME")
	private Date updatedAt = new Date();

	public void setItemName(String itemName) {
		if (this.name == null || this.name.trim().isEmpty()) {
			this.name = itemName;
		}
	}

	public String getItemName() {
		return this.name;
	}
}
