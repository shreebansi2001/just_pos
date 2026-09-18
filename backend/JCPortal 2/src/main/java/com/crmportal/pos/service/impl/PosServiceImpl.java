package com.crmportal.pos.service.impl;

import java.text.SimpleDateFormat;
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

    private static int orderSequence = 100;
    private static int kotSequence = 100;
    private static int invoiceSequence = 100;

    @Override
    public PosStatsDto getStats() {
        List<PosTableEntity> tables = tableRepository.findByActiveTrue();
        int free = 0;
        int inUse = 0;
        for (PosTableEntity t : tables) {
            if ("available".equalsIgnoreCase(t.getStatus())) {
                free++;
            } else {
                inUse++;
            }
        }
        List<PosKotEntity> activeKots = kotRepository.findByStatusNot("served");
        List<PosInvoiceEntity> invoices = invoiceRepository.findAll();
        double sales = 0.0;
        for (PosInvoiceEntity inv : invoices) {
            if ("paid".equalsIgnoreCase(inv.getStatus()) && inv.getTotal() != null) {
                sales += inv.getTotal();
            }
        }
        return new PosStatsDto(free, inUse, activeKots.size(), sales);
    }

    @Override
    public List<PosTaxEntity> getAllTaxes() {
        return taxRepository.findAll();
    }

    @Override
    public PosTaxEntity saveTax(PosTaxEntity tax) {
        return taxRepository.save(tax);
    }

    @Override
    public void deleteTax(Long id) {
        taxRepository.deleteById(id);
    }

    @Override
    public List<PosCategoryEntity> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    @Override
    public PosCategoryEntity saveCategory(PosCategoryEntity category) {
        if (category.getCode() == null || category.getCode().trim().length() == 0) {
            category.setCode("CAT" + System.currentTimeMillis());
        }
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<PosFloorEntity> getAllFloors() {
        return floorRepository.findAllByOrderBySortOrderAsc();
    }

    @Override
    public PosFloorEntity saveFloor(PosFloorEntity floor) {
        if (floor.getCode() == null || floor.getCode().trim().length() == 0) {
            floor.setCode("FLR" + System.currentTimeMillis());
        }
        return floorRepository.save(floor);
    }

    @Override
    public void deleteFloor(Long id) {
        floorRepository.deleteById(id);
    }

    @Override
    public List<PosTableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    @Override
    public PosTableEntity saveTable(PosTableEntity table) {
        if (table.getCode() == null || table.getCode().trim().length() == 0) {
            table.setCode("TBL" + System.currentTimeMillis());
        }
        return tableRepository.save(table);
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

    @Override
    public List<PosItemEntity> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public PosItemEntity saveItem(PosItemEntity item) {
        if (item.getCode() == null || item.getCode().trim().length() == 0) {
            item.setCode("ITM" + System.currentTimeMillis());
        }
        return itemRepository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public PosOrderEntity createOrder(PosDto.OrderCreateRequest request) {
        PosOrderEntity order = new PosOrderEntity();
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
        return saved;
    }

    @Override
    public PosOrderEntity getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public List<PosOrderEntity> getActiveOrders() {
        return orderRepository.findByStatus("open");
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
                entity.setItemId(dto.getItemId());
                entity.setItemKey(dto.getItemKey());
                entity.setItemName(dto.getItemName());
                entity.setPrice(dto.getPrice() != null ? dto.getPrice() : 0.0);
                entity.setQty(dto.getQty() != null ? dto.getQty() : 0);
                entity.setSentQty(dto.getSentQty() != null ? dto.getSentQty() : 0);
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
        orderRepository.save(order);
    }

    @Override
    public PosKotEntity sendKot(Long orderId, List<PosDto.OrderItemUpdate> items) {
        PosOrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;

        PosKotEntity kot = new PosKotEntity();
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
                kitem.setItemName(item.getItemName());
                kitem.setQty(item.getQty() != null ? item.getQty() : 1);
                kot.getItems().add(kitem);
            }
        }
        return kotRepository.save(kot);
    }

    @Override
    public List<PosKotEntity> getActiveKots() {
        return kotRepository.findAll();
    }

    @Override
    public PosKotEntity updateKotStatus(Long kotId, String status) {
        PosKotEntity kot = kotRepository.findById(kotId).orElse(null);
        if (kot != null) {
            kot.setStatus(status);
            return kotRepository.save(kot);
        }
        return null;
    }

    @Override
    public PosInvoiceEntity generateInvoice(Long orderId) {
        PosOrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;

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

        return invoiceRepository.save(invoice);
    }

    @Override
    public PosInvoiceEntity payInvoice(Long invoiceId, String paymentMode) {
        PosInvoiceEntity invoice = invoiceRepository.findById(invoiceId).orElse(null);
        if (invoice == null) return null;

        invoice.setStatus("paid");
        invoice.setPaymentMode(paymentMode);
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
            }
        }
        return invoice;
    }

    @Override
    public List<PosInvoiceEntity> getAllInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<PosReservationEntity> getReservations(String date, String status) {
        if (date != null && status != null && !"all".equalsIgnoreCase(status)) {
            return reservationRepository.findByResDateAndStatus(date, status);
        } else if (date != null) {
            return reservationRepository.findByResDate(date);
        }
        return reservationRepository.findAll();
    }

    @Override
    public PosReservationEntity saveReservation(PosReservationEntity reservation) {
        if (reservation.getResCode() == null || reservation.getResCode().trim().length() == 0) {
            reservation.setResCode("RES" + System.currentTimeMillis());
        }
        return reservationRepository.save(reservation);
    }

    @Override
    public PosOrderEntity seatReservation(Long resId, Long tableId) {
        PosReservationEntity res = reservationRepository.findById(resId).orElse(null);
        if (res == null) return null;

        PosDto.OrderCreateRequest req = new PosDto.OrderCreateRequest();
        req.setType("dine-in");
        req.setTableId(tableId);
        req.setCustomerName(res.getGuestName());
        req.setCustomerPhone(res.getPhone());
        req.setReservationId(res.getId());

        PosOrderEntity order = createOrder(req);
        res.setStatus("seated");
        res.setTableId(tableId);
        res.setOrderId(order.getId());
        reservationRepository.save(res);

        return order;
    }
}
