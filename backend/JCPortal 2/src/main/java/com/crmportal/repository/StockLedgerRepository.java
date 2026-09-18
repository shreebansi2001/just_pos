package com.crmportal.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.crmportal.entity.StockLedgerEntity;

@Repository
public interface StockLedgerRepository extends JpaRepository<StockLedgerEntity, Long> {

	// Delete old entries when updating a transaction
	@Modifying
	@Query("UPDATE StockLedgerEntity s SET s.isDelete = true " + "WHERE s.refId = :refId AND s.refType = :refType")
	void softDeleteByRefIdAndRefType(@Param("refId") Long refId, @Param("refType") String refType);

	// Get ledger by rawMaterial and date range
	@Query("SELECT s FROM StockLedgerEntity s " + "WHERE s.rawMaterial.id = :rawMaterialId "
			+ "AND s.transactionDate >= :fromDate " + "AND s.transactionDate <= :toDate " + "AND s.isDelete = false "
			+ "ORDER BY s.transactionDate ASC, s.createdAt ASC")
	List<StockLedgerEntity> findByRawMaterialAndDateRange(@Param("rawMaterialId") Long rawMaterialId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	// Sum qty_in by rawMaterial and date range
	@Query(value = "SELECT COALESCE(SUM(qty_in), 0) FROM stock_ledger " + "WHERE raw_material_id = :rawMaterialId "
			+ "AND transaction_date >= :fromDate " + "AND transaction_date <= :toDate " + "AND ref_type = :refType "
			+ "AND is_delete = 0", nativeQuery = true)
	double sumQtyInByRawMaterialAndDateRangeAndRefType(@Param("rawMaterialId") Long rawMaterialId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("refType") String refType);
	
	// Sum qty_in by rawMaterial and date range and stock type
		@Query(value = "SELECT COALESCE(SUM(qty_in), 0) FROM stock_ledger " + "WHERE raw_material_id = :rawMaterialId "
				+ "AND transaction_date >= :fromDate " + "AND transaction_date <= :toDate " + "AND ref_type = :refType AND stock_type_id = :stockTypeId "
				+ "AND is_delete = 0", nativeQuery = true)
		double sumQtyInByRawMaterialAndDateRangeAndRefTypeAndStockType(@Param("rawMaterialId") Long rawMaterialId,
				@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("stockTypeId") Long stockTypeId, @Param("refType") String refType);
		
		// Sum qty_in by rawMaterial and date range and stock type
		@Query(value = "SELECT COALESCE(SUM(qty_in), 0) FROM stock_ledger " + "WHERE raw_material_id = :rawMaterialId "
				+ "AND transaction_date >= :fromDate " + "AND transaction_date <= :toDate " + "AND ref_type = :refType AND kitchen_type_id = :kitchenTypeId "
				+ "AND is_delete = 0", nativeQuery = true)
		double sumQtyInByRawMaterialAndDateRangeAndRefTypeAndKitchenType(@Param("rawMaterialId") Long rawMaterialId,
				@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("kitchenTypeId") Long kitchenTypeId, @Param("refType") String refType);

	// Sum qty_out by rawMaterial and date range
	@Query(value = "SELECT COALESCE(SUM(qty_out), 0) FROM stock_ledger " + "WHERE raw_material_id = :rawMaterialId "
			+ "AND transaction_date >= :fromDate " + "AND transaction_date <= :toDate " + "AND ref_type = :refType "
			+ "AND is_delete = 0", nativeQuery = true)
	double sumQtyOutByRawMaterialAndDateRangeAndRefType(@Param("rawMaterialId") Long rawMaterialId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("refType") String refType);
	
	// Sum qty_out by rawMaterial and date range and stock type
		@Query(value = "SELECT COALESCE(SUM(qty_out), 0) FROM stock_ledger " + "WHERE raw_material_id = :rawMaterialId "
				+ "AND transaction_date >= :fromDate " + "AND transaction_date <= :toDate " + "AND ref_type = :refType AND stock_type_id = :stockTypeId "
				+ "AND is_delete = 0", nativeQuery = true)
		double sumQtyOutByRawMaterialAndDateRangeAndRefTypeAndStockType(@Param("rawMaterialId") Long rawMaterialId,
				@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("stockTypeId") Long stockTypeId, @Param("refType") String refType);

	// For date wise stock report — sum by stockType
	@Query(value = "SELECT COALESCE(SUM(qty_out), 0) FROM stock_ledger " + "WHERE raw_material_id = :rawMaterialId "
			+ "AND transaction_date >= :fromDate " + "AND transaction_date <= :toDate "
			+ "AND ref_type IN ('STORE_ISSUE', 'CHEF_REQUISITION') " + "AND stock_type_id = :stockTypeId "
			+ "AND is_delete = 0", nativeQuery = true)
	double sumStoreIssueQtyByStockType(@Param("rawMaterialId") Long rawMaterialId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
			@Param("stockTypeId") Long stockTypeId);
	
	// For date wise stock report — sum by kitchenType
	@Query(value = "SELECT COALESCE(SUM(qty_out), 0) FROM stock_ledger " + "WHERE raw_material_id = :rawMaterialId "
			+ "AND transaction_date >= :fromDate " + "AND transaction_date <= :toDate "
			+ "AND ref_type IN ('STORE_ISSUE', 'CHEF_REQUISITION') " + "AND kitchen_type_id = :kitchenTypeId "
			+ "AND is_delete = 0", nativeQuery = true)
	double sumStoreIssueQtyByKitchenType(@Param("rawMaterialId") Long rawMaterialId,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
			@Param("kitchenTypeId") Long kitchenTypeId);

	// Get distinct raw materials by stockType for date wise report
	@Query(value = "SELECT DISTINCT raw_material_id FROM stock_ledger " + "WHERE stock_type_id = :stockTypeId "
			+ "AND is_delete = 0", nativeQuery = true)
	List<Long> findRawMaterialIdsByStockType(@Param("stockTypeId") Long stockTypeId);
	
	@Query(value = "SELECT DISTINCT raw_material_id FROM stock_ledger " +
            "WHERE transaction_date >= :fromDate " +
            "AND transaction_date <= :toDate " +
            "AND stock_type_id = :stockTypeId " +
            "AND is_delete = 0",
    nativeQuery = true)
	List<Long> findActiveRawMaterialIdsByDateRangeAndStockType(
	     @Param("fromDate") LocalDate fromDate,
	     @Param("toDate") LocalDate toDate,
	     @Param("stockTypeId") Long stockTypeId);
	
	
	@Query(value = "SELECT DISTINCT raw_material_id FROM stock_ledger " +
            "WHERE transaction_date >= :fromDate " +
            "AND transaction_date <= :toDate " +
            "AND is_delete = 0",
    nativeQuery = true)
	List<Long> findActiveRawMaterialIdsByDateRange(
	     @Param("fromDate") LocalDate fromDate,
	     @Param("toDate") LocalDate toDate);
	
	@Query(value = "SELECT DISTINCT raw_material_id FROM stock_ledger " +
            "WHERE transaction_date >= :fromDate " +
            "AND transaction_date <= :toDate " +
            "AND kitchen_type_id = :kitchenTypeId " +
            "AND is_delete = 0",
    nativeQuery = true)
	List<Long> findActiveRawMaterialIdsByDateRangeAndKitchenType(
	     @Param("fromDate") LocalDate fromDate,
	     @Param("toDate") LocalDate toDate,
	     @Param("kitchenTypeId") Long kitchenTypeId);
	
	@Query(value = "SELECT DISTINCT raw_material_id FROM stock_ledger " +
            "WHERE transaction_date >= :fromDate " +
            "AND transaction_date <= :toDate and user_id = :userId " +
            "AND (:kitchenTypeId IS NULL OR :kitchenTypeId = 0 OR kitchen_type_id = :kitchenTypeId) AND (:stockTypeId IS NULL OR :stockTypeId = 0 OR stock_type_id = :stockTypeId) " +
            "AND is_delete = 0",
    nativeQuery = true)
	List<Long> findActiveRawMaterialIdsByDateRangeAndStockTypeAndKitchenType(
	     @Param("fromDate") LocalDate fromDate,
	     @Param("toDate") LocalDate toDate,@Param("stockTypeId") Long stockTypeId,
	     @Param("kitchenTypeId") Long kitchenTypeId,@Param("userId") Long userId);
	
	@Query("SELECT COALESCE(SUM(s.qtyIn),0) "
			+ " FROM StockLedgerEntity s "
			+ " WHERE s.rawMaterial.id = :rawMaterialId "
			+ " AND s.transactionDate < :fromDate "
			+ " AND s.refType = :refType "
			+ " AND s.isDelete = false")
			double sumQtyInByRawMaterialAndDateLessThanAndRefType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("fromDate") LocalDate fromDate,
			        @Param("refType") String refType);
	
	@Query("SELECT COALESCE(SUM(s.qtyOut),0) FROM StockLedgerEntity s WHERE s.rawMaterial.id = :rawMaterialId "
			+ " AND s.transactionDate < :fromDate AND s.refType = :refType AND s.isDelete = false")
			double sumQtyOutByRawMaterialAndDateLessThanAndRefType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("fromDate") LocalDate fromDate,
			        @Param("refType") String refType);
	
	@Query("SELECT COALESCE(SUM(s.qtyOut),0) FROM StockLedgerEntity s WHERE s.rawMaterial.id = :rawMaterialId AND s.transactionDate < :fromDate "
			+ " AND s.refType IN ('STORE_ISSUE','CHEF_REQUISITION','SOT_STORE_ISSUE') AND s.isDelete = false")
			double sumSellQtyBeforeDate(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("fromDate") LocalDate fromDate);
	
	@Query("SELECT COALESCE(SUM(s.qtyIn),0) FROM StockLedgerEntity s WHERE s.rawMaterial.id = :rawMaterialId "
			+ " AND s.transactionDate < :fromDate AND s.refType IN ('STORE_ISSUE_RETURN','SOT_STORE_RETURN') AND s.isDelete = false")
			double sumSellReturnBeforeDate(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("fromDate") LocalDate fromDate);
	
	@Query(value = "SELECT DISTINCT raw_material_id FROM stock_ledger WHERE transaction_date < :fromDate "
			+ " AND user_id = :userId AND is_delete = 0 AND (:stockTypeId IS NULL OR stock_type_id = :stockTypeId) AND (:kitchenTypeId IS NULL OR kitchen_type_id = :kitchenTypeId)", nativeQuery = true)
			List<Long> findActiveRawMaterialIdsBeforeDate(
			        @Param("fromDate") LocalDate fromDate,
			        @Param("stockTypeId") Long stockTypeId,
			        @Param("kitchenTypeId") Long kitchenTypeId,
			        @Param("userId") Long userId);
	
	@Query(value = "SELECT COALESCE(SUM(qty_in),0) FROM stock_ledger WHERE raw_material_id = :rawMaterialId AND transaction_date < :date "
			+ " AND ref_type = :refType AND stock_type_id = :stockTypeId AND is_delete = 0", nativeQuery = true)
			double sumQtyInByRawMaterialAndDateLessThanAndRefTypeAndStockType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("date") LocalDate date,
			        @Param("stockTypeId") Long stockTypeId,
			        @Param("refType") String refType);
	
	@Query(value = "SELECT COALESCE(SUM(qty_in),0) FROM stock_ledger WHERE raw_material_id = :rawMaterialId AND transaction_date < :date "
			+ " AND ref_type = :refType AND kitchen_type_id = :kitchenTypeId AND is_delete = 0", nativeQuery = true)
			double sumQtyInByRawMaterialAndDateLessThanAndRefTypeAndKitchenType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("date") LocalDate date,
			        @Param("kitchenTypeId") Long kitchenTypeId,
			        @Param("refType") String refType);
	
	@Query(value = "SELECT COALESCE(SUM(qty_out),0) FROM stock_ledger WHERE raw_material_id = :rawMaterialId AND transaction_date < :date "
			+ " AND ref_type = :refType AND stock_type_id = :stockTypeId AND is_delete = 0", nativeQuery = true)
			double sumQtyOutByRawMaterialAndDateLessThanAndRefTypeAndStockType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("date") LocalDate date,
			        @Param("stockTypeId") Long stockTypeId,
			        @Param("refType") String refType);
	
	@Query(value = "SELECT COALESCE(SUM(qty_out),0) FROM stock_ledger WHERE raw_material_id = :rawMaterialId AND transaction_date < :date "
			+ " AND ref_type = :refType AND kitchen_type_id = :kitchenTypeId AND is_delete = 0", nativeQuery = true)
			double sumQtyOutByRawMaterialAndDateLessThanAndRefTypeAndKitchenType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("date") LocalDate date,
			        @Param("kitchenTypeId") Long kitchenTypeId,
			        @Param("refType") String refType);
	
	@Query(value = "SELECT COALESCE(SUM(qty_out),0) FROM stock_ledger WHERE raw_material_id = :rawMaterialId AND transaction_date < :date"
			+ " AND ref_type='STORE_ISSUE' AND stock_type_id = :stockTypeId AND is_delete = 0", nativeQuery = true)
			double sumStoreIssueQtyBeforeDateByStockType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("date") LocalDate date,
			        @Param("stockTypeId") Long stockTypeId);
	
	@Query(value = "SELECT COALESCE(SUM(qty_out),0) FROM stock_ledger WHERE raw_material_id = :rawMaterialId AND transaction_date < :date"
			+ " AND ref_type='STORE_ISSUE' AND kitchen_type_id = :kitchenTypeId AND is_delete = 0", nativeQuery = true)
			double sumStoreIssueQtyBeforeDateByKitchenType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("date") LocalDate date,
			        @Param("kitchenTypeId") Long kitchenTypeId);
	
	@Query(value = "SELECT COALESCE(SUM(qty_in),0) FROM stock_ledger WHERE raw_material_id = :rawMaterialId "
			+ " AND transaction_date < :date "
			+ " AND ref_type='STORE_ISSUE_RETURN' AND stock_type_id = :stockTypeId AND is_delete = 0", nativeQuery = true)
			double sumStoreIssueReturnBeforeDateByStockType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("date") LocalDate date,
			        @Param("stockTypeId") Long stockTypeId);
	
	@Query(value = "SELECT COALESCE(SUM(qty_in),0) FROM stock_ledger WHERE raw_material_id = :rawMaterialId AND transaction_date < :date"
			+ " AND ref_type='STORE_ISSUE_RETURN' AND kitchen_type_id = :kitchenTypeId AND is_delete = 0", nativeQuery = true)
			double sumStoreIssueReturnBeforeDateByKitchenType(
			        @Param("rawMaterialId") Long rawMaterialId,
			        @Param("date") LocalDate date,
			        @Param("kitchenTypeId") Long kitchenTypeId);
}
