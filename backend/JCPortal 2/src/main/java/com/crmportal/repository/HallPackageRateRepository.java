package com.crmportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crmportal.entity.BanquetHallMasterEntity;
import com.crmportal.entity.HallPackageRateEntity;
import com.crmportal.entity.UserMasterEntity;

public interface HallPackageRateRepository extends JpaRepository<HallPackageRateEntity, Long>{

	Optional<HallPackageRateEntity> findByHall_IdAndCustomPackage_IdAndTierLabelAndIsDeleteFalse(Long hallId,
			Long packageId, String tierLabel);

	Optional<BanquetHallMasterEntity> findByHall_IdAndCustomPackage_IdAndTierLabelAndIsDeleteFalseAndUser(Long hallId,
			Long packageId, String trim, UserMasterEntity user);
	 @Query("SELECT r FROM HallPackageRateEntity r " +
	           "WHERE r.isDelete = false " +
	           "AND r.hall.userId = :userId " +
	           "AND (:isActive IS NULL OR r.isActive = :isActive) " +
	           "AND (:hallId IS NULL OR r.hall.id = :hallId) " +
	           "AND (:packageId IS NULL OR r.customPackage.id = :packageId) " +
	           "ORDER BY r.hall.id ASC, r.packageSequence ASC, r.tierSequence ASC")
	    List<HallPackageRateEntity> findAllByFilters(
	            @Param("userId") Long userId,
	            @Param("isActive") Boolean isActive,
	            @Param("hallId") Long hallId,
	            @Param("packageId") Long packageId);

//	 @Query("SELECT r FROM HallPackageRateEntity r " +
//		       "WHERE r.isDelete = false " +
//		       "AND r.isActive = true " +
//		       "AND r.hall.id = :hallId " +
//		       "AND r.customPackage.id = :packageId " +
//		       "AND r.minGuests <= :functionPax " +
//		       "ORDER BY r.minGuests DESC")
//		List<HallPackageRateEntity> findApplicableRates(
//		        @Param("hallId") Long hallId,
//		        @Param("packageId") Long packageId,
//		        @Param("functionPax") Integer functionPax);
	 
	 @Query("SELECT r FROM HallPackageRateEntity r " +
		       "WHERE r.isDelete = false " +
		       "AND r.isActive = true " +
		       "AND r.hall.id = :hallId " +
		       "AND r.customPackage.id = :packageId " +
		       "ORDER BY " +
		       "CASE WHEN r.minGuests <= :functionPax THEN 0 ELSE 1 END, " +
		       "CASE WHEN r.minGuests <= :functionPax THEN r.minGuests ELSE 0 END DESC, " +
		       "r.minGuests ASC")
		List<HallPackageRateEntity> findApplicableRates(
		        @Param("hallId") Long hallId,
		        @Param("packageId") Long packageId,
		        @Param("functionPax") Integer functionPax);

	Optional<HallPackageRateEntity> findByIdAndIsDeleteFalse(Long id);
}
