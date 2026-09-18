package com.crmportal.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationFilter jwtFilter;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private static final String[] WHITELIST = { "/v1/api/auth/**","/api/download/**", "/v1/api/statemaster/getbycountryid",
			"/v1/api/citymaster/getbystateid", "/v1/api/auth/add", "/swagger-ui.html", "/swagger-ui/**","/v1/api/menu-share/verify",
			"/swagger-resources/**", "/v3/api-docs/**", "/v2/api-docs/**", "/webjars/**", "/v1/api/vendor/auth/**", "/v1/api/jt-enquiry/**", "/v1/api/tap-inquiry**",
			"/v1/api/event-food-testing/**"};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		if (isLocalEnvironment()) {
			http.authorizeRequests().antMatchers("/**").permitAll().and().headers().frameOptions().disable();
		} else {
			http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll().antMatchers(WHITELIST)
					.permitAll().anyRequest().authenticated().and()
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		}
	}

	private boolean isLocalEnvironment() {
		return "local".equalsIgnoreCase(activeProfile);
	}



}
