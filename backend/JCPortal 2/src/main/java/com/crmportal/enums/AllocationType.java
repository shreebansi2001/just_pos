package com.crmportal.enums;

public enum AllocationType {
	OUTSIDE, CHEF, INSIDE, LABOUR;

	public static AllocationType from(Boolean chef, Boolean outside, Boolean inside) {
		if (Boolean.TRUE.equals(outside))
			return OUTSIDE;
		if (Boolean.TRUE.equals(chef))
			return CHEF;
		return INSIDE;
	}

	public static AllocationType fromString(String type) {
		if (type.equalsIgnoreCase("outside"))
			return OUTSIDE;
		if (type.equalsIgnoreCase("chef"))
			return CHEF;
		if (type.equalsIgnoreCase("labour"))
			return LABOUR;
		return INSIDE;
	}
}