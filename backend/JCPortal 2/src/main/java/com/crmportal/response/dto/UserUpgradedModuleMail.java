package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpgradedModuleMail {

	private String userName;
	private String moduleName;
	private String billingCycle;
	private String email;
}
