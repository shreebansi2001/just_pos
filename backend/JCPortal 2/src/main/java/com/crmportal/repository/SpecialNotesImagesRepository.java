package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.EventLabourChecklistEntity;
import com.crmportal.entity.SpecialNotesImagesEntity;

@Repository
public interface SpecialNotesImagesRepository extends JpaRepository<SpecialNotesImagesEntity, Long>{

	List<SpecialNotesImagesEntity> findAllBySpecialNotesIdAndIsDeleteFalse(Long id);

	Optional<SpecialNotesImagesEntity> findByIdAndIsDeleteFalse(Long moduleRecordId);

}
