package com.crmportal.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "menupreparation")
@Data
public class MenuPreparationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_preparation_id")
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "event_function_id", nullable = false)
    private EventFunctionMasterEntity eventFunction;
	
	@Column(name = "pax")
	private Integer pax;
	
	@Column(name = "sortorder")
	private Integer sortorder;
	
	@Column(name = "is_package")
	private Boolean isPackage;
	
	@ManyToOne
    @JoinColumn(name = "package_id", nullable = true)
    private CustomPackageEntity customPackage;
	
	@Column(name = "package_name")
	private String packageName;
	
	@Column(name = "package_price")
	private BigDecimal packagePrice;
	
	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal price = BigDecimal.ZERO;
	
	@Column(name = "default_price", precision = 10, scale = 2)
	private BigDecimal defaultPrice;
	
	@Column(name = "isDelete", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "isUpdate", nullable = false)
	private Boolean isUpdate = false;

	@CreationTimestamp
	@Column(name = "created_at", columnDefinition = "DATETIME", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
//	@OneToMany(mappedBy = "menuPreparation", cascade = CascadeType.ALL, orphanRemoval = true)
//	 private List<MenuPreparationDetailsEntity> menuPreparationDetails = new ArrayList<>();
}
