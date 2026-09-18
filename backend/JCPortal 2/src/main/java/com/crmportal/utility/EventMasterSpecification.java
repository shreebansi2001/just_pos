package com.crmportal.utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.UserMasterEntity;

public class EventMasterSpecification {

	public static Specification<EventMasterEntity> filter(UserMasterEntity user, String partyName,
			LocalDateTime eventDateExact, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer status) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// Not deleted
			predicates.add(cb.isFalse(root.get("isDelete")));

			// User filter - mandatory
			predicates.add(cb.equal(root.get("user"), user));

			// Party name filter
			if (partyName != null && !partyName.trim().isEmpty()) {
				predicates.add(
						cb.like(cb.lower(root.get("party").get("nameEnglish")), "%" + partyName.toLowerCase() + "%"));
			}

			// EventStatus filter
			if (status != null && status != -1) {
				predicates.add(cb.equal(root.get("status"), status));
			}

			// Case A: Exact event date always applies IF provided
			if (eventDateExact != null) {
				predicates.add(cb.equal(root.get("eventStartDateTime"), eventDateExact));
			}

			// Case B: Date range applies whether eventDateExact exists or not
			if (startDateTime != null && endDateTime != null) {

				Predicate eventStartInside = cb.between(root.get("eventStartDateTime"), startDateTime, endDateTime);

				predicates.add(cb.and(eventStartInside));

			} else if (eventDateExact == null) {
				// Case C: Default upcoming (only when no exact date AND no range)
				LocalDateTime todayStart = LocalDate.now().atStartOfDay();

				Predicate upcomingEvent = cb.greaterThanOrEqualTo(root.get("eventStartDateTime"), todayStart);

				predicates.add(upcomingEvent);
			}

			// Order by eventStartDateTime ASC
			query.orderBy(cb.asc(root.get("eventStartDateTime")));

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

}
