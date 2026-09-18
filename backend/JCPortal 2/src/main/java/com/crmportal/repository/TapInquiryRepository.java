package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.TapInquiryEntity;

@Repository
public interface TapInquiryRepository extends JpaRepository<TapInquiryEntity, Long> {

	List<TapInquiryEntity> findAllByIsDeleteFalse();

}
