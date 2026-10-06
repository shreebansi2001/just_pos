package com.crmportal.pos.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.crmportal.pos.dto.PosDto;
import com.crmportal.pos.dto.PosStatsDto;
import com.crmportal.pos.entity.PosCategoryEntity;
import com.crmportal.pos.entity.PosFloorEntity;
import com.crmportal.pos.entity.PosInvoiceEntity;
import com.crmportal.pos.entity.PosItemEntity;
import com.crmportal.pos.entity.PosKotEntity;
import com.crmportal.pos.entity.PosKotItemEntity;
import com.crmportal.pos.entity.PosOrderActivityEntity;
import com.crmportal.pos.entity.PosOrderEntity;
import com.crmportal.pos.entity.PosOrderItemEntity;
import com.crmportal.pos.entity.PosReservationEntity;
import com.crmportal.pos.entity.PosTableEntity;
import com.crmportal.pos.entity.PosTaxEntity;
import com.crmportal.pos.repository.PosCategoryRepository;
import com.crmportal.pos.repository.PosFloorRepository;
import com.crmportal.pos.repository.PosInvoiceRepository;
import com.crmportal.pos.repository.PosItemRepository;
import com.crmportal.pos.repository.PosKotRepository;
import com.crmportal.pos.repository.PosOrderActivityRepository;
import com.crmportal.pos.repository.PosOrderItemRepository;
import com.crmportal.pos.repository.PosOrderRepository;
import com.crmportal.pos.repository.PosReservationRepository;
import com.crmportal.pos.repository.PosTableRepository;
import com.crmportal.pos.repository.PosTaxRepository;
import com.crmportal.pos.service.PosService;

@Service
@Transactional
public class PosServiceImpl implements PosService {

    @Autowired
    private PosTaxRepository taxRepository;

    @Autowired
    private PosCategoryRepository categoryRepository;

    @Autowired
    private PosFloorRepository floorRepository;

    @Autowired
    private PosTableRepository tableRepository;

    @Autowired
    private PosItemRepository itemRepository;

    @Autowired
    private PosOrderRepository orderRepository;

    @Autowired
    private PosOrderItemRepository orderItemRepository;

    @Autowired
    private PosKotRepository kotRepository;

    @Autowired
    private PosInvoiceRepository invoiceRepository;

    @Autowired
    private PosReservationRepository reservationRepository;

    @Autowired
    private PosOrderActivityRepository orderActivityRepository;

    private static int orderSequence = 100;
    private static int kotSequence = 100;
    private static int invoiceSequence = 100;

    private Long resolveUserId(Long userId) {
        return (userId != null && userId > 0) ? userId : 1L;
    }

    private void recordActivity(Long userId, Long orderId, String role, String action, String remarks) {
        try {
            PosOrderActivityEntity act = new PosOrderActivityEntity();
            act.setUserId(resolveUserId(userId));
            act.setOrderId(orderId);
            act.setRoleName(role != null ? role : "Cashier");
            act.setAction(action);
            act.setRemarks(remarks);
            act.setCreatedAt(new Date());
            orderActivityRepository.save(act);
        } catch (Exception e) {
            // graceful non-blocking activity log
        }
    }

    @Override
    public PosStatsDto getStats(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosTableEntity> tables = tableRepository.findByUserIdAndActiveTrue(uid);
        if (tables.isEmpty() && uid == 1L) {
            tables = tableRepository.findByActiveTrue();
        }
        int free = 0;
        int inUse = 0;
        for (PosTableEntity t : tables) {
            if ("available".equalsIgnoreCase(t.getStatus())) {
                free++;
            } else {
                inUse++;
            }
        }
        List<PosKotEntity> activeKots = kotRepository.findByUserIdAndStatusNot(uid, "served");
        if (activeKots.isEmpty() && uid == 1L) {
            activeKots = kotRepository.findByStatusNot("served");
        }
        List<PosInvoiceEntity> invoices = invoiceRepository.findByUserId(uid);
        if (invoices.isEmpty() && uid == 1L) {
            invoices = invoiceRepository.findAll();
        }
        double sales = 0.0;
        for (PosInvoiceEntity inv : invoices) {
            if ("paid".equalsIgnoreCase(inv.getStatus()) && inv.getTotal() != null) {
                sales += inv.getTotal();
            }
        }
        return new PosStatsDto(free, inUse, activeKots.size(), sales);
    }

    @Override
    public PosStatsDto getStats() {
        return getStats(1L);
    }

    // Taxes
    @Override
    public List<PosTaxEntity> getAllTaxes(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosTaxEntity> taxes = taxRepository.findByUserId(uid);
        if (taxes.isEmpty() && uid == 1L) {
            return taxRepository.findAll();
        }
        return taxes;
    }

    @Override
    public List<PosTaxEntity> getAllTaxes() {
        return getAllTaxes(1L);
    }

    @Override
    public PosTaxEntity saveTax(PosTaxEntity tax, Long userId) {
        if (tax.getId() != null && tax.getId() > 10000000000L) {
            tax.setId(null);
        }
        if (tax.getTaxName() == null || tax.getTaxName().trim().isEmpty()) {
            tax.setTaxName("Tax " + System.currentTimeMillis());
        }
        if (tax.getPercentage() == null) {
            tax.setPercentage(0.0);
        }
        if (tax.getUserId() == null) {
            tax.setUserId(resolveUserId(userId));
        }
        return taxRepository.save(tax);
    }

    @Override
    public PosTaxEntity saveTax(PosTaxEntity tax) {
        return saveTax(tax, 1L);
    }

    @Override
    public void deleteTax(Long id) {
        taxRepository.deleteById(id);
    }

    // Categories
    @Override
    public List<PosCategoryEntity> getAllCategories(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosCategoryEntity> list = categoryRepository.findByUserIdOrderBySortOrderAsc(uid);
        if (list.isEmpty() && uid == 1L) {
            return categoryRepository.findAllByOrderBySortOrderAsc();
        }
        return list;
    }

    @Override
    public List<PosCategoryEntity> getAllCategories() {
        return getAllCategories(1L);
    }

    @Override
    public PosCategoryEntity saveCategory(PosCategoryEntity category, Long userId) {
        if (category.getId() != null && category.getId() > 10000000000L) {
            category.setId(null);
        }
        if (category.getCode() == null || category.getCode().trim().length() == 0) {
            category.setCode("CAT" + System.currentTimeMillis());
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            category.setName("Category " + category.getCode());
        }
        if (category.getUserId() == null) {
            category.setUserId(resolveUserId(userId));
        }
        return categoryRepository.save(category);
    }

    @Override
    public PosCategoryEntity saveCategory(PosCategoryEntity category) {
        return saveCategory(category, 1L);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // Floors
    @Override
    public List<PosFloorEntity> getAllFloors(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosFloorEntity> list = floorRepository.findByUserIdOrderBySortOrderAsc(uid);
        if (list.isEmpty() && uid == 1L) {
            return floorRepository.findAllByOrderBySortOrderAsc();
        }
        return list;
    }

    @Override
    public List<PosFloorEntity> getAllFloors() {
        return getAllFloors(1L);
    }

    @Override
    public PosFloorEntity saveFloor(PosFloorEntity floor, Long userId) {
        if (floor.getId() != null && floor.getId() > 10000000000L) {
            floor.setId(null);
        }
        if (floor.getCode() == null || floor.getCode().trim().length() == 0) {
            floor.setCode("FLR" + System.currentTimeMillis());
        }
        if (floor.getShortcode() == null || floor.getShortcode().trim().length() == 0) {
            floor.setShortcode(floor.getCode().substring(0, Math.min(4, floor.getCode().length())));
        }
        if (floor.getName() == null || floor.getName().trim().isEmpty()) {
            floor.setName("Floor " + floor.getCode());
        }
        if (floor.getUserId() == null) {
            floor.setUserId(resolveUserId(userId));
        }
        return floorRepository.save(floor);
    }

    @Override
    public PosFloorEntity saveFloor(PosFloorEntity floor) {
        return saveFloor(floor, 1L);
    }

    @Override
    public void deleteFloor(Long id) {
        floorRepository.deleteById(id);
    }

    // Tables
    @Override
    public List<PosTableEntity> getAllTables(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosTableEntity> list = tableRepository.findByUserId(uid);
        if (list.isEmpty() && uid == 1L) {
            return tableRepository.findAll();
        }
        return list;
    }

    @Override
    public List<PosTableEntity> getAllTables() {
        return getAllTables(1L);
    }

    @Override
    public PosTableEntity saveTable(PosTableEntity table, Long userId) {
        if (table.getId() != null && table.getId() > 10000000000L) {
            table.setId(null);
        }
        if (table.getCode() == null || table.getCode().trim().length() == 0) {
            table.setCode("TBL" + System.currentTimeMillis());
        }
        if (table.getShortcode() == null || table.getShortcode().trim().length() == 0) {
            table.setShortcode("T" + (table.getName() != null ? table.getName().replaceAll("[^0-9]", "") : ""));
        }
        if (table.getName() == null || table.getName().trim().isEmpty()) {
            table.setName("Table " + table.getCode());
        }
        if (table.getCapacity() == null || table.getCapacity() <= 0) {
            table.setCapacity(4);
        }
        if (table.getUserId() == null) {
            table.setUserId(resolveUserId(userId));
        }
        return tableRepository.save(table);
    }

    @Override
    public PosTableEntity saveTable(PosTableEntity table) {
        return saveTable(table, 1L);
    }

    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    @Override
    public void updateTableStatus(Long tableId, String status) {
        Optional<PosTableEntity> opt = tableRepository.findById(tableId);
        if (opt.isPresent()) {
            PosTableEntity t = opt.get();
            t.setStatus(status);
            if ("available".equalsIgnoreCase(status)) {
                t.setCurrentOrderId(null);
                t.setCurrentOrderCode(null);
            }
            tableRepository.save(t);
        }
    }

    // Items
    @Override
    public List<PosItemEntity> getAllItems(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosItemEntity> list = itemRepository.findByUserId(uid);
        if (list.isEmpty() && uid == 1L) {
            return itemRepository.findAll();
        }
        return list;
    }

    @Override
    public List<PosItemEntity> getAllItems() {
        return getAllItems(1L);
    }

    @Override
    public PosItemEntity saveItem(PosItemEntity item, Long userId) {
        if (item.getId() != null && item.getId() > 10000000000L) {
            item.setId(null);
        }
        if (item.getCode() == null || item.getCode().trim().length() == 0) {
            item.setCode("ITM" + System.currentTimeMillis());
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            item.setName("Item " + item.getCode());
        }
        if (item.getPrice() == null) {
            item.setPrice(0.0);
        }
        if (item.getUserId() == null) {
            item.setUserId(resolveUserId(userId));
        }
        return itemRepository.save(item);
    }

    @Override
    public PosItemEntity saveItem(PosItemEntity item) {
        return saveItem(item, 1L);
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    // Orders
    @Override
    public PosOrderEntity createOrder(PosDto.OrderCreateRequest request, Long userId) {
        Long uid = request.getUserId() != null ? request.getUserId() : resolveUserId(userId);
        PosOrderEntity order = new PosOrderEntity();
        order.setUserId(uid);
        order.setCreatedByUserId(request.getCreatedByUserId() != null ? request.getCreatedByUserId() : uid);
        order.setWaiterId(request.getWaiterId());
        order.setOutletCode(request.getOutletCode() != null ? request.getOutletCode() : "MAIN");
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setOrderCode("ORD" + (++orderSequence));
        order.setOrderType(request.getType() != null ? request.getType() : "dine-in");
        order.setTableId(request.getTableId());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setReservationId(request.getReservationId());
        order.setStatus("open");

        if (request.getTableId() != null) {
            Optional<PosTableEntity> topt = tableRepository.findById(request.getTableId());
            if (topt.isPresent()) {
                PosTableEntity t = topt.get();
                order.setTableLabel(t.getShortcode());
                t.setStatus("occupied");
                t.setCurrentOrderId(order.getId());
                t.setCurrentOrderCode(order.getOrderCode());
                tableRepository.save(t);
            }
        }

        PosOrderEntity saved = orderRepository.save(order);
        if (request.getTableId() != null) {
            Optional<PosTableEntity> topt = tableRepository.findById(request.getTableId());
            if (topt.isPresent()) {
                PosTableEntity t = topt.get();
                t.setCurrentOrderId(saved.getId());
                tableRepository.save(t);
            }
        }

        recordActivity(uid, saved.getId(), "Captain", "ORDER_CREATED", "Order " + saved.getOrderCode() + " created for " + saved.getOrderType());
        return saved;
    }

    @Override
    public PosOrderEntity createOrder(PosDto.OrderCreateRequest request) {
        return createOrder(request, 1L);
    }

    @Override
    public PosOrderEntity getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<PosOrderEntity> getActiveOrders(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosOrderEntity> orders = orderRepository.findByUserIdAndStatus(uid, "open");
        if (orders.isEmpty() && uid == 1L) {
            return orderRepository.findByStatus("open");
        }
        return orders;
    }

    @Override
    public List<PosOrderEntity> getActiveOrders() {
        return getActiveOrders(1L);
    }

    @Override
    public PosOrderEntity updateOrderItems(Long orderId, List<PosDto.OrderItemUpdate> items) {
        PosOrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;

        order.getItems().clear();
        if (items != null) {
            for (PosDto.OrderItemUpdate dto : items) {
                PosOrderItemEntity entity = new PosOrderItemEntity();
                entity.setOrder(order);
                entity.setUserId(order.getUserId());
                entity.setItemId(dto.getItemId());
                entity.setItemKey(dto.getItemKey());
                entity.setItemName(dto.getItemName());
                entity.setPrice(dto.getPrice() != null ? dto.getPrice() : 0.0);
                entity.setQty(dto.getQty() != null ? dto.getQty() : 0);
                entity.setSentQty(dto.getSentQty() != null ? dto.getSentQty() : 0);
                entity.setUnitMode(dto.getUnitMode() != null ? dto.getUnitMode() : "plate");
                entity.setWeightKg(dto.getWeightKg());
                entity.setVariantLabel(dto.getVariantLabel());
                entity.setNote(dto.getNote());
                order.getItems().add(entity);
            }
        }
        return orderRepository.save(order);
    }

    @Override
    public PosOrderEntity updateOrderDiscount(Long orderId, PosDto.DiscountUpdate discount) {
        PosOrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;
        if (discount != null) {
            order.setDiscountType(discount.getDiscountType());
            order.setDiscountVal(discount.getDiscountVal());
        }
        return orderRepository.save(order);
    }

    @Override
    public PosOrderEntity moveTable(Long orderId, Long newTableId) {
        PosOrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;

        String oldLabel = order.getTableLabel();
        if (order.getTableId() != null) {
            Optional<PosTableEntity> oldOpt = tableRepository.findById(order.getTableId());
            if (oldOpt.isPresent()) {
                PosTableEntity oldTable = oldOpt.get();
                oldTable.setStatus("available");
                oldTable.setCurrentOrderId(null);
                oldTable.setCurrentOrderCode(null);
                tableRepository.save(oldTable);
            }
        }

        Optional<PosTableEntity> newOpt = tableRepository.findById(newTableId);
        if (newOpt.isPresent()) {
            PosTableEntity newTable = newOpt.get();
            newTable.setStatus("occupied");
            newTable.setCurrentOrderId(order.getId());
            newTable.setCurrentOrderCode(order.getOrderCode());
            tableRepository.save(newTable);
            order.setTableId(newTable.getId());
            order.setTableLabel(newTable.getShortcode());
        }

        recordActivity(order.getUserId(), order.getId(), "Captain", "TABLE_MOVED", "Order moved from " + oldLabel + " to " + order.getTableLabel());
        return orderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long orderId) {
        PosOrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return;
        order.setStatus("cancelled");
        if (order.getTableId() != null) {
            Optional<PosTableEntity> topt = tableRepository.findById(order.getTableId());
            if (topt.isPresent()) {
                PosTableEntity t = topt.get();
                t.setStatus("available");
                t.setCurrentOrderId(null);
                t.setCurrentOrderCode(null);
                tableRepository.save(t);
            }
        }
        recordActivity(order.getUserId(), order.getId(), "Manager", "ORDER_CANCELLED", "Order " + order.getOrderCode() + " cancelled");
        orderRepository.save(order);
    }

    // KOTs
    @Override
    public PosKotEntity sendKot(Long orderId, List<PosDto.OrderItemUpdate> items, Long userId) {
        PosOrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;

        Long uid = order.getUserId() != null ? order.getUserId() : resolveUserId(userId);
        PosKotEntity kot = new PosKotEntity();
        kot.setUserId(uid);
        kot.setCreatedByUserId(uid);
        kot.setKotCode("KOT" + (++kotSequence));
        kot.setOrderId(order.getId());
        kot.setOrderCode(order.getOrderCode());
        kot.setTableLabel(order.getTableLabel());
        kot.setOrderType(order.getOrderType());
        kot.setStatus("new");

        if (items != null) {
            for (PosDto.OrderItemUpdate item : items) {
                PosKotItemEntity kitem = new PosKotItemEntity();
                kitem.setKot(kot);
                kitem.setUserId(uid);
                kitem.setItemName(item.getItemName());
                kitem.setQty(item.getQty() != null ? item.getQty() : 1);
                kitem.setUnitMode(item.getUnitMode() != null ? item.getUnitMode() : "plate");
                kitem.setWeightKg(item.getWeightKg());
                kitem.setVariantLabel(item.getVariantLabel());
                kot.getItems().add(kitem);
            }
        }
        PosKotEntity saved = kotRepository.save(kot);
        recordActivity(uid, order.getId(), "Kitchen", "KOT_SENT", "KOT " + saved.getKotCode() + " sent to kitchen");
        return saved;
    }

    @Override
    public PosKotEntity sendKot(Long orderId, List<PosDto.OrderItemUpdate> items) {
        return sendKot(orderId, items, 1L);
    }

    @Override
    public List<PosKotEntity> getActiveKots(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosKotEntity> list = kotRepository.findByUserIdAndStatusNot(uid, "served");
        if (list.isEmpty() && uid == 1L) {
            return kotRepository.findAll();
        }
        return list;
    }

    @Override
    public List<PosKotEntity> getActiveKots() {
        return getActiveKots(1L);
    }

    @Override
    public PosKotEntity updateKotStatus(Long kotId, String status) {
        PosKotEntity kot = kotRepository.findById(kotId).orElse(null);
        if (kot != null) {
            kot.setStatus(status);
            PosKotEntity saved = kotRepository.save(kot);
            recordActivity(saved.getUserId(), saved.getOrderId(), "Kitchen", "KOT_STATUS_UPDATE", "KOT " + saved.getKotCode() + " status changed to " + status);
            return saved;
        }
        return null;
    }

    // Invoices
    @Override
    public PosInvoiceEntity generateInvoice(Long orderId, Long userId) {
        PosOrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;

        Long uid = order.getUserId() != null ? order.getUserId() : resolveUserId(userId);
        double sub = 0.0;
        for (PosOrderItemEntity item : order.getItems()) {
            sub += (item.getPrice() * item.getQty());
        }

        double disc = 0.0;
        if (order.getDiscountVal() != null && order.getDiscountVal() > 0) {
            if ("pct".equalsIgnoreCase(order.getDiscountType())) {
                disc = sub * (order.getDiscountVal() / 100.0);
            } else {
                disc = Math.min(order.getDiscountVal(), sub);
            }
        }

        double taxable = Math.max(sub - disc, 0.0);
        double cgst = taxable * 0.025;
        double sgst = taxable * 0.025;
        double total = taxable + cgst + sgst;

        PosInvoiceEntity invoice = new PosInvoiceEntity();
        invoice.setUserId(uid);
        invoice.setCreatedByUserId(uid);
        invoice.setOutletCode(order.getOutletCode() != null ? order.getOutletCode() : "MAIN");
        invoice.setInvoiceCode("INV" + (++invoiceSequence));
        invoice.setOrderId(order.getId());
        invoice.setOrderCode(order.getOrderCode());
        invoice.setOrderType(order.getOrderType());
        invoice.setTableLabel(order.getTableLabel());
        invoice.setCustomerName(order.getCustomerName());
        invoice.setCustomerPhone(order.getCustomerPhone());
        invoice.setSubtotal(sub);
        invoice.setDiscount(disc);
        invoice.setCgst(cgst);
        invoice.setSgst(sgst);
        invoice.setTotal(total);
        invoice.setStatus("unpaid");

        if (order.getTableId() != null) {
            Optional<PosTableEntity> topt = tableRepository.findById(order.getTableId());
            if (topt.isPresent()) {
                PosTableEntity t = topt.get();
                t.setStatus("billed");
                tableRepository.save(t);
            }
        }

        PosInvoiceEntity saved = invoiceRepository.save(invoice);
        recordActivity(uid, order.getId(), "Cashier", "INVOICE_GENERATED", "Invoice " + saved.getInvoiceCode() + " generated. Total: ₹" + saved.getTotal());
        return saved;
    }

    @Override
    public PosInvoiceEntity generateInvoice(Long orderId) {
        return generateInvoice(orderId, 1L);
    }

    @Override
    public PosInvoiceEntity payInvoice(Long invoiceId, String paymentMode, Long userId) {
        PosInvoiceEntity invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null) return null;

        Long uid = invoice.getUserId() != null ? invoice.getUserId() : resolveUserId(userId);
        invoice.setStatus("paid");
        invoice.setPaymentMode(paymentMode);
        invoice.setCashierId(uid);
        invoiceRepository.save(invoice);

        if (invoice.getOrderId() != null) {
            PosOrderEntity order = orderRepository.findById(invoice.getOrderId()).orElse(null);
            if (order != null) {
                order.setStatus("completed");
                orderRepository.save(order);

                if (order.getTableId() != null) {
                    Optional<PosTableEntity> topt = tableRepository.findById(order.getTableId());
                    if (topt.isPresent()) {
                        PosTableEntity t = topt.get();
                        t.setStatus("cleaning");
                        t.setCurrentOrderId(null);
                        t.setCurrentOrderCode(null);
                        tableRepository.save(t);
                    }
                }

                if (order.getReservationId() != null) {
                    Optional<PosReservationEntity> ropt = reservationRepository.findById(order.getReservationId());
                    if (ropt.isPresent()) {
                        PosReservationEntity r = ropt.get();
                        r.setStatus("completed");
                        reservationRepository.save(r);
                    }
                }
                recordActivity(uid, order.getId(), "Cashier", "INVOICE_PAID", "Invoice " + invoice.getInvoiceCode() + " settled via " + paymentMode);
            }
        }
        return invoice;
    }

    @Override
    public PosInvoiceEntity payInvoice(Long invoiceId, String paymentMode) {
        return payInvoice(invoiceId, paymentMode, 1L);
    }

    @Override
    public List<PosInvoiceEntity> getAllInvoices(Long userId) {
        Long uid = resolveUserId(userId);
        List<PosInvoiceEntity> list = invoiceRepository.findByUserIdOrderByCreatedAtDesc(uid);
        if (list.isEmpty() && uid == 1L) {
            return invoiceRepository.findAllByOrderByCreatedAtDesc();
        }
        return list;
    }

    @Override
    public List<PosInvoiceEntity> getAllInvoices() {
        return getAllInvoices(1L);
    }

    // Reservations
    @Override
    public List<PosReservationEntity> getReservations(String date, String status, Long userId) {
        Long uid = resolveUserId(userId);
        if (date != null && status != null && !"all".equalsIgnoreCase(status)) {
            List<PosReservationEntity> list = reservationRepository.findByUserIdAndResDateAndStatus(uid, date, status);
            if (list.isEmpty() && uid == 1L) {
                return reservationRepository.findByResDateAndStatus(date, status);
            }
            return list;
        } else if (date != null) {
            List<PosReservationEntity> list = reservationRepository.findByUserIdAndResDate(uid, date);
            if (list.isEmpty() && uid == 1L) {
                return reservationRepository.findByResDate(date);
            }
            return list;
        }
        List<PosReservationEntity> list = reservationRepository.findByUserId(uid);
        if (list.isEmpty() && uid == 1L) {
            return reservationRepository.findAll();
        }
        return list;
    }

    @Override
    public List<PosReservationEntity> getReservations(String date, String status) {
        return getReservations(date, status, 1L);
    }

    @Override
    public PosReservationEntity saveReservation(PosReservationEntity reservation, Long userId) {
        if (reservation.getResCode() == null || reservation.getResCode().trim().length() == 0) {
            reservation.setResCode("RES" + System.currentTimeMillis());
        }
        if (reservation.getUserId() == null) {
            reservation.setUserId(resolveUserId(userId));
        }
        return reservationRepository.save(reservation);
    }

    @Override
    public PosReservationEntity saveReservation(PosReservationEntity reservation) {
        return saveReservation(reservation, 1L);
    }

    @Override
    public PosOrderEntity seatReservation(Long resId, Long tableId, Long userId) {
        PosReservationEntity res = reservationRepository.findById(resId).orElse(null);
        if (res == null) return null;

        Long uid = res.getUserId() != null ? res.getUserId() : resolveUserId(userId);
        PosDto.OrderCreateRequest req = new PosDto.OrderCreateRequest();
        req.setUserId(uid);
        req.setType("dine-in");
        req.setTableId(tableId);
        req.setCustomerName(res.getGuestName());
        req.setCustomerPhone(res.getPhone());
        req.setReservationId(res.getId());

        PosOrderEntity order = createOrder(req, uid);
        res.setStatus("seated");
        res.setTableId(tableId);
        res.setOrderId(order.getId());
        reservationRepository.save(res);

        return order;
    }

    @Override
    public PosOrderEntity seatReservation(Long resId, Long tableId) {
        return seatReservation(resId, tableId, 1L);
    }
}
