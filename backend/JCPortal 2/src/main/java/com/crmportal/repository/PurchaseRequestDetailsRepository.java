package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crmportal.entity.PurchaseRequestDetailsEntity;
import com.crmportal.entity.PurchaseRequestEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UserMasterEntity;

@Repository
public interface PurchaseRequestDetailsRepository extends JpaRepository<PurchaseRequestDetailsEntity, Long> {

	List<PurchaseRequestDetailsEntity> findByPurchaseRequest(PurchaseRequestEntity parentEntity);

	List<PurchaseRequestDetailsEntity> findByPurchaseRequestIn(List<PurchaseRequestEntity> requests);

	@Query(" "
			+ " SELECT "
			+ " 	d "
			+ " FROM PurchaseRequestDetailsEntity d "
            + " WHERE "
            + " d.purchaseRequest.id = :purchaseApproveId "
            + " AND d.purchaseRequest.isDelete = FALSE "
            + " AND LOWER(d.purchaseRequest.status) = LOWER(:status) "
            + " AND d.isDelete = FALSE "
            + " AND (:rawMaterialCatId IS NULL OR d.rawMaterial.rawMaterialCat.id = :rawMaterialCatId) "
            + " AND (:unitId IS NULL OR d.rawMaterial.unit.id = :unitId) "
            + " AND (:rawMaterialName IS NULL OR LOWER(d.rawMaterial.nameEnglish) LIKE LOWER(CONCAT('%', :rawMaterialName, '%')))")
    Page<PurchaseRequestDetailsEntity> findApprovedRawMaterials(
            @Param("purchaseApproveId") Long purchaseApproveId,
            @Param("status") String status,
            @Param("rawMaterialCatId") Long rawMaterialCatId,
            @Param("unitId") Long unitId,
            @Param("rawMaterialName") String rawMaterialName,
            Pageable pageable);

	List<PurchaseRequestDetailsEntity> findByRawMaterialInAndPurchaseRequestIdAndIsDeleteFalseAndPurchaseRequestIsDeleteFalse(
			List<RawMaterialMasterEntity> rawMaterials, Long purchaseRequestId);

	Optional<PurchaseRequestDetailsEntity> findByIdAndIsDeleteFalse(Long id);
	
}
