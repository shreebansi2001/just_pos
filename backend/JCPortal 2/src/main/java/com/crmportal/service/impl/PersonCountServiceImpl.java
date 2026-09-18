package com.crmportal.service.impl;

import org.springframework.http.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.crmportal.entity.PersonCount;
import com.crmportal.repository.PersonCountRepository;
import com.crmportal.response.dto.PersonCountResponse;
import com.crmportal.service.PersonCountService;

@Service
public class PersonCountServiceImpl implements PersonCountService {

	@Value("${camera.ip}")
	private String cameraIp;

	@Value("${camera.username}")
	private String username;

	@Value("${camera.password}")
	private String password;

	@Autowired
	private PersonCountRepository repository;

	public PersonCountResponse fetchFromCamera() {

		PersonCountResponse response = new PersonCountResponse();

		try {

			String apiUrl = "http://" + cameraIp + "/matrix-cgi/eventaction?action=geteventstatus";

			System.out.println("\n========================================");
			System.out.println("CAMERA API DEBUG");
			System.out.println("========================================");
			System.out.println("Camera IP      : " + cameraIp);
			System.out.println("Username       : " + username);
			System.out.println("URL            : " + apiUrl);

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setBasicAuth(username, password);

			System.out.println("Authorization  : Basic Auth Added");

			HttpEntity<Void> entity = new HttpEntity<>(headers);

			System.out.println("Calling Camera API...");

			ResponseEntity<String> result = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

			System.out.println("HTTP Status    : " + result.getStatusCode());
			System.out.println("Status Code    : " + result.getStatusCodeValue());

			String body = result.getBody();

			System.out.println("\n========== RAW RESPONSE ==========");
			System.out.println(body);
			System.out.println("==================================\n");

			if (body == null || body.trim().isEmpty()) {

				System.out.println("ERROR : Empty response received from camera");
				return null;
			}

			String[] lines = body.split("\n");

			System.out.println("Total Lines Received : " + lines.length);

			for (String line : lines) {

				System.out.println("LINE -> " + line);

				if (line.contains("response-code")) {

					System.out.println("FOUND RESPONSE CODE : " + line);
				}

				if (line.contains("line0_count-in")) {

					String value = line.split("=")[1].trim();

					System.out.println("Count In Found : " + value);

					response.setCountIn(Integer.parseInt(value));
				}

				if (line.contains("line0_count-out")) {

					String value = line.split("=")[1].trim();

					System.out.println("Count Out Found : " + value);

					response.setCountOut(Integer.parseInt(value));
				}

				if (line.contains("line0_total-count-in")) {

					String value = line.split("=")[1].trim();

					System.out.println("Total In Found : " + value);

					response.setTotalIn(Integer.parseInt(value));
				}

				if (line.contains("line0_total-count-out")) {

					String value = line.split("=")[1].trim();

					System.out.println("Total Out Found : " + value);

					response.setTotalOut(Integer.parseInt(value));
				}
			}

			System.out.println("\n========== PARSED DATA ==========");
			System.out.println("Count In  : " + response.getCountIn());
			System.out.println("Count Out : " + response.getCountOut());
			System.out.println("Total In  : " + response.getTotalIn());
			System.out.println("Total Out : " + response.getTotalOut());
			System.out.println("=================================\n");

			return response;

		} catch (Exception e) {

			System.out.println("\n========== ERROR ==========");
			System.out.println("Exception Type : " + e.getClass().getName());
			System.out.println("Message        : " + e.getMessage());
			System.out.println("===========================\n");

			e.printStackTrace();

			return null;
		}
	}

	public void fetchAndSave() {

		System.out.println("\n========================================");
		System.out.println("STARTING CAMERA SYNC");
		System.out.println("========================================");

		PersonCountResponse response = fetchFromCamera();

		if (response == null) {

			System.out.println("Response object is NULL");
			System.out.println("Skipping database save");
			return;
		}

		System.out.println("Response Object Received");

		System.out.println("Count In  : " + response.getCountIn());
		System.out.println("Count Out : " + response.getCountOut());
		System.out.println("Total In  : " + response.getTotalIn());
		System.out.println("Total Out : " + response.getTotalOut());

		if (response.getTotalIn() == null || response.getTotalOut() == null) {

			System.out.println("People count data not available from camera");
			System.out.println("Skipping database save");
			return;
		}

		PersonCount entity = new PersonCount();

		entity.setCountIn(response.getCountIn());
		entity.setCountOut(response.getCountOut());
		entity.setTotalIn(response.getTotalIn());
		entity.setTotalOut(response.getTotalOut());

		int occupancy = response.getTotalIn() - response.getTotalOut();

		entity.setOccupancy(occupancy);
		entity.setCreatedAt(LocalDateTime.now());

		System.out.println("Calculated Occupancy : " + occupancy);

		repository.save(entity);

		System.out.println("Saved Successfully");
	}
}
