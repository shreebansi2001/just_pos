package com.crmportal.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.poi.util.Internal;
import org.springframework.data.jpa.repository.JpaRepository;

import com.crmportal.entity.InquiryEntity;

@Internal
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {

	Optional<InquiryEntity> findByIdAndIsDeleteFalse(Long id);

	List<InquiryEntity> findByUserIdAndIsDeleteFalse(Long userId);

	List<InquiryEntity> findByIdAndUserIdAndIsDeleteFalse(Long id, Long userId);

	List<InquiryEntity> findByUserIdAndIsDeleteFalseAndInquiryDateBetween(Long userId, LocalDateTime formattedStartDate,
			LocalDateTime formattedEndDate);

}
