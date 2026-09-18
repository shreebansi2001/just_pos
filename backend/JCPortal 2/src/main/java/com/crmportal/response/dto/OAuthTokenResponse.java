package com.crmportal.response.dto;


import lombok.Data;

@Data
public class OAuthTokenResponse {

	 private String access_token;
	    private String token_type;
	    private Integer expires_in;
}
