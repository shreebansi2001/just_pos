package com.crmportal.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.crmportal.entity.MenuItemRawMaterialEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UnitRangeEntity;
import com.crmportal.entity.UnitStepwiseRangeEntity;
import com.crmportal.repository.UnitRangeRepository;
import com.crmportal.repository.UnitStepwiseRangeRepository;
import com.crmportal.service.UnitMasterService;

@Component
public class RoundOffUtility {

	@Autowired
	UnitStepwiseRangeRepository unitStepwiseRangeRepository;

	@Autowired
	UnitMasterService unitMasterService;

	@Autowired
	UnitRangeRepository unitRangeRepository;
	private static final BigDecimal ZERO = BigDecimal.ZERO;
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);

	public BigDecimal applyUnitRange(BigDecimal value, UnitMasterEntity unit,
			MenuItemRawMaterialEntity itemRawMaterialEntity) {

		if (unit == null || value == null) {
			return value;
		}

		String rangeType = unitMasterService.getById(unit.getId()).getRangeType() == null ? null
				: unitMasterService.getById(unit.getId()).getRangeType().toString();
		if (rangeType == null) {
			return value;
		}

		/* ================= STEPWISE ================= */
		if ("STEPWISE".equals(rangeType)) {

			UnitStepwiseRangeEntity stepwiseRange = unitStepwiseRangeRepository.findByUnitAndIsDeleteFalse(unit);

			if (stepwiseRange != null) {
				BigDecimal step = BigDecimal.valueOf(stepwiseRange.getStepValue());
				return value.divide(step, 2, RoundingMode.CEILING).multiply(step);
			}
		}

		/* ================= RANGE ================= */
		else if ("RANGE".equals(rangeType)) {

			List<UnitRangeEntity> ranges = unitRangeRepository.findAllByUnitAndIsDeleteFalse(unit);

			for (UnitRangeEntity range : ranges) {

				BigDecimal min = BigDecimal.valueOf(range.getMinValue());
				BigDecimal max = BigDecimal.valueOf(range.getMaxValue());

				if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
					return BigDecimal.valueOf(range.getRoundOffValue());
				}
			}
		}

		/* ================= PRECISION ================= */
		else if ("PRECISION".equals(rangeType)) {

			List<UnitRangeEntity> ranges = unitRangeRepository.findAllByUnitAndIsDeleteFalse(unit);

			BigDecimal integerPart = value.setScale(0, RoundingMode.FLOOR);
			BigDecimal decimalPart = value.subtract(integerPart).movePointRight(3);

			for (UnitRangeEntity range : ranges) {

				BigDecimal min = BigDecimal.valueOf(range.getMinValue());
				BigDecimal max = BigDecimal.valueOf(range.getMaxValue());

				if (decimalPart.compareTo(min) >= 0 && decimalPart.compareTo(max) <= 0) {
					decimalPart = BigDecimal.valueOf(range.getRoundOffValue());
					break;
				}
			}

			return integerPart.add(decimalPart.movePointLeft(3));
		}

		return value;
	}

}
