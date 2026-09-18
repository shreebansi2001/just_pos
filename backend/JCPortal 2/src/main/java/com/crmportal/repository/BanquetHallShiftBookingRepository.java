package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.crmportal.entity.BanquetHallShiftBookingEntity;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;

public interface BanquetHallShiftBookingRepository
        extends JpaRepository<BanquetHallShiftBookingEntity, Long> {

    // Check if a hall+shift+date is already booked (by any event)
    @Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
           "WHERE b.hall.id = :hallId " +
           "AND b.shift.id = :shiftId " +
           "AND b.bookingDate = :bookingDate " +
           "AND b.isDelete = false")
    Optional<BanquetHallShiftBookingEntity> findBooking(
            @Param("hallId")      Long hallId,
            @Param("shiftId")     Long shiftId,
            @Param("bookingDate") LocalDate bookingDate);

    // All bookings for a hall on a date
    @Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
           "WHERE b.hall.id = :hallId " +
           "AND b.bookingDate = :bookingDate " +
           "AND b.isDelete = false")
    List<BanquetHallShiftBookingEntity> findByHallAndDate(
            @Param("hallId")      Long hallId,
            @Param("bookingDate") LocalDate bookingDate);

    @Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.event.id = :eventId " +
    	       "AND b.isDelete = false")
    	List<BanquetHallShiftBookingEntity> findBookingsByEventId(
    	        @Param("eventId") Long eventId);

    	
//    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
//    		       "WHERE b.event.id = :eventId " +
//    		       "AND b.eventFunction IS NULL " +
//    		       "AND b.isDelete = false")
//    		List<BanquetHallShiftBookingEntity> findEventLevelBookingsForEvent(
//    		        @Param("eventId") Long eventId);
    	
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    		       "WHERE b.event.id = :eventId " +
    		       "AND b.eventFunction IS NULL " +
    		       "AND b.isDelete = false")
    		List<BanquetHallShiftBookingEntity> findEventLevelBookings(
    		        @Param("eventId") Long eventId);
    
    	
    // Check if a hall+shift+date is already booked (by any eventFunctionId)
    	// Check booking by hall + shift + date + function
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.hall.id = :hallId " +
    	       "AND b.shift.id = :shiftId " +
    	       "AND b.bookingDate = :bookingDate " +
    	       "AND b.eventFunction.id = :eventFunctionId " +
    	       "AND b.isDelete = false")
    	Optional<BanquetHallShiftBookingEntity> findBookingByFunction(
    	        @Param("hallId")          Long hallId,
    	        @Param("shiftId")         Long shiftId,
    	        @Param("bookingDate")     LocalDate bookingDate,
    	        @Param("eventFunctionId") Long eventFunctionId);

    	// All bookings for a hall + date + function
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.hall.id = :hallId " +
    	       "AND b.bookingDate = :bookingDate " +
    	       "AND b.eventFunction.id = :eventFunctionId " +
    	       "AND b.isDelete = false")
    	List<BanquetHallShiftBookingEntity> findByHallAndDateAndFunction(
    	        @Param("hallId")          Long hallId,
    	        @Param("bookingDate")     LocalDate bookingDate,
    	        @Param("eventFunctionId") Long eventFunctionId);

    	// All bookings for an event function
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.eventFunction.id = :eventFunctionId " +
    	       "AND b.isDelete = false")
    	List<BanquetHallShiftBookingEntity> findBookingsByEventFunctionId(
    	        @Param("eventFunctionId") Long eventFunctionId);

    	// First booking for an event function
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.eventFunction.id = :eventFunctionId " +
    	       "AND b.isDelete = false")
    	Optional<BanquetHallShiftBookingEntity> findFirstBookingByEventFunctionId(
    	        @Param("eventFunctionId") Long eventFunctionId);
    	
    	// Event-level booking (event_function_id IS NULL)
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.event.id = :eventId " +
    	       "AND b.eventFunction IS NULL " +
    	       "AND b.isDelete = false " +
    	       "ORDER BY b.createdAt DESC")
    	List<BanquetHallShiftBookingEntity> findEventLevelBookings(
    	        @Param("eventId") Long eventId,
    	        org.springframework.data.domain.Pageable pageable);

    	// Function-level booking (event_function_id = given id)
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.eventFunction.id = :eventFunctionId " +
    	       "AND b.isDelete = false " +
    	       "ORDER BY b.createdAt DESC")
    	List<BanquetHallShiftBookingEntity> findFunctionLevelBookings(
    	        @Param("eventFunctionId") Long eventFunctionId,
    	        org.springframework.data.domain.Pageable pageable);
    	
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    		       "WHERE b.event.id = :eventId " +
    		       "AND b.eventFunction IS NULL " +
    		       "AND b.isDelete = false")
    		List<BanquetHallShiftBookingEntity> findEventLevelBookingsForRelease(
    		        @Param("eventId") Long eventId);
    	
    	@Query("SELECT b.hall.hallName FROM BanquetHallShiftBookingEntity b WHERE b.event.id = :eventId AND b.eventFunction.id = :eventFunctionId AND b.isDelete = false")
		List<String> findVenueNameByEventAndFunction(Long eventId, Long eventFunctionId);
    	
    	// All bookings for a hall in a date range
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.hall.id = :hallId " +
    	       "AND b.bookingDate BETWEEN :fromDate AND :toDate " +
    	       "AND b.isDelete = false " +
    	       "AND (b.event IS NULL OR b.event.status IN (1,3))")
    	List<BanquetHallShiftBookingEntity> findByHallAndDateRange(
    	        @Param("hallId")   Long      hallId,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate")   LocalDate toDate);
    	
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    		       "WHERE b.bookingDate BETWEEN :fromDate AND :toDate " +
    		       "AND b.isDelete = false " +
    		       "AND (b.event IS NULL OR b.event.status IN (1,3))")
    		List<BanquetHallShiftBookingEntity> findByDateRange(
    		        @Param("fromDate") LocalDate fromDate,
    		        @Param("toDate") LocalDate toDate);
    	
    	// ADD to existing repository:
    	@Query("SELECT b FROM BanquetHallShiftBookingEntity b " +
    	       "WHERE b.eventFunction.id = :eventFunctionId " +
    	       "AND b.isDelete = false " +
    	       "ORDER BY b.id ASC")
    	List<BanquetHallShiftBookingEntity> findFunctionLevelBookingsAll(
    	        @Param("eventFunctionId") Long eventFunctionId);
    	
    	
    	@Query("SELECT new com.crmportal.response.dto.BanquetHallShiftInfoDto( "
    	        + " b.hall.id, "
    	        + " b.hall.hallName, "
    	        + " b.shift.id, "
    	        + " b.shift.shiftName, "
    	        + " b.shift.startTime, "
    	        + " b.shift.endTime, "
    	        + " b.bookingDate "
    	        + ") "
    	        + "FROM BanquetHallShiftBookingEntity b "
    	        + "WHERE b.event.id = :eventId "
    	        + "AND b.eventFunction.id = :eventFunctionId "
    	        + "AND b.isDelete = FALSE")
    	List<BanquetHallShiftInfoDto> findBanquetByEventFunctionId(
    	        @Param("eventId") Long eventId,
    	        @Param("eventFunctionId") Long eventFunctionId);
}