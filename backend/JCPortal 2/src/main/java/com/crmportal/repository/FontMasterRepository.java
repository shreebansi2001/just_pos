package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.FontMasterEntity;

@Repository
public interface FontMasterRepository extends JpaRepository<FontMasterEntity, Long>{

	Optional<FontMasterEntity> findByFontIdAndIsDeleteFalse(Long fontId);
	
	Optional<FontMasterEntity> findByFontNameAndIsDeleteFalse(String fontName);
	
	List<FontMasterEntity> findAllByIsActiveTrueAndIsDeleteFalse();
	
	List<FontMasterEntity> findAllByIsDeleteFalse();
}
