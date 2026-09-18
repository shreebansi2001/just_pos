package com.crmportal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocktype_rights")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTypeRightsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rights_id")
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "stock_type_id")
	private Long stockTypeId;

}
