package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.response.dto.UnitConversionResult;

@Service
public class UnitConversionServiceImpl {

	private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
	private static final BigDecimal ONE = BigDecimal.ONE;

	public UnitConversionResult autoConvert(UnitMasterEntity unit, BigDecimal qty) {

		if (unit == null || qty == null) {
			return new UnitConversionResult(unit, qty);
		}

		// CHILD → PARENT
		if (unit.getParentUnit() != null) {

			validateEquivalent(unit.getEquivalentValue());

			BigDecimal equivalent = BigDecimal.valueOf(unit.getEquivalentValue());

			if (qty.compareTo(equivalent) >= 0) {

				int scale = (unit.getParentUnit().getDecimalLimit() == null 
			             || unit.getParentUnit().getDecimalLimit() < 1)
			        ? 2
			        : unit.getParentUnit().getDecimalLimit();

				
				BigDecimal parentQty = qty.divide(equivalent,
						scale, RoundingMode.HALF_UP);

				return new UnitConversionResult(unit.getParentUnit(), parentQty);
			}
		}

		// PARENT → CHILD
		if (unit.getParentUnit() == null && unit.getChildren() != null && !unit.getChildren().isEmpty()
				&& qty.compareTo(ONE) < 0) {

			UnitMasterEntity child = unit.getChildren().get(0);
			validateEquivalent(child.getEquivalentValue());

			BigDecimal childQty = qty.multiply(BigDecimal.valueOf(child.getEquivalentValue()));

			return new UnitConversionResult(child, childQty);
		}

		return new UnitConversionResult(unit, qty);
	}

	private void validateEquivalent(Double equivalent) {
		if (equivalent == null || equivalent <= 0) {
			throw new IllegalStateException("Invalid unit equivalent value");
		}
	}
}
