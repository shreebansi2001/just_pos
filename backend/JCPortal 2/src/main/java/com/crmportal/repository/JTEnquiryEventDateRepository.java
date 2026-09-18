package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.JTEnquiryEventDateEntity;
import com.crmportal.entity.JTEnquiryEntity;


@Repository
public interface JTEnquiryEventDateRepository extends JpaRepository<JTEnquiryEventDateEntity, Long> {

	void deleteAllByEnquiry(JTEnquiryEntity enquiry);
	
	List<JTEnquiryEventDateEntity> findByEnquiryIdIn(List<Long> enquiryIds);
	
	List<JTEnquiryEventDateEntity> findByEnquiry(JTEnquiryEntity enquiry);
}
