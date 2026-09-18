package com.crmportal.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.BanquetHallMasterEntity;
import com.crmportal.entity.BanquetHallShiftBookingEntity;
import com.crmportal.entity.BanquetShiftMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.BanquetHallMasterRepository;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.BanquetShiftMasterRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.BanquetShiftBookingRequestDto;
import com.crmportal.request.dto.BanquetShiftRequestDto;
import com.crmportal.response.dto.BanquetShiftAvailabilityResponseDto;
import com.crmportal.response.dto.BanquetShiftResponseDto;
import com.crmportal.response.dto.HallAvailabilityResponseDto;
import com.crmportal.response.dto.MonthwiseAvailabilityResponseDto;
import com.crmportal.service.BanquetShiftService;

@Service
@Transactional
public class BanquetShiftServiceImpl implements BanquetShiftService {

	@Autowired
	private BanquetShiftMasterRepository shiftRepository;

	@Autowired
	private BanquetHallShiftBookingRepository bookingRepository;

	@Autowired
	private BanquetHallMasterRepository hallRepository;

	@Autowired
	private EventMasterRepository eventMasterRepository;

	@Autowired
	private EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	private UserMasterRepository userRepository;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// ── Add or Update shift master ─────────────────────────────────────────
	@Override
	public BanquetShiftResponseDto addOrUpdate(BanquetShiftRequestDto request) {

		BanquetShiftMasterEntity entity;

		if (request.getId() == null || request.getId() <= 0) {
			entity = new BanquetShiftMasterEntity();
		} else {
			entity = shiftRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Shift not found with id: " + request.getId()));
		}

		entity.setShiftName(request.getShiftName());
		entity.setStartTime(request.getStartTime());
		entity.setEndTime(request.getEndTime());
		entity.setUserId(request.getUserId());
		entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
		entity.setIsDelete(false);

		return toDto(shiftRepository.save(entity));
	}

	// ── Get all shifts ─────────────────────────────────────────────────────
	@Override
	public List<BanquetShiftResponseDto> getAll(Long userId) {
		return shiftRepository.findByUserIdAndIsDeleteFalse(userId).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	// ── Get by ID ──────────────────────────────────────────────────────────
	@Override
	public BanquetShiftResponseDto getById(Long id) {
		return toDto(shiftRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Shift not found with id: " + id)));
	}

	// ── Soft delete ────────────────────────────────────────────────────────
	@Override
	public Boolean delete(Long id) {
		BanquetShiftMasterEntity entity = shiftRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));
		entity.setIsDelete(true);
		shiftRepository.save(entity);
		return true;
	}

	// ── Toggle active status ───────────────────────────────────────────────
	@Override
	public Boolean toggleStatus(Long id) {
		BanquetShiftMasterEntity entity = shiftRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));
		entity.setIsActive(!Boolean.TRUE.equals(entity.getIsActive()));
		shiftRepository.save(entity);
		return true;
	}

	// ── Availability check for a hall on a given date ──────────────────────
	@Override
	public List<BanquetShiftAvailabilityResponseDto> getAvailability(Long hallId, String bookingDate, Long userId,
			Long eventId) {

		LocalDate date = LocalDate.parse(bookingDate, formatter);

		List<BanquetShiftMasterEntity> allShifts = shiftRepository.findByUserIdAndIsDeleteFalseAndIsActiveTrue(userId);

		List<BanquetHallShiftBookingEntity> bookings = bookingRepository.findByHallAndDate(hallId, date).stream()
				.filter(b -> b.getEvent() != null && Integer.valueOf(1).equals(b.getEvent().getStatus()))
				.collect(Collectors.toList());

		Map<Long, BanquetHallShiftBookingEntity> bookedMap = bookings.stream().collect(
				Collectors.toMap(b -> b.getShift().getId(), Function.identity(), (existing, duplicate) -> existing));

		List<BanquetShiftAvailabilityResponseDto> result = new ArrayList<>();

		for (BanquetShiftMasterEntity shift : allShifts) {

			BanquetShiftAvailabilityResponseDto dto = new BanquetShiftAvailabilityResponseDto();

			dto.setShiftId(shift.getId());
			dto.setShiftName(shift.getShiftName());
			dto.setStartTime(shift.getStartTime());
			dto.setEndTime(shift.getEndTime());

			BanquetHallShiftBookingEntity booking = bookedMap.get(shift.getId());

			if (booking != null) {

				Long bookedEventId = booking.getEvent() != null ? booking.getEvent().getId() : null;

				boolean currentEventBooking = eventId != null && Objects.equals(bookedEventId, eventId);

				// Same event => allow editing
				dto.setIsAvailable(currentEventBooking);

				dto.setBookedByEventId(booking.getEvent() != null ? booking.getEvent().getEventNo() : null);

				dto.setBookedByEventName(booking.getEvent() != null && booking.getEvent().getParty() != null
						? booking.getEvent().getParty().getNameEnglish()
						: "-");

			} else {

				dto.setIsAvailable(true);
				dto.setBookedByEventId(null);
				dto.setBookedByEventName(null);
			}

			result.add(dto);
		}

		// Find Full Day shift
		BanquetShiftAvailabilityResponseDto fullDayShift = result.stream()
				.filter(s -> "full day".equalsIgnoreCase(s.getShiftName() == null ? "" : s.getShiftName().trim()))
				.findFirst().orElse(null);

		if (fullDayShift != null) {

			// Is Full Day booked by another event?
			boolean fullDayBookedByOtherEvent = bookings.stream()
					.anyMatch(b -> "full day".equalsIgnoreCase(b.getShift().getShiftName())
							&& (eventId == null || !Objects.equals(b.getEvent().getId(), eventId)));

			// Is any normal shift booked by another event?
			boolean normalShiftBookedByOtherEvent = bookings.stream()
					.anyMatch(b -> !"full day".equalsIgnoreCase(b.getShift().getShiftName())
							&& (eventId == null || !Objects.equals(b.getEvent().getId(), eventId)));

			// Full Day booked by another event
			if (fullDayBookedByOtherEvent) {

				result.forEach(s -> {
					if (!"full day".equalsIgnoreCase(s.getShiftName())) {
						s.setIsAvailable(false);
					}
				});
			}

			// Any shift booked by another event
			if (normalShiftBookedByOtherEvent) {
				fullDayShift.setIsAvailable(false);
			}
		}

		return result;
	}

	// ── Book a shift for an event ──────────────────────────────────────────
	@Override
	public void bookShift(BanquetShiftBookingRequestDto request) {

		LocalDate date = LocalDate.parse(request.getBookingDate(), formatter);

		// Check availability
		bookingRepository.findBooking(request.getHallId(), request.getShiftId(), date).ifPresent(existing -> {
			throw new RuntimeException("This shift is not available for this banquet on " + request.getBookingDate()
					+ ". Already booked for event: " + existing.getEvent().getEventNo());
		});

		BanquetHallMasterEntity hall = hallRepository.findByIdAndIsDeleteFalse(request.getHallId())
				.orElseThrow(() -> new RuntimeException("Hall not found: " + request.getHallId()));

		BanquetShiftMasterEntity shift = shiftRepository.findByIdAndIsDeleteFalse(request.getShiftId())
				.orElseThrow(() -> new RuntimeException("Shift not found: " + request.getShiftId()));

		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found: " + request.getEventId()));

		UserMasterEntity user = userRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		BanquetHallShiftBookingEntity booking = new BanquetHallShiftBookingEntity();
		booking.setHall(hall);
		booking.setShift(shift);
		booking.setEvent(event);
		booking.setBookingDate(date);
		booking.setUser(user);
		booking.setIsDelete(false);

		bookingRepository.save(booking);
	}

	// ── Release all bookings when event is deleted ─────────────────────────
//    @Override
//    public void releaseBookingsByEvent(Long eventId) {
//        List<BanquetHallShiftBookingEntity> bookings =
//                bookingRepository.findBookingsByEventId(eventId);
//        bookings.forEach(b -> b.setIsDelete(true));
//        bookingRepository.saveAll(bookings);
//    }

	@Override
	public void releaseBookingsByEvent(Long eventId) {
		// Only release event-level bookings, not function-level ones
		List<BanquetHallShiftBookingEntity> bookings = bookingRepository.findEventLevelBookingsForRelease(eventId);
		bookings.forEach(b -> b.setIsDelete(true));
		bookingRepository.saveAll(bookings);
	}

	// ── Helper ────────────────────────────────────────────────────────────
	private BanquetShiftResponseDto toDto(BanquetShiftMasterEntity e) {
		BanquetShiftResponseDto dto = new BanquetShiftResponseDto();
		dto.setId(e.getId());
		dto.setShiftName(e.getShiftName());
		dto.setStartTime(e.getStartTime());
		dto.setEndTime(e.getEndTime());
		dto.setUserId(e.getUserId());
		dto.setIsActive(e.getIsActive());
		dto.setCreatedAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null);
		return dto;
	}

	// ── Availability for a specific event function ─────────────────────────
	@Override
	public List<BanquetShiftAvailabilityResponseDto> getAvailabilityByFunction(Long hallId, String bookingDate,
			Long eventFunctionId, Long userId) {

		if (hallId == null || hallId == 0) {
			return new ArrayList<>();
		}

		LocalDate date = LocalDate.parse(bookingDate, formatter);

		List<BanquetShiftMasterEntity> allShifts = shiftRepository.findByUserIdAndIsDeleteFalseAndIsActiveTrue(userId);

		List<BanquetHallShiftBookingEntity> bookings = bookingRepository.findByHallAndDateAndFunction(hallId, date,
				eventFunctionId);

		Map<Long, BanquetHallShiftBookingEntity> bookedMap = bookings.stream()
				.collect(Collectors.toMap(b -> b.getShift().getId(), b -> b));

		List<BanquetShiftAvailabilityResponseDto> result = new ArrayList<>();

		for (BanquetShiftMasterEntity shift : allShifts) {
			BanquetShiftAvailabilityResponseDto dto = new BanquetShiftAvailabilityResponseDto();
			dto.setShiftId(shift.getId());
			dto.setShiftName(shift.getShiftName());
			dto.setStartTime(shift.getStartTime());
			dto.setEndTime(shift.getEndTime());
			dto.setEventFunctionId(eventFunctionId);

			if (bookedMap.containsKey(shift.getId())) {
				BanquetHallShiftBookingEntity booking = bookedMap.get(shift.getId());
				dto.setIsAvailable(false);
				dto.setBookedByEventId(booking.getEvent().getEventNo());
				dto.setBookedByEventName(
						booking.getEvent().getParty() != null ? booking.getEvent().getParty().getNameEnglish() : "-");
			} else {
				dto.setIsAvailable(true);
				dto.setBookedByEventId(null);
				dto.setBookedByEventName(null);
			}
			result.add(dto);
		}
		return result;
	}

	// ── Book shift for a specific event function ───────────────────────────
	@Override
	public void bookShiftForFunction(BanquetShiftBookingRequestDto request) {

		LocalDate date = LocalDate.parse(request.getBookingDate(), formatter);

		// Check if already booked for this hall+shift+date+function
		bookingRepository
				.findBookingByFunction(request.getHallId(), request.getShiftId(), date, request.getEventFunctionId())
				.ifPresent(existing -> {
					throw new RuntimeException(
							"This shift is not available for this banquet on " + request.getBookingDate()
									+ ". Already booked for event: " + existing.getEvent().getEventNo());
				});

		BanquetHallMasterEntity hall = hallRepository.findByIdAndIsDeleteFalse(request.getHallId())
				.orElseThrow(() -> new RuntimeException("Hall not found: " + request.getHallId()));

		BanquetShiftMasterEntity shift = shiftRepository.findByIdAndIsDeleteFalse(request.getShiftId())
				.orElseThrow(() -> new RuntimeException("Shift not found: " + request.getShiftId()));

		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found: " + request.getEventId()));

		// Fetch event function
		com.crmportal.entity.EventFunctionMasterEntity eventFunction = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(request.getEventFunctionId())
				.orElseThrow(() -> new RuntimeException("Event Function not found: " + request.getEventFunctionId()));

		UserMasterEntity user = userRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		BanquetHallShiftBookingEntity booking = new BanquetHallShiftBookingEntity();
		booking.setHall(hall);
		booking.setShift(shift);
		booking.setEvent(event);
		booking.setEventFunction(eventFunction);
		booking.setBookingDate(date);
		booking.setUser(user);
		booking.setIsDelete(false);

		bookingRepository.save(booking);
	}

	// ── Release bookings for an event function ─────────────────────────────
	@Override
	public void releaseBookingsByEventFunction(Long eventFunctionId) {
		List<BanquetHallShiftBookingEntity> bookings = bookingRepository.findBookingsByEventFunctionId(eventFunctionId);
		bookings.forEach(b -> b.setIsDelete(true));
		bookingRepository.saveAll(bookings);
	}

	@Override
	public List<HallAvailabilityResponseDto> getDatewiseAvailability(String startDate, String endDate, Long userId) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate fromDate = LocalDate.parse(startDate, formatter);
		LocalDate toDate = LocalDate.parse(endDate, formatter);

		List<BanquetHallMasterEntity> halls = hallRepository.findByUserIdAndIsDeleteFalseAndIsActiveTrue(userId);

		List<BanquetShiftMasterEntity> shifts = shiftRepository.findByUserIdAndIsDeleteFalseAndIsActiveTrue(userId);

		List<BanquetHallShiftBookingEntity> bookings = bookingRepository.findByDateRange(fromDate, toDate);

		Map<String, BanquetHallShiftBookingEntity> bookingMap = bookings.stream()
				.collect(Collectors.toMap(
						b -> b.getBookingDate() + "_" + b.getHall().getId() + "_" + b.getShift().getId(),
						Function.identity(), (a, b) -> a));

		List<HallAvailabilityResponseDto> result = new ArrayList<>();

		LocalDate current = fromDate;

		while (!current.isAfter(toDate)) {

			HallAvailabilityResponseDto dateDto = new HallAvailabilityResponseDto();

			dateDto.setDate(current.format(formatter));
			dateDto.setDayOfWeek(current.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

			List<HallAvailabilityResponseDto.HallDto> hallDtos = new ArrayList<>();

			for (BanquetHallMasterEntity hall : halls) {

				HallAvailabilityResponseDto.HallDto hallDto = new HallAvailabilityResponseDto.HallDto();

				hallDto.setHallId(hall.getId());
				hallDto.setHallName(hall.getHallName());

				List<HallAvailabilityResponseDto.ShiftDto> shiftDtos = new ArrayList<>();

				// -----------------------------
				// Hall level booking checks
				// -----------------------------

				final LocalDate bookingDate = current;
				final Long hallId = hall.getId();

				boolean fullDayBooked = bookings.stream()
						.anyMatch(b -> b.getBookingDate().equals(bookingDate) && b.getHall().getId().equals(hallId)
								&& "full day".equalsIgnoreCase(b.getShift().getShiftName()));

				boolean anyNormalShiftBooked = bookings.stream()
						.anyMatch(b -> b.getBookingDate().equals(bookingDate) && b.getHall().getId().equals(hallId)
								&& !"full day".equalsIgnoreCase(b.getShift().getShiftName()));

				for (BanquetShiftMasterEntity shift : shifts) {

					HallAvailabilityResponseDto.ShiftDto shiftDto = new HallAvailabilityResponseDto.ShiftDto();

					shiftDto.setShiftId(shift.getId());
					shiftDto.setShiftName(shift.getShiftName());
					shiftDto.setStartTime(shift.getStartTime());
					shiftDto.setEndTime(shift.getEndTime());

					String key = current + "_" + hall.getId() + "_" + shift.getId();

					BanquetHallShiftBookingEntity booking = bookingMap.get(key);

					boolean isFullDay = "full day"
							.equalsIgnoreCase(shift.getShiftName() == null ? "" : shift.getShiftName().trim());

					if (booking != null) {

						// Direct booking
						shiftDto.setIsAvailable(false);

						if (booking.getEvent() != null) {

							shiftDto.setBookedByEventId(booking.getEvent().getId());

							shiftDto.setBookedByEventNo(booking.getEvent().getEventNo());

							shiftDto.setBookedByPartyName(booking.getEvent().getParty() != null
									? booking.getEvent().getParty().getNameEnglish()
									: null);

							shiftDto.setStatus(booking.getEvent().getStatus());
						}

					}
					// Full Day blocked because another shift booked
					else if (isFullDay && anyNormalShiftBooked) {

						shiftDto.setIsAvailable(false);

						BanquetHallShiftBookingEntity normalShiftBooking = bookings.stream()
								.filter(b -> b.getBookingDate().equals(bookingDate)
										&& b.getHall().getId().equals(hall.getId())
										&& !"full day".equalsIgnoreCase(b.getShift().getShiftName()))
								.findFirst().orElse(null);

						if (normalShiftBooking != null && normalShiftBooking.getEvent() != null) {

							shiftDto.setBookedByEventId(normalShiftBooking.getEvent().getId());

							shiftDto.setBookedByEventNo(normalShiftBooking.getEvent().getEventNo());

							shiftDto.setBookedByPartyName(normalShiftBooking.getEvent().getParty() != null
									? normalShiftBooking.getEvent().getParty().getNameEnglish()
									: null);

							shiftDto.setStatus(normalShiftBooking.getEvent().getStatus());
						}
					}
					// Normal shift blocked because Full Day booked
					else if (!isFullDay && fullDayBooked) {

						shiftDto.setIsAvailable(false);
						BanquetHallShiftBookingEntity fullDayBooking = bookings.stream()
								.filter(b -> b.getBookingDate().equals(bookingDate)
										&& b.getHall().getId().equals(hall.getId())
										&& "full day".equalsIgnoreCase(b.getShift().getShiftName()))
								.findFirst().orElse(null);

						if (fullDayBooking != null && fullDayBooking.getEvent() != null) {

							shiftDto.setBookedByEventId(fullDayBooking.getEvent().getId());

							shiftDto.setBookedByEventNo(fullDayBooking.getEvent().getEventNo());

							shiftDto.setBookedByPartyName(fullDayBooking.getEvent().getParty() != null
									? fullDayBooking.getEvent().getParty().getNameEnglish()
									: null);
							shiftDto.setStatus(fullDayBooking.getEvent().getStatus());
						}

					} else {

						shiftDto.setIsAvailable(true);
					}

					shiftDtos.add(shiftDto);
				}

				hallDto.setShifts(shiftDtos);
				hallDtos.add(hallDto);
			}

			dateDto.setHalls(hallDtos);
			result.add(dateDto);

			current = current.plusDays(1);
		}

		return result;
	}

}