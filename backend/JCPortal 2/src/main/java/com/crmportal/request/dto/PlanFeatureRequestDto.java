package com.crmportal.request.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PlanFeatureRequestDto {

	@NotBlank(message = "Feature text cannot be blank")
    private String featureText;
	
	public String getFeatureText() {
        return featureText != null ? featureText.trim() : null;
    }

    public void setFeatureText(String featureText) {
        this.featureText = featureText;
    }
}
