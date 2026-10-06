package com.crmportal.service;

import java.util.List;

import com.crmportal.dto.PosDto;
import com.crmportal.dto.PosStatsDto;
import com.crmportal.entity.PosCategoryEntity;
import com.crmportal.entity.PosFloorEntity;
import com.crmportal.entity.PosInvoiceEntity;
import com.crmportal.entity.PosItemEntity;
import com.crmportal.entity.PosKotEntity;
import com.crmportal.entity.PosOrderEntity;
import com.crmportal.entity.PosReservationEntity;
import com.crmportal.entity.PosTableEntity;
import com.crmportal.entity.PosTaxEntity;

public interface PosService {

	PosStatsDto getStats(Long userId);

	PosStatsDto getStats();

	// Taxes
	List<PosTaxEntity> getAllTaxes(Long userId);

	List<PosTaxEntity> getAllTaxes();

	PosTaxEntity saveTax(PosTaxEntity tax, Long userId);

	PosTaxEntity saveTax(PosTaxEntity tax);

	void deleteTax(Long id);

	// Categories
	List<PosCategoryEntity> getAllCategories(Long userId);

	List<PosCategoryEntity> getAllCategories();

	PosCategoryEntity saveCategory(PosCategoryEntity category, Long userId);

	PosCategoryEntity saveCategory(PosCategoryEntity category);

	void deleteCategory(Long id);

	// Floors
	List<PosFloorEntity> getAllFloors(Long userId);

	List<PosFloorEntity> getAllFloors();

	PosFloorEntity saveFloor(PosFloorEntity floor, Long userId);

	PosFloorEntity saveFloor(PosFloorEntity floor);

	void deleteFloor(Long id);

	// Tables
	List<PosTableEntity> getAllTables(Long userId);

	List<PosTableEntity> getAllTables();

	PosTableEntity saveTable(PosTableEntity table, Long userId);

	PosTableEntity saveTable(PosTableEntity table);

	void deleteTable(Long id);

	void updateTableStatus(Long tableId, String status);

	// Items
	List<PosItemEntity> getAllItems(Long userId);

	List<PosItemEntity> getAllItems();

	PosItemEntity saveItem(PosItemEntity item, Long userId);

	PosItemEntity saveItem(PosItemEntity item);

	void deleteItem(Long id);

	// Orders
	PosOrderEntity createOrder(PosDto.OrderCreateRequest request, Long userId);

	PosOrderEntity createOrder(PosDto.OrderCreateRequest request);

	PosOrderEntity getOrder(Long id);

	List<PosOrderEntity> getActiveOrders(Long userId);

	List<PosOrderEntity> getActiveOrders();

	PosOrderEntity updateOrderItems(Long orderId, List<PosDto.OrderItemUpdate> items);

	PosOrderEntity updateOrderDiscount(Long orderId, PosDto.DiscountUpdate discount);

	PosOrderEntity moveTable(Long orderId, Long newTableId);

	void cancelOrder(Long orderId);

	// KOTs
	PosKotEntity sendKot(Long orderId, List<PosDto.OrderItemUpdate> items, Long userId);

	PosKotEntity sendKot(Long orderId, List<PosDto.OrderItemUpdate> items);

	List<PosKotEntity> getActiveKots(Long userId);

	List<PosKotEntity> getActiveKots();

	PosKotEntity updateKotStatus(Long kotId, String status);

	// Invoices
	PosInvoiceEntity generateInvoice(Long orderId, Long userId);

	PosInvoiceEntity generateInvoice(Long orderId);

	PosInvoiceEntity payInvoice(Long invoiceId, String paymentMode, Long userId);

	PosInvoiceEntity payInvoice(Long invoiceId, String paymentMode);

	List<PosInvoiceEntity> getAllInvoices(Long userId);

	List<PosInvoiceEntity> getAllInvoices();

	// Reservations
	List<PosReservationEntity> getReservations(String date, String status, Long userId);

	List<PosReservationEntity> getReservations(String date, String status);

	PosReservationEntity saveReservation(PosReservationEntity reservation, Long userId);

	PosReservationEntity saveReservation(PosReservationEntity reservation);

	PosOrderEntity seatReservation(Long resId, Long tableId, Long userId);

	PosOrderEntity seatReservation(Long resId, Long tableId);
}
