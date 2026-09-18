package com.crmportal.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.service.IpAddressService;

@Service
public class IpAddressServiceImpl implements IpAddressService {

	 @Override
	 public String getClientIp(HttpServletRequest request) {
	        String ip = request.getHeader("X-Forwarded-For");
	        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
	            // In case of multiple IPs, take the first one
	            return ip.split(",")[0];
	        }

	        ip = request.getHeader("Proxy-Client-IP");
	        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
	            return ip;
	        }

	        ip = request.getHeader("WL-Proxy-Client-IP");
	        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
	            return ip;
	        }

	        return request.getRemoteAddr();  // fallback
	    }
}
