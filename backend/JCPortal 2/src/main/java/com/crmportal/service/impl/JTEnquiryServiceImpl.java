package com.crmportal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.JTEnquiryEntity;
import com.crmportal.entity.JTEnquiryEventDateEntity;
import com.crmportal.enums.JTEnquiryServiceType;
import com.crmportal.repository.JTEnquiryEventDateRepository;
import com.crmportal.repository.JTEnquiryRepository;
import com.crmportal.request.dto.JTEnquiryRequestDto;
import com.crmportal.response.dto.JTEnquiryResponseDto;
import com.crmportal.service.JTEnquiryService;

@Service
public class JTEnquiryServiceImpl implements JTEnquiryService {

	@Autowired
	JTEnquiryRepository jtEnquiryRepository;

	@Autowired
	JTEnquiryEventDateRepository jtEnquiryEventDateRepository;

	@Override
	public JTEnquiryResponseDto addOrUpdateEnquiry(@Valid JTEnquiryRequestDto request) {
		DateTimeFormatter dateFomat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		JTEnquiryEntity entity;

		if (request.getId() != null && request.getId() > 0) {
			entity = jtEnquiryRepository.findById(request.getId())
					.orElseThrow(() -> new RuntimeException("Enquiry not found with id : " + request.getId()));

			entity.setUpdatedAt(LocalDateTime.now());

		} else {
			entity = new JTEnquiryEntity();
		}

		entity.setFullName(request.getFullName());
		entity.setMobileNo(request.getMobileNo());
		entity.setCmpName(request.getCmpName());
		entity.setCity(request.getCity());
		entity.setNotes(request.getNotes());
		entity.setType(request.getType());

		entity = jtEnquiryRepository.save(entity);

		jtEnquiryEventDateRepository.deleteAllByEnquiry(entity);

		if (request.getEventDates() != null && !request.getEventDates().isEmpty()) {

			List<JTEnquiryEventDateEntity> eventDateEntities = new ArrayList<>();

			for (String date : request.getEventDates()) {

				JTEnquiryEventDateEntity dateEntity = new JTEnquiryEventDateEntity();

				dateEntity.setEnquiry(entity);
				dateEntity.setEventDate(LocalDate.parse(date, dateFomat));

				eventDateEntities.add(dateEntity);
			}
			jtEnquiryEventDateRepository.saveAll(eventDateEntities);
		}

		JTEnquiryResponseDto response = new JTEnquiryResponseDto();

		response.setId(entity.getId());
		response.setFullName(entity.getFullName());
		response.setMobileNo(entity.getMobileNo());
		response.setCmpName(entity.getCmpName());
		response.setCity(entity.getCity());
		response.setNotes(entity.getNotes());
		response.setEventDates(request.getEventDates());

		return response;
	}

	@Override
	public List<JTEnquiryResponseDto> getAllEnquiry(String startDate, String endDate, JTEnquiryServiceType type) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDateTime start = startDate != null ? LocalDate.parse(startDate, dateFormat).atStartOfDay() : null;
		LocalDateTime end = endDate != null ? LocalDate.parse(endDate, dateFormat).atTime(23, 59, 59) : null;

		List<JTEnquiryEntity> enquiryList = jtEnquiryRepository.getAllEnquiry(start, end, type);

		List<Long> enquiryIds = enquiryList.stream().map(JTEnquiryEntity::getId).collect(Collectors.toList());

		List<JTEnquiryEventDateEntity> eventDateList = jtEnquiryEventDateRepository.findByEnquiryIdIn(enquiryIds);

		Map<Long, Set<String>> eventDateMap = eventDateList.stream().collect(Collectors.groupingBy(
				e -> e.getEnquiry().getId(), Collectors.mapping(e -> e.getEventDate().format(dateFormat), Collectors.toSet())));

		return enquiryList.stream().map(entity -> {

			JTEnquiryResponseDto dto = new JTEnquiryResponseDto();

			dto.setId(entity.getId());
			dto.setFullName(entity.getFullName());
			dto.setMobileNo(entity.getMobileNo());
			dto.setCmpName(entity.getCmpName());
			dto.setCity(entity.getCity());
			dto.setNotes(entity.getNotes());

			dto.setEventDates(eventDateMap.getOrDefault(entity.getId(), new HashSet<>()));

			return dto;

		}).collect(Collectors.toList());
	}

	@Override
	public JTEnquiryResponseDto getEnquiryById(Long id) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		JTEnquiryEntity entity = jtEnquiryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Enquiry not found with id : " + id));

		List<JTEnquiryEventDateEntity> eventDateList = jtEnquiryEventDateRepository.findByEnquiry(entity);

		Set<String> eventDates = eventDateList.stream().map(e -> e.getEventDate().format(dateFormat))
				.collect(Collectors.toSet());

		JTEnquiryResponseDto response = new JTEnquiryResponseDto();

		response.setId(entity.getId());
		response.setFullName(entity.getFullName());
		response.setMobileNo(entity.getMobileNo());
		response.setCmpName(entity.getCmpName());
		response.setCity(entity.getCity());
		response.setNotes(entity.getNotes());
		response.setEventDates(eventDates);

		return response;
	}
}
