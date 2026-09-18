package com.crmportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.NamePlateImagesEntity;

@Repository
public interface NamePlateImagesRepository extends JpaRepository<NamePlateImagesEntity, Long> {

	List<NamePlateImagesEntity> findByTemplateMasterId(Long templateMasterId);

}
