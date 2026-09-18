package com.crmportal.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventLaborEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.ContactCategoryMasterRepository;
import com.crmportal.repository.ContactTypeMasterRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventLaborRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.request.dto.EventLaborDetailsRequestDto;
import com.crmportal.request.dto.EventLaborDetailsRequestForAppDto;
import com.crmportal.request.dto.EventLaborRequestDto;
import com.crmportal.request.dto.EventLaborShiftDetailsRequestDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.EventLaborDetailsResponseDto;
import com.crmportal.response.dto.EventLaborResponseDto;
import com.crmportal.response.dto.EventLaborShiftDetailsResponseDto;
import com.crmportal.response.dto.GetEventLaborResponseDto;
import com.crmportal.response.dto.MenuQuantityReponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventLaborService;

@Service
public class EventLaborServiceImpl implements EventLaborService {

	@Autowired
	EventLaborRepository eventLaborRepository;

	@Autowired
	ContactTypeMasterRepository contactTypeMasterRepository;

	@Autowired
	ContactCategoryMasterRepository contactCategoryMasterRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;
	
	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	private static final DateTimeFormatter CREATE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a",
			Locale.ENGLISH);

	public EventLaborResponseDto getEventLabor(Long eventId, Long eventFunctionId) {
		EventLaborResponseDto eventLab = new EventLaborResponseDto();
		List<EventLaborEntity> evLabEntity = eventLaborRepository
				.findByEvent_IdAndEventFunction_IdOrderBySortOrderAsc(eventId, eventFunctionId);
		eventLab.setEventLabor(toDtoList(evLabEntity));
		eventLab.setEventId(eventId);
		eventLab.setEventFunctionId(eventFunctionId);
		return eventLab;
	}

	public EventLaborResponseDto getEventLaborBySupplier(Long eventId, Long eventFunctionId, Long partyId) {
		EventLaborResponseDto eventLab = new EventLaborResponseDto();
		List<EventLaborEntity> evLabEntity = eventLaborRepository
				.findByEvent_IdAndEventFunction_IdAndContact_Id(eventId, eventFunctionId, partyId);
		eventLab.setEventLabor(toDtoList(evLabEntity));
		eventLab.setEventId(eventId);
		eventLab.setEventFunctionId(eventFunctionId);
		return eventLab;
	}

	public static List<EventLaborDetailsResponseDto> toDtoList(List<EventLaborEntity> entities) {

		Map<String, List<EventLaborEntity>> groupingEntities = entities.stream()
				.collect(Collectors.groupingBy(e -> (e.getContact() != null ? e.getContact() : 0L) + "_"
						+ (e.getContactCategory() != null ? e.getContactCategory() : 0L)));

		return groupingEntities.values().stream().map(group -> {
			EventLaborEntity first = group.get(0);

			List<EventLaborShiftDetailsResponseDto> labourShifts = group.stream()
					.sorted(Comparator.comparing(e -> e.getSortOrder() != null ? e.getSortOrder() : Integer.MAX_VALUE))
					.map(e -> new EventLaborShiftDetailsResponseDto(e.getLaborshift(),
							e.getLabordatetime() != null ? e.getLabordatetime().format(CREATE_DATE_FORMATTER) : "",
							e.getShiftTransPrice(), e.getPrice() != null ? e.getPrice() : 0.0,
							e.getQty() != null ? e.getQty() : 0.0, e.getTotalprice() != null ? e.getTotalprice() : 0.0,
							e.getPlace(), e.getNotesEnglish(), e.getNotesHindi(), e.getNotesGujarati()))
					.collect(Collectors.toList());

			return new EventLaborDetailsResponseDto(first.getId(),
					first.getContactCategory() != null ? first.getContactCategory().getId() : null,
					first.getContactCategory() != null ? first.getContactCategory().getNameEnglish() : "",
					first.getContact() != null ? first.getContact().getId() : null,
					first.getContact() != null ? first.getContact().getNameEnglish() : "",
					first.getContact() != null ? first.getContact().getMobileno() : "", first.getSortOrder(),
					labourShifts);
		}).sorted(Comparator.comparing(e -> e.getSortOrder() != null ? e.getSortOrder() : Integer.MAX_VALUE))
				.collect(Collectors.toList());
	}

	@Transactional
	public EventLaborResponseDto saveUpdateEventLabour(EventLaborRequestDto req) {
		try {

			EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(req.getEventId())
					.orElseThrow(() -> new RuntimeException(
							"Event Master with this eventId not found: " + req.getEventId()));

			EventFunctionMasterEntity functionEntity = eventFunctionMasterRepository
					.findByIdAndIsDeleteFalse(req.getEventFunctionId()).orElseThrow(() -> new RuntimeException(
							"Event Function Master not found with id: " + req.getEventFunctionId()));

			List<EventLaborEntity> eveLab = new ArrayList<>();
			List<EventLaborDetailsRequestDto> evDet = req.getEventLaborDetails();

			for (EventLaborDetailsRequestDto e : evDet) {
				if (e.getLabourShift().isEmpty()) {

					EventLaborEntity el = new EventLaborEntity();
					el.setEvent(eventMaster); // from your context
					el.setEventFunction(functionEntity);
					if (e.getLabortypeid() != null && e.getLabortypeid() != 0) {
						el.setContactCategory(contactCategoryMasterRepository.getById(e.getLabortypeid()));
					}
					eveLab.add(el);
				} else {
					for (EventLaborShiftDetailsRequestDto es : e.getLabourShift()) {
						EventLaborEntity el = new EventLaborEntity();

						el.setEvent(eventMaster); // from your context
						el.setEventFunction(functionEntity);
						if (e.getLabortypeid() != null && e.getLabortypeid() != 0) {
							el.setContactCategory(contactCategoryMasterRepository.getById(e.getLabortypeid()));
						}
						if (e.getContactid() != null && e.getContactid() != 0) {
							el.setContact(partyMasterRepository.getById(e.getContactid()));
						}
						el.setLaborshift(es.getLaborshift());

						// date and time
						if (es.getLabordatetime() != null && !es.getLabordatetime().isEmpty()) {
							el.setLabordatetime(LocalDateTime.parse(es.getLabordatetime(), CREATE_DATE_FORMATTER));
						}

						// pricing
						el.setPrice(es.getPrice() != null ? es.getPrice() : 0.0);
						el.setQty(es.getQty() != null ? es.getQty() : 0.0);
						el.setTotalprice(es.getTotalprice() != null ? es.getTotalprice() : 0.0);
						el.setNotesEnglish(es.getNotesEnglish());
						el.setNotesHindi(es.getNotesHindi());
						el.setNotesGujarati(es.getNotesGujarati());
						el.setShiftTransPrice(es.getShiftTranPrice());
						el.setSortOrder(e.getSortOrder());

						// place
						el.setPlace(es.getPlace());

						eveLab.add(el);
					}
				}

			}
			eventLaborRepository.deleteByEventIdAndEventFunctionId(req.getEventId(), req.getEventFunctionId());
			if (eveLab.size() != 0) {
				eventLaborRepository.saveAll(eveLab);
			}
			return getEventLabor(req.getEventId(), req.getEventFunctionId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to add/update event labor details ", e);
		}
	}

	@Transactional
	public EventLaborResponseDto saveUpdateEventLabourForApp(List<EventLaborDetailsRequestForAppDto> requests) {
		try {

			if (requests == null || requests.isEmpty()) {
				throw new IllegalArgumentException("Event labor details cannot be null or empty");
			}

			Long eventId = requests.get(0).getEventId();
			Long eventFunctionId = requests.get(0).getEventFunctionId();

			EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

			EventFunctionMasterEntity functionEntity = eventFunctionMasterRepository
					.findByIdAndIsDeleteFalse(eventFunctionId)
					.orElseThrow(() -> new RuntimeException("Function not found with id: " + eventFunctionId));

			eventLaborRepository.deleteByEventIdAndEventFunctionId(eventId, eventFunctionId);

			Set<Long> laborTypeIds = requests.stream().map(EventLaborDetailsRequestForAppDto::getLabortypeid)
					.filter(id -> id != null && id != 0).collect(Collectors.toSet());

			Set<Long> contactIds = requests.stream().map(EventLaborDetailsRequestForAppDto::getContactid)
					.filter(id -> id != null && id != 0).collect(Collectors.toSet());

			Map<Long, ContactCategoryMasterEntity> laborTypeMap = contactCategoryMasterRepository
					.findAllById(laborTypeIds).stream()
					.collect(Collectors.toMap(ContactCategoryMasterEntity::getId, Function.identity()));

			Map<Long, PartyMasterEntity> contactMap = partyMasterRepository.findAllById(contactIds).stream()
					.collect(Collectors.toMap(PartyMasterEntity::getId, Function.identity()));

			List<EventLaborEntity> entities = new ArrayList<>();

			for (EventLaborDetailsRequestForAppDto e : requests) {

				EventLaborEntity el = new EventLaborEntity();

				el.setEvent(eventMaster);
				el.setEventFunction(functionEntity);

				if (e.getLabortypeid() != null && e.getLabortypeid() != 0) {
					ContactCategoryMasterEntity category = laborTypeMap.get(e.getLabortypeid());
					if (category == null) {
						throw new RuntimeException("Invalid labortypeid: " + e.getLabortypeid());
					}
					el.setContactCategory(category);
				}

				if (e.getContactid() != null && e.getContactid() != 0) {
					PartyMasterEntity contact = contactMap.get(e.getContactid());
					if (contact == null) {
						throw new RuntimeException("Invalid contactid: " + e.getContactid());
					}
					el.setContact(contact);
				}

				el.setLaborshift(e.getLaborshift());

				if (e.getLabordatetime() != null && !e.getLabordatetime().isEmpty()) {
					el.setLabordatetime(LocalDateTime.parse(e.getLabordatetime(), CREATE_DATE_FORMATTER));
				}

				el.setPrice(Optional.ofNullable(e.getPrice()).orElse(0.0));
				el.setQty(Optional.ofNullable(e.getQty()).orElse(0.0));
				el.setTotalprice(Optional.ofNullable(e.getTotalprice()).orElse(0.0));

				el.setPlace(e.getPlace());
				el.setNotesEnglish(e.getNotesEnglish());
				el.setNotesHindi(e.getNotesHindi());
				el.setNotesGujarati(e.getNotesGujarati());
				el.setShiftTransPrice(e.getShiftTransPrice());
				el.setSortOrder(e.getSortOrder());
				entities.add(el);
			}

			eventLaborRepository.saveAll(entities);

			return getEventLabor(eventId, eventFunctionId);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Failed to add/update event labor details", ex);
		}
	}

	@Override
	public Boolean deleteEventLaborById(Long id) {
		if (eventLaborRepository.existsById(id)) {
			eventLaborRepository.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	public List<GetEventLaborResponseDto> fetchEventLaborData(Long eventId, Long eventFunctionId, HttpServletRequest re,
			int lang, Long userid, List<Long> agencyId) {

		if (agencyId == null) {
			agencyId = partyMasterRepository.findAllIds();
		}
		List<Object[]> responseDtos = eventLaborRepository.getLaborEventwise(eventId, eventFunctionId, lang, agencyId);
		List<GetEventLaborResponseDto> dtos = new ArrayList<>();
		for (Object[] row : responseDtos) {
			try {
				int index = 0;
				GetEventLaborResponseDto dto = new GetEventLaborResponseDto();

				dto.setPartyId(commonService.getLong(row[index++]));
				dto.setEventId(commonService.getLong(row[index++]));
				dto.setEventFunctionId(commonService.getLong(row[index++]));
				dto.setLaborName(commonService.getString(row[index++]));
				dto.setLaborMobile(commonService.getString(row[index++]));

				// Null-safe datetime parsing for labor datetime
				Object datetimeObj = row[index++]; // Get and increment
				if (datetimeObj != null) {
					try {
						String datetimeString = datetimeObj.toString();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
						LocalDateTime dateTime = LocalDateTime.parse(datetimeString, formatter);

						DateTimeFormatter dateOutput = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						DateTimeFormatter timeOutput = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

						dto.setLaborDate(dateTime.format(dateOutput));
						dto.setLaborTime(dateTime.format(timeOutput));
					} catch (DateTimeParseException e) {
						System.err.println("Error parsing labor datetime: " + datetimeObj + " - " + e.getMessage());
						dto.setLaborDate("");
						dto.setLaborTime("");
					}
				} else {
					dto.setLaborDate("");
					dto.setLaborTime("");
				}

				dto.setVenue(commonService.getString(row[index++]));
				dto.setCompanyName(commonService.getString(row[index++]));
				dto.setCountryCode(commonService.getString(row[index++]));
				dto.setCompanyMobile(commonService.getString(row[index++]));
				dto.setCompanyEmail(commonService.getString(row[index++]));
				dto.setLogo(commonService.getString(row[index++]));
				dto.setFunctionVenue(commonService.getString(row[index++]));

				// Null-safe datetime parsing for function datetime
				Object funDatetimeObj = row[index++]; // Get and increment

				dto.setQty(commonService.getInteger(row[index++]));
				dto.setContactCategoryId(commonService.getLong(row[index++]));
				dto.setContactCategoryName(commonService.getString(row[index++]));
				dto.setContactCategoryNameGujarati(commonService.getString(row[index++]));
				dto.setContactCategoryNameHindi(commonService.getString(row[index++]));
				dto.setLaborShift(commonService.getString(row[index++]));
				dto.setPrice(commonService.getBigDecimal(row[index++]));
				dto.setTotalPrice(commonService.getBigDecimal(row[index++]));

				if (funDatetimeObj != null) {
					try {
						String funDatetimeString = funDatetimeObj.toString();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
						LocalDateTime dateTime = LocalDateTime.parse(funDatetimeString, formatter);

						DateTimeFormatter dateOutput = DateTimeFormatter.ofPattern("dd/MM/yyyy");
						DateTimeFormatter timeOutput = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

						dto.setFunctionStartDate(dateTime.format(dateOutput));
						dto.setFunctionStartTime(dateTime.format(timeOutput));
					} catch (DateTimeParseException e) {
						System.err
								.println("Error parsing function datetime: " + funDatetimeObj + " - " + e.getMessage());
						dto.setFunctionStartDate("");
						dto.setFunctionStartTime("");
					}
				} else {
					dto.setFunctionStartDate("");
					dto.setFunctionStartTime("");
				}
				dto.setEventStartDateTime(((Timestamp) row[index++]).toLocalDateTime());

				dto.setFunctionName(commonService.getString(row[index++]));
				dto.setFunctionNameHindi(commonService.getString(row[index++]));
				dto.setFunctionNameGujarati(commonService.getString(row[index++]));

				dto.setEventNameEnglish(commonService.getString(row[index++]));
				dto.setEventNameHindi(commonService.getString(row[index++]));
				dto.setEventNameGujarati(commonService.getString(row[index++]));
				dto.setShiftTransPrice(commonService.getBigDecimal(row[index++]));
				dtos.add(dto);

			} catch (Exception e) {
				// Log the error with row details for debugging
				System.err.println("Error processing row ");
				e.printStackTrace();
				// Optionally: continue to next row or rethrow
				// throw new RuntimeException("Failed to process labor data", e);
			}
		}
		return dtos;
	}

	public List<GetEventLaborResponseDto> fetchEventLaborDataDetail(Long eventId, Long eventFunctionId,
			HttpServletRequest re, int lang, Long userid, Long partyId) {

		List<Object[]> responseDtos = eventLaborRepository.getLaborDetailEventwise(eventId, eventFunctionId, lang,
				partyId);
		List<GetEventLaborResponseDto> dtos = new ArrayList<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

		DateTimeFormatter dateOutput = DateTimeFormatter.ofPattern("dd/MM/yyy");
		DateTimeFormatter timeOutput = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

		for (Object[] row : responseDtos) {
			int index = 0;
			GetEventLaborResponseDto dto = new GetEventLaborResponseDto();
			dto.setPartyId(commonService.getLong(row[index++]));
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setLaborName(commonService.getString(row[index++]));
			dto.setLaborMobile(commonService.getString(row[index++]));
			Object value = row[index++];

			if (value != null) {

				String datetimeString = value.toString();

				if (!datetimeString.trim().isEmpty()) {

					LocalDateTime dateTime = LocalDateTime.parse(datetimeString, formatter);

					dto.setLaborDate(dateTime.format(dateOutput));
					dto.setLaborTime(dateTime.format(timeOutput));
				}
			}
			dto.setVenue(commonService.getString(row[index++]));

			dto.setContactCategoryId(commonService.getLong(row[index++]));
			dto.setContactCategoryName(commonService.getString(row[index++]));
			dto.setPrice(commonService.getBigDecimal(row[index++]));
			dto.setQty(commonService.getInteger(row[index++]));
			dto.setTotalPrice(commonService.getBigDecimal(row[index++]));
			dto.setUserId(commonService.getLong(row[index++]));

			dto.setCompanyName(commonService.getString(row[index++]));
			dto.setCountryCode(commonService.getString(row[index++]));
			dto.setCompanyMobile(commonService.getString(row[index++]));
			dto.setCompanyEmail(commonService.getString(row[index++]));

			dto.setFunctionVenue(commonService.getString(row[index++]));

			Object laborDateObj = row[index++];

			if (laborDateObj != null) {

				String datetimeString = laborDateObj.toString();

				if (!datetimeString.trim().isEmpty()) {

					LocalDateTime dateTime = LocalDateTime.parse(datetimeString, formatter);

					String dateString = dateTime.format(dateOutput);
					String timeString = dateTime.format(timeOutput);

					dto.setLaborDate(dateString);
					dto.setLaborTime(timeString);
				}
			}
			dto.setLaborShift(commonService.getString(row[index++]));
			dto.setShiftTransPrice(commonService.getBigDecimal(row[index++]));
			dtos.add(dto);
		}
		return dtos;

	}

	public List<GetEventLaborResponseDto> getEventData(Long eventId, Long eventFunctionId, String startDate,
			String endDate, int lang, List<Long> agencyId) {

		if (agencyId == null) {
			agencyId = partyMasterRepository.findAllIds();
		}

		List<Object[]> responseDtos = eventLaborRepository.getEventData(eventId, eventFunctionId, startDate, endDate,
				lang, agencyId);
		List<GetEventLaborResponseDto> dtos = new ArrayList<>();

		for (Object[] row : responseDtos) {
			GetEventLaborResponseDto dto = new GetEventLaborResponseDto();
			int index = 0;
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setPartyId(commonService.getLong(row[index++]));
			dto.setPartyName(commonService.getString(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setFunctionVenue(commonService.getString(row[index++]));
			dto.setEventNo(commonService.getString(row[index++]));
			dto.setShiftTransPrice(commonService.getBigDecimal(row[index++]));
			dto.setFunctionVenueHindi(commonService.getString(row[index++]));
			dto.setFunctionVenueGujarati(commonService.getString(row[index++]));
			dtos.add(dto);

		}

		return dtos;
	}

	public List<GetEventLaborResponseDto> getLaborData(Long eventId, Long functionId, String startDate, String endDate,
			int lang, Long partyId) {
		List<Object[]> responseDtos = eventLaborRepository.getLaborData(eventId, functionId, startDate, endDate, lang,
				partyId);
		List<GetEventLaborResponseDto> dtos = new ArrayList<>();

		for (Object[] row : responseDtos) {
			GetEventLaborResponseDto dto = new GetEventLaborResponseDto();
			int index = 0;
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setPartyId(commonService.getLong(row[index++]));
			dto.setLaborName(commonService.getString(row[index++]));
			dto.setLaborMobile(commonService.getString(row[index++]));
			dto.setShiftTransPrice(commonService.getBigDecimal(row[index++]));

			dtos.add(dto);

		}

		return dtos;
	}

	public List<GetEventLaborResponseDto> getLaborShiftData(Long eventId, Long functionId, String startDate,
			String endDate, Long pId, int lang, Long userId) {
		List<Object[]> responseDtos = eventLaborRepository.getLaborShiftData(eventId, functionId, startDate, endDate,
				pId, lang, userId);
		List<GetEventLaborResponseDto> dtos = new ArrayList<>();

		for (Object[] row : responseDtos) {
			GetEventLaborResponseDto dto = new GetEventLaborResponseDto();
			int index = 0;
			dto.setEventId(commonService.getLong(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setLaborId(commonService.getLong(row[index++]));
			dto.setPartyId(commonService.getLong(row[index++]));
			dto.setContactCategoryId(commonService.getLong(row[index++]));
			dto.setContactCategoryName(commonService.getString(row[index++]));
			dto.setLaborDate(commonService.getString(row[index++]));
			dto.setLaborShift(commonService.getString(row[index++]));
			dto.setQty(commonService.getInteger(row[index++]));
			dto.setNotes(commonService.getString(row[index++]));
			dto.setPrice(commonService.getBigDecimal(row[index++]));
			dto.setTotalPrice(commonService.getBigDecimal(row[index++]));
			dto.setShiftTransPrice(commonService.getBigDecimal(row[index++]));
			dtos.add(dto);

		}

		return dtos;
	}

	public GetEventLaborResponseDto getCmpAndEventData(Long eventId, int lang) {

		Object result = eventLaborRepository.getCmpAndEventData(eventId);

		if (result == null) {
			return null;
		}

		Object[] cmpData = (Object[]) result;

		int index = 0;
		GetEventLaborResponseDto dto = new GetEventLaborResponseDto();
		dto.setEventId(commonService.getLong(cmpData[index++]));
		dto.setEventNo(commonService.getString(cmpData[index++]));
		dto.setUserId(commonService.getLong(cmpData[index++]));
		dto.setCompanyName(commonService.getString(cmpData[index++]));
		dto.setCompanyEmail(commonService.getString(cmpData[index++]));
		dto.setCountryCode(commonService.getString(cmpData[index++]));
		dto.setCompanyMobile(commonService.getString(cmpData[index++]));
		dto.setCmpAddress(commonService.getString(cmpData[index++]));
		dto.setLogo(commonService.getString(cmpData[index++]));
		dto.setEventStartDateTime(((Timestamp) cmpData[index++]).toLocalDateTime());

		return dto;
	}

	public GetEventLaborResponseDto getCmpData(Long userid, int lang) {

		Object result = eventLaborRepository.getCmpData(userid);

		if (result == null) {
			return null;
		}

		Object[] cmpData = (Object[]) result;

		int index = 0;
		GetEventLaborResponseDto dto = new GetEventLaborResponseDto();
		dto.setUserId(commonService.getLong(cmpData[index++]));
		dto.setCompanyName(commonService.getString(cmpData[index++]));
		dto.setCompanyEmail(commonService.getString(cmpData[index++]));
		dto.setCountryCode(commonService.getString(cmpData[index++]));
		dto.setCompanyMobile(commonService.getString(cmpData[index++]));
		dto.setCmpAddress(commonService.getString(cmpData[index++]));
		dto.setLogo(commonService.getString(cmpData[index++]));

		return dto;
	}

	public List<GetEventLaborResponseDto> getEventLabourData(String startDate, String endDate, int lang, Long userid,
			List<Long> agencyId, Long partyId) {

		Integer flag = 0;
		if (agencyId != null && !agencyId.isEmpty() && agencyId.size() > 0) {
			flag = 1;
		}

		List<Object[]> responseDtos = eventLaborRepository.getLaborDatawise(startDate, endDate, lang, flag, agencyId,
				partyId, userid);
		List<GetEventLaborResponseDto> dtos = new ArrayList<>();

		for (Object[] row : responseDtos) {
			GetEventLaborResponseDto dto = new GetEventLaborResponseDto();
			int index = 0;
			dto.setPartyId(commonService.getLong(row[index++]));
			dto.setPartyName(commonService.getString(row[index++]));
			dto.setLaborDateTime(commonService.getString(row[index++]));
			String venue = commonService.getString(row[index++]); 
			dto.setContactCategoryId(commonService.getLong(row[index++]));
			dto.setContactCategoryName(commonService.getString(row[index++]));
			dto.setLaborDate(commonService.getString(row[index++]));
			dto.setLaborTime(commonService.getString(row[index++]));
			dto.setLaborShift(commonService.getString(row[index++]));
			dto.setQty(commonService.getInteger(row[index++]));
			dto.setPrice(commonService.getBigDecimal(row[index++]));
			dto.setTotalPrice(commonService.getBigDecimal(row[index++]));
			dto.setNotes(commonService.getString(row[index++]));
			dto.setMobileNo(commonService.getString(row[index++]));
			dto.setShiftTransPrice(commonService.getBigDecimal(row[index++]));
			Long eventFunctionId = commonService.getLong(row[index++]);
			Long eventId = commonService.getLong(row[index++]);
			
			
			List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository
					.findBanquetByEventFunctionId(eventId, eventFunctionId);

			venue = !banquets.isEmpty()
			        ? banquets.stream()
			                .map(BanquetHallShiftInfoDto::getBanquetHallName)
			                .collect(Collectors.joining(", "))
			        : (String) row[5];
			
			
			dtos.add(dto);

		}

		return dtos;
	}

}
