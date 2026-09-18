package com.crmportal.request.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMasterRequestDto {

	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name must not exceed 50 characters")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 50, message = "Last name must not exceed 50 characters")
	private String lastName;

	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be between 10 and 15 digits")
	private String contactNo;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	private String companyName;

	private String companyEmail;

	private String address;

	private String officeNo;

	@NotBlank(message = "Country code is required")
	@Pattern(regexp = "^[+][0-9]{1,4}$", message = "Country code must be in the format +91, +1, etc.")
	private String countryCode;

	private Boolean isTaskAccess = true;

	private Boolean isAttendanceLeaveAccess = true;

	private Long countryId;

	private Long stateId;

	private Long cityId;

	@NotNull(message = "Role ID is required")
	private Long roleId;

	private Long reportingManagerId;

	private Long clientId;

	private Long leadId;

	private String remarks;

	private String password;

	private String confirmPassword;

	private Boolean ischilduser = false;

	private String softType = "jcx";

	private String gstNumber;

	private String panNumber;

	private String fdaLincense;

	private String cinNumber;

	private String hsnNumber;
	private String fssaiNumber;
	private Integer followupDay;
}
