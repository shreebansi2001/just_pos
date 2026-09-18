package com.crmportal.request.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class EventRawMaterialAppRequestDetails {
	Long supplierId=0l;
	@NotBlank(message = ", is a mandatory field")
	Long rawMaterialId;
	@NotBlank(message = ", is a mandatory field")
	Long unitId;
	Double qty=0.0;
	Double finalQty=0.0;
	String place;
	Double totalprice=0.0;
}
