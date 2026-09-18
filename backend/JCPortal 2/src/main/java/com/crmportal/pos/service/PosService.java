package com.crmportal.pos.service;

import java.util.List;
import com.crmportal.pos.dto.PosDto;
import com.crmportal.pos.dto.PosStatsDto;
import com.crmportal.pos.entity.PosCategoryEntity;
import com.crmportal.pos.entity.PosFloorEntity;
import com.crmportal.pos.entity.PosInvoiceEntity;
import com.crmportal.pos.entity.PosItemEntity;
import com.crmportal.pos.entity.PosKotEntity;
import com.crmportal.pos.entity.PosOrderEntity;
import com.crmportal.pos.entity.PosReservationEntity;
import com.crmportal.pos.entity.PosTableEntity;
import com.crmportal.pos.entity.PosTaxEntity;

public interface PosService {

    PosStatsDto getStats();

    // Taxes
    List<PosTaxEntity> getAllTaxes();
    PosTaxEntity saveTax(PosTaxEntity tax);
    void deleteTax(Long id);

    // Categories
    List<PosCategoryEntity> getAllCategories();
    PosCategoryEntity saveCategory(PosCategoryEntity category);
    void deleteCategory(Long id);

    // Floors
    List<PosFloorEntity> getAllFloors();
    PosFloorEntity saveFloor(PosFloorEntity floor);
    void deleteFloor(Long id);

    // Tables
    List<PosTableEntity> getAllTables();
    PosTableEntity saveTable(PosTableEntity table);
    void deleteTable(Long id);
    void updateTableStatus(Long tableId, String status);

    // Items
    List<PosItemEntity> getAllItems();
    PosItemEntity saveItem(PosItemEntity item);
    void deleteItem(Long id);

    // Orders
    PosOrderEntity createOrder(PosDto.OrderCreateRequest request);
    PosOrderEntity getOrder(Long id);
    List<PosOrderEntity> getActiveOrders();
    PosOrderEntity updateOrderItems(Long orderId, List<PosDto.OrderItemUpdate> items);
    PosOrderEntity updateOrderDiscount(Long orderId, PosDto.DiscountUpdate discount);
    PosOrderEntity moveTable(Long orderId, Long newTableId);
    void cancelOrder(Long orderId);

    // KOTs
    PosKotEntity sendKot(Long orderId, List<PosDto.OrderItemUpdate> items);
    List<PosKotEntity> getActiveKots();
    PosKotEntity updateKotStatus(Long kotId, String status);

    // Invoices
    PosInvoiceEntity generateInvoice(Long orderId);
    PosInvoiceEntity payInvoice(Long invoiceId, String paymentMode);
    List<PosInvoiceEntity> getAllInvoices();

    // Reservations
    List<PosReservationEntity> getReservations(String date, String status);
    PosReservationEntity saveReservation(PosReservationEntity reservation);
    PosOrderEntity seatReservation(Long resId, Long tableId);
}
