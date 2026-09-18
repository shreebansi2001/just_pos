package com.crmportal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface IpAddressService {

	String getClientIp(HttpServletRequest request);


}
