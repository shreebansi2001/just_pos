package com.crmportal.service.impl;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.*;
import com.crmportal.mapper.EventMasterMapper;
import com.crmportal.repository.*;
import com.crmportal.request.dto.*;
import com.crmportal.response.dto.*;
import com.crmportal.service.StockLedgerEntryService;
import com.crmportal.service.StoreOrderingTicketService;
import java.io.ByteArrayOutputStream;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.core.env.Environment;

@Service
@Transactional
public class StoreOrderingTicketServiceImpl implements StoreOrderingTicketService {

	@Autowired
	private StoreOrderingTicketRepository sotRepository;

	@Autowired
	private StoreOrderingTicketDetailRepository sotDetailRepository;

	@Autowired
	private SotManualPoRepository sotPoRepository;

	@Autowired
	private SotManualPoDetailRepository sotPoDetailRepository;

	@Autowired
	private EventRawMaterialRepository eventRawMaterialRepository;

	@Autowired
	private EventMasterRepository eventMasterRepository;

	@Autowired
	private UserMasterRepository userRepository;

	@Autowired
	private StockLedgerRepository stockLedgerRepository;

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

	@Autowired
	private StockLedgerEntryService stockLedgerEntryService;

	@Autowired
	private RawMaterialMasterRepository rawMaterialRepository;

	@Autowired
	private PartyMasterRepository partyRepository;

	@Autowired
	private UnitMasterRepository unitMasterRepository;

	@Autowired
	private UnitConversionServiceImpl unitConversionService;

	@Autowired
	EventRawMaterialServiceImpl eventRawMaterialServiceImpl;

	@Autowired
	EventFunctionMenuAllocationRepository menuAllocationRepository;

	@Autowired
	private Environment environment;

	@Autowired
	EventMasterMapper eventMasterMapper;

	@Autowired
	private MenuPreparationServiceImpl menuPreparationServiceImpl;
	
	@Autowired
	PurchaseRequestRepository purchaseRequestRepository;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// ── Generate SOT ──────────────────────────────────────────────────────────
	@Override
	public SotResponseDto generateSot(GenerateSotRequestDto request) {

		// Check if SOT already exists for this event
		Optional<StoreOrderingTicketEntity> existing = sotRepository
				.findByEventIdAndIsDeleteFalseAndStatusNot(request.getEventId(), "DELETED");

		if (existing.isPresent()) {
			throw new RuntimeException("SOT already exists for this event: " + existing.get().getSotNo());
		}

		EventMasterEntity event = eventMasterRepository.findById(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found"));

		// Create SOT
		StoreOrderingTicketEntity sot = new StoreOrderingTicketEntity();
		sot.setSotNo(generateSotNo(request.getUserId()));
		sot.setEvent(event);
		sot.setStatus("PENDING");
		sot.setUser(userRepository.findById(request.getUserId()).orElse(null));
		sot = sotRepository.save(sot);

		// Fetch all event raw materials for this event
		List<EventRawMaterialEntity> rawMaterials = eventRawMaterialRepository.findByEvent_Id(request.getEventId());

		List<StoreOrderingTicketDetailEntity> details = new ArrayList<>();

		for (EventRawMaterialEntity erm : rawMaterials) {

			if (erm.getRawMaterial() == null)
				continue;

			// Calculate available stock from stock_ledger
			double availableStock = calculateAvailableStock(erm.getRawMaterial().getId());

			StoreOrderingTicketDetailEntity detail = new StoreOrderingTicketDetailEntity();
			detail.setSot(sot);
			detail.setRawMaterial(erm.getRawMaterial());
			detail.setRawMaterialCat(erm.getRawMaterialCat());
			detail.setUnit(erm.getUnit());
			detail.setParty(erm.getSupplier());
			detail.setQty(erm.getFinalqty() != null ? erm.getFinalqty() : 0.0);
			detail.setAcceptedQty(0.0);
			detail.setReturnQty(0.0);
//			detail.setAvailableStock(availableStock);
			details.add(detail);
		}

		sotDetailRepository.saveAll(details);

		return convertToDto(sot);
	}

	// ── Accept SOT ────────────────────────────────────────────────────────────
	@Override
	public SotResponseDto acceptSot(AcceptSotRequestDto request) {

		StoreOrderingTicketEntity sot = sotRepository.findById(request.getSotId())
				.orElseThrow(() -> new RuntimeException("SOT not found"));

		// Update accepted qty for each detail
		for (AcceptSotDetailRequestDto d : request.getDetails()) {

			StoreOrderingTicketDetailEntity detail = sotDetailRepository.findById(d.getSotDetailId())
					.orElseThrow(() -> new RuntimeException("SOT detail not found: " + d.getSotDetailId()));

			detail.setAcceptedQty(d.getAcceptedQty());
			sotDetailRepository.save(detail);
		}

		sot.setStatus("ACCEPTED");
		sot.setUser(userRepository.findById(request.getUserId()).orElse(null));
		sot = sotRepository.save(sot);

		// NO stock ledger entry here — only status update

		return convertToDto(sot);
	}

	// ── Generate Manual PO agency wise ────────────────────────────────────────
	@Override
	public List<SotManualPoResponseDto> generateManualPo(Long sotId, Long userId) {

		StoreOrderingTicketEntity sot = sotRepository.findById(sotId)
				.orElseThrow(() -> new RuntimeException("SOT not found"));

		// Check if SOT is accepted first
		if (!"ACCEPTED".equalsIgnoreCase(sot.getStatus())) {
			throw new RuntimeException(
					"Cannot generate PO. SOT must be ACCEPTED first. Current status: " + sot.getStatus());
		}

		List<StoreOrderingTicketDetailEntity> details = sotDetailRepository.findBySotId(sotId);

		// Group by party
		Map<Long, List<StoreOrderingTicketDetailEntity>> groupedByParty = details.stream()
				.filter(d -> d.getParty() != null).collect(Collectors.groupingBy(d -> d.getParty().getId()));

		// Delete old manual POs for this SOT
		List<SotManualPoEntity> oldPos = sotPoRepository.findBySotIdAndIsDeleteFalse(sotId);
		for (SotManualPoEntity oldPo : oldPos) {
			oldPo.setIsDelete(true);
			sotPoRepository.save(oldPo);
		}

		List<SotManualPoEntity> newPos = new ArrayList<>();

		for (Map.Entry<Long, List<StoreOrderingTicketDetailEntity>> entry : groupedByParty.entrySet()) {

			PartyMasterEntity party = partyRepository.findById(entry.getKey()).orElse(null);
			if (party == null)
				continue;

			SotManualPoEntity po = new SotManualPoEntity();
			po.setVoucherNo(generateVoucherNo());
			po.setSot(sot);
			po.setParty(party);
			po.setEvent(sot.getEvent());
			po.setStatus("PENDING");
			po.setUser(userRepository.findById(userId).orElse(null));
			po = sotPoRepository.save(po);

			List<SotManualPoDetailEntity> poDetails = new ArrayList<>();
			for (StoreOrderingTicketDetailEntity d : entry.getValue()) {
				SotManualPoDetailEntity poDetail = new SotManualPoDetailEntity();
				poDetail.setSotManualPo(po);
				poDetail.setRawMaterial(d.getRawMaterial());
				poDetail.setRawMaterialCat(d.getRawMaterialCat());
				poDetail.setUnit(d.getUnit());
				poDetail.setParty(party);
				poDetail.setQty(d.getAcceptedQty());
				poDetails.add(poDetail);
			}
			sotPoDetailRepository.saveAll(poDetails);
			newPos.add(po);
		}

		// Update SOT status
		sot.setStatus("PO_GENERATED");
		sotRepository.save(sot);
		sotRepository.flush();
		sotDetailRepository.flush();

		// Save accepted qty to stock_ledger as STORE_ISSUE here
		saveAcceptedSotToStockLedger(sot);

		return newPos.stream().map(this::convertPoToDto).collect(Collectors.toList());
	}

	// ── Generate Purchase Invoice ─────────────────────────────────────────────
	@Override
	public void generatePurchaseInvoice(GeneratePurchaseInvoiceRequestDto request) {

		SotManualPoEntity sotPo = sotPoRepository.findById(request.getSotPoId())
				.orElseThrow(() -> new RuntimeException("Manual PO not found"));

		// ── Create ONE Purchase Order for this supplier ───────────────────────
		PurchaseOrderEntity po = new PurchaseOrderEntity();
		po.setPocode(generatePurchasePoCode());
		po.setSupplier(sotPo.getParty());
		po.setPodate(LocalDate.parse(request.getPodate(), formatter));
		po.setVoucher(sotPo.getVoucherNo());
		po.setBillno(request.getBillno());
		po.setInvoicetype(request.getInvoicetype());
		po.setRemarks(request.getRemarks());
		po.setSubamount(request.getSubamount());
		po.setDiscountper(request.getDiscountper());
		po.setDiscountval(request.getDiscountval());
		po.setAdjustamount(request.getAdjustamount());
		po.setFinalamount(request.getFinalamount());
		po.setGrnNumber(request.getGrnNumber());

		po.setPotype(0);
		po.setIsDelete(false);
		po.setUser(userRepository.findById(request.getUserId()).orElse(null));
		po = purchaseOrderRepository.save(po); // Save ONE PO

		// ── Create ALL items under that ONE PO ────────────────────────────────
		List<PurchaseOrderDetailEntity> purchaseDetails = new ArrayList<>();

		for (PurchaseInvoiceDetailRequestDto d : request.getDetails()) {

			RawMaterialMasterEntity rm = rawMaterialRepository.findById(d.getRawMaterialId()).orElse(null);

			// Use unit from request
			UnitMasterEntity requestUnit = unitMasterRepository.findById(d.getUnitId()).orElse(null);

			PurchaseOrderDetailEntity detail = new PurchaseOrderDetailEntity();
			detail.setPo(po); // all items linked to same PO
			detail.setRawMaterial(rm);
			detail.setRawMaterialCat(rm != null ? rm.getRawMaterialCat() : null);
			detail.setUnit(requestUnit != null ? requestUnit : (rm != null ? rm.getUnit() : null));
			detail.setHsccode(d.getHsccode());
			detail.setCgst(d.getCgst());
			detail.setSgst(d.getSgst());
			detail.setIgst(d.getIgst());
			detail.setCess(d.getCess());
			detail.setQty(d.getQty());
			detail.setPrice(d.getPrice());
			detail.setOthercharge(d.getOthercharge());
			detail.setTotal(d.getTotal());
			detail.setUserid(request.getUserId());
			detail.setIsAddInStock(d.getIsAddInStock());
			purchaseDetails.add(detail);
		}

		// Save ALL details under ONE PO
		purchaseOrderDetailRepository.saveAll(purchaseDetails);
		purchaseOrderDetailRepository.flush();

		// Save to stock_ledger as PURCHASE — one call for one PO

		stockLedgerEntryService.savePurchase(po);

		// Update Manual PO status
		sotPo.setStatus("INVOICE_GENERATED");
		sotPoRepository.save(sotPo);
		sotPoRepository.flush();

		if (sotPo.getSot() != null) {
			List<SotManualPoEntity> allPos = sotPoRepository.findBySotIdAndIsDeleteFalse(sotPo.getSot().getId());

			boolean allInvoiced = allPos.stream().allMatch(p -> "INVOICE_GENERATED".equalsIgnoreCase(p.getStatus()));

			if (allInvoiced) {
				StoreOrderingTicketEntity sot = sotRepository.findById(sotPo.getSot().getId())
						.orElseThrow(() -> new RuntimeException("SOT not found"));
				sot.setStatus("INVOICE_GENERATED");
				sotRepository.save(sot);
			}
		}
	}

	// ── Return SOT ────────────────────────────────────────────────────────────
	@Override
	public SotResponseDto returnSot(ReturnSotRequestDto request) {

		StoreOrderingTicketEntity sot = sotRepository.findById(request.getSotId())
				.orElseThrow(() -> new RuntimeException("SOT not found"));

		for (ReturnSotDetailRequestDto d : request.getDetails()) {

			StoreOrderingTicketDetailEntity detail = sotDetailRepository.findById(d.getSotDetailId())
					.orElseThrow(() -> new RuntimeException("SOT detail not found: " + d.getSotDetailId()));

			detail.setReturnQty(d.getReturnQty());
			sotDetailRepository.save(detail);
		}

		sotRepository.flush();
		sotDetailRepository.flush();

		// Save return qty to stock_ledger as STORE_ISSUE_RETURN
		saveReturnSotToStockLedger(sot, request.getUserId());

		return convertToDto(sot);
	}

	@Override
	public void deleteManualPo(Long sotPoId) {
		SotManualPoEntity sotPo = sotPoRepository.findById(sotPoId)
				.orElseThrow(() -> new RuntimeException("Manual PO not found"));

		// Only allow delete if not yet invoiced
		if ("INVOICE_GENERATED".equalsIgnoreCase(sotPo.getStatus())) {
			throw new RuntimeException("Cannot delete — Purchase Invoice already generated for this PO");
		}

		sotPo.setIsDelete(true);
		sotPoRepository.save(sotPo);
	}

	// ── Getters ───────────────────────────────────────────────────────────────

	@Override
	public List<SotListResponseDto> getAll(Long userId) {
		return sotRepository.findByUserIdAndIsDeleteFalseOrderByCreatedAtDesc(userId).stream().map(this::convertToList)
				.collect(Collectors.toList());
	}

	@Override
	public SotResponseDto getById(Long sotId) {
		StoreOrderingTicketEntity sot = sotRepository.findById(sotId)
				.orElseThrow(() -> new RuntimeException("SOT not found"));
		return convertToDto(sot);
	}

	@Override
	public List<SotResponseDto> getByEvent(Long eventId) {
		return sotRepository.findByEventIdAndIsDeleteFalse(eventId).stream().map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<SotManualPoResponseDto> getManualPoBySot(Long userId) {
		return sotPoRepository.getByUserDesc(userId).stream().map(this::convertPoToDto).collect(Collectors.toList());
	}

	@Override
	public SotManualPoResponseDto getManualPoById(Long sotPoId) {
		SotManualPoEntity po = sotPoRepository.findById(sotPoId)
				.orElseThrow(() -> new RuntimeException("Manual PO not found"));
		return convertPoToDto(po);
	}

	@Override
	public void deleteSot(Long sotId) {
		StoreOrderingTicketEntity sot = sotRepository.findById(sotId)
				.orElseThrow(() -> new RuntimeException("SOT not found"));
		sot.setIsDelete(true);
		sotRepository.save(sot);
	}

	@Override
	public SotResponseDto getStoreReport(Long sotId) {
		return getById(sotId);
	}

	// ── Stock Ledger Helpers ──────────────────────────────────────────────────

	private void saveAcceptedSotToStockLedger(StoreOrderingTicketEntity sot) {

		// Soft delete old entries
		stockLedgerRepository.softDeleteByRefIdAndRefType(sot.getId(), "SOT_STORE_ISSUE");
		stockLedgerRepository.flush();

		List<StoreOrderingTicketDetailEntity> details = sotDetailRepository.findBySotId(sot.getId());

		List<StockLedgerEntity> entries = new ArrayList<>();

		for (StoreOrderingTicketDetailEntity d : details) {
			if (d.getAcceptedQty() <= 0)
				continue;

			double baseQty = d.getAcceptedQty();
			UnitMasterEntity unit = d.getUnit();
			if (d.getUnit().getParentUnit() != null && d.getUnit().getEquivalentValue() != null
					&& d.getUnit().getEquivalentValue() != 0.0) {
				unit = d.getUnit().getParentUnit();
				baseQty = convertUnitQtyToParentQty(d.getUnit(), baseQty);
			}

			StockLedgerEntity entry = new StockLedgerEntity();
			entry.setRawMaterial(d.getRawMaterial());
			entry.setRawMaterialCat(d.getRawMaterialCat());
			entry.setUnit(unit);
			entry.setParty(d.getParty());
			entry.setStockType(null);
			entry.setUser(sot.getUser());
			entry.setRefId(sot.getId());
			entry.setRefCode(sot.getSotNo());
			entry.setRefType("SOT_STORE_ISSUE");
			entry.setTransactionDate(LocalDate.now());
			entry.setVoucher(sot.getSotNo());
			entry.setBillNo(null);
			entry.setQty(baseQty);
			entry.setQtyIn(0.0);
			entry.setQtyOut(baseQty);
			entry.setIsDelete(false);
			entries.add(entry);
		}

		stockLedgerRepository.saveAll(entries);
		stockLedgerRepository.flush();
	}

	private void saveReturnSotToStockLedger(StoreOrderingTicketEntity sot, Long userId) {

		// Soft delete old return entries
		stockLedgerRepository.softDeleteByRefIdAndRefType(sot.getId(), "SOT_STORE_RETURN");
		stockLedgerRepository.flush();

		List<StoreOrderingTicketDetailEntity> details = sotDetailRepository.findBySotId(sot.getId());

		List<StockLedgerEntity> entries = new ArrayList<>();

		for (StoreOrderingTicketDetailEntity d : details) {
			if (d.getReturnQty() <= 0)
				continue;

			double baseQty = d.getReturnQty();
			UnitMasterEntity unit = d.getUnit();
			if (d.getUnit().getParentUnit() != null && d.getUnit().getEquivalentValue() != null
					&& d.getUnit().getEquivalentValue() != 0.0) {
				unit = d.getUnit().getParentUnit();
				baseQty = convertUnitQtyToParentQty(d.getUnit(), baseQty);
			}
			StockLedgerEntity entry = new StockLedgerEntity();
			entry.setRawMaterial(d.getRawMaterial());
			entry.setRawMaterialCat(d.getRawMaterialCat());
			entry.setUnit(unit);
			entry.setParty(d.getParty());
			entry.setStockType(null);
			entry.setUser(userRepository.findById(userId).orElse(null));
			entry.setRefId(sot.getId());
			entry.setRefCode(sot.getSotNo());
			entry.setRefType("SOT_STORE_RETURN");
			entry.setTransactionDate(LocalDate.now());
			entry.setVoucher(sot.getSotNo());
			entry.setBillNo(null);
			entry.setQty(baseQty);
			entry.setQtyIn(baseQty);
			entry.setQtyOut(0.0);
			entry.setIsDelete(false);
			entries.add(entry);
		}

		stockLedgerRepository.saveAll(entries);
		stockLedgerRepository.flush();
	}

	private double calculateAvailableStock(Long rawMaterialId) {
		try {
			// OPB from raw material master
			double opb = rawMaterialRepository.findById(rawMaterialId)
					.map(r -> r.getOpbStock() != null ? r.getOpbStock().doubleValue() : 0.0).orElse(0.0);

			// Purchase — qty_in
			double purchase = stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rawMaterialId,
					LocalDate.of(2000, 1, 1), LocalDate.now(), "PURCHASE");

			// Purchase Return — qty_out
			double purchaseReturn = stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rawMaterialId,
					LocalDate.of(2000, 1, 1), LocalDate.now(), "PURCHASE_RETURN");

			// Store Issue — qty_out
			double storeIssue = stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rawMaterialId,
					LocalDate.of(2000, 1, 1), LocalDate.now(), "STORE_ISSUE");

			// Store Issue Return — qty_in
			double storeReturn = stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rawMaterialId,
					LocalDate.of(2000, 1, 1), LocalDate.now(), "STORE_ISSUE_RETURN");

			// Chef Requisition — qty_out (same as store issue)
			double chefRequisition = stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rawMaterialId,
					LocalDate.of(2000, 1, 1), LocalDate.now(), "CHEF_REQUISITION");

			// SOT Store Issue — qty_out
			double sotStoreIssue = stockLedgerRepository.sumQtyOutByRawMaterialAndDateRangeAndRefType(rawMaterialId,
					LocalDate.of(2000, 1, 1), LocalDate.now(), "SOT_STORE_ISSUE");

			// SOT Store Return — qty_in
			double sotStoreReturn = stockLedgerRepository.sumQtyInByRawMaterialAndDateRangeAndRefType(rawMaterialId,
					LocalDate.of(2000, 1, 1), LocalDate.now(), "SOT_STORE_RETURN");

			// Same formula as stock ledger closing stock
			return opb + purchase - purchaseReturn - storeIssue - chefRequisition - sotStoreIssue + storeReturn
					+ sotStoreReturn;

		} catch (Exception e) {
			return 0.0;
		}
	}

//	private double calculateAvailableStock(Long rawMaterialId) {
//		try {
//
//			RawMaterialMasterEntity rawMaterial = rawMaterialRepository.findByIdAndIsDeleteFalse(rawMaterialId);
//
//			if(rawMaterial != null) {
//				double openingStock = rawMaterial.getOpbStock() != null ? rawMaterial.getOpbStock().doubleValue() : 0.0;
//	
//				List<StockLedgerEntity> ledgerList = stockLedgerRepository.findAllByRawMaterialAndDateRange(rawMaterialId,
//						LocalDate.of(2000, 1, 1), LocalDate.now());
//	
//				// ==================== TOTAL QTY IN ====================
//				Map<Long, Double> qtyInMap = ledgerList.stream()
//						.filter(l -> l.getQtyIn() != null && l.getQtyIn() > 0 && l.getUnit() != null)
//						.collect(Collectors.groupingBy(l -> l.getUnit().getId(),
//								Collectors.summingDouble(StockLedgerEntity::getQtyIn)));
//	
//				Map<Long, Double> convertedQtyInMap = eventRawMaterialServiceImpl.convertToParentUnit(qtyInMap);
//	
//				double totalQtyIn = convertedQtyInMap.values().stream().mapToDouble(Double::doubleValue).sum();
//	
//				// ==================== TOTAL QTY OUT ====================
//				Map<Long, Double> qtyOutMap = ledgerList.stream()
//						.filter(l -> l.getQtyOut() != null && l.getQtyOut() > 0 && l.getUnit() != null)
//						.collect(Collectors.groupingBy(l -> l.getUnit().getId(),
//								Collectors.summingDouble(StockLedgerEntity::getQtyOut)));
//	
//				Map<Long, Double> convertedQtyOutMap = eventRawMaterialServiceImpl.convertToParentUnit(qtyOutMap);
//	
//				double totalQtyOut = convertedQtyOutMap.values().stream().mapToDouble(Double::doubleValue).sum();
//	
//				return openingStock + totalQtyIn - totalQtyOut;
//			}else {
//				return 0.0;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return 0.0;
//		}
//	}

	// ── Code Generators ───────────────────────────────────────────────────────

	/*
	 * private String generateSotNo() { String prefix = "SOT"; String lastCode =
	 * sotRepository.findMaxSotNo(prefix); long next = 1; if (lastCode != null) {
	 * try { next = Long.parseLong(lastCode.replace(prefix, "")) + 1; } catch
	 * (NumberFormatException e) { next = 1; } } return prefix +
	 * String.format("%05d", next); // e.g. SOT00001 }
	 */

	private String generateSotNo(Long userId) {

		StoreOrderingTicketEntity lastSot = sotRepository.findTopByUserIdOrderByIdDesc(userId);

		int nextNumber = 1;

		if (lastSot != null && lastSot.getSotNo() != null) {
			String lastNo = lastSot.getSotNo(); // Example: SOT00025
			nextNumber = Integer.parseInt(lastNo.replace("SOT", "")) + 1;
		}

		return String.format("SOT%05d", nextNumber);
	}

	private String generateVoucherNo() {
		String prefix = "PO";
		String lastCode = sotPoRepository.findMaxVoucherNo(prefix);
		long next = 1;
		if (lastCode != null) {
			try {
				next = Long.parseLong(lastCode.replace(prefix, "")) + 1;
			} catch (NumberFormatException e) {
				next = 1;
			}
		}
		return prefix + String.format("%05d", next); // e.g. PO00001
	}

	private String generatePurchasePoCode() {
		LocalDate today = LocalDate.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
		String prefix = "PO-" + today.format(dtf) + "-";
		String lastCode = purchaseOrderRepository.findMaxPocodeByPrefix(prefix);
		long next = 1;
		if (lastCode != null) {
			try {
				String[] parts = lastCode.split("-");
				next = Long.parseLong(parts[parts.length - 1]) + 1;
			} catch (NumberFormatException e) {
				next = 1;
			}
		}
		return prefix + String.format("%03d", next);
	}

	// ── Converters ────────────────────────────────────────────────────────────

	private SotResponseDto convertToDto(StoreOrderingTicketEntity sot) {
		SotResponseDto dto = new SotResponseDto();
		dto.setId(sot.getId());
		dto.setSotNo(sot.getSotNo());
		dto.setEventId(sot.getEvent().getId());
		dto.setEventName(sot.getEvent().getParty() != null ? sot.getEvent().getParty().getNameEnglish() : "");
		dto.setStatus(sot.getStatus());
		dto.setCreatedAt(sot.getCreatedAt().toString());
		dto.setUserId(sot.getUser() != null ? sot.getUser().getId() : null);

		List<SotDetailResponseDto> details = sotDetailRepository.findBySotId(sot.getId()).stream().map(d -> {
			SotDetailResponseDto rd = new SotDetailResponseDto();
			rd.setId(d.getId());
			if (d.getRawMaterial() != null) {
				rd.setRawMaterialId(d.getRawMaterial().getId());
				rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
			}
			if (d.getRawMaterialCat() != null) {
				rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
				rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
			}
			if (d.getUnit() != null) {
				rd.setUnitId(d.getUnit().getId());
				rd.setUnitName(d.getUnit().getNameEnglish());
			}
			if (d.getParty() != null) {
				rd.setPartyId(d.getParty().getId());
				rd.setPartyName(d.getParty().getNameEnglish());
			}
			rd.setQty(d.getQty());
			rd.setAcceptedQty(d.getAcceptedQty());
			rd.setReturnQty(d.getReturnQty());

			double availableStock = calculateAvailableStock(d.getRawMaterial().getId());

			rd.setAvailableStock(availableStock);

			UnitMasterEntity unit = getUnitByRawMaterialId(d.getRawMaterial().getId());

			rd.setAvailableStockUnitId(unit.getId());
			rd.setAvailableStockUnitName(unit.getNameEnglish());

			return rd;
		}).collect(Collectors.toList());

		dto.setDetails(details);
		return dto;
	}

	private SotListResponseDto convertToList(StoreOrderingTicketEntity sot) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		SotListResponseDto dto = new SotListResponseDto();
		dto.setId(sot.getId());
		dto.setSotNo(sot.getSotNo());
		dto.setEventId(sot.getEvent().getId());
		dto.setEventName(sot.getEvent().getParty() != null ? sot.getEvent().getParty().getNameEnglish() : "");
		dto.setStatus(sot.getStatus());
		dto.setCreatedAt(sot.getCreatedAt().toString());
		dto.setUserId(sot.getUser() != null ? sot.getUser().getId() : null);
		dto.setEventDate(sot.getEvent().getEventStartDateTime() != null ? sot.getEvent().getEventStartDateTime().format(dateFormatter) : "");

		return dto;
	}

	private SotManualPoResponseDto convertPoToDto(SotManualPoEntity po) {
		SotManualPoResponseDto dto = new SotManualPoResponseDto();
		dto.setId(po.getId());
		dto.setVoucherNo(po.getVoucherNo());

		// Null check for sot
		if (po.getSot() != null) {
			dto.setSotId(po.getSot().getId());
			dto.setSotNo(po.getSot().getSotNo());
		}

		// Null check for party
		if (po.getParty() != null) {
			dto.setPartyId(po.getParty().getId());
			dto.setPartyName(po.getParty().getNameEnglish());
		}

		// Null check for event
		if (po.getEvent() != null) {
			dto.setEventId(po.getEvent().getId());
			dto.setEventName(po.getEvent().getEventNo());
		}

		dto.setStatus(po.getStatus());
		dto.setCreatedAt(po.getCreatedAt() != null ? po.getCreatedAt().toString() : null);

		dto.setVoucherDate(po.getVoucherDate() != null ? po.getVoucherDate().format(formatter) : null);
		dto.setBillno(po.getBillno());
		dto.setInvoicetype(po.getInvoicetype());
		dto.setRemarks(po.getRemarks());
		dto.setSubamount(po.getSubamount());
		dto.setDiscountper(po.getDiscountper());
		dto.setDiscountval(po.getDiscountval());
		dto.setAdjustamount(po.getAdjustamount());
		dto.setFinalamount(po.getFinalamount());

		List<SotManualPoDetailResponseDto> details = sotPoDetailRepository.findBySotManualPoId(po.getId()).stream()
				.map(d -> {
					SotManualPoDetailResponseDto rd = new SotManualPoDetailResponseDto();
					rd.setId(d.getId());
					if (d.getRawMaterial() != null) {
						rd.setRawMaterialId(d.getRawMaterial().getId());
						rd.setRawMaterialName(d.getRawMaterial().getNameEnglish());
					}
					if (d.getRawMaterialCat() != null) {
						rd.setRawMaterialCatId(d.getRawMaterialCat().getId());
						rd.setRawMaterialCatName(d.getRawMaterialCat().getNameEnglish());
					}
					if (d.getUnit() != null) {
						rd.setUnitId(d.getUnit().getId());
						rd.setUnitName(d.getUnit().getNameEnglish());
					}
					if (d.getParty() != null) {
						rd.setPartyId(d.getParty().getId());
						rd.setPartyName(d.getParty().getNameEnglish());
					}
					rd.setQty(d.getQty());

					// New detail fields with null check
					// No null check needed — primitive float fields
					rd.setHsccode(d.getHsccode());
					rd.setCgst(d.getCgst());
					rd.setSgst(d.getSgst());
					rd.setIgst(d.getIgst());
					rd.setPrice(d.getPrice());
					rd.setOthercharge(d.getOthercharge());
					rd.setTotal(d.getTotal());

					return rd;
				}).collect(Collectors.toList());

		dto.setDetails(details);
		return dto;
	}

	@Override
	public byte[] generatePdfReport(Long sotId, Long userId, Integer isCompanyDetails) {
		try {
			SotResponseDto sot = getById(sotId);
			if (sot == null) {
				throw new RuntimeException("SOT not found with ID: " + sotId);
			}

			EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(sot.getEventId())
					.orElseThrow(() -> new RuntimeException("Event not found."));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter pdfWriter = new PdfWriter(baos);
			PdfDocument pdfDoc = new PdfDocument(pdfWriter);
			Document document = new Document(pdfDoc, PageSize.A4.rotate());
			document.setMargins(25, 25, 40, 25); // bottom margin bigger for footer

			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

			// ── Colors (matching sample: dark header, white text, gray alt) ──
			Color darkHeader = new DeviceRgb(50, 50, 50); // near-black header
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color altRow = new DeviceRgb(245, 245, 245); // light gray alt
			Color totalRowBg = new DeviceRgb(200, 200, 200); // gray total row
			Color borderColor = new DeviceRgb(150, 150, 150); // grid border
			Color darkBlue = new DeviceRgb(13, 71, 116);
			Color labelGray = new DeviceRgb(100, 120, 140);
			Color lightBlueBg = new DeviceRgb(235, 245, 255);

			// ══════════════════════════════════════════════════════════════════
			// SECTION 0 — COMPANY HEADER
			// ══════════════════════════════════════════════════════════════════
			CompanyDetailsResponseDto cmpDto = null;
			try {
				cmpDto = getCompanyDetails(userId);
			} catch (Exception ignored) {
			}

			if (isCompanyDetails == 1) {

				float[] companyWidths = { 20f, 2f, 78f };
				Table headerTable = new Table(UnitValue.createPercentArray(companyWidths));
				headerTable.setWidth(UnitValue.createPercentValue(100));

				// Logo (spans 3 rows × 2 cols — exact match to reference)
				try {
					ImageData logoData = menuPreparationServiceImpl
							.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());

					Image logo = new Image(logoData);
					logo.setWidth(100f);
					logo.setAutoScale(false);
					logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

					headerTable.addCell(new Cell(3, 2).add(logo)
							.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
							.setBorder(Border.NO_BORDER).setPaddingBottom(10f));

				} catch (Exception e) {
					headerTable.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
				}

				// Company Name
				headerTable.addCell(new Cell()
						.add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont).setFontSize(14)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT))
						.setBorder(Border.NO_BORDER).setPaddingLeft(15f));

				// Phone
				headerTable
						.addCell(new Cell()
								.add(new Paragraph()
										.add(new Text("Phone : ").setFont(boldFont).setFontSize(14)
												.setFontColor(blackColor))
										.add(new Text(cmpDto.getOfficeNo() != null ? cmpDto.getOfficeNo() : "")
												.setFont(regularFont).setFontSize(14).setFontColor(blackColor)))
								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(15f)
								.setPaddingBottom(10f));

				// Email
				headerTable
						.addCell(new Cell()
								.add(new Paragraph()
										.add(new Text("Email : ").setFont(boldFont).setFontSize(14)
												.setFontColor(blackColor))
										.add(new Text(cmpDto.getCompanyEmail() != null ? cmpDto.getCompanyEmail() : "")
												.setFont(regularFont).setFontSize(14).setFontColor(blackColor)))
								.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setPaddingLeft(15f)
								.setPaddingBottom(10f));

				// ── Empty filler cell (reference has this exact line) ─────────
				headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));

				document.add(headerTable);

				// Dark blue divider
				Table companyDivider = new Table(UnitValue.createPercentArray(new float[] { 100f }));
				companyDivider.setWidth(UnitValue.createPercentValue(100));
				companyDivider
						.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
				document.add(companyDivider);
				document.add(new Paragraph("").setMarginBottom(8));
			}

			// ══════════════════════════════════════════════════════════════════
			// SECTION 1 — TOP HEADER (SOT No + Event + Status)
			// ══════════════════════════════════════════════════════════════════
			float[] topW = { 50f, 50f };
			Table topTable = new Table(UnitValue.createPercentArray(topW));
			topTable.setWidth(UnitValue.createPercentValue(100));

			topTable.addCell(new Cell(1, 2)
					.add(new Paragraph("Store Report (SOT)").setFont(boldFont).setFontSize(16).setFontColor(darkBlue))
					.add(new Paragraph("SOT No: " + sotNvl(sot.getSotNo()) + "   |   Event Date: "
							+ sotNvl(eventMaster.getEventStartDateTime()
									.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
							+ "   |   Party Name: " + sotNvl(eventMaster.getParty().getNameEnglish())
							+ "   |   Event Name: " + sotNvl(eventMaster.getEventType().getNameEnglish()))
							.setFont(regularFont).setFontSize(9).setFontColor(labelGray))
					.setBorder(Border.NO_BORDER).setPaddingBottom(4));

			document.add(topTable);
			document.add(new Paragraph("").setMarginBottom(6));

			// ══════════════════════════════════════════════════════════════════
			// SECTION 2 — MAIN TABLE
			// Columns: Item Name | Category | Transfer | Return Qty | Qty |
			// Unit | Supp Rate | Main Unit | Total
			// ══════════════════════════════════════════════════════════════════
			float[] colW = { 22f, 13f, 8f, 9f, 7f, 8f, 9f, 8f, 10f };
			Table mainTable = new Table(UnitValue.createPercentArray(colW));
			mainTable.setWidth(UnitValue.createPercentValue(100));

			String[] headers = { "ITEM NAME", "CATEGORY NAME", "TRANSFER", "RETURN QTY", "QTY", "UNIT", "SUPP RATE",
					"MAIN UNIT", "TOTAL" };
			TextAlignment[] hAligns = { TextAlignment.LEFT, TextAlignment.LEFT, TextAlignment.RIGHT,
					TextAlignment.RIGHT, TextAlignment.RIGHT, TextAlignment.CENTER, TextAlignment.RIGHT,
					TextAlignment.CENTER, TextAlignment.RIGHT };

			for (int i = 0; i < headers.length; i++) {
				mainTable.addHeaderCell(new Cell()
						.add(new Paragraph(headers[i]).setFont(boldFont).setFontSize(8)
								.setFontColor(ColorConstants.WHITE))
						.setBackgroundColor(darkHeader).setTextAlignment(hAligns[i]).setPaddingTop(6)
						.setPaddingBottom(6).setPaddingLeft(4).setPaddingRight(4)
						.setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f)));
			}

			// ── Data rows + totals ────────────────────────────────────────────
			boolean alternate = false;
			double grandTotal = 0;

			for (SotDetailResponseDto d : sot.getDetails()) {
				Color bg = alternate ? altRow : ColorConstants.WHITE;
				alternate = !alternate;

				double transfer = d.getAcceptedQty();
				double returnQty = d.getReturnQty();
				double qty = transfer - returnQty; // net qty
				double suppRate = 0.0;

				// Get supplier rate from raw material master
				try {
					suppRate = rawMaterialRepository.findById(d.getRawMaterialId())
							.map(r -> r.getSupplierRate() != null ? r.getSupplierRate().doubleValue() : 0.0)
							.orElse(0.0);
				} catch (Exception ignored) {
				}

				double totalQty = d.getQty();

				if (d.getAvailableStockUnitId() != null && d.getUnitId() != null
						&& d.getAvailableStockUnitId() != d.getUnitId()) {

					Optional<UnitMasterEntity> unitOp = unitMasterRepository
							.findByIdAndIsParentUnitFalse(d.getUnitId());

					Map<Long, Double> resultMap = new HashMap<>();

					if (unitOp.isPresent() && unitOp.get().getParentUnit() != null
							&& unitOp.get().getEquivalentValue() != null && unitOp.get().getEquivalentValue() != 0.0) {

						Long parentId = unitOp.get().getParentUnit().getId();
						Double convertedVal = convertUnitQtyToParentQty(unitOp.get(), qty);

						// safely add or update
						resultMap.merge(parentId, convertedVal, Double::sum);

					} else {
						// keep original unit if no parent
						resultMap.merge(d.getUnitId(), qty, Double::sum);
					}
					totalQty = resultMap.getOrDefault(d.getAvailableStockUnitId(), d.getQty());
				}

				double total = totalQty * suppRate;
				grandTotal += total;

				String unitName = sotNvl(d.getUnitName());
				String mainUnitName = unitName; // default same

				// Resolve parent unit for main unit column
				try {
					final Long rawMatId = d.getRawMaterialId();
					Long unitId = d.getUnitId();
					if (unitId != null) {
						com.itextpdf.kernel.colors.Color ignored2 = null; // just to scope
						UnitMasterEntity unitEnt = unitMasterRepository.findById(unitId).orElse(null);
						if (unitEnt != null && unitEnt.getParentUnit() != null) {
							mainUnitName = unitEnt.getParentUnit().getNameEnglish();
						}
					}
				} catch (Exception ignored) {
				}

				mainTable.addCell(
						sotCell(sotNvl(d.getRawMaterialName()), bg, regularFont, TextAlignment.LEFT, borderColor));
				mainTable.addCell(
						sotCell(sotNvl(d.getRawMaterialCatName()), bg, regularFont, TextAlignment.LEFT, borderColor));
				mainTable.addCell(sotCell(sotFmt(transfer), bg, regularFont, TextAlignment.RIGHT, borderColor));
				mainTable.addCell(sotCell(sotFmt(returnQty), bg, regularFont, TextAlignment.RIGHT, borderColor));
				mainTable.addCell(sotCell(sotFmt(qty), bg, regularFont, TextAlignment.RIGHT, borderColor));
				mainTable.addCell(sotCell(unitName, bg, regularFont, TextAlignment.CENTER, borderColor));
				mainTable.addCell(sotCell(sotFmt(suppRate), bg, regularFont, TextAlignment.RIGHT, borderColor));
				mainTable.addCell(sotCell(mainUnitName, bg, regularFont, TextAlignment.CENTER, borderColor));
				mainTable.addCell(sotCell(sotFmt(total), bg, regularFont, TextAlignment.RIGHT, borderColor));
			}

			// ── AMOUNT total footer row ───────────────────────────────────────
			mainTable.addCell(new Cell(1, 8)
					.add(new Paragraph("AMOUNT").setFont(boldFont).setFontSize(9).setFontColor(ColorConstants.BLACK))
					.setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(totalRowBg)
					.setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f)).setPaddingTop(5)
					.setPaddingBottom(5).setPaddingRight(6));

			mainTable.addCell(new Cell().add(new Paragraph(sotFmt(grandTotal)).setFont(boldFont).setFontSize(9))
					.setTextAlignment(TextAlignment.RIGHT).setBackgroundColor(totalRowBg)
					.setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.5f)).setPaddingTop(5)
					.setPaddingBottom(5).setPaddingRight(4));

			document.add(mainTable);

			// ══════════════════════════════════════════════════════════════════
			// SECTION 3 — FOOTER (Sign | Page No | Timestamp)
			// ══════════════════════════════════════════════════════════════════
			document.add(new Paragraph("").setMarginBottom(10));

			String timestamp = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " "
					+ java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h.mm a"));

			float[] footW = { 33f, 34f, 33f };
			Table footerTable = new Table(UnitValue.createPercentArray(footW));
			footerTable.setWidth(UnitValue.createPercentValue(100));

			// Left: Sign
			footerTable
					.addCell(
							new Cell()
									.add(new Paragraph("Sign :  ___________________").setFont(regularFont)
											.setFontSize(9).setFontColor(ColorConstants.BLACK))
									.setBorder(Border.NO_BORDER));

			// Center: page number placeholder (static 1 — for multi-page use event handler)
			footerTable.addCell(new Cell()
					.add(new Paragraph("1").setFont(regularFont).setFontSize(9).setTextAlignment(TextAlignment.CENTER))
					.setBorder(Border.NO_BORDER));

			document.add(footerTable);
			document.close();

			return baos.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Cell sotCell(String text, Color bg, PdfFont font, TextAlignment align, Color borderColor) {
		return new Cell().add(new Paragraph(text != null ? text : "").setFont(font).setFontSize(8))
				.setBackgroundColor(bg).setTextAlignment(align)
				.setBorder(new com.itextpdf.layout.borders.SolidBorder(borderColor, 0.3f)).setPaddingTop(4)
				.setPaddingBottom(4).setPaddingLeft(4).setPaddingRight(4);
	}

	private String sotFmt(double val) {
		if (val == Math.floor(val) && !Double.isInfinite(val)) {
			return String.format("%.2f", val);
		}
		return String.format("%.2f", val);
	}

	private String sotNvl(String val) {
		return (val != null && !val.isEmpty()) ? val : "";
	}

	public CompanyDetailsResponseDto getCompanyDetails(Long userId) {
		UserMasterEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		Optional<Object[]> op = menuAllocationRepository.getCompanyDetailsByUserId(userId);

		if (!op.isPresent()) {
			return new CompanyDetailsResponseDto();
		} else {
			Object[] row = op.get();

			if (row.length == 1 && row[0] instanceof Object[]) {
				row = (Object[]) row[0];
			}

			CompanyDetailsResponseDto dto = new CompanyDetailsResponseDto();
			dto.setCompanyName(row[0] != null ? row[0].toString() : "");
			dto.setCountryCode(row[1] != null ? row[1].toString() : "");
			dto.setCompanyEmail(row[2] != null ? row[2].toString() : "");
			dto.setOfficeNo(row[3] != null ? row[3].toString() : "");
			dto.setAddress(row[4] != null ? row[4].toString() : "");
			dto.setLogo(row[5] != null ? row[5].toString() : "");
			return dto;
		}
	}

	@Override
	public SotResponseDto updateSotDetails(UpdateSotDetailsRequestDto request) {

		StoreOrderingTicketEntity sot = sotRepository.findById(request.getSotId())
				.orElseThrow(() -> new RuntimeException("SOT not found"));

		for (UpdateSotDetailItemRequestDto d : request.getDetails()) {

			StoreOrderingTicketDetailEntity detail = sotDetailRepository.findById(d.getSotDetailId())
					.orElseThrow(() -> new RuntimeException("SOT detail not found: " + d.getSotDetailId()));

			// Update qty
			detail.setAcceptedQty(d.getQty());

			// Update supplier if changed
			if (d.getPartyId() != null && d.getPartyId() != 0) {
				detail.setParty(partyRepository.findById(d.getPartyId()).orElse(null));
			}

			// Update unit if changed
			if (d.getUnitId() != null) {
				detail.setUnit(unitMasterRepository.findById(d.getUnitId()).orElse(null));
			}

			sotDetailRepository.save(detail);
		}

		sotDetailRepository.flush();
		return convertToDto(sot);
	}

	// =========================================================================
	// ── GET Manual PO Info ────────────────────────────────────────────────────
	// =========================================================================
	@Override
	public SotPoInfoResponseDto getManualPoInfo(Long sotPoId) {

		SotManualPoEntity po = sotPoRepository.findById(sotPoId)
				.orElseThrow(() -> new RuntimeException("Manual PO not found: " + sotPoId));

		return convertPoInfoToDto(po);
	}

	// =========================================================================
	// ── SAVE Manual PO Info (update directly on sot_manual_po row) ───────────
	// =========================================================================
	@Override
	public SotPoInfoResponseDto saveManualPoInfo(SotPoInfoRequestDto request) {

		SotManualPoEntity po = sotPoRepository.findById(request.getSotPoId())
				.orElseThrow(() -> new RuntimeException("Manual PO not found: " + request.getSotPoId()));

		po.setChallanNo(request.getChallanNo());
		po.setEventName(request.getEventName());
		po.setDeliveryVenue(request.getDeliveryVenue());
		po.setDeliveryTime(request.getDeliveryTime());
		po.setRemarks(request.getRemarks());

		po = sotPoRepository.save(po);
		return convertPoInfoToDto(po);
	}

	// =========================================================================
	// ── GENERATE PDF for Manual PO ────────────────────────────────────────────
	// Prints: Company Header → Title block → Info block → Items table → Footer
	// =========================================================================
	@Override
	public byte[] generateManualPoPdf(Long sotPoId, Long userId, Integer isCompanyDetails) {
		try {
			// ── Fetch PO data ─────────────────────────────────────────────────
			SotManualPoEntity po = sotPoRepository.findById(sotPoId)
					.orElseThrow(() -> new RuntimeException("Manual PO not found: " + sotPoId));

			SotManualPoResponseDto poDto = convertPoToDto(po);

			// ── iText PDF setup ───────────────────────────────────────────────
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter writer = new PdfWriter(baos);
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc, PageSize.A4);
			document.setMargins(25, 30, 40, 30);

			PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
			PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

			// ── Colour palette ────────────────────────────────────────────────
			Color darkBlue = new DeviceRgb(13, 71, 116);
			Color darkHeader = new DeviceRgb(50, 50, 50);
			Color altRow = new DeviceRgb(245, 245, 245);
			Color totalRowBg = new DeviceRgb(200, 200, 200);
			Color borderColor = new DeviceRgb(150, 150, 150);
			Color labelGray = new DeviceRgb(100, 120, 140);
			Color blackColor = new DeviceRgb(0, 0, 0);
			Color infoBg = new DeviceRgb(235, 245, 255);
			Color infoBorder = new DeviceRgb(180, 210, 240);
			Color lightBlueBg = new DeviceRgb(235, 245, 255);

			// =================================================================
			// SECTION 0 — COMPANY HEADER
			// =================================================================
			if (isCompanyDetails == 1) {
				CompanyDetailsResponseDto cmpDto = null;
				try {
					cmpDto = getCompanyDetails(userId);
				} catch (Exception ignored) {
				}

				if (cmpDto != null) {
					float[] cw = { 20f, 2f, 78f };
					Table headerTable = new Table(UnitValue.createPercentArray(cw));
					headerTable.setWidth(UnitValue.createPercentValue(100));

					// Logo
					try {
						ImageData logoData = menuPreparationServiceImpl
								.loadImageFromResource(environment.getProperty("app.image.url") + cmpDto.getLogo());
						Image logo = new Image(logoData);
						logo.setWidth(100f).setAutoScale(false);
						logo.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
						headerTable.addCell(new Cell(3, 2).add(logo)
								.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
								.setBorder(Border.NO_BORDER).setPaddingBottom(10f));
					} catch (Exception ignored) {
						headerTable.addCell(new Cell(3, 2).setBorder(Border.NO_BORDER));
					}

					// Company name
					headerTable
							.addCell(
									new Cell()
											.add(new Paragraph(cmpDto.getCompanyName()).setFont(boldFont)
													.setFontSize(14).setFontColor(blackColor))
											.setBorder(Border.NO_BORDER).setPaddingLeft(15f));

					// Phone
					headerTable.addCell(new Cell()
							.add(new Paragraph()
									.add(new Text("Phone : ").setFont(boldFont).setFontSize(11)
											.setFontColor(blackColor))
									.add(new Text(sotNvl(cmpDto.getOfficeNo())).setFont(regularFont).setFontSize(11)
											.setFontColor(blackColor)))
							.setBorder(Border.NO_BORDER).setPaddingLeft(15f).setPaddingBottom(5f));

					// Email
					headerTable.addCell(new Cell()
							.add(new Paragraph()
									.add(new Text("Email : ").setFont(boldFont).setFontSize(11)
											.setFontColor(blackColor))
									.add(new Text(sotNvl(cmpDto.getCompanyEmail())).setFont(regularFont).setFontSize(11)
											.setFontColor(blackColor)))
							.setBorder(Border.NO_BORDER).setPaddingLeft(15f).setPaddingBottom(5f));

					headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));
					document.add(headerTable);

					// Dark-blue divider line
					Table divider = new Table(UnitValue.createPercentArray(new float[] { 100f }));
					divider.setWidth(UnitValue.createPercentValue(100));
					divider.addCell(new Cell().setHeight(2f).setBackgroundColor(darkBlue).setBorder(Border.NO_BORDER));
					document.add(divider);
					document.add(new Paragraph("").setMarginBottom(8));
				}
			}

			// =================================================================
			// SECTION 1 — TITLE + PO META
			// =================================================================
			float[] topW = { 60f, 40f };
			Table topTable = new Table(UnitValue.createPercentArray(topW));
			topTable.setWidth(UnitValue.createPercentValue(100));

			topTable.addCell(new Cell()
					.add(new Paragraph("Auto / Manual PO").setFont(boldFont).setFontSize(15).setFontColor(darkBlue))
//                   .add(new Paragraph("Voucher No: " + sotNvl(poDto.getVoucherNo())
//                            + "   |   SOT No: " + sotNvl(poDto.getSotNo()))
//                            .setFont(regularFont).setFontSize(9).setFontColor(labelGray))
					.setBorder(Border.NO_BORDER).setPaddingBottom(4));

//            topTable.addCell(new Cell()
//                    .add(new Paragraph("Party: " + sotNvl(poDto.getPartyName()))
//                            .setFont(regularFont).setFontSize(9).setFontColor(labelGray)
//                            .setTextAlignment(TextAlignment.RIGHT))
//                    .add(new Paragraph("Event: " + sotNvl(poDto.getEventName()))
//                            .setFont(regularFont).setFontSize(9).setFontColor(labelGray)
//                            .setTextAlignment(TextAlignment.RIGHT))
//                    .setBorder(Border.NO_BORDER).setPaddingBottom(4));

			
			document.add(topTable);
			document.add(new Paragraph("").setMarginBottom(4));
			
			float[] detailWidths = {18f, 20f, 16f, 22f, 24f};
            Table detailTable = new Table(UnitValue.createPercentArray(detailWidths));
            detailTable.setWidth(UnitValue.createPercentValue(100));

            // helper: add a vendor info cell (label on top, value below)
            addDetailCell(detailTable, "Voucher No",  poDto.getVoucherNo() != null ? poDto.getVoucherNo() : "", lightBlueBg, labelGray, boldFont, regularFont, true,  false);
            addDetailCell(detailTable, "PO Generated Date", poDto.getVoucherDate() != null ? poDto.getVoucherDate() : "",       lightBlueBg, labelGray, boldFont, regularFont, false, false);
            addDetailCell(detailTable, "SOT No", poDto.getSotNo() != null ? poDto.getSotNo().toString() : "",   lightBlueBg, labelGray, boldFont, regularFont, false, false);
            addDetailCell(detailTable, "Supplier", poDto.getPartyName() != null ? poDto.getPartyName() : "",  lightBlueBg, labelGray, boldFont, regularFont, false, false);
            addDetailCell(detailTable, "Remarks", poDto.getRemarks() != null ? poDto.getRemarks() : "",   lightBlueBg, labelGray, boldFont, regularFont, false, true);

            document.add(detailTable);
            document.add(new Paragraph("").setMarginBottom(12));

			// =================================================================
			// SECTION 2 — INFO BLOCK (only rows where data exists are shown)
			// Challan No | Event Name | Delivery Venue | Delivery Time | Remarks
			// =================================================================
			boolean hasInfo = isNonEmpty(po.getChallanNo()) || isNonEmpty(po.getEventName())
					|| isNonEmpty(po.getDeliveryVenue()) || isNonEmpty(po.getDeliveryTime())
					|| isNonEmpty(po.getRemarks());

			if (hasInfo) {
				float[] infoW = { 30f, 70f };
				Table infoTable = new Table(UnitValue.createPercentArray(infoW));
				infoTable.setWidth(UnitValue.createPercentValue(100));

				addInfoRow(infoTable, "To", poDto.getPartyName(), boldFont, regularFont, infoBg, infoBorder);
				addInfoRow(infoTable, "Challan No", po.getChallanNo(), boldFont, regularFont, infoBg, infoBorder);
				addInfoRow(infoTable, "Event Name", po.getEventName(), boldFont, regularFont, infoBg, infoBorder);
				addInfoRow(infoTable, "Delivery Venue", po.getDeliveryVenue(), boldFont, regularFont, infoBg,
						infoBorder);
				addInfoRow(infoTable, "Delivery Time", po.getDeliveryTime(), boldFont, regularFont, infoBg, infoBorder);
				addInfoRow(infoTable, "Remarks", po.getRemarks(), boldFont, regularFont, infoBg, infoBorder);

				document.add(infoTable);
				document.add(new Paragraph("").setMarginBottom(8));
			}

			// =================================================================
			// SECTION 3 — ITEMS TABLE (Sr.No | Item Name | Qty | Unit)
			// =================================================================
			float[] colW = { 8f, 42f, 15f, 15f, 15f, 18f };

			Table mainTable = new Table(UnitValue.createPercentArray(colW));
			mainTable.setWidth(UnitValue.createPercentValue(100));

			String[] headers = { "SR.", "ITEM NAME", "QTY", "UNIT", "PRICE", "TOTAL" };
			TextAlignment[] hAlign = {
			    TextAlignment.CENTER, TextAlignment.LEFT, TextAlignment.RIGHT,
			    TextAlignment.CENTER, TextAlignment.RIGHT, TextAlignment.RIGHT
			};

			for (int i = 0; i < headers.length; i++) {
			    mainTable.addHeaderCell(new Cell()
			        .add(new Paragraph(headers[i])
			            .setFont(boldFont).setFontSize(9)
			            .setFontColor(ColorConstants.WHITE))
			        .setBackgroundColor(darkHeader)
			        .setTextAlignment(hAlign[i])
			        .setPadding(7)
			        .setBorder(new SolidBorder(borderColor, 0.5f)));
			}

			boolean alternate = false;
			int srNo = 1;
			double grandTotal = 0;

			for (SotManualPoDetailResponseDto d : poDto.getDetails()) {

			    Color bg = alternate ? altRow : ColorConstants.WHITE;
			    alternate = !alternate;

			    double qty = d.getQty();
			    double price = d.getPrice();
			    double total = qty * price;

			    grandTotal += total;

			    mainTable.addCell(sotCell(String.valueOf(srNo++), bg, regularFont,
			            TextAlignment.CENTER, borderColor));

			    mainTable.addCell(sotCell(sotNvl(d.getRawMaterialName()), bg, regularFont,
			            TextAlignment.LEFT, borderColor));

			    mainTable.addCell(sotCell(sotFmt(qty), bg, regularFont,
			            TextAlignment.RIGHT, borderColor));

			    mainTable.addCell(sotCell(sotNvl(d.getUnitName()), bg, regularFont,
			            TextAlignment.CENTER, borderColor));

			    mainTable.addCell(sotCell(sotFmt(price), bg, regularFont,
			            TextAlignment.RIGHT, borderColor));

			    mainTable.addCell(sotCell(sotFmt(total), bg, regularFont,
			            TextAlignment.RIGHT, borderColor));
			}

			// GRAND TOTAL
			mainTable.addCell(new Cell(1, 5)
			    .add(new Paragraph("GRAND TOTAL")
			        .setFont(boldFont).setFontSize(9))
			    .setTextAlignment(TextAlignment.RIGHT)
			    .setBackgroundColor(totalRowBg)
			    .setBorder(new SolidBorder(borderColor, 0.5f))
			    .setPadding(6));

			mainTable.addCell(new Cell()
			    .add(new Paragraph(sotFmt(grandTotal))
			        .setFont(boldFont).setFontSize(9))
			    .setTextAlignment(TextAlignment.RIGHT)
			    .setBackgroundColor(totalRowBg)
			    .setBorder(new SolidBorder(borderColor, 0.5f))
			    .setPadding(6));

			document.add(mainTable);

			// =================================================================
			// SECTION 4 — FOOTER (Signature + Timestamp)
			// =================================================================
			document.add(new Paragraph("").setMarginBottom(14));

			String timestamp = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " "
					+ java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"));

			float[] footW = { 50f, 50f };
			Table footer = new Table(UnitValue.createPercentArray(footW));
			footer.setWidth(UnitValue.createPercentValue(100));

			footer.addCell(
					new Cell().add(new Paragraph("Sign :  ___________________").setFont(regularFont).setFontSize(9))
							.setBorder(Border.NO_BORDER));

			footer.addCell(new Cell()
					.add(new Paragraph("Printed on: " + timestamp).setFont(regularFont).setFontSize(9)
							.setFontColor(labelGray).setTextAlignment(TextAlignment.RIGHT))
					.setBorder(Border.NO_BORDER));

			document.add(footer);
			document.close();

			return baos.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Failed to generate Manual PO PDF: " + e.getMessage(), e);
		}
	}

	@Override
	public SotManualPoResponseDto addOrUpdateManualPo(SotManualPoRequestDto request) {

		SotManualPoEntity po;
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		if(request.getIsPurchaseApprove()) {
			PurchaseRequestEntity pr = purchaseRequestRepository
					.findByIdAndIsDeleteFalse(request.getPurchaseApproveRequestId())
					.orElseThrow(() -> new RuntimeException(
							"Purchase Approve Request not found with id : " + request.getPurchaseApproveRequestId()));
		}
		
		boolean isNew = (request.getId() == null || request.getId() == 0 || request.getId() == -1);

		if (isNew) {
			po = new SotManualPoEntity();
			po.setVoucherNo(generateVoucherNo());
			po.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");

		} else {
			po = sotPoRepository.findById(request.getId())
					.orElseThrow(() -> new RuntimeException("Manual PO not found with id: " + request.getId()));

			List<SotManualPoDetailEntity> oldDetails = sotPoDetailRepository.findBySotManualPoId(po.getId());
			if (!oldDetails.isEmpty()) {
				sotPoDetailRepository.deleteAll(oldDetails);
				sotPoDetailRepository.flush();
			}

			if (request.getStatus() != null) {
				po.setStatus(request.getStatus());
			}
		}

		// ── Header fields ─────────────────────────────────────────────────────
		if (request.getPartyId() != null) {
			PartyMasterEntity party = partyRepository.findById(request.getPartyId())
					.orElseThrow(() -> new RuntimeException("Party not found with id: " + request.getPartyId()));
			po.setParty(party);
		}

		if (request.getUserId() != null) {
			po.setUser(userRepository.findById(request.getUserId()).orElse(null));
		}

		if (request.getVoucherDate() != null && !request.getVoucherDate().isEmpty()) {
			po.setVoucherDate(LocalDate.parse(request.getVoucherDate(), fmt));
		}

		po.setBillno(request.getBillno());
		po.setInvoicetype(request.getInvoicetype());
		po.setRemarks(request.getRemarks());
		po.setSubamount(request.getSubamount());
		po.setDiscountper(request.getDiscountper());
		po.setDiscountval(request.getDiscountval());
		po.setAdjustamount(request.getAdjustamount());
		po.setFinalamount(request.getFinalamount());
		po.setIsPurchaseApprove(request.getIsPurchaseApprove());
		po.setPurchaseApproveRequestId(request.getPurchaseApproveRequestId());

		po = sotPoRepository.save(po);

		// ── Details ───────────────────────────────────────────────────────────
		if (request.getDetails() != null && !request.getDetails().isEmpty()) {

			List<SotManualPoDetailEntity> detailList = new ArrayList<>();

			for (SotManualPoDetailRequestDto d : request.getDetails()) {

				SotManualPoDetailEntity detail = new SotManualPoDetailEntity();
				detail.setSotManualPo(po);

				RawMaterialMasterEntity rm = d.getRawMaterialId() != null
						? rawMaterialRepository.findById(d.getRawMaterialId()).orElse(null)
						: null;

				detail.setRawMaterial(rm);
				detail.setRawMaterialCat(rm != null ? rm.getRawMaterialCat() : null);

				detail.setUnit(d.getUnitId() != null ? unitMasterRepository.findById(d.getUnitId()).orElse(null)
						: (rm != null ? rm.getUnit() : null));

				detail.setParty(
						d.getPartyId() != null ? partyRepository.findById(d.getPartyId()).orElse(null) : po.getParty());

				detail.setHsccode(d.getHsccode());
				detail.setCgst(d.getCgst());
				detail.setSgst(d.getSgst());
				detail.setIgst(d.getIgst());
				detail.setQty(d.getQty());
				detail.setPrice(d.getPrice());
				detail.setOthercharge(d.getOthercharge());
				detail.setTotal(d.getTotal());

				detailList.add(detail);
			}

			sotPoDetailRepository.saveAll(detailList);
			sotPoDetailRepository.flush();
		}

		return convertPoToDto(po);
	}

	// ── Private helpers ───────────────────────────────────────────────────────

	/** Converts info fields of SotManualPoEntity into SotPoInfoResponseDto. */
	private SotPoInfoResponseDto convertPoInfoToDto(SotManualPoEntity po) {
		SotPoInfoResponseDto dto = new SotPoInfoResponseDto();
		dto.setSotPoId(po.getId());
		dto.setChallanNo(po.getChallanNo());
		dto.setEventName(po.getEventName());
		dto.setDeliveryVenue(po.getDeliveryVenue());
		dto.setDeliveryTime(po.getDeliveryTime());
		dto.setRemarks(po.getRemarks());
		return dto;
	}

	/** Adds a key-value row to the info block table; skips if value is blank. */
	private void addInfoRow(Table table, String label, String value, PdfFont boldFont, PdfFont regularFont, Color bg,
			Color border) {
		if (value == null || value.trim().isEmpty())
			return;

		table.addCell(new Cell()
				.add(new Paragraph(label).setFont(boldFont).setFontSize(9).setFontColor(new DeviceRgb(13, 71, 116)))
				.setBackgroundColor(bg).setBorder(new com.itextpdf.layout.borders.SolidBorder(border, 0.5f))
				.setPaddingTop(4).setPaddingBottom(4).setPaddingLeft(8));

		table.addCell(new Cell()
				.add(new Paragraph(value).setFont(regularFont).setFontSize(9).setFontColor(ColorConstants.BLACK))
				.setBackgroundColor(bg).setBorder(new com.itextpdf.layout.borders.SolidBorder(border, 0.5f))
				.setPaddingTop(4).setPaddingBottom(4).setPaddingLeft(8));
	}

	/** Null-safe non-empty check. */
	private boolean isNonEmpty(String s) {
		return s != null && !s.trim().isEmpty();
	}

	// ── Helper: convert to base unit ─────────────────────────────────────────
	private double convertToBase(double qty, UnitMasterEntity unit) {
		if (unit == null || qty == 0)
			return qty;
		try {
			// If unit has a parent (e.g. GM has parent KG)
			// then it's already base unit — return as is
			if (unit.getParentUnit() == null) {
				// This is parent unit (KG) — convert to child (GM)
				// KG → GM: multiply by equivalentValue
				UnitMasterEntity childUnit = unitMasterRepository.findByParentUnitIdAndIsDeleteFalse(unit.getId());
				if (childUnit != null && childUnit.getEquivalentValue() != null
						&& childUnit.getEquivalentValue() != 0) {
					return qty * childUnit.getEquivalentValue();
				}
			}
			// Already base unit (GM) — return as is
			return qty;
		} catch (Exception e) {
			return qty;
		}
	}

	private Double convertUnitQtyToParentQty(UnitMasterEntity unit, Double qty) {

		return BigDecimal.valueOf(qty).divide(BigDecimal.valueOf(unit.getEquivalentValue()), 2, RoundingMode.HALF_UP)
				.doubleValue();
	}

	@Override
	public boolean checkSotExists(Long eventId, Long userId) {
		return sotRepository.existsByEventIdAndUserIdAndIsDeleteFalse(eventId, userId);
	}

	@Override
	public List<MultipleSotResponseDto> getMultipleSotData(List<Long> sotIds, Long userId) {

		List<StoreOrderingTicketDetailEntity> data = sotDetailRepository.findAllBySotIds(sotIds, userId);

		MultipleSotResponseDto response = new MultipleSotResponseDto();
		response.setUserId(userId);

		Map<String, MultipleSotDetailsResponseDto> groupedData = new LinkedHashMap<>();

		for (StoreOrderingTicketDetailEntity detail : data) {

			String key = detail.getRawMaterial().getId() + "_"
					+ (detail.getParty() == null ? 0L : detail.getParty().getId());

			MultipleSotDetailsResponseDto dto = groupedData.get(key);

			if (dto == null) {

				dto = new MultipleSotDetailsResponseDto();

				dto.setRawMaterialId(detail.getRawMaterial().getId());
				dto.setRawMaterialName(detail.getRawMaterial().getNameEnglish());

				if (detail.getRawMaterialCat() != null) {
					dto.setRawMaterialCatId(detail.getRawMaterialCat().getId());
					dto.setRawMaterialCatName(detail.getRawMaterialCat().getNameEnglish());
				}

				if (detail.getParty() != null) {
					dto.setPartyId(detail.getParty().getId());
					dto.setPartyName(detail.getParty().getNameEnglish());
				}

				dto.setQty(0);
				dto.setAcceptedQty(0);
				dto.setReturnQty(0);
				dto.setAvailableStock(0);

				dto.setEventWiseSotDetails(new ArrayList<>());

				groupedData.put(key, dto);
			}

			// Sum accepted/return/stock only
//	        double acceptedQty = dto.getAcceptedQty() + detail.getAcceptedQty();
//			dto.setAcceptedQty(BigDecimal.valueOf(acceptedQty).setScale(2, RoundingMode.HALF_UP).doubleValue());

			double returnQty = dto.getReturnQty() + detail.getReturnQty();
			dto.setReturnQty(BigDecimal.valueOf(returnQty).setScale(2, RoundingMode.HALF_UP).doubleValue());

//			double availableStock = dto.getAvailableStock() + detail.getAvailableStock();
//	        dto.setAvailableStock(BigDecimal.valueOf(availableStock).setScale(2, RoundingMode.HALF_UP).doubleValue());

			// Preserve original event values
			EventWiseSotDetailsResponseDto eventDto = new EventWiseSotDetailsResponseDto();

			eventDto.setSotId(detail.getSot().getId());

			if (detail.getSot().getEvent() != null) {
				eventDto.setEventId(detail.getSot().getEvent().getId());
				eventDto.setEventName(detail.getSot().getEvent().getParty().getNameEnglish());
			}

			eventDto.setSotDetailId(detail.getId());

			if (detail.getUnit() != null) {
				eventDto.setUnitId(detail.getUnit().getId());
				eventDto.setUnitName(detail.getUnit().getNameEnglish());
			}

			eventDto.setQty(detail.getQty());
			eventDto.setAcceptedQty(
					BigDecimal.valueOf(detail.getAcceptedQty()).setScale(2, RoundingMode.HALF_UP).doubleValue());
			eventDto.setReturnQty(
					BigDecimal.valueOf(detail.getReturnQty()).setScale(2, RoundingMode.HALF_UP).doubleValue());
//	        eventDto.setAvailableStock(BigDecimal.valueOf(detail.getAvailableStock()).setScale(2, RoundingMode.HALF_UP).doubleValue());

			dto.getEventWiseSotDetails().add(eventDto);
		}

		// Calculate grouped Qty & Unit only.
		// Event-wise Qty & Unit remain unchanged.
		for (MultipleSotDetailsResponseDto dto : groupedData.values()) {

			Map<Long, Double> unitQtyMap = new LinkedHashMap<>();
			Map<Long, Double> unitAcceptedQtyMap = new LinkedHashMap<>();

			for (EventWiseSotDetailsResponseDto eventDto : dto.getEventWiseSotDetails()) {
				unitQtyMap.merge(eventDto.getUnitId(), eventDto.getQty(), Double::sum);
				unitAcceptedQtyMap.merge(eventDto.getUnitId(), eventDto.getAcceptedQty(), Double::sum);
			}

			Map<Long, Double> finalQtyMap = null;
			Map<Long, Double> finalAcceptedQtyMap = null;

			if (unitQtyMap.size() == 1) {
				finalQtyMap = unitQtyMap;
				finalAcceptedQtyMap = unitAcceptedQtyMap;
			} else {
				finalQtyMap = eventRawMaterialServiceImpl.convertToParentUnit(unitQtyMap);
				finalAcceptedQtyMap = eventRawMaterialServiceImpl.convertToParentUnit(unitAcceptedQtyMap);
			}

			double totalQty = 0;
			double totalAcceptedQty = 0;

			Long displayUnitId = null;
			String displayUnitName = "";

			for (Map.Entry<Long, Double> entry : finalQtyMap.entrySet()) {

				totalQty += entry.getValue();

				if (displayUnitId == null) {
					displayUnitId = entry.getKey();

					UnitMasterEntity unit = unitMasterRepository.findById(displayUnitId).orElse(null);

					if (unit != null) {
						displayUnitName = unit.getNameEnglish();
					}
				}
			}

			for (Double value : finalAcceptedQtyMap.values()) {
				totalAcceptedQty += value;
			}

			dto.setQty(BigDecimal.valueOf(totalQty).setScale(2, RoundingMode.HALF_UP).doubleValue());
			dto.setUnitId(displayUnitId);
			dto.setUnitName(displayUnitName);
			dto.setAcceptedQty(BigDecimal.valueOf(totalAcceptedQty).setScale(2, RoundingMode.HALF_UP).doubleValue());

			// Available Stock
			double availableStock = calculateAvailableStock(dto.getRawMaterialId());
			UnitMasterEntity stockUnit = getUnitByRawMaterialId(dto.getRawMaterialId());

			if (stockUnit != null) {

				double acceptedQtyInStockUnit = totalAcceptedQty;

				// If grouped unit and stock unit are different, convert accepted qty
				if (dto.getUnitId() != null && !stockUnit.getId().equals(dto.getUnitId())) {

					Map<Long, Double> qtyMap = new LinkedHashMap<>();
					qtyMap.put(dto.getUnitId(), totalAcceptedQty);

					Map<Long, Double> convertedMap = eventRawMaterialServiceImpl.convertToParentUnit(qtyMap);

					acceptedQtyInStockUnit = convertedMap.getOrDefault(stockUnit.getId(), totalAcceptedQty);
				}
				dto.setAvailableStockUnitId(stockUnit.getId());
				dto.setAvailableStockUnitName(stockUnit.getNameEnglish());

				dto.setAvailableStock(BigDecimal.valueOf(availableStock - acceptedQtyInStockUnit)
						.setScale(2, RoundingMode.HALF_UP).doubleValue());
			}
		}

		response.setDetails(new ArrayList<>(groupedData.values()));

		return Collections.singletonList(response);
	}

	@Override
	@Transactional
	public List<SotResponseDto> addUpdateMultipleSotData(List<MultipleSotResponseDto> request) {

		List<SotResponseDto> responseList = new ArrayList<>();

		if (request == null || request.isEmpty()) {
			return responseList;
		}

		for (MultipleSotResponseDto userData : request) {

			Long userId = userData.getUserId();

			UserMasterEntity user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("User not found : " + userId));

			// To avoid updating the same SOT multiple times
			Set<Long> updatedSotIds = new HashSet<>();

			if (userData.getDetails() == null) {
				continue;
			}

			for (MultipleSotDetailsResponseDto detail : userData.getDetails()) {

				if (detail.getEventWiseSotDetails() == null) {
					continue;
				}

				for (EventWiseSotDetailsResponseDto eventDetail : detail.getEventWiseSotDetails()) {

					StoreOrderingTicketDetailEntity sotDetail = sotDetailRepository
							.findById(eventDetail.getSotDetailId()).orElseThrow(() -> new RuntimeException(
									"SOT Detail not found : " + eventDetail.getSotDetailId()));

					sotDetail.setParty(partyRepository.findByIdAndIsDeleteFalse(detail.getPartyId()).orElse(null));
					sotDetail.setAcceptedQty(eventDetail.getAcceptedQty());
					sotDetailRepository.save(sotDetail);

					StoreOrderingTicketEntity sot = sotDetail.getSot();

					if (!updatedSotIds.contains(sot.getId())) {

						sot.setStatus("ACCEPTED");
						sot.setUser(user);

						sot = sotRepository.save(sot);

						responseList.add(convertToDto(sot));

						updatedSotIds.add(sot.getId());
					}
				}
			}
		}

		return responseList;
	}

	@Override
	public Boolean deleteSotDetail(List<Long> sotDetailIds) {
		List<StoreOrderingTicketDetailEntity> toDeleted = new ArrayList<>();

		sotDetailIds.stream().forEach(id -> {
			StoreOrderingTicketDetailEntity entity = sotDetailRepository.findById(id).orElse(null);

			if (entity != null) {
				toDeleted.add(entity);
			}
		});

		sotDetailRepository.deleteAll(toDeleted);

		return true;
	}

	private UnitMasterEntity getUnitByRawMaterialId(Long unitId) {
		RawMaterialMasterEntity rawMaterial = rawMaterialRepository.findByIdAndIsDeleteFalse(unitId);

		if (rawMaterial == null) {
			return null;
		}

		return rawMaterial.getUnit();
	}
	
	private void addDetailCell(Table table, String label, String value,
            Color bg, Color labelColor,
            PdfFont boldFont, PdfFont regularFont,
            boolean isFirst, boolean isLast) {

        com.itextpdf.layout.borders.Border border =
                new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(180, 200, 220), 0.5f);

        Cell cell = new Cell()
                .add(new Paragraph(label)
                        .setFont(regularFont).setFontSize(9)
                        .setFontColor(labelColor).setMarginBottom(2))
                .add(new Paragraph(value)
                        .setFont(boldFont).setFontSize(10)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(bg)
                .setPadding(10)
                .setBorder(border)
                .setBorderLeft( isFirst ? border : Border.NO_BORDER)
                .setBorderRight(isLast  ? border : border);
        table.addCell(cell);
    }
}