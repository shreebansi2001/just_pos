package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.crmportal.entity.UnitMasterEntity;

public class UnitConversionResult {
	private UnitMasterEntity unit;
	private BigDecimal quantity;

	public UnitConversionResult(UnitMasterEntity unit, BigDecimal quantity) {
		this.unit = unit;
		this.quantity = quantity;
	}

	public UnitMasterEntity getUnit() {
		return unit;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setUnit(UnitMasterEntity unit) {
		this.unit = unit;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	
	
}
