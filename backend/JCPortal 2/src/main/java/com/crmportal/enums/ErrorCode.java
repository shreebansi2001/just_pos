package com.crmportal.enums;

/**
 * @author Nahid Sheikh
 * @since 13-January-2023
 *
 */
public enum ErrorCode {

	CONSTRAINT_VIOLATION(101), BAD_REQUEST(400), UNAUTHORIZED(401), ACCESS_DENIED(403), NOT_FOUND(404),
	METHOD_NOT_SUPPORTED(405), CONFLICT(409), INVALID_OPERATION(412), INVALID_CUSTOMER(413), INTERNAL_SERVER_ERROR(500);

	private int code;

	ErrorCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}