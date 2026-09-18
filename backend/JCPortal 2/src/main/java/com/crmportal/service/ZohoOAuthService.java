package com.crmportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.crmportal.response.dto.OAuthTokenResponse;

@Service
public class ZohoOAuthService {
	
//	****DO NOT DELETE BELOW COMMENT*****
	
//	step1:  https://accounts.zoho.in/oauth/v2/org/auth?scope=ZohoPay.payments.CREATE&client_id=1005.GEGCNT6DU8M3MVJBTGOSDXC6NPS3TH&soid=zohopay.60064121975&response_type=code&redirect_uri=http://app.justcatering.in&access_type=offline
//	step2: copy genatete url like below and copy code from it and call post method in post man for refresh token
//	https://app.justcatering.in/?code=1005.8a1960b59db8c918193e1fd5549e244a.bed90be43148c55ac450e41cfbf11792&location=in&accounts-server=https%3A%2F%2Faccounts.zoho.in
//	step 3 : post man collection for refresh token
//		curl --location 'https://accounts.zoho.in/oauth/v2/token' \
//		--header 'Content-Type: application/x-www-form-urlencoded' \
//		--header 'Cookie: iamcsr=437cd6b1-61cd-4a7f-b88f-9d6e3f5fd311; zalb_6e73717622=cb69139f2b568dd90a2d2ea66b6b9727' \
//		--data-urlencode 'code=1005.8a1960b59db8c918193e1fd5549e244a.bed90be43148c55ac450e41cfbf11792' \
//		--data-urlencode 'client_id=1005.GEGCNT6DU8M3MVJBTGOSDXC6NPS3TH' \
//		--data-urlencode 'client_secret=32496f9a3ad41425648a7b19493ec158ef12b6988c' \
//		--data-urlencode 'redirect_uri=http://app.justcatering.in' \
//		--data-urlencode 'grant_type=authorization_code'
//		
//	copy refresh tokent from response and add in application.properties.

    @Value("${zoho.oauth.base-url}")
    private String oauthBaseUrl;

    @Value("${zoho.client-id}")
    private String clientId;

    @Value("${zoho.client-secret}")
    private String clientSecret;

    @Value("${zoho.refresh-token}")
    private String refreshToken;

    private String cachedAccessToken;
    
    @Value("${zoho.redirect-uri}")
    private String redirectUri;

    public String getAccessToken() {

        if (cachedAccessToken != null) {
            return cachedAccessToken;
        }
        
        String url = oauthBaseUrl + "/oauth/v2/token"
                + "?refresh_token=" + refreshToken
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&grant_type=refresh_token";

        /*String url = oauthBaseUrl + "/oauth/v2/token"
                + "?code=" + refreshToken
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&grant_type=authorization_code";*/
        
        System.out.println("url : "+url);

        RestTemplate restTemplate = new RestTemplate();
        OAuthTokenResponse response =
                restTemplate.postForObject(url, null, OAuthTokenResponse.class);

        cachedAccessToken = response.getAccess_token();
        System.out.println("cachedAccessToken : "+cachedAccessToken);
        return cachedAccessToken;
    }
}

