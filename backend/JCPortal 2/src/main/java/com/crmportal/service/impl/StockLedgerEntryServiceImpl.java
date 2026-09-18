package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.crmportal.entity.*;
import com.crmportal.repository.*;
import com.crmportal.service.StockLedgerEntryService;

@Service
@Transactional
public class StockLedgerEntryServiceImpl implements StockLedgerEntryService {

	@Autowired
	private StockLedgerRepository stockLedgerRepository;

	@Autowired
	private PurchaseOrderDetailRepository poDetailRepository;

	@Autowired
	private PurchaseOrderReturnDetailRepository porDetailRepository;

	@Autowired
	private PurchaseOrderStoreDetailRepository storeDetailRepository;

	@Autowired
	private StoreIssueReturnDetailRepository sirDetailRepository;

	@Autowired
	private ChefRequisitionDetailRepository crDetailRepository;

	@Autowired
	private UnitConversionServiceImpl unitConversionService;

	@Autowired
	private UnitMasterRepository unitMasterRepository;

	@Autowired
	EventRawMaterialServiceImpl eventRawMaterialServiceImpl;

	// ── PURCHASE ──────────────────────────────────────────────────────────────
	@Override
	public void savePurchase(PurchaseOrderEntity po) {

		// Soft delete old entries for this PO (for update case)
		stockLedgerRepository.softDeleteByRefIdAndRefType(po.getId(), "PURCHASE");

		List<PurchaseOrderDetailEntity> details = poDetailRepository.findByPoId(po.getId());
		List<StockLedgerEntity> entries = new ArrayList<>();

		for (PurchaseOrderDetailEntity d : details) {
			if (d.getIsAddInStock()) {
				Double finalQty = Double.valueOf(d.getQty());
				UnitMasterEntity finalUnit = d.getUnit();

				Optional<UnitMasterEntity> unitOp = unitMasterRepository
						.findByIdAndIsParentUnitFalse(d.getUnit().getId());

				if (unitOp.isPresent() && unitOp.get().getParentUnit() != null
						&& unitOp.get().getEquivalentValue() != null && unitOp.get().getEquivalentValue() != 0.0) {
					finalQty = convertUnitQtyToParentQty(unitOp.get(), finalQty);
					finalUnit = unitOp.get().getParentUnit();
				}

				StockLedgerEntity entry = new StockLedgerEntity();
				entry.setRawMaterial(d.getRawMaterial());
				entry.setRawMaterialCat(d.getRawMaterialCat());
				entry.setUnit(finalUnit); // parent unit (or original if no parent)
				entry.setParty(po.getSupplier());
				entry.setStockType(po.getStocktype());
				entry.setUser(po.getUser());
				entry.setRefId(po.getId());
				entry.setRefCode(po.getPocode());
				entry.setRefType("PURCHASE");
				entry.setTransactionDate(po.getPodate());
				entry.setVoucher(po.getPocode());
				entry.setBillNo(po.getBillno());
				entry.setQty(finalQty); // converted qty
				entry.setQtyIn(finalQty); // converted qty
				entry.setQtyOut(0.0);
				entry.setIsDelete(false);
				entries.add(entry);
			}
		}

		stockLedgerRepository.saveAll(entries);
	}

	// ── PURCHASE RETURN ───────────────────────────────────────────────────────
	@Override
	public void savePurchaseReturn(PurchaseOrderReturnEntity por) {

		stockLedgerRepository.softDeleteByRefIdAndRefType(por.getId(), "PURCHASE_RETURN");

		List<PurchaseOrderReturnDetailEntity> details = porDetailRepository.findByPurchaseOrderReturnId(por.getId());
		List<StockLedgerEntity> entries = new ArrayList<>();

		for (PurchaseOrderReturnDetailEntity d : details) {

			Double finalQty = Double.valueOf(d.getQty());
			UnitMasterEntity finalUnit = d.getUnit();

			Optional<UnitMasterEntity> unitOp = unitMasterRepository.findByIdAndIsParentUnitFalse(d.getUnit().getId());

			if (unitOp.isPresent() && unitOp.get().getParentUnit() != null && unitOp.get().getEquivalentValue() != null
					&& unitOp.get().getEquivalentValue() != 0.0) {
				finalQty = convertUnitQtyToParentQty(unitOp.get(), finalQty);
				finalUnit = unitOp.get().getParentUnit();
			}

			StockLedgerEntity entry = new StockLedgerEntity();
			entry.setRawMaterial(d.getRawMaterial());
			entry.setRawMaterialCat(d.getRawMaterialCat());
			entry.setUnit(finalUnit); // from stock ledger logic
			entry.setParty(por.getSupplier());
			entry.setStockType(null);
			entry.setUser(por.getUser());
			entry.setRefId(por.getId());
			entry.setRefCode(por.getPorcode());
			entry.setRefType("PURCHASE_RETURN");
			entry.setTransactionDate(por.getReturndate());
			entry.setVoucher(por.getPorcode());
			entry.setBillNo(por.getBillno());
			entry.setQty(finalQty);
			entry.setQtyIn(0.0);
			entry.setQtyOut(finalQty); // converted qty
			entry.setIsDelete(false);
			entries.add(entry);
		}

		stockLedgerRepository.saveAll(entries);
	}

	// ── STORE ISSUE ───────────────────────────────────────────────────────────
	@Override
	@Transactional
	public void saveStoreIssue(PurchaseOrderStoreEntity storeIssue) {

		List<PurchaseOrderStoreDetailEntity> details = storeDetailRepository.findByPoId(storeIssue.getId());

		System.out.println("DETAIL SIZE = " + details.size());

		// IMPORTANT SAFETY CHECK
		if (details == null || details.isEmpty()) {

			System.out.println("NO DETAILS FOUND - STOCK LEDGER SKIPPED");

			return;
		}

		// Soft delete ONLY after details confirmed
		stockLedgerRepository.softDeleteByRefIdAndRefType(storeIssue.getId(), "STORE_ISSUE");

		List<StockLedgerEntity> entries = new ArrayList<>();

		for (PurchaseOrderStoreDetailEntity d : details) {

			if (d.getIsAddInStock()) {
				Double finalQty = Double.valueOf(d.getQty());
				UnitMasterEntity finalUnit = d.getUnit();

				Optional<UnitMasterEntity> unitOp = unitMasterRepository
						.findByIdAndIsParentUnitFalse(d.getUnit().getId());

				if (unitOp.isPresent() && unitOp.get().getParentUnit() != null
						&& unitOp.get().getEquivalentValue() != null && unitOp.get().getEquivalentValue() != 0.0) {

					finalQty = convertUnitQtyToParentQty(unitOp.get(), finalQty);

					finalUnit = unitOp.get().getParentUnit();
				}

				StockLedgerEntity entry = new StockLedgerEntity();

				entry.setRawMaterial(d.getRawMaterial());
				entry.setRawMaterialCat(d.getRawMaterialCat());
				entry.setUnit(finalUnit);
				entry.setParty(storeIssue.getParty());
				entry.setStockType(storeIssue.getStocktype());
				entry.setUser(storeIssue.getUser());

				entry.setRefId(storeIssue.getId());
				entry.setRefCode(storeIssue.getPocode());
				entry.setRefType("STORE_ISSUE");

				entry.setTransactionDate(storeIssue.getPodate());
				entry.setVoucher(storeIssue.getPocode());
				entry.setBillNo(null);

				entry.setQty(finalQty);
				entry.setQtyIn(0.0);
				entry.setQtyOut(finalQty);

				entry.setIsDelete(false);
				entry.setKichenType(storeIssue.getKitchentype());
				entries.add(entry);

				System.out.println("ENTRY ADDED : " + d.getRawMaterial().getNameEnglish());
			}
		}

		System.out.println("TOTAL ENTRIES = " + entries.size());

		stockLedgerRepository.saveAllAndFlush(entries);

		System.out.println("STOCK LEDGER SAVED");
	}

	// ── STORE ISSUE RETURN ────────────────────────────────────────────────────
	@Override
	public void saveStoreIssueReturn(StoreIssueReturnEntity sir) {

		stockLedgerRepository.softDeleteByRefIdAndRefType(sir.getId(), "STORE_ISSUE_RETURN");

		List<StoreIssueReturnDetailEntity> details = sirDetailRepository.findByStoreIssueReturnId(sir.getId());
		List<StockLedgerEntity> entries = new ArrayList<>();

		for (StoreIssueReturnDetailEntity d : details) {

			Double finalQty = Double.valueOf(d.getQty());
			UnitMasterEntity finalUnit = d.getUnit();

			Optional<UnitMasterEntity> unitOp = unitMasterRepository.findByIdAndIsParentUnitFalse(d.getUnit().getId());

			if (unitOp.isPresent() && unitOp.get().getParentUnit() != null && unitOp.get().getEquivalentValue() != null
					&& unitOp.get().getEquivalentValue() != 0.0) {
				finalQty = convertUnitQtyToParentQty(unitOp.get(), finalQty);
				finalUnit = unitOp.get().getParentUnit();
			}

			StockLedgerEntity entry = new StockLedgerEntity();
			entry.setRawMaterial(d.getRawMaterial());
			entry.setRawMaterialCat(d.getRawMaterialCat());
			entry.setUnit(finalUnit); // from stock ledger logic
			entry.setParty(sir.getParty());
			entry.setStockType(sir.getStocktype());
			entry.setUser(sir.getUser());
			entry.setRefId(sir.getId());
			entry.setRefCode(sir.getSircode());
			entry.setRefType("STORE_ISSUE_RETURN");
			entry.setTransactionDate(sir.getReturndate());
			entry.setVoucher(sir.getSircode());
			entry.setBillNo(null);
			entry.setQty(finalQty);
			entry.setQtyIn(finalQty); // converted qty — IN
			entry.setQtyOut(0.0);
			entry.setIsDelete(false);
			entries.add(entry);
		}

		stockLedgerRepository.saveAll(entries);
	}

	// ── CHEF REQUISITION ──────────────────────────────────────────────────────
	@Override
	public void saveChefRequisition(ChefRequisitionEntity cr) {

		stockLedgerRepository.softDeleteByRefIdAndRefType(cr.getId(), "CHEF_REQUISITION");
		stockLedgerRepository.flush();

		List<ChefRequisitionDetailEntity> details = crDetailRepository.findByChefRequisitionId(cr.getId());
		List<StockLedgerEntity> entries = new ArrayList<>();

		for (ChefRequisitionDetailEntity d : details) {

			Double finalQty = Double.valueOf(d.getQty());
			UnitMasterEntity finalUnit = d.getUnit();

			Optional<UnitMasterEntity> unitOp = unitMasterRepository.findByIdAndIsParentUnitFalse(d.getUnit().getId());

			if (unitOp.isPresent() && unitOp.get().getParentUnit() != null && unitOp.get().getEquivalentValue() != null
					&& unitOp.get().getEquivalentValue() != 0.0) {
				finalQty = convertUnitQtyToParentQty(unitOp.get(), finalQty);
				finalUnit = unitOp.get().getParentUnit();
			}

			StockLedgerEntity entry = new StockLedgerEntity();
			entry.setRawMaterial(d.getRawMaterial());
			entry.setRawMaterialCat(d.getRawMaterialCat());
			entry.setUnit(finalUnit); // from stock ledger logic
			entry.setParty(cr.getParty());
			entry.setStockType(cr.getStocktype());
			entry.setUser(cr.getUser());
			entry.setRefId(cr.getId());
			entry.setRefCode(cr.getCrcode());
			entry.setRefType("CHEF_REQUISITION");
			entry.setTransactionDate(cr.getCrdate());
			entry.setVoucher(cr.getCrcode());
			entry.setBillNo(null);
			entry.setQty(finalQty);
			entry.setQtyIn(0.0);
			entry.setQtyOut(finalQty); // converted qty — OUT
			entry.setIsDelete(false);
			entries.add(entry);
		}

		stockLedgerRepository.saveAll(entries);
		stockLedgerRepository.flush();
	}

	@Override
	public void saveStoreManageIncrease(RawMaterialMasterEntity rm, Double qty, StoreManageEntity storeManage,
			Long userId, RawMaterialCategoryMasterEntity rawMaterialCategoryMasterEntity,
			UnitMasterEntity unitMasterEntity) {
		try {
			StockLedgerEntity ledger = new StockLedgerEntity();
			ledger.setRawMaterial(rm);
			ledger.setQtyIn(qty);
			ledger.setQtyOut(0.0);
			ledger.setRefType("INCREASE");
			ledger.setRefId(storeManage.getId());
			ledger.setRefCode(storeManage.getVoucherNo());
			ledger.setTransactionDate(storeManage.getManageDate());
			ledger.setId(userId);
			ledger.setBillNo("NA");
			ledger.setRawMaterialCat(rawMaterialCategoryMasterEntity);
			ledger.setUnit(unitMasterEntity);
			ledger.setStockType(storeManage.getStockType());
			stockLedgerRepository.save(ledger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveStoreManageWastage(RawMaterialMasterEntity rm, Double qty, StoreManageEntity storeManage,
			Long userId, RawMaterialCategoryMasterEntity rawMaterialCategoryMasterEntity,
			UnitMasterEntity unitMasterEntity) {
		try {
			StockLedgerEntity ledger = new StockLedgerEntity();
			ledger.setRawMaterial(rm);
			ledger.setQtyIn(0.0);
			ledger.setQtyOut(qty);
			ledger.setRefType("WASTAGE");
			ledger.setRefId(storeManage.getId());
			ledger.setRefCode(storeManage.getVoucherNo());
			ledger.setTransactionDate(storeManage.getManageDate());
			ledger.setId(userId);
			ledger.setBillNo("NA");
			ledger.setRawMaterialCat(rawMaterialCategoryMasterEntity);
			ledger.setUnit(unitMasterEntity);
			ledger.setStockType(storeManage.getStockType());
			stockLedgerRepository.save(ledger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reversePurchase(Long poId) {
		stockLedgerRepository.softDeleteByRefIdAndRefType(poId, "PURCHASE");
	}

	@Override
	public void reversePurchaseReturn(Long porId) {
		stockLedgerRepository.softDeleteByRefIdAndRefType(porId, "PURCHASE_RETURN");
	}

	@Override
	public void reverseStoreIssue(Long poId) {
		stockLedgerRepository.softDeleteByRefIdAndRefType(poId, "STORE_ISSUE");
	}

	@Override
	public void reverseStoreIssueReturn(Long sirId) {
		stockLedgerRepository.softDeleteByRefIdAndRefType(sirId, "STORE_ISSUE_RETURN");
	}

	private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {

		return BigDecimal.valueOf(qty).divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
				.doubleValue();
	}
}